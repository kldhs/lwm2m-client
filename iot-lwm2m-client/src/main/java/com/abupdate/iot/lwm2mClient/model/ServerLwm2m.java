package com.abupdate.iot.lwm2mClient.model;

import org.eclipse.leshan.client.object.Server;
import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.client.servers.ServerIdentity;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.model.ResourceModel;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.request.BindingMode;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @program: iot-cloud-ota-coap
 * @description:
 * @author: miaomingwei
 * @create: 2020-09-04 12:28
 */

public class ServerLwm2m extends BaseInstanceEnabler {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    private final static List<Integer> supportedResources = Arrays.asList(0, 1, 2, 3, 6, 7, 8);

    private String op;
    private int shortServerId;
    private long lifetime;
    private Long defaultMinPeriod;
    private Long defaultMaxPeriod;
    private BindingMode binding;
    private boolean notifyWhenDisable;

    public ServerLwm2m() {
        // should only be used at bootstrap time
    }


    public ServerLwm2m(int shortServerId, long lifetime, BindingMode binding, boolean notifyWhenDisable, String op) {
        this.shortServerId = shortServerId;
        this.lifetime = lifetime;
        this.binding = binding;
        this.notifyWhenDisable = notifyWhenDisable;
    }

    @Override
    public ReadResponse read(ServerIdentity identity, int resourceid) {
        if (!identity.isSystem()) {
            LOG.debug("Read on Server resource /{}/{}/{}", getModel().id, getId(), resourceid);
        }
        LOG.debug("Read on Server resource /{}/{}/{}", getModel().id, getId(), resourceid);
        switch (resourceid) {
            case 0: // short server ID
                return ReadResponse.success(resourceid, shortServerId);

            case 1: // lifetime
                return ReadResponse.success(resourceid, lifetime);

            case 2: // default min period
                if (null == defaultMinPeriod) {
                    return ReadResponse.notFound();
                }
                return ReadResponse.success(resourceid, defaultMinPeriod);

            case 3: // default max period
                if (null == defaultMaxPeriod) {
                    return ReadResponse.notFound();
                }
                return ReadResponse.success(resourceid, defaultMaxPeriod);

            case 6: // notification storing when disable or offline
                return ReadResponse.success(resourceid, notifyWhenDisable);

            case 7: // binding
                return ReadResponse.success(resourceid, binding.toString());

//            case 8:
//                return  ReadResponse.success(resourceid, op);
            default:
                return super.read(identity, resourceid);
        }
    }

    @Override
    public WriteResponse write(ServerIdentity identity, int resourceid, LwM2mResource value) {
        if (!identity.isSystem()) {
            LOG.debug("Write on Server resource /{}/{}/{}", getModel().id, getId(), resourceid);
        }
        LOG.debug("Write on Server resource /{}/{}/{}", getModel().id, getId(), resourceid);
        switch (resourceid) {
            case 0:
                if (value.getType() != ResourceModel.Type.INTEGER) {
                    return WriteResponse.badRequest("invalid type");
                }
                int previousShortServerId = shortServerId;
                shortServerId = ((Long) value.getValue()).intValue();
                if (previousShortServerId != shortServerId) {
                    fireResourcesChange(resourceid);
                }
                return WriteResponse.success();

            case 1:
                if (value.getType() != ResourceModel.Type.INTEGER) {
                    return WriteResponse.badRequest("invalid type");
                }
                long previousLifetime = lifetime;
                lifetime = (Long) value.getValue();
                if (previousLifetime != lifetime) {
                    fireResourcesChange(resourceid);
                }
                return WriteResponse.success();

            case 2:
                if (value.getType() != ResourceModel.Type.INTEGER) {
                    return WriteResponse.badRequest("invalid type");
                }
                Long previousDefaultMinPeriod = defaultMinPeriod;
                defaultMinPeriod = (Long) value.getValue();
                if (!Objects.equals(previousDefaultMinPeriod, defaultMinPeriod)) {
                    fireResourcesChange(resourceid);
                }
                return WriteResponse.success();

            case 3:
                if (value.getType() != ResourceModel.Type.INTEGER) {
                    return WriteResponse.badRequest("invalid type");
                }
                Long previousDefaultMaxPeriod = defaultMaxPeriod;
                defaultMaxPeriod = (Long) value.getValue();
                if (!Objects.equals(previousDefaultMaxPeriod, defaultMaxPeriod)) {
                    fireResourcesChange(resourceid);
                }
                return WriteResponse.success();

            case 6: // notification storing when disable or offline
                if (value.getType() != ResourceModel.Type.BOOLEAN) {
                    return WriteResponse.badRequest("invalid type");
                }
                boolean previousNotifyWhenDisable = notifyWhenDisable;
                notifyWhenDisable = (boolean) value.getValue();
                if (previousNotifyWhenDisable != notifyWhenDisable) {
                    fireResourcesChange(resourceid);
                }
                return WriteResponse.success();

            case 7: // binding
                if (value.getType() != ResourceModel.Type.STRING) {
                    return WriteResponse.badRequest("invalid type");
                }
                try {
                    BindingMode previousBinding = binding;
                    binding = BindingMode.valueOf((String) value.getValue());
                    if (!Objects.equals(previousBinding, binding)) {
                        fireResourcesChange(resourceid);
                    }
                    return WriteResponse.success();
                } catch (IllegalArgumentException e) {
                    return WriteResponse.badRequest("invalid value");
                }

            default:
                return super.write(identity, resourceid, value);
        }
    }

    @Override
    public ExecuteResponse execute(ServerIdentity identity, int resourceid, String params) {
        LOG.debug("Execute on Server resource /{}/{}/{}", getModel().id, getId(), resourceid);
        if (resourceid == 8) {
            getLwM2mClient().triggerRegistrationUpdate(identity);
            return ExecuteResponse.success();
        } else {
            return super.execute(identity, resourceid, params);
        }
    }

    @Override
    public void reset(int resourceid) {
        switch (resourceid) {
            case 2:
                defaultMinPeriod = null;
                break;
            case 3:
                defaultMaxPeriod = null;
                break;
            default:
                super.reset(resourceid);
        }
    }

    @Override
    public List<Integer> getAvailableResourceIds(ObjectModel model) {
        return supportedResources;
    }
}
