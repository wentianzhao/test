package com.boornet.gateway;

import com.boor.sdk.apisign.constant.ApiSignConstants;
import com.boor.sdk.apisign.util.HeaderUtil;
import com.boornet.gateway.model.ApiHeaderResponse;
import com.boornet.gateway.util.SignUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GatewayApplicationTests {

    @Test
    public void contextLoads() {}

    @Test
    public void testSign() {
        String signType = "MD5";
        ApiHeaderResponse headerResponse = ApiHeaderResponse.builder()
                .appId("ZHJG")
                .timeStamp("2019-07-29 11:33:08")
                .sign("ceshi")
                .build();
        headerResponse.setSign(SignUtils.createSign(headerResponse, signType,
                "9748B7B9F96783E8905EC0820938D678", null));
        System.out.println(headerResponse.toString());

        boolean checkSign = SignUtils.checkSign(headerResponse, signType, "111");
        System.out.println(checkSign);
    }

    @Test
    public void createHeader(){
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("enpName", "上海好药师唐黄药店");
        paramMap.put("modifyTime", "2016-09-17");
        HttpHeaders httpHeaders = HeaderUtil.createHeader("ZHJG", SignUtils.createSign(paramMap, ApiSignConstants.MD5, "9748B7B9F96783E8905EC0820938D678", null));
        System.out.println(httpHeaders.get("appId"));
        System.out.println(httpHeaders.get("timestamp"));
        System.out.println(httpHeaders.get("sign"));

    }

}
