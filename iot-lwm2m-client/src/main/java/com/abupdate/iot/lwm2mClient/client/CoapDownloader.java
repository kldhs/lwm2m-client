package com.abupdate.iot.lwm2mClient.client;

import com.abupdate.iot.lwm2mClient.properties.Lwm2mConfigProperties;
import com.abupdate.iot.lwm2mClient.util.Codec2;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @program: iot-cloud-ota-coap
 * @description:
 * @author: miaomingwei
 * @create: 2020-10-30 12:31
 */
public class CoapDownloader {

    private static final Logger logger = LoggerFactory.getLogger(CoapDownloader.class);

    public  static String buildSign(String mid, String productId, String productSecret, String timeStamp) {
        String sign = Codec2.hexString(mid + productId + timeStamp, productSecret);
        return sign;
    }

    public static  void downLoad(){
        //String url="coap://iotapi-download-test.abupdate.com:9683/rd/downloads/1607482435/3100229/b8d0051a-819b-444c-9933-79b32b76ee51.zip";
        String url= Lwm2mConfigProperties.getDownLoadUrl();
        //String url="coap://49.235.117.186/mqtt/topic1?c=client1&u=tom&p=secret";
        try {
            logger.info("will try to download {}", url);
            URI uri=new URI(url);
            CoapClient coapClient=new CoapClient(uri);
            coapClient.setURI(url);
            coapClient.setTimeout(5000L);
            //CoapResponse coapResponse = coapClient.get(MediaTypeRegistry.APPLICATION_VND_OMA_LWM2M_TLV);
            CoapResponse coapResponse = coapClient.get();
            logger.info("coapResponse == null ?{}",(coapResponse==null));
            if (coapResponse != null) {
                String responseText = coapResponse.getResponseText();
                logger.info("responseText :{}",responseText);
            }
        } catch (Exception e) {
            logger.error("error:",e);
        }
    }

}