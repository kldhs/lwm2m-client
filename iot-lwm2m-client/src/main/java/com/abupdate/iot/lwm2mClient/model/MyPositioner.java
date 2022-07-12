package com.abupdate.iot.lwm2mClient.model;

import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.client.servers.ServerIdentity;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.WriteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @program: iot-cloud-ota-coap
 * @description:
 * @author: miaomingwei
 * @create: 2020-09-18 16:30
 */
public class MyPositioner extends BaseInstanceEnabler {

    private static final Logger LOG = LoggerFactory.getLogger(MyDevice.class);

    private static final List<Integer> supportedResources = Arrays.asList(5750);


    public MyPositioner() {
        // notify new date each 5 second
        Timer timer = new Timer("Device-Current Time");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                fireResourcesChange(5750);
            }
        }, 1000, 1000);
    }


    @Override
    public WriteResponse write(ServerIdentity identity, int resourceid, LwM2mResource value) {
        LOG.info("Write on Device resource /{}/{}/{},value:{}", getModel().id, getId(), resourceid,value.getValue());
        //LOG.info("Server write data" + value.getValue());
        switch (resourceid) {
            case 199:
            fireResourcesChange(resourceid);
            return WriteResponse.success();
            default:
                return super.write(identity, resourceid, value);
        }
    }

    @Override
    public List<Integer> getAvailableResourceIds(ObjectModel model) {
        ArrayList<Integer> resourceIds = new ArrayList<>(model.resources.keySet());
        Collections.sort(resourceIds);
        return resourceIds;
    }
}