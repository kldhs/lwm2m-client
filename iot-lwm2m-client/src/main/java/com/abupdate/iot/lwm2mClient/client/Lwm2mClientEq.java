package com.abupdate.iot.lwm2mClient.client;

import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import com.abupdate.iot.lwm2mClient.model.MyDevice;
import com.abupdate.iot.lwm2mClient.model.MyPositioner;
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
 * @description: emqx完成lwm2m研究
 * @author: miaomingwei
 * @create: 2020-09-18 16:29
 */
public class Lwm2mClientEq {
    public final static String[] modelPaths = new String[]{"10241.xml", "10242.xml", "10243.xml", "10244.xml",
            "10245.xml", "10246.xml", "10247.xml", "10248.xml", "10249.xml", "10250.xml", "10251.xml",
            "10252.xml", "10253.xml", "10254.xml", "10255.xml", "10256.xml", "10257.xml", "10258.xml",
            "10259.xml", "10260-2_0.xml", "10262.xml", "10263.xml", "10264.xml", "10265.xml",
            "10266.xml", "10267.xml", "10268.xml", "10269.xml", "10270.xml", "10271.xml", "10272.xml",
            "10273.xml", "10274.xml", "10275.xml", "10276.xml", "10277.xml", "10278.xml", "10279.xml",
            "10280.xml", "10281.xml", "10282.xml", "10283.xml", "10284.xml", "10286.xml", "10290.xml",
            "10291.xml", "10292.xml", "10299.xml", "10300.xml", "10308-2_0.xml", "10309.xml",
            "10311.xml", "10313.xml", "10314.xml", "10315.xml", "10316.xml", "10318.xml", "10319.xml",
            "10320.xml", "10322.xml", "10323.xml", "10324.xml", "10326.xml", "10327.xml", "10328.xml",
            "10329.xml", "10330.xml", "10331.xml", "10332.xml", "10333.xml", "10334.xml", "10335.xml",
            "10336.xml", "10337.xml", "10338.xml", "10339.xml", "10340.xml", "10341.xml", "10342.xml",
            "10343.xml", "10344.xml", "10345.xml", "10346.xml", "10347.xml", "10348.xml", "10349.xml",
            "10350.xml", "10351.xml", "10352.xml", "10353.xml", "10354.xml", "10355.xml", "10356.xml",
            "10357.xml", "10358.xml", "10359.xml", "10360.xml", "10361.xml", "10362.xml", "10363.xml",
            "10364.xml", "10365.xml", "10366.xml", "10368.xml", "10369.xml",

            "2048.xml", "2049.xml", "2050.xml", "2051.xml", "2052.xml", "2053.xml", "2054.xml",
            "2055.xml", "2056.xml", "2057.xml",

            "3200.xml", "3201.xml", "3202.xml", "3203.xml", "3300.xml", "3301.xml", "3302.xml",
            "3303.xml", "3304.xml", "3305.xml", "3306.xml", "3308.xml", "3310.xml", "3311.xml",
            "3312.xml", "3313.xml", "3314.xml", "3315.xml", "3316.xml", "3317.xml", "3318.xml",
            "3319.xml", "3320.xml", "3321.xml", "3322.xml", "3323.xml", "3324.xml", "3325.xml",
            "3326.xml", "3327.xml", "3328.xml", "3329.xml", "3330.xml", "3331.xml", "3332.xml",
            "3333.xml", "3334.xml", "3335.xml", "3336.xml", "3337.xml", "3338.xml", "3339.xml",
            "3340.xml", "3341.xml", "3342.xml", "3343.xml", "3344.xml", "3345.xml", "3346.xml",
            "3347.xml", "3348.xml", "3349.xml", "3350.xml", "3351.xml", "3352.xml", "3353.xml",
            "3354.xml", "3355.xml", "3356.xml", "3357.xml", "3358.xml", "3359.xml", "3360.xml",
            "3361.xml", "3362.xml", "3363.xml", "3364.xml", "3365.xml", "3366.xml", "3367.xml",
            "3368.xml", "3369.xml", "3370.xml", "3371.xml", "3372.xml", "3373.xml", "3374.xml",
            "3375.xml", "3376.xml", "3377.xml", "3378.xml", "3379.xml", "3380-2_0.xml", "3381.xml",
            "3382.xml", "3383.xml", "3384.xml", "3385.xml", "3386.xml",
            "LWM2M_Device-v1_0_3.xml",
            "LWM2M_APN_Connection_Profile-v1_0_1.xml", "LWM2M_Bearer_Selection-v1_0_1.xml",
            "LWM2M_Cellular_Connectivity-v1_0_1.xml", "LWM2M_DevCapMgmt-v1_0.xml",
            "LWM2M_LOCKWIPE-v1_0_1.xml", "LWM2M_Portfolio-v1_0.xml",
            "LWM2M_Software_Component-v1_0.xml", "LWM2M_Software_Management-v1_0.xml",
            "LWM2M_WLAN_connectivity4-v1_0.xml", "LwM2M_BinaryAppDataContainer-v1_0_1.xml",
            "LwM2M_EventLog-V1_0.xml"};


    private static final int OBJECT_ID_TEMPERATURE_SENSOR = 3303;
    private final static String DEFAULT_ENDPOINT = "LeshanClientDemo";
    private final static int DEFAULT_LIFETIME = 5 * 60; // 5min in seconds
    private final static String USAGE = "java -jar leshan-client-demo.jar [OPTION]\n\n";

//    @Value("${spring.lwm2mconfig.op}")
//    private static String op;
//    public  void setOp(String op) {
//        Lwm2mClient.op = op;
//    }
//
//    @Value("${spring.lwm2mconfig.count}")
//    private static Integer count;
//    public  void setCount(Integer count) {
//        Lwm2mClient.count = count;
//    }
//    @Value("${spring.lwm2mconfig.midBegin}")
//    private static String midBegin;
//    public  void setMidBegin(String midBegin) {
//        Lwm2mClient.midBegin = midBegin;
//    }
//    @Value("${spring.lwm2mconfig.loopCount}")
//    private static Integer loopCount;
//    public  void setLoopCount(Integer loopCount) {
//        Lwm2mClient.loopCount = loopCount;
//    }

    /**
     *@Description: 多线程创建lwm2m2客户端
     *@Param:
     *@return:
     *@Author: miaomingwei
     *@date: 2020/8/31 16:36
     */
    public  static   void  start()
    {

        ExecutorService executorService = Executors.newCachedThreadPool();
        CopyOnWriteArrayList<String> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
        copyOnWriteArrayList.add("867726035717307");
//        List<String> copyOnWriteArrayList = new ArrayList<>();
        BigInteger integer = new BigInteger(Lwm2mConfigProperties.getMidBegin());

        for (int i = 0; i < 1; i++) {
            BigInteger add = integer.add(new BigInteger(String.valueOf(i)));
            copyOnWriteArrayList.add(add.toString());
        }
        try {
            initLwm2mClient("867726035717307");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidModelException e) {
            e.printStackTrace();
        } catch (InvalidDDFFileException e) {
            e.printStackTrace();
        }

        //initLwm2mClient("867726035717307");
//        copyOnWriteArrayList.forEach(t -> {
//            executorService.submit(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//                    } catch (InvalidModelException e) {
//                        e.printStackTrace();
//                    } catch (InvalidDDFFileException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        });
//        executorService.shutdown();
    }

    /**
     *@Description: 初始化lwm2m对象的属性
     *@Param:
     *@return:
     *@Author: miaomingwei
     *@date: 2020/8/31 16:51
     */
    public  static void initLwm2mClient(String sn) throws InvalidModelException, InvalidDDFFileException, IOException {

        // Initialize model
        List<ObjectModel> models = ObjectLoader.loadDefault();
        models.addAll(ObjectLoader.loadDdfResources("/models/", modelPaths));

        // Initialize object list
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
        //additionalAttributes.put("productId",Lwm2mConfigProperties.getProductId());
        additionalAttributes.put("productId","1597124766");
        additionalAttributes.put("platform", Lwm2mConfigProperties.getPlatform());
        additionalAttributes.put("deviceType", Lwm2mConfigProperties.getDeviceType());
        additionalAttributes.put("ep",sn);
        additionalAttributes.put("sms","1597124766");
        //additionalAttributes.put("ep", "0048003D5452510320333837YJP4IH2KVHO6V0L6UW0FSW00");
        // 注册
        //additionalAttributes.put("op", "rg");
        //cv
        additionalAttributes.put("op", "cv");


        //ac需要参数
//        additionalAttributes.put("op", "ac");
//        additionalAttributes.put("uuid", "1347393034323638002A001F");
//        additionalAttributes.put("bid", "Y6S8YJF3F4XEMCIETMYNEQ00");
        //下载结果上报
//        additionalAttributes.put("op", "rd");
//        additionalAttributes.put("deltaID", "3096067");

//        additionalAttributes.put("downloadStatus", "1");
//        additionalAttributes.put("downStart", "1504883570");
//        additionalAttributes.put("downEnd", "1504883570");
        //升级结果上报
//        additionalAttributes.put("op", "ru");
//        additionalAttributes.put("deltaID", "3096067");
//        additionalAttributes.put("updateStatus", "1");
//        additionalAttributes.put("sms",Lwm2mConfigProperties.getProductId());
//        additionalAttributes.put("b", "U");

         //ua
//        additionalAttributes.put("op", "ua");
//        additionalAttributes.put("mid", sn);
        //ac
//        additionalAttributes.put("op", "ac");
//        additionalAttributes.put("uuid", "1347393034323638002A001F");
//        additionalAttributes.put("bid", "YJP4IH2KVHO6V0L6UW0FSW00");

        additionalAttributes.put("oem", Lwm2mConfigProperties.getOem());
        additionalAttributes.put("models", Lwm2mConfigProperties.getModels());
        additionalAttributes.put("sdkversion", Lwm2mConfigProperties.getSdkversion());
        additionalAttributes.put("appversion", Lwm2mConfigProperties.getAppversion());
        additionalAttributes.put("version",Lwm2mConfigProperties.getVersion());
        additionalAttributes.put("networkType", Lwm2mConfigProperties.getNetworkType());
        additionalAttributes.put("timestamp", Long.toString(timeStamp));

        //additionalAttributes.put("sign","7dc9500879d1b2be81bbd765a2b0c5ed");
//        additionalAttributes.put("sign", buildSign(Lwm2mConfigProperties.getMidBegin(), Lwm2mConfigProperties.getProductId(), Lwm2mConfigProperties.getProductSecret(), timeStamp.toString()));
        additionalAttributes.put("sign", buildSign(sn, Lwm2mConfigProperties.getProductId(), Lwm2mConfigProperties.getProductSecret(), timeStamp.toString()));
        // Configure Registration Engine
        DefaultRegistrationEngineFactory engineFactory = new DefaultRegistrationEngineFactory();
        // configure EndpointFactory
        DefaultEndpointFactory endpointFactory = new DefaultEndpointFactory("LWM2M CLIENT") {
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

        // Get additional attributes for bootstrap
        Map<String, String> bsAdditionalAttributes = null;


        MyPositioner myPositioner = new MyPositioner();
        initializer.setInstancesForObject(LwM2mId.SECURITY, Security.noSec("coap://49.235.117.186:5683", 12345));
        ServerLwm2m cv = new ServerLwm2m(12345, 5 * 60, BindingMode.U, false, "cv");
        initializer.setInstancesForObject(LwM2mId.SERVER, cv);
        initializer.setInstancesForObject(3337, myPositioner);
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
                System.out.println("Object {} disabled.");
            }

            @Override
            public void objectAdded(LwM2mObjectEnabler object) {
                //LOG.info("Object {} enabled.", object.getId());
                System.out.println("Object {} disabled.");
            }
        });
        client.start();


//        try {
//            Thread.sleep(1500);
//            for (String key : additionalAttributes.keySet()) {
//                if (key.equals("op")) {
//                    additionalAttributes.put(key, Lwm2mConfigProperties.getOp());
////                additionalAttributes.put(key, "ac");
////                additionalAttributes.put("bid", "MUDXIK65VC3UE00SUV74SA00");
////                additionalAttributes.put("uuid", "1947383031303731001B002A");
//                }
//                //additionalAttributes.put("uuid", "867726035717307");
//            }
//            client.triggerRegistrationUpdate();
//            myPositioner.fireResourcesChange(5750,5536,5537);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }



        //getActiveCode(additionalAttributes,myDevice,client);

    }


    /**
     *@Description: 获取激活码
     *@Param:
     *@return:
     *@Author: miaomingwei
     *@date: 2020/9/8 11:26
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
     *@Description: 校验版本方法
     *@Param:
     *@return:
     *@Author: miaomingwei
     *@date: 2020/9/8 11:26
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
     *@Description: 创建Sign
     *@Param:
     *@return: 返回Sign
     *@Author: miaomingwei
     *@date: 2020/8/31 16:36
     */
    public  static String buildSign(String mid, String productId, String productSecret, String timeStamp) {
        //Integer calc = CRCUtils.calc((mid + productId + productSecret + timeStamp).getBytes());
        //return  calc.toString();
        //uriQuery.getEp() + uriQuery.getProductId() + uriQuery.getTimestamp(), product.getProductSecret()
        String sign = Codec2.hexString(mid + productId + timeStamp, productSecret);
        //System.out.println(sign + timeStamp);
        return sign;
    }
}