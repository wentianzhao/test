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
import org.springframework.util.CollectionUtils;
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
 * @Description //TODO 应用权限验证
 * @Date 2019/8/28 17:56
 * @Program: shfda-server
 **/

@Component
public class AppAuthFilter implements GlobalFilter, Ordered {

    private static final String AUTHORIZE_APPID = "appId";
    private static final String AUTHORIZE_TIME_STAMP = "timeStamp";
    private static final String AUTHORIZE_APPSECRET = "appSecret";
    private static final String AUTHORIZE_SIGN = "sign";

    /*@Setter
    @Value("#{'${filter.ignore}'.split(',')}")
    private List<String> ignoreLists = new ArrayList();*/

    @Autowired
    private QxglService qxglService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        URI uri = exchange.getRequest().getURI();
        ApiLog apiLog = new ApiLog();
        apiLog.setUrlPath(uri.toString());
        apiLog.setAppIp(uri.getHost());
        apiLog.setCreateTime(new Date());
        if (uri.toString().contains("qyglServer")) {
            apiLog.setRemark("拦截未校验！");
            qxglService.saveApiLog(apiLog);
            return chain.filter(exchange);
        }
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        //Header中参数不能为空
        HttpHeaders headers = request.getHeaders();
        String appId = headers.getFirst(AUTHORIZE_APPID);
        String headSign = headers.getFirst(AUTHORIZE_SIGN);
        String timaStamp = headers.getFirst(AUTHORIZE_TIME_STAMP);
        ApiHeaderResponse headerResponse = ApiHeaderResponse.builder()
                .appId(appId)
                .timeStamp(timaStamp)
                .sign(headSign)
                .build();
        String msg = headerResponse.checkFields();
        apiLog.setSysCode(appId);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(msg)) {
            apiLog.setRemark("Header中参数不能为空，校验失败！");
            qxglService.saveApiLog(apiLog);
            response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
            return response.writeWith(Mono.just(getResponseMsg(response, "Header中参数不能为空！！")));
        }
        //timeStamp时间校验，十分钟有效
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        try {
            DateTime dateTime = dateTimeFormatter.parseDateTime(timaStamp);
            DateTime currentDateTime = new DateTime();
            int minutes = Minutes.minutesBetween(currentDateTime, dateTime).getMinutes();
            if (minutes > 10 || minutes < -10) {
                apiLog.setRemark("timeStamp超时，校验失败！");
                qxglService.saveApiLog(apiLog);
                response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
                return response.writeWith(Mono.just(getResponseMsg(response, "timeStamp超时！！")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            apiLog.setRemark("timeStamp格式错误，校验失败！");
            qxglService.saveApiLog(apiLog);
            response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
            return response.writeWith(Mono.just(getResponseMsg(response, "timeStamp格式错误！！")));
        }
        String sysSecret = qxglService.getappSecret(appId);
        if (sysSecret == null) {
            apiLog.setRemark("应用无权限，校验失败！");
            qxglService.saveApiLog(apiLog);
            response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
            return response.writeWith(Mono.just(getResponseMsg(response, "应用无权限！！")));
        }

        //校验签名，参数为空不校验
        if (!queryParams.isEmpty()) {
            Map<String, String> singleMap = new HashMap<>();
            queryParams.toSingleValueMap().forEach((key, val) -> singleMap.put(key, val));
            singleMap.put("sign", headSign);
            if (!SignUtils.checkSign(singleMap, appId, sysSecret)) {
                apiLog.setRemark("签名校验失败，校验失败！");
                qxglService.saveApiLog(apiLog);
                response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
                return response.writeWith(Mono.just(getResponseMsg(response, "签名校验失败！！")));
            }
        }

        //校验接口权限
        String path = uri.getPath();
        if (!qxglService.isHaveApi(appId, path)) {
            apiLog.setRemark("没有此接口权限，校验失败！");
            qxglService.saveApiLog(apiLog);
            response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
            return response.writeWith(Mono.just(getResponseMsg(response, "没有此接口权限，请向管理员申请！！")));
        }
        apiLog.setRemark("校验通过！");
        qxglService.saveApiLog(apiLog);
        if (uri.toString().contains("/zhjg/") || uri.toString().contains("/zcbg/") || uri.toString().contains("/zssb/")) {
            return chain.filter(exchange);
        }

        //添加权限管理系统权限验证参数
        StringBuilder query = new StringBuilder();
        String originalQuery = uri.getRawQuery();
        if (uri.toString().contains("/qxglServer/")) {
            if (StringUtils.hasText(originalQuery)) {
                query.append(originalQuery);
                if (originalQuery.charAt(originalQuery.length() - 1) != '&') {
                    query.append('&');
                }
            }
            query.append("sysCode=" + appId + "&userToken=").append(ShfdaConfusionUtil.getLoginStr("ADMIN_INNER", "ADMIN_INNER"));
        } else {
            return chain.filter(exchange);
        }
        try {
            URI newUri = UriComponentsBuilder.fromUri(uri)
                    .replaceQuery(query.toString())
                    .build(true)
                    .toUri();
            ServerHttpRequest request1 = exchange.getRequest().mutate()
                    .uri(newUri)
                    .build();
            return chain.filter(exchange.mutate().request(request1).build());
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Invalid URI query: \"" + query.toString() + "\"");
        }
    }

    @Override
    public int getOrder() {
        return -2;
    }

    /**
     * 返回信息
     *
     * @param response
     * @param msg
     * @return
     */
    protected DataBuffer getResponseMsg(ServerHttpResponse response, String msg) {
        JSONObject message = new JSONObject();
        message.put("returnCode", -1);
        message.put("returnMsg", msg);
        byte[] bits = message.toJSONString().getBytes(StandardCharsets.UTF_8);
        return response.bufferFactory().wrap(bits);
    }

    /**
     * 判读是否在忽略列表中
     */
    /*private boolean inIgnore() {
        if (CollectionUtils.isEmpty(ignoreLists)) {
            return false;
        }
        //   if()
        return true;
    }*/

}