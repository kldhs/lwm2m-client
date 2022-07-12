package com.abupdate.iot.lwm2mClient.util;

/**
 * @program: iot-cloud-ota-coap
 * @description:
 * @author: miaomingwei
 * @create: 2020-09-15 14:20
 */
public class SignUtils {
    public static String buildSign(String mid, String productId, String productSecret, String timeStamp) {
        String sign = Codec2.hexString(mid + productId + timeStamp, productSecret);
        //System.out.println(sign + timeStamp);
        return sign;
    }
}