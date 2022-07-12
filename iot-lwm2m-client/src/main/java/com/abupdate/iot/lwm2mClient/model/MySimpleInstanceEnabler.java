package com.abupdate.iot.lwm2mClient.model;

import org.eclipse.leshan.client.resource.SimpleInstanceEnabler;
import org.eclipse.leshan.client.servers.ServerIdentity;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.WriteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @program: iot-cloud-ota-coap
 * @description:
 * @author: miaomingwei
 * @create: 2021-01-15 17:16
 */
public class MySimpleInstanceEnabler extends SimpleInstanceEnabler {
    private static final Logger LOG = LoggerFactory.getLogger(MySimpleInstanceEnabler.class);


    public MySimpleInstanceEnabler() {
    }

    public MySimpleInstanceEnabler(int id) {
        super(id);
    }

    public MySimpleInstanceEnabler(int id, Map<Integer, Object> initialValues) {
        super(id);
        this.initialValues = initialValues;
    }

    @Override
    public WriteResponse write(ServerIdentity identity, int resourceid, LwM2mResource value) {
        LOG.info("resourceid:{},value: {}:",resourceid ,value);
        if (resourceid == 101) {
            // 这个是delID

        }


        this.getLwM2mClient().triggerRegistrationUpdate();

        return super.write(identity, resourceid, value);
    }

    @Override
    public void setModel(ObjectModel objectModel) {
        super.setModel(objectModel);
    }
    @Override
    public List<Integer> getAvailableResourceIds(ObjectModel model) {
        List<Integer> resourceIds = Arrays.asList(1,100,101,102);
        Collections.sort(resourceIds);
        return resourceIds;
    }
}
