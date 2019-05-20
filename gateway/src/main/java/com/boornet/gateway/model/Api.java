package com.boornet.gateway.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * @Author: 文天兆
 * @Description  //TODO 接口信息表
 * @Date 2019/8/28 17:57
 * @Program: shfda-server
 **/
@Data
@Accessors(chain = true)
public class Api implements Serializable {

    private String apiId;

    private String apiUrl;

    private String sysCode;

    private Integer status;

    private String apiName;

}