package com.ErasmusProject.util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
@Scope("singleton")
public class Conf {
    private Config config;

    private Boolean initialize;

    @PostConstruct
    public void init() {
        config = ConfigFactory.load("application.conf");
        this.initialize = config.getBoolean("initialize");

    }

    public Boolean getInitialize(){
        return this.initialize;
    }


}