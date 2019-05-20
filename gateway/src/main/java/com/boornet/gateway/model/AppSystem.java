package com.boornet.gateway.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Date;

/**
 * @Author 文天兆
 * @Description //TODO 系统信息表
 * @Date 2019/8/13 09:45
 **/
@Data
@Accessors(chain = true)
public class AppSystem implements Serializable {

    private String sysCode;

    private String sysInfo;

    private String sysAddr;

    private Integer isVirtual;

    private String sysJoin;

    private String ssoUrl;

    private String sysName;

    private String sysSrcret;

    private String sysStatus;

    private Date createTime;

    private String enpName;

    private String contact;

    private String tel;
}