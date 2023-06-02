package org.dromara.hodor.client;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.dromara.hodor.client.config.HodorClientConfig;
import org.dromara.hodor.common.connect.ConnectStringParser;
import org.dromara.hodor.common.exception.HodorException;
import org.dromara.hodor.common.utils.Utils.Assert;
import org.dromara.hodor.common.utils.Utils.Reflections;

/**
 * hodor api client
 *
 * @author tomgs
 * @since 1.0
 */
public class HodorApiClient {

    private final String appName;

    private final String appKey;

    private final ConnectStringParser connectStringParser;

    public HodorApiClient(final HodorClientConfig config) {
        Assert.notBlank(config.getAppName(), "App name must be not null");
        Assert.notBlank(config.getAppKey(), "App key must be not null");
        Assert.notBlank(config.getRegistryAddress(), "Registry address must be not null");
        this.connectStringParser = new ConnectStringParser(config.getRegistryAddress());
        this.appKey = config.getAppKey();
        this.appName = config.getAppName();
    }

    public <API> API createApi(Class<API> apiClass) {
        try {
            final Constructor<API> constructor = Reflections.getConstructor(apiClass, ConnectStringParser.class, String.class, String.class);
            return constructor.newInstance(connectStringParser, appName, appKey);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new HodorException(e);
        }
    }

}
