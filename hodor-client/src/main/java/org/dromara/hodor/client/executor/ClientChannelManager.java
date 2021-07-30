package org.dromara.hodor.client.executor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.dromara.hodor.common.utils.HostUtils;
import org.dromara.hodor.remoting.api.HodorChannel;

/**
 * client channel manager
 *
 * @author tomgs
 * @since 2021/7/30
 */
public class ClientChannelManager {

    private static final ClientChannelManager INSTANCE = new ClientChannelManager();

    private final Map<String, HodorChannel> activeChannels;

    private long lastFireTime = System.currentTimeMillis();

    private ClientChannelManager() {
        this.activeChannels = new ConcurrentHashMap<>();
    }

    public static ClientChannelManager getInstance() {
        return INSTANCE;
    }

    public void addActiveChannel(HodorChannel activeChannel) {
        String remoteIp = HostUtils.getIp(activeChannel.remoteAddress());
        activeChannels.put(remoteIp, activeChannel);
        pruneInactiveChannel();
    }

    private void pruneInactiveChannel() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastFireTime < 60 * 1000) {
            return;
        }
        lastFireTime = currentTimeMillis;
        activeChannels.entrySet().removeIf(channelEntry -> !channelEntry.getValue().isOpen());
    }

    public HodorChannel getActiveChannel(String remoteIp) {
        return activeChannels.get(remoteIp);
    }

    public HodorChannel getActiveOrBackupChannel(String remoteIp) {
        HodorChannel activeChannel = getActiveChannel(remoteIp);
        if (activeChannel != null && activeChannel.isOpen()) {
            return activeChannel;
        }
        for (HodorChannel backupChannel : activeChannels.values()) {
            if (backupChannel.isOpen()) {
                activeChannel = backupChannel;
                break;
            }
        }
        return activeChannel;
    }

    public void remove(String remoteIp) {
        activeChannels.remove(remoteIp);
    }

    public void remove(HodorChannel activeChannel) {
        activeChannels.remove(HostUtils.getIp(activeChannel.remoteAddress()));
    }

}
