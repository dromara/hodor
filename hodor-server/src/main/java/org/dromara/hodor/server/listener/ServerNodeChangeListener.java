package org.dromara.hodor.server.listener;

import org.dromara.hodor.core.manager.NodeServerManager;
import org.dromara.hodor.register.api.DataChangeEvent;
import org.dromara.hodor.register.api.DataChangeListener;

/**
 * server node change listener
 *
 * @author tomgs
 * @since 2020/7/23
 */
public class ServerNodeChangeListener implements DataChangeListener {

    private final NodeServerManager manager;

    public ServerNodeChangeListener(final NodeServerManager nodeServerManager) {
        this.manager = nodeServerManager;
    }

    @Override
    public void dataChanged(DataChangeEvent event) {

        String nodeIp = event.getPath();
        if (event.getType() == DataChangeEvent.Type.NODE_ADDED) {
            manager.addNodeServer(nodeIp);
        } else if (event.getType() == DataChangeEvent.Type.NODE_REMOVED) {
            manager.removeNodeServer(nodeIp);
        }

    }

}
