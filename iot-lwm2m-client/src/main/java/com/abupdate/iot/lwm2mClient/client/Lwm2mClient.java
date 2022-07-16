package com.abupdate.iot.lwm2mClient.client;

import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import com.abupdate.iot.lwm2mClient.model.MyDevice;
import com.abupdate.iot.lwm2mClient.model.MyObject;
import com.abupdate.iot.lwm2mClient.model.MySimpleInstanceEnabler;
import com.abupdate.iot.lwm2mClient.model.ServerLwm2m;
import com.abupdate.iot.lwm2mClient.properties.Lwm2mConfigProperties;
import com.abupdate.iot.lwm2mClient.util.Codec2;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.elements.Connector;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.*;
import org.eclipse.leshan.client.californium.LeshanClient;
import org.eclipse.leshan.client.californium.LeshanClientBuilder;
import org.eclipse.leshan.client.engine.DefaultRegistrationEngineFactory;
import org.eclipse.leshan.client.object.Security;
import org.eclipse.leshan.client.resource.LwM2mObjectEnabler;
import org.eclipse.leshan.client.resource.ObjectsInitializer;
import org.eclipse.leshan.client.resource.listener.ObjectsListenerAdapter;
import org.eclipse.leshan.core.LwM2mId;
import org.eclipse.leshan.core.californium.DefaultEndpointFactory;

import org.eclipse.leshan.core.model.*;
import org.eclipse.leshan.core.request.BindingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @program: iot-cloud-ota-coap
 * @description: Lwm2m客户端
 * @author: miaomingwei
 * @create: 2020-09-04 12:26
 */
@Slf4j
@Component
public class Lwm2mClient {

    private static final Logger logger = LoggerFactory.getLogger(Lwm2mClient.class);

    public final static String[] modelPaths = new String[]{
            "LWM2M_Device-v1_0_3.xml",
            "LWM2M_APN_Connection_Profile-v1_0_1.xml", "LWM2M_Bearer_Selection-v1_0_1.xml",
            "LWM2M_Cellular_Connectivity-v1_0_1.xml", "LWM2M_DevCapMgmt-v1_0.xml",
            "LWM2M_LOCKWIPE-v1_0_1.xml", "LWM2M_Portfolio-v1_0.xml",
            "LWM2M_Software_Component-v1_0.xml", "LWM2M_Software_Management-v1_0.xml",
            "LWM2M_WLAN_connectivity4-v1_0.xml", "LwM2M_BinaryAppDataContainer-v1_0_1.xml",
            "LwM2M_EventLog-V1_0.xml", "LWM2M_Firmware_Update-v1_0_3.xml", "3308.xml"};

    /**
     * @Description: 多线程创建lwm2m2客户端
     * @Param:
     * @return:
     * @Author: miaomingwei
     * @date: 2020/8/31 16:36
     */
    public static void start() {
        try {
            initLwm2mClient(Lwm2mConfigProperties.getMidBegin());
        } catch (InvalidModelException e) {
            e.printStackTrace();
        } catch (InvalidDDFFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description: 初始化lwm2m对象的属性
     * @Param:
     * @return:
     * @Author: miaomingwei
     * @date: 2020/8/31 16:51
     */
    public static void initLwm2mClient(String sn) throws InvalidModelException, InvalidDDFFileException, IOException {
        // Initialize model
        List<ObjectModel> models = ObjectLoader.loadDefault();
        models.addAll(ObjectLoader.loadDdfResources("/models/", modelPaths));

        final LwM2mModel model = new StaticModel(models);
        final ObjectsInitializer initializer = new ObjectsInitializer(model);

        // Create CoAP Config
        NetworkConfig coapConfig;
        File configFile = new File(NetworkConfig.DEFAULT_FILE_NAME);
        if (configFile.isFile()) {
            coapConfig = new NetworkConfig();
            coapConfig.load(configFile);
        } else {
            coapConfig = LeshanClientBuilder.createDefaultNetworkConfig();
            coapConfig.store(configFile);
        }
        Map<String, String> additionalAttributes = new HashMap<String, String>();
        Long timeStamp = System.currentTimeMillis();
        additionalAttributes.put("productId", Lwm2mConfigProperties.getProductId());
        additionalAttributes.put("platform", Lwm2mConfigProperties.getPlatform());
        additionalAttributes.put("deviceType", Lwm2mConfigProperties.getDeviceType());
        // mid
        additionalAttributes.put("ep", sn);
        //additionalAttributes.put("ep", "GUSQVI9VXUXLP0GMR45SHQ00");
        //additionalAttributes.put("op", Lwm2mConfigProperties.getOp());
        additionalAttributes.put("sms", Lwm2mConfigProperties.getProductId());
        additionalAttributes.put("b", "U");
        additionalAttributes.put("oem", Lwm2mConfigProperties.getOem());
        additionalAttributes.put("models", Lwm2mConfigProperties.getModels());
        additionalAttributes.put("sdkversion", Lwm2mConfigProperties.getSdkversion());
        additionalAttributes.put("appversion", Lwm2mConfigProperties.getAppversion());
        additionalAttributes.put("version", Lwm2mConfigProperties.getVersion());
        additionalAttributes.put("networkType", Lwm2mConfigProperties.getNetworkType());
        additionalAttributes.put("timestamp", Long.toString(timeStamp));
        additionalAttributes.put("sign", buildSign(sn, Lwm2mConfigProperties.getProductId(), Lwm2mConfigProperties.getProductSecret(), timeStamp.toString()));
        // Configure Registration Engine
        DefaultRegistrationEngineFactory engineFactory = new DefaultRegistrationEngineFactory();
        // configure EndpointFactory
        DefaultEndpointFactory endpointFactory = getEndpointFactory();

        // Get additional attributes for bootstrap
        Map<String, String> bsAdditionalAttributes = null;
        MySimpleInstanceEnabler mySimpleInstanceEnabler = new MySimpleInstanceEnabler(0);
        mySimpleInstanceEnabler.setModel(models.get(21));
        MyDevice myDevice = new MyDevice();
        MyObject myObject = new MyObject();
        initializer.setInstancesForObject(LwM2mId.SECURITY, Security.noSec("coap://" + Lwm2mConfigProperties.getHost() + ":5683", 12345));
        ServerLwm2m cv = new ServerLwm2m(12345, 5 * 60, BindingMode.U, false, "cv");
        initializer.setInstancesForObject(LwM2mId.SERVER, cv);
        initializer.setInstancesForObject(LwM2mId.DEVICE, myDevice);
        initializer.setInstancesForObject(3308, myObject);
        initializer.setInstancesForObject(LwM2mId.FIRMWARE, mySimpleInstanceEnabler);
        List<LwM2mObjectEnabler> enablers = initializer.createAll();
        //String endpoint = "867726035711307" ; // 选择一个端点名称
        String endpoint = sn;
        LeshanClientBuilder builder = new LeshanClientBuilder(endpoint);
        builder.setAdditionalAttributes(additionalAttributes);
        builder.setCoapConfig(coapConfig);
        builder.setObjects(enablers);
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(20);
        builder.setSharedExecutor(scheduledExecutorService);
        builder.setRegistrationEngineFactory(engineFactory);
        builder.setEndpointFactory(endpointFactory);
        builder.setBootstrapAdditionalAttributes(bsAdditionalAttributes);
        // add it to the client  添加到客户端
        LeshanClient client = builder.build();
        client.getObjectTree().addListener(new ObjectsListenerAdapter() {
            @Override
            public void objectRemoved(LwM2mObjectEnabler object) {
                //LOG.info();
                logger.info("Object {} disabled.");
            }

            @Override
            public void objectAdded(LwM2mObjectEnabler object) {
                //LOG.info("Object {} enabled.", object.getId());
                logger.info("Object {} disabled.");
            }
        });
        client.start();
        //toDoCheck(client,additionalAttributes,myDevice,mySimpleInstanceEnabler);
    }

    static DefaultEndpointFactory getEndpointFactory() {
        return new DefaultEndpointFactory("LWM2M CLIENT") {
            @Override
            protected Connector createSecuredConnector(DtlsConnectorConfig dtlsConfig) {
                return new DTLSConnector(dtlsConfig) {
                    @Override
                    protected void onInitializeHandshaker(Handshaker handshaker) {
                        handshaker.addSessionListener(new SessionAdapter() {

                            @Override
                            public void handshakeStarted(Handshaker handshaker) throws HandshakeException {
                                if (handshaker instanceof ServerHandshaker) {
                                    //LOG.info("DTLS Full Handshake initiated by server : STARTED ...");
                                } else if (handshaker instanceof ResumingServerHandshaker) {
                                    //LOG.info("DTLS abbreviated Handshake initiated by server : STARTED ...");
                                } else if (handshaker instanceof ClientHandshaker) {
                                    //LOG.info("DTLS Full Handshake initiated by client : STARTED ...");
                                } else if (handshaker instanceof ResumingClientHandshaker) {
                                    //LOG.info("DTLS abbreviated Handshake initiated by client : STARTED ...");
                                }
                            }

                            @Override
                            public void sessionEstablished(Handshaker handshaker, DTLSSession establishedSession)
                                    throws HandshakeException {
                                if (handshaker instanceof ServerHandshaker) {
                                    //LOG.info("DTLS Full Handshake initiated by server : SUCCEED");
                                } else if (handshaker instanceof ResumingServerHandshaker) {
                                    //LOG.info("DTLS abbreviated Handshake initiated by server : SUCCEED");
                                } else if (handshaker instanceof ClientHandshaker) {
                                    //LOG.info("DTLS Full Handshake initiated by client : SUCCEED");
                                } else if (handshaker instanceof ResumingClientHandshaker) {
                                    //LOG.info("DTLS abbreviated Handshake initiated by client : SUCCEED");
                                }
                            }

                            @Override
                            public void handshakeFailed(Handshaker handshaker, Throwable error) {
                                // get cause
                                String cause;
                                if (error != null) {
                                    if (error.getMessage() != null) {
                                        cause = error.getMessage();
                                    } else {
                                        cause = error.getClass().getName();
                                    }
                                } else {
                                    cause = "unknown cause";
                                }

                                if (handshaker instanceof ServerHandshaker) {
                                    //LOG.info("DTLS Full Handshake initiated by server : FAILED ({})", cause);
                                } else if (handshaker instanceof ResumingServerHandshaker) {
                                    // LOG.info("DTLS abbreviated Handshake initiated by server : FAILED ({})", cause);
                                } else if (handshaker instanceof ClientHandshaker) {
                                    //LOG.info("DTLS Full Handshake initiated by client : FAILED ({})", cause);
                                } else if (handshaker instanceof ResumingClientHandshaker) {
                                    //LOG.info("DTLS abbreviated Handshake initiated by client : FAILED ({})", cause);
                                }
                            }
                        });
                    }
                };
            }
        };
    }


    private static void toDoCheck(LeshanClient client, Map<String, String> additionalAttributes, MyDevice myDevice,
                                  MySimpleInstanceEnabler mySimpleInstanceEnabler) {

        logger.info("toDoCheck 1/2");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }

        additionalAttributes.put("op", "ua");
        additionalAttributes.put("v", "1");
        additionalAttributes.put("version", "1");
        additionalAttributes.put("mid", "ted_test_20210507_01");
        additionalAttributes.put("productId", "1600392888");
        logger.info("set op = ua");
//
//        for (String key : additionalAttributes.keySet()) {
//            if (key.equals("op")) {
//                additionalAttributes.put(key,"cv");
//
//                logger.info("set op = cv");
//            }
//        }
        client.triggerRegistrationUpdate();
        myDevice.fireResourcesChange(0, 200, 180, 8);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        client.triggerRegistrationUpdate();
        mySimpleInstanceEnabler.fireResourcesChange(0, 1, 100, 101, 102);

        logger.info("toDoCheck 2/2");
    }

    /**
     * @Description: 获取激活码
     * @Param:
     * @return:
     * @Author: miaomingwei
     * @date: 2020/9/8 11:26
     */
    private static void getActiveCode(Map<String, String> additionalAttributes, MyDevice myDevice, LeshanClient client) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (String key : additionalAttributes.keySet()) {
            if (key.equals("op")) {
                additionalAttributes.put(key, "ac");
            }
        }
        client.triggerRegistrationUpdate();
        myDevice.fireResourcesChange(0, 8);
    }

    /**
     * @Description: 校验版本方法
     * @Param:
     * @return:
     * @Author: miaomingwei
     * @date: 2020/9/8 11:26
     */
    private static void checkVersion(Map<String, String> additionalAttributes, MyDevice myDevice, LeshanClient client) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (String key : additionalAttributes.keySet()) {
            if (key.equals("op")) {
                additionalAttributes.put(key, "cv");
            }
        }
        client.triggerRegistrationUpdate();
        myDevice.fireResourcesChange(0, 8);
    }


    /**
     * @Description: 创建Sign
     * @Param:
     * @return: 返回Sign
     * @Author: miaomingwei
     * @date: 2020/8/31 16:36
     */
    public static String buildSign(String mid, String productId, String productSecret, String timeStamp) {
        //Integer calc = CRCUtils.calc((mid + productId + productSecret + timeStamp).getBytes());
        //return  calc.toString();
        //uriQuery.getEp() + uriQuery.getProductId() + uriQuery.getTimestamp(), product.getProductSecret()
        String sign = Codec2.hexString(mid + productId + timeStamp, productSecret);
        //System.out.println(sign + timeStamp);
        return sign;
    }
}
