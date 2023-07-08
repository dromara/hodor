package org.dromara.hodor.server.listener;

import lombok.extern.slf4j.Slf4j;
import org.dromara.hodor.register.api.ConnectionState;
import org.dromara.hodor.register.api.ConnectionStateChangeListener;
import org.dromara.hodor.server.manager.ActuatorNodeManager;
import org.dromara.hodor.server.service.HodorService;

/**
 * RegisterConnectionStateListener
 * @author tomgs
 * @since 1.0
 */
@Slf4j
public class RegistryConnectionStateListener implements ConnectionStateChangeListener {

    private final HodorService hodorService;

    public RegistryConnectionStateListener(final HodorService hodorService) {
        this.hodorService = hodorService;
    }

    @Override
    public void stateChanged(ConnectionState newState) {
        if (ConnectionState.SUSPENDED == newState || ConnectionState.LOST == newState) {
            log.error("scheduler {} connection {} from registry.", hodorService.getServerEndpoint(), newState);
            ActuatorNodeManager.getInstance().stopOfflineActuatorClean();
        } else if (ConnectionState.RECONNECTED == newState) {
            log.info("scheduler {} reconnected registry.", hodorService.getServerEndpoint());
            hodorService.init();
        }
    }

}
