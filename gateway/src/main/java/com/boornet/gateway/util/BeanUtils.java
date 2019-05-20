package com.boornet.gateway.util;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: 文天兆
 * @Description  //TODO bean操作的一些工具类
 * @Date 2019/8/28 17:57
 * @Program: shfda-server
 **/
public class BeanUtils {
    private static Logger log = LoggerFactory.getLogger(BeanUtils.class);

    /**
     * 检查bean里field是否为空，为空则返回信息
     *
     * @param bean 要检查的bean对象
     */
    public static String checkRequiredFields(Object bean) {
        List<String> requiredFields = Lists.newArrayList();
        String msg = "";
        List<Field> fields = new ArrayList<>(Arrays.asList(bean.getClass().getDeclaredFields()));
        fields.addAll(Arrays.asList(bean.getClass().getSuperclass().getDeclaredFields()));
        for (Field field : fields) {
            try {
                boolean isAccessible = field.isAccessible();
                field.setAccessible(true);
                // 两种情况，一种是值为null，
                // 另外一种情况是类型为字符串，但是字符串内容为空的，都认为是没有提供值
                boolean isRequiredMissing = field.get(bean) == null
                        || (field.get(bean) instanceof String
                        && StringUtils.isBlank(field.get(bean).toString())
                );
                if (isRequiredMissing) {
                    requiredFields.add(field.getName());
                }
                field.setAccessible(isAccessible);
            } catch (SecurityException | IllegalArgumentException
                    | IllegalAccessException e) {
                log.error(e.getMessage(), e);
            }
        }

        if (!requiredFields.isEmpty()) {
            msg = "必填字段 " + requiredFields + " 必须提供值";
            log.debug(msg);
        }
        return msg;
    }
}
