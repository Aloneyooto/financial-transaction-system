package com.alone.counter.util;

import com.alone.counter.bean.order.OrderCmd;
import com.alone.counter.bean.order.OrderStatus;
import com.alone.counter.bean.res.Account;
import com.alone.counter.bean.res.OrderInfo;
import com.alone.counter.bean.res.PosiInfo;
import com.alone.counter.bean.res.TradeInfo;
import com.alone.counter.cache.CacheType;
import com.alone.counter.cache.RedisStringCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @BelongsProject: counter
 * @BelongsPackage: com.alone.counter.util
 * @Author: Alone
 * @CreateTime: 2020-10-27 17:24
 * @Description: 数据库连接
 */

@Component
public class DbUtil {

    //单例对象
    private static DbUtil dbUtil = null;

    /**
     * 与数据库交互的模板类
     * 因为该类是静态调用(即使用类名调用的),Spring无法将该模板类注入到DbUtil中
     */
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    /**
     * 如何在静态调用的工具类中注入Spring管理的对象？
     * 加入@PostConstruct之后,该方法会在类构造完成后第一时间执行
     * 执行下面代码后DBUtil类和sqlSessionTemplate即可建立关联
     */
    @PostConstruct
    private void init() {
        dbUtil = new DbUtil();
        dbUtil.setSqlSessionTemplate(sqlSessionTemplate);
    }

    private void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
    }

    private SqlSessionTemplate getSqlSessionTemplate() {
        return this.sqlSessionTemplate;
    }

    private DbUtil(){}

    ///////////////////////////身份认证//////////////////////////////

    /**
     * 根据uid和密码查询账户信息
     * @param uid
     * @param password
     * @return
     */
    public static Account queryAccount(long uid, String password) {
        return dbUtil.getSqlSessionTemplate().selectOne(
                "userMapper.queryAccount",
                ImmutableMap.of("UId", uid, "Password", password)
        );
    }

    /**
     * 更新最近一次访问时间
     */
    public static void updateLoginTime(long uid, String nowDate, String nowTime) {
        dbUtil.getSqlSessionTemplate().update(
                "userMapper.updateAccountLoginTime",
                ImmutableMap.of(
                        "UId", uid,
                        "ModifyDate", nowDate,
                        "ModifyTime", nowTime
                )
        );
    }

    /**
     * 修改密码
     */
    public static int updatePwd(long uid, String oldPwd, String newPwd) {
        return dbUtil.getSqlSessionTemplate().update(
                "userMapper.updatePwd",
                ImmutableMap.of("UId", uid,
                        "newPwd", newPwd,
                        "oldPwd", oldPwd));
    }

    ////////////////////////////////////////////////////////////////

    //////////////////////////资金类/////////////////////////////////

    public static long getBalance(long uid) {
        Long res = dbUtil.getSqlSessionTemplate()
                .selectOne("orderMapper.queryBalance",
                        ImmutableMap.of("UId", uid));
        if(res == null) {
            return -1;
        } else {
            return res;
        }
    }

    /**
     * 增加资金
     * @param uid
     * @return
     */
    public static void addBalance(long uid, long balance) {
        dbUtil.getSqlSessionTemplate().update("orderMapper.updateBalance",
                ImmutableMap.of("UId", uid, "Balance", balance));
    }

    /**
     * 减少资金
     * @param uid
     * @param balance
     */
    public static void minusBalance(long uid, long balance) {
        addBalance(uid, -balance);
    }

    //////////////////////////持仓类/////////////////////////////////

    public static List<PosiInfo> getPosiList(long uid) {
        //查缓存
        String suid = Long.toString(uid);
        String posiS = RedisStringCache.get(suid, CacheType.POSI);
        if(StringUtils.isEmpty(posiS)) {
            //未查到 查库 删除缓存
            List<PosiInfo> tmp = dbUtil.getSqlSessionTemplate().selectList(
                    "orderMapper.queryPosi",
                    ImmutableMap.of("UId", uid)
            );
            List<PosiInfo> result = CollectionUtils.isEmpty(tmp) ? Lists.newArrayList() : tmp;
            //更新缓存
            RedisStringCache.cache(suid, JsonUtil.toJson(result), CacheType.POSI);
            return result;
        } else {
            //查到 命中缓存
            return JsonUtil.fromJsonArr(posiS, PosiInfo.class);
        }
    }

    /**
     * 查询单个持仓
     * @param uid
     * @param code
     * @return
     */
    public static PosiInfo getPosi(long uid, int code) {
        return dbUtil.getSqlSessionTemplate().selectOne("orderMapper.queryPosi",
                ImmutableMap.of("UId", uid, "Code", code));
    }

    /**
     * 增加持仓
     * @param uid
     * @param code
     * @param volume
     * @param price
     */
    public static void addPosi(long uid, int code, long volume, long price) {
        //持仓是否存在
        PosiInfo posiInfo = getPosi(uid, code);
        if(posiInfo == null) {
            //新增一条持仓
            insertPosi(uid, code, volume, price);
        } else {
            //修改持仓
            posiInfo.setCount(posiInfo.getCount() + volume);
            posiInfo.setCost(posiInfo.getCost() + price * volume);
//            if(posiInfo.getCount() == 0) { //实际要在当天收盘时才删除
//                deletePosi(posi);
//            } else {
                updatePosi(posiInfo);
//            }
        }
    }


    public static void minusPosi(long uid, int code, long volume, long price) {
        addPosi(uid, code, -volume, price);
    }

    /**
     * 添加持仓信息到数据库
     * @param uid
     * @param code
     * @param volume
     * @param price
     */
    private static void insertPosi(long uid, int code, long volume, long price) {
        dbUtil.getSqlSessionTemplate().insert("orderMapper.insertPosi",
                ImmutableMap.of("UId", uid,
                        "Code", code,
                        "Count", volume,
                        "Cost", volume * price)
        );
    }

    private static void updatePosi(PosiInfo posiInfo) {
        dbUtil.getSqlSessionTemplate().update("orderMapper.updatePosi",
                ImmutableMap.of("UId", posiInfo.getUid(),
                        "Code", posiInfo.getCode(),
                        "Count", posiInfo.getCount(),
                        "Cost", posiInfo.getCost())
        );
    }


    //////////////////////////委托类/////////////////////////////////

    public static List<OrderInfo> getOrderList(long uid) {
        //查缓存
        String suid = Long.toString(uid);
        String orderS = RedisStringCache.get(suid, CacheType.ORDER);
        if(StringUtils.isEmpty(orderS)) {
            //未查到 查库 删除缓存
            List<OrderInfo> tmp = dbUtil.getSqlSessionTemplate().selectList(
                    "orderMapper.queryOrder",
                    ImmutableMap.of("UId", uid)
            );
            List<OrderInfo> result = CollectionUtils.isEmpty(tmp) ? Lists.newArrayList() : tmp;
            //更新缓存
            RedisStringCache.cache(suid, JsonUtil.toJson(result), CacheType.ORDER);
            return result;
        } else {
            //查到 命中缓存
            return JsonUtil.fromJsonArr(orderS, OrderInfo.class);
        }
    }

    //////////////////////////成交类/////////////////////////////////

    public static List<TradeInfo> getTradeList(long uid) {
        //查缓存
        String suid = Long.toString(uid);
        String tradeS = RedisStringCache.get(suid, CacheType.TRADE);
        if(StringUtils.isEmpty(tradeS)) {
            //未查到 查库 删除缓存
            List<TradeInfo> tmp = dbUtil.getSqlSessionTemplate().selectList(
                    "orderMapper.queryTrade",
                    ImmutableMap.of("UId", uid)
            );
            List<TradeInfo> result = CollectionUtils.isEmpty(tmp) ? Lists.newArrayList() : tmp;
            //更新缓存
            RedisStringCache.cache(suid, JsonUtil.toJson(result), CacheType.TRADE);
            return result;
        } else {
            //查到 命中缓存
            return JsonUtil.fromJsonArr(tradeS, TradeInfo.class);
        }
    }

    //////////////////////////订单处理类////////////////////////////

    public static int saveOrder(OrderCmd orderCmd) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("UId", orderCmd.uid);
        param.put("Code", orderCmd.code);
        param.put("Direction", orderCmd.direction.getDirection());
        param.put("Type", orderCmd.orderType.getType());
        param.put("Price", orderCmd.price);
        param.put("OCount", orderCmd.volume);
        param.put("TCount", 0);
        param.put("Status", OrderStatus.NOT_SET.getCode());

        param.put("Date", TimeformatUtil.yyyyMMdd(orderCmd.timestamp));
        param.put("Time", TimeformatUtil.hhMMss(orderCmd.timestamp));

        int count = dbUtil.getSqlSessionTemplate().insert(
                "orderMapper.saveOrder", param
        );
        //判断是否成功
        if(count > 0) {
            return Integer.parseInt(param.get("ID").toString());
        } else {
            return -1;
        }
    }

    /////////////////////查询所有股票信息////////////////////////////

    public static List<Map<String, Object>> queryAllStockInfo() {
        return dbUtil.getSqlSessionTemplate()
                .selectList("stockMapper.queryStock");
    }
}
