package com.abupdate.iot.lwm2mClient;

import com.abupdate.iot.lwm2mClient.client.Lwm2mClient;
import com.abupdate.iot.lwm2mClient.properties.Lwm2mConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(Lwm2mConfigProperties.class)
@SpringBootApplication
public class IotLwm2mClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotLwm2mClientApplication.class, args);

        /**
         * 测试 设备注册/检测/上报
         */
        Lwm2mClient.start();

        /**
         * 测试下载功能
         */
        //CoapDownloader.downLoad();
    }

}
