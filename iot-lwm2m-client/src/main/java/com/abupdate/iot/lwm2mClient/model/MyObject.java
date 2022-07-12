package com.abupdate.iot.lwm2mClient.model;

import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.client.servers.ServerIdentity;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.WriteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jackmiao on 2021/4/16.
 */
public class MyObject extends BaseInstanceEnabler {
    private static final Logger LOG = LoggerFactory.getLogger(MyDevice.class);

    private static final List<Integer> supportedResources = Arrays.asList(5900);


    public MyObject() {
        // notify new date each 5 second
        Timer timer = new Timer("Device-Current Time");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                fireResourcesChange(5900);
            }
        }, 1000, 1000);
    }


    @Override
    public WriteResponse write(ServerIdentity identity, int resourceid, LwM2mResource value) {

//        LwM2mClient lwM2mClient = (LeshanClient)this.getLwM2mClient();
//        CoapEndpoint coapEndpoint = ((LeshanClient) lwM2mClient).coap().getEndpoint(identity);
//        URI uri = coapEndpoint.getUri();
//        String rawPath = uri.getRawPath();
//        String rawQuery = uri.getRawQuery();
        LOG.info("write On Device value :{}",value.getValue());
        LOG.info("Write on Device resource /{}/{}/{}", getModel().id, getId(), resourceid);
        //LOG.info("Server write data" + value.getValue());
        switch (resourceid) {
            default:
                return super.write(identity, resourceid, value);
        }
    }
}
