package com.boornet.gateway.service;

import com.boornet.gateway.model.ApiLog;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: 文天兆
 * @Description  //TODO 权限管理接口
 * @Date 2019/8/28 17:56
 * @Program: shfda-server
 **/

@FeignClient(name = "search-qxgl", url = "http://192.168.3.210:8887")
public interface QxglFeignService {

    /**
     * 获取区县列表--测试
     *
     * @return
     */
    @RequestMapping(value = "/openApi/getRegionList",produces = "application/json; charset=UTF-8")
    String getRegionList();

    /**
     * 获取应用列表
     *
     * @return
     */
    @RequestMapping(value = "/openApi/allAppSystem", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    String allAppSystem();

    /**
     * 获取该应用所有接口权限
     *
     * @param enpSysCode
     * @return
     */
    @RequestMapping(value = "/openApi/enpApis", produces = "application/json; charset=UTF-8")
    String enpApis(@RequestParam("enpSysCode") String enpSysCode);

    /**
     * 保存接口调用日志
     *
     * @param apiLog
     * @return
     */
    @PostMapping(value = "/openApi/saveApiLog", produces = "application/json; charset=UTF-8")
    String saveApiLog(@RequestBody ApiLog apiLog);

}