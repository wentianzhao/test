package com.boornet.gateway.controller;

import com.boornet.gateway.service.QxglFeignService;
import com.boornet.gateway.service.QxglService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: 文天兆
 * @Description  //TODO Feign接口测试
 * @Date 2019/8/28 17:55
 * @Program: shfda-server
 **/
@RestController
public class QxglController {

    @Autowired
    private QxglFeignService qxglFeign;
    @Autowired
    private QxglService qxglService;

    @RequestMapping(value = "/search" , method = RequestMethod.GET)
    public String search(){
        String sysSecret = qxglService.getappSecret("ZSSB");
        System.out.println(sysSecret);
        return sysSecret;
    }
    @RequestMapping(value = "/allAppSystem" , method = RequestMethod.GET)
    public String allAppSystem(){
        return qxglFeign.allAppSystem();
    }

    @GetMapping("/enpApis")
    public String enpApis(){
        return qxglFeign.enpApis("ZSSB");
    }

    @RequestMapping(value = "/isHaveApi" , method = RequestMethod.GET)
    public void isHaveApi(){
        Boolean sysSecret = qxglService.isHaveApi("SSJK","/");
        System.out.println(sysSecret);
    }
}