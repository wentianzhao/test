package com.boornet.gateway.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author 文天兆

 * @Description //TODO JWT加密
 * @Date 2019/8/23 14:46
 **/
@Deprecated
public class JwtTokenUtil {

    //密钥
    public static final String SECRET = "sdjhakdhajdk.slasdfslkfh;o653632asdfasdfklasda";
    //过期时间:秒
    public static final int EXPIRE = 10 * 60 ;

    /**
     * 生成Token
     *
     * @param userId
     * @param userName
     * @return
     * @throws Exception
     */
    public static String createToken(String userId, String userName) throws Exception {
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.SECOND, EXPIRE);
        Date expireDate = nowTime.getTime();

        Map<String, Object> map = new HashMap<>();
        map.put("alg", "_HS256");
        map.put("typ", "_JWT");

        String token = JWT.create()
                .withHeader(map)//头
                .withClaim("userId", userId)
                .withClaim("userName", userName)
                .withSubject("测试")//
                .withIssuedAt(new Date())//签名时间
                .withExpiresAt(expireDate)//过期时间
                .sign(Algorithm.HMAC256(SECRET));//签名
        return token;
    }

    /**
     * 验证Token
     *
     * @param token
     * @return
     * @throws Exception
     */
    public static Map<String, Claim> verifyToken(String token) throws Exception {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
        DecodedJWT jwt = null;
        try {
            jwt = verifier.verify(token);
        } catch (Exception e) {
            throw new RuntimeException("凭证已过期，请重新登录");
        }
        return jwt.getClaims();
    }

    /**
     * 解析Token
     *
     * @param token
     * @return
     */
    public static Map<String, Claim> parseToken(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getClaims();
    }

    public static void main(String[] args) {
        try {
            String token = createToken("12345", "wangbo");
            System.out.println("token=" + token);
            Map<String, Claim> map = verifyToken(token);
            //遍历
            for (Map.Entry<String, Claim> entry : map.entrySet()) {
                if (entry.getValue().asString() != null) {
                    System.out.println(entry.getKey() + "===" + entry.getValue().asString());
                } else {
                    System.out.println(entry.getKey() + "===" + entry.getValue().asDate());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}