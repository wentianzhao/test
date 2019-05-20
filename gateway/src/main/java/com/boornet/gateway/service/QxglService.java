package com.boornet.gateway.service;

import com.alibaba.fastjson.JSON;
import com.boornet.gateway.model.Api;
import com.boornet.gateway.model.ApiLog;
import com.boornet.gateway.model.AppSystem;
import com.boornet.gateway.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.Long.valueOf;

/**
 * @Author: 文天兆
 * @Description //TODO 权限校验
 * @Date 2019/8/28 17:57
 * @Program: shfda-server
 **/
@Slf4j
@Component
public class QxglService {

    @Autowired
    private QxglFeignService qxglFeign;

    private static final String APP_LIST = "getAllAppSystem";
    private static final String APP_APIS = "getAppApis";
    private static final Long REDIS_TIMEOUT = valueOf(3600 * 5);

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 获取应用密钥
     *
     * @param appId
     * @return
     */
    public String getappSecret(String appId) {
        List<AppSystem> list = (List<AppSystem>) redisUtils.get(APP_LIST);
        if (list == null) {
            String sys = qxglFeign.allAppSystem();
            list = JSON.parseArray(sys, AppSystem.class);
            redisUtils.set(APP_LIST, list, REDIS_TIMEOUT);
        }
        if (list.toString().contains(appId)) {
            for (AppSystem as : list) {
                if (appId.equals(as.getSysCode())) {
                    return as.getSysSrcret();
                }
            }
        }
        return "";
    }

    /**
     * 验证是否有调用权限
     *
     * @param enpSysCode
     * @param url
     * @return
     */
    public Boolean isHaveApi(String enpSysCode, String url) {
        List<Api> list = (List<Api>)redisUtils.get(APP_APIS + "_" + enpSysCode);
        if (list == null || list.size() == 0) {
            String enpApis = qxglFeign.enpApis(enpSysCode);
            list = JSON.parseArray(enpApis, Api.class);
            redisUtils.set(APP_APIS + "_" + enpSysCode, list, REDIS_TIMEOUT);
        }
        for (Api as : list) {
            if (url.contains(as.getApiUrl())) {
                return true;
            }
        }
        log.info(enpSysCode + "----" + url + "11无权限");
        System.out.println(enpSysCode + "QxglService---isHaveApi" + "----" + url + "22无权限");
        return false;
    }

    /**
     * 保存调用日志
     *
     * @param apiLog
     */
    public void saveApiLog(ApiLog apiLog) {
        System.out.println(apiLog);
        try {
            qxglFeign.saveApiLog(apiLog);
        } catch (Exception e) {
            log.error("QxglService.saveApiLog:保存调用日志失败！");
            e.printStackTrace();
        }
    }

}