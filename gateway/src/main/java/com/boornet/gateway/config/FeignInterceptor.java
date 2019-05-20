package com.boornet.gateway.config;

import com.boor.sdk.util.ShfdaConfusionUtil;
import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * @Author 文天兆
 *
 * @Description //TODO 使用自定义的RequestInterceptor，在request发送之前，将信息放入请求
 * @Date 2019/8/21 10:37
 **/

@Component
public class FeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        String method = template.method();
        Map<String, Collection<String>> headers = template.headers();
        Request.Body body = template.requestBody();
        String url = template.url();
        if (url.indexOf("?") > -1) {
            url += "&";
        } else {
            url += "?";
        }
        url += "sysCode=ZSSB&userToken=" + ShfdaConfusionUtil.getLoginStr("ADMIN_INNER", "ADMIN_INNER");
       template.uri(url);
    }
}