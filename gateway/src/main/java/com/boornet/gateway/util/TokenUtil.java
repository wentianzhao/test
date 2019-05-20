package com.boornet.gateway.util;

import com.boor.sdk.util.DateUtil;
import com.boor.sdk.util.MD5Util;
import java.util.Date;

/**
 * @Author: 文天兆
 * @Description  //TODO TokenUtil
 * @Date 2019/9/12 17:28
 * @Program: shfda-server
 **/
public class TokenUtil {
    public TokenUtil() {
    }

    public static String[] getHeader(String sysCode) {
        String[] str = new String[]{DateUtil.getDateStr(new Date(), DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS), null};
        str[1] = MD5Util.getMD5(sysCode + str[0] + sysCode).toLowerCase();
        return str;
    }
}
