package com.boornet.gateway.model;

import com.boornet.gateway.util.BeanUtils;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author 文天兆

 * @Description //TODO 接口Header签名
 * @Date 2019/8/28 10:29
 **/
@Data
@Builder
public class ApiHeaderResponse implements Serializable {
    private static final long serialVersionUID = -7966682379048446567L;

    private String appId;
    private String timeStamp;
    private String sign;

    /**
     * 检查请求参数内容，包括必填参数以及特殊约束.
     */
    public String checkFields() {
        //check required fields
        return BeanUtils.checkRequiredFields(this);
    }

}
