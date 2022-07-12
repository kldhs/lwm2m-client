package com.abupdate.iot.lwm2mClient.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @program: iot-cloud-ota-coap
 * @description:
 * @author: miaomingwei
 * @create: 2020-09-14 14:51
 */
@Component
@ConfigurationProperties(prefix = "lwm2m.config")
public class Lwm2mConfigProperties {

    public static String getDownLoadUrl() {
        return downLoadUrl;
    }

    public void setDownLoadUrl(String downLoadUrl) {
        Lwm2mConfigProperties.downLoadUrl = downLoadUrl;
    }

    @Value("${lwm2m.config.downLoadUrl}")
    private static String downLoadUrl;

    public static String getHost() {
        return host;
    }

    public  void setHost(String host) {
        Lwm2mConfigProperties.host = host;
    }

    @Value("${lwm2m.config.host}")
    private static  String host;
    /**
     * 操作
     */
    @Value("${lwm2m.config.op}")
    private static String op;
    /**
     * mid
     */
    @Value("${lwm2m.config.midBegin}")
    private static   String midBegin;
    /**
     * 压测数量
     */
    @Value("${lwm2m.config.count}")
    private static   Integer count;
    /**
     * 循环次数
     */
    @Value("${lwm2m.config.loopCount}")
    private static   Integer loopCount;
    /**
     * 产品工作网络环境
     */
    @Value("${lwm2m.config.networkType}")
    private static String networkType;
    /**
     * 产品id
     */
    @Value("${lwm2m.config.productId}")
    private static String productId;
    /**
     * 设备平台
     */
    @Value("${lwm2m.config.platform}")
    private static String platform;

    /**
     * 设备类型
     */
    @Value("${lwm2m.config.deviceType}")
    private static String deviceType;
    /**
     * 设备oem
     */
    @Value("${lwm2m.config.oem}")
    private static String oem;
    /**
     * 设备模型
     */
    @Value("${lwm2m.config.models}")
    private static String models;
    /**
     * sdk版本
     */
    @Value("${lwm2m.config.sdkversion}")
    private static String sdkversion;
    /**
     * 设备版本
     */
    @Value("${lwm2m.config.appversion}")
    private static String appversion;

    /**
     * 版本
     */
    @Value("${lwm2m.config.version}")
    private static String version;
    /**
     * 产品密钥
     */
    @Value("${lwm2m.config.productSecret}")
    private static String productSecret;
    /**
     * 注册url
     */
    @Value("${lwm2m.config.registerUrl}")
    private static String registerUrl;



    /**
     * 注册url
     */
    @Value("${lwm2m.config.isRegister}")
    private static boolean isRegister;

    public  void setMidBegin(String midBegin) {
        Lwm2mConfigProperties.midBegin = midBegin;
    }
    public static String getMidBegin() {
        return midBegin;
    }
    public  void setOp(String op) {
        Lwm2mConfigProperties.op = op;
    }
    public static String getOp() {
        return op;
    }
    public static Integer getCount() {
        return count;
    }
    public  void setCount(Integer count) {
        Lwm2mConfigProperties.count = count;
    }
    public static Integer getLoopCount() {
        return loopCount;
    }
    public  void setLoopCount(Integer loopCount) {
        Lwm2mConfigProperties.loopCount = loopCount;
    }
    public static String getNetworkType() {
        return networkType;
    }
    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }
    public static String getProductId() {
        return productId;
    }
    public  void setProductId(String productId) {
        Lwm2mConfigProperties.productId = productId;
    }
    public static String getPlatform() {
        return platform;
    }
    public  void setPlatform(String platform) {
        Lwm2mConfigProperties.platform = platform;
    }
    public static String getDeviceType() {
        return deviceType;
    }
    public  void setDeviceType(String deviceType) {
        Lwm2mConfigProperties.deviceType = deviceType;
    }
    public static String getOem() {
        return oem;
    }
    public  void setOem(String oem) {
        Lwm2mConfigProperties.oem = oem;
    }
    public static String getModels() {
        return models;
    }
    public  void setModels(String models) {
        Lwm2mConfigProperties.models = models;
    }
    public static String getSdkversion() {
        return sdkversion;
    }
    public  void setSdkversion(String sdkversion) {
        Lwm2mConfigProperties.sdkversion = sdkversion;
    }
    public static String getAppversion() {
        return appversion;
    }
    public  void setAppversion(String appversion) {
        Lwm2mConfigProperties.appversion = appversion;
    }
    public static String getVersion() {
        return version;
    }
    public  void setVersion(String version) {
        Lwm2mConfigProperties.version = version;
    }
    public static String getProductSecret() {
        return productSecret;
    }
    public  void setProductSecret(String productSecret) {
        Lwm2mConfigProperties.productSecret = productSecret;
    }
    public static String getRegisterUrl() {
        return registerUrl;
    }

    public  void setRegisterUrl(String registerUrl) {
        Lwm2mConfigProperties.registerUrl = registerUrl;
    }
    public static boolean getIsRegister() {
        return isRegister;
    }

    public  void setIsRegister(boolean isRegister) {
        Lwm2mConfigProperties.isRegister = isRegister;
    }
}