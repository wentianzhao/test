package com.boornet.gateway.model;

import lombok.Data;

import java.util.Date;

/**
 * @Author: 文天兆
 * @Description //TODO 接口调用日志
 * @Date 2019/9/6 16:32
 * @Program: qxglServer
 */

@Data
public class ApiLog {

    private String logId;

    private String appIp;

    private Date createTime;

    private String sysCode;

    private Integer status;

    private String remark;

    private String urlPath;
}
