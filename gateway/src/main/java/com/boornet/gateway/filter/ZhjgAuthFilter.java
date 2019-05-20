package com.boornet.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.boor.sdk.util.ShfdaConfusionUtil;
import com.boornet.gateway.model.ApiHeaderResponse;
import com.boornet.gateway.model.ApiLog;
import com.boornet.gateway.service.QxglService;
import com.boornet.gateway.util.SignUtils;
import com.boornet.gateway.util.TokenUtil;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @Author: 文天兆
 * @Description  //TODO 添加证候监管系统权限验证参数
 * @Date 2019/9/16 14:18
 * @Program: shfda-server
 **/

@Component
public class ZhjgAuthFilter implements GlobalFilter, Ordered {

    private static final String AUTHORIZE_APPID = "appId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        URI uri = exchange.getRequest().getURI();
        boolean addheader = false;
        StringBuffer sysCode = new StringBuffer();
        if (uri.toString().contains("/zhjg/")){
            sysCode.append("ZHJG");
            addheader = true;
        }
        if (uri.toString().contains("/zcbg/")){
            sysCode.append("ZCBG");
            addheader = true;
        }
        if (uri.toString().contains("/zssb/")){
            sysCode.append("ZSSB");
            addheader = true;
        }
        //添加证候监管系统权限验证参数
        if (addheader) {
            String[] strings = TokenUtil.getHeader(sysCode.toString());
            ServerHttpRequest request1 = exchange.getRequest();
            MultiValueMap<String, String> queryParams = request1.getQueryParams();
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header("requestTime", strings[0])
                    .header("token", strings[1])
                    .build();
            return chain.filter(exchange.mutate().request(request).build());
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}