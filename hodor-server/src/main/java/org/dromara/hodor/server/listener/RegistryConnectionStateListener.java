package org.dromara.hodor.server.listener;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.register.api.ConnectionState;
import org.dromara.hodor.register.api.ConnectionStateChangeListener;
import org.dromara.hodor.server.manager.ActuatorNodeManager;
import org.dromara.hodor.server.service.RegistryService;

/**
 * RegisterConnectionStateListener
 * @author tomgs
 * @since 2021/8/18
 */
@Slf4j
public class RegistryConnectionStateListener implements ConnectionStateChangeListener {

    private final RegistryService registryService;

    public RegistryConnectionStateListener(final RegistryService registryService) {
        this.registryService = registryService;
    }

    @Override
    public void stateChanged(ConnectionState newState) {
        if (ConnectionState.SUSPENDED == newState || ConnectionState.LOST == newState) {
            log.error("scheduler {} connection {} from registry.", registryService.getServerEndpoint(), newState);
            ActuatorNodeManager.getInstance().stopOfflineActuatorClean();
        } else if (ConnectionState.RECONNECTED == newState) {
            log.info("scheduler {} reconnected registry.", registryService.getServerEndpoint());
            registryService.initNode();
        }
    }

}
