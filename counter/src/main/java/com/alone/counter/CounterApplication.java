package com.alone.counter;

import com.alone.counter.config.CounterConfig;
import com.alone.counter.util.DbUtil;
import com.alone.counter.util.thirdpart.GudyUuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import javax.annotation.PostConstruct;

@SpringBootApplication
@ServletComponentScan(basePackages = {"com.alone.counter.filter"})
public class CounterApplication {

    @Autowired
    private DbUtil dbUtil;

    @Autowired
    private CounterConfig counterConfig;

    /**
     * 初始化验证码
     */
    @PostConstruct
    private void init() {
        GudyUuid.getInstance().init(counterConfig.getDataCenterId(), counterConfig.getWorkerId());
    }

    public static void main(String[] args) {
        SpringApplication.run(CounterApplication.class, args);
    }

}
