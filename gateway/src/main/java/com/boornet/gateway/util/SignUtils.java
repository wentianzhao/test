package com.boornet.gateway.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @Author: 文天兆
 * @Description  //TODO 签名相关工具类
 * @Date 2019/8/28 17:58
 * @Program: shfda-server
 **/
 
@Slf4j
public class SignUtils {

    /**
     * 签名算法
     *
     * @param xmlBean       Bean里的属性如果存在XML注解，则使用其作为key，否则使用变量名
     * @param signType      签名类型，如果为空，则默认为MD5
     * @param signKey       签名Key
     * @param ignoredParams 签名时需要忽略的特殊参数
     * @return 签名字符串 string
     */
    public static String createSign(Object xmlBean, String signType, String signKey, String[] ignoredParams) {
        return createSign(xmlBean2Map(xmlBean), signType, signKey, ignoredParams);
    }

    /**
     * 签名算法
     *
     * @param params        参数信息
     * @param signType      签名类型，如果为空，则默认为MD5
     * @param signKey       签名Key
     * @param ignoredParams 签名时需要忽略的特殊参数
     * @return 签名字符串 string
     */
    public static String createSign(Map<String, String> params, String signType, String signKey, String[] ignoredParams) {
        SortedMap<String, String> sortedMap = new TreeMap<>(params);

        StringBuilder toSign = new StringBuilder();
        for (String key : sortedMap.keySet()) {
            String value = params.get(key);
            boolean shouldSign = false;
            if (StringUtils.isNotEmpty(value) && !ArrayUtils.contains(ignoredParams, key)
                    && !Lists.newArrayList("sign", "key").contains(key)) {
                shouldSign = true;
            }
            if (shouldSign) {
                toSign.append(key).append(value);
            }
        }
        toSign.insert(0,signKey+"+").append("+").append(signKey);
        return DigestUtils.md5Hex(toSign.toString()).toUpperCase();
    }

    /**
     * 校验签名是否正确.
     *
     * @param xmlBean  Bean需要标记有XML注解
     * @param signType 签名类型，如果为空，则默认为MD5
     * @param signKey  校验的签名Key
     * @return true - 签名校验成功，false - 签名校验失败
     */
    public static boolean checkSign(Object xmlBean, String signType, String signKey) {
        return checkSign(xmlBean2Map(xmlBean), signType, signKey);
    }

    /**
     * 校验签名是否正确.
     *
     * @param params   需要校验的参数Map
     * @param signType 签名类型，如果为空，则默认为MD5
     * @param signKey  校验的签名Key
     * @return true - 签名校验成功，false - 签名校验失败
     */
    public static boolean checkSign(Map<String, String> params, String signType, String signKey) {
        String sign = createSign(params, signType, signKey, new String[0]);
        return sign.equals(params.get("sign"));
    }

    /**
     * map对象.
     *
     * @param bean 包含@XStreamAlias的xml bean对象
     * @return map对象 map
     */
    public static Map<String, String> xmlBean2Map(Object bean) {
        Map<String, String> result = Maps.newHashMap();
        List<Field> fields = new ArrayList<>(Arrays.asList(bean.getClass().getDeclaredFields()));
        fields.addAll(Arrays.asList(bean.getClass().getSuperclass().getDeclaredFields()));
        for (Field field : fields) {
            try {
                boolean isAccessible = field.isAccessible();
                field.setAccessible(true);
                if (field.get(bean) == null) {
                    field.setAccessible(isAccessible);
                    continue;
                }
                if (!Modifier.isStatic(field.getModifiers())) {
                    //忽略掉静态成员变量
                    result.put(field.getName(), field.get(bean).toString());
                }
                field.setAccessible(isAccessible);
            } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
                log.error(e.getMessage(), e);
            }

        }
        return result;
    }
}
