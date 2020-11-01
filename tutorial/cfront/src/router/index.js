import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Login',//修改默认地址为登录页面
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: () => import('../views/Home.vue'),
    children : [
        {
          path: '/dashboard',
          name: 'Dashboard',//添加资金股份子页面
          component: () => import('../views/Dashboard.vue')
        },
        {
          path: '/pwdsetting',
          name: 'PwdSetting',//添加修改密码子页面
          component: () => import('../views/PwdSetting.vue'),
          meta: {requiredAuth: true}//标志当前路由需要验证身份
        },
        {
          path: '/transfer',
          name: 'Transfer',//添加转账子页面
          component: () => import('../views/Transfer.vue'),
          meta: {requiredAuth: false}//标志当前路由需要验证身份
        },
        {
          path: '/transferquery',
          name: 'TransferQuery',//转账查询子页面
          component: () => import('../views/TransferQuery.vue'),
          meta: {requiredAuth: false}//标志当前路由需要验证身份
        },
        {
          path: '/orderquery',
          name: 'OrderQuery',//当日委托子页面
          component: () => import('../views/OrderQuery.vue'),
          meta: {requiredAuth: false}//标志当前路由需要验证身份
        },
        {
          path: '/tradequery',
          name: 'TradeQuery',//当日委托子页面
          component: () => import('../views/TradeQuery.vue'),
          meta: {requiredAuth: false}//标志当前路由需要验证身份
        },
        {
          path: '/hisorderquery',
          name: 'HisOrderQuery',//历史委托子页面
          component: () => import('../views/HisOrderQuery.vue'),
          meta: {requiredAuth: false}//标志当前路由需要验证身份
        },
        {
          path: '/histradequery',
          name: 'HisTradeQuery',//历史委托子页面
          component: () => import('../views/HisTradeQuery.vue'),
          meta: {requiredAuth: false}//标志当前路由需要验证身份
        },
        {
          path: '/buy',
          name: 'Buy',//委托买入子页面
          component: () => import('../views/Buy.vue'),
          meta: {requiredAuth: false}//标志当前路由需要验证身份
        },
        {
          path: '/sell',
          name: 'Sell',//委托卖出子页面
          component: () => import('../views/Sell.vue'),
          meta: {requiredAuth: false}//标志当前路由需要验证身份
        },
    ]
  },
  {
    path: '/404',
    name: '404',//404错误页面路由
    component: () => import('../views/404.vue')
  },
  {
    path: '*',//前面所有路由都匹配不上,就重定向到404
    redirect: '/404'
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

// 路由拦截器
router.beforeEach((to, from, next) => {
    if(to.meta.requiredAuth) { //前往的页面是否需要身份认证
      if(Boolean(sessionStorage.getItem("uid"))) { //判断是否合法
        next();
      } else {
          next({
            path: '/',
          });
      }
    } else {
      next();
    }
})

export default router
