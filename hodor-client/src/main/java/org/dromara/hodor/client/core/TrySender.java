package org.dromara.hodor.client.core;

import java.util.function.Function;
import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.utils.StringUtils;

/**
 * try sender
 *
 * @author tomgs
 * @since 1.0
 */
public class TrySender {

    private static volatile String lastConnectUrl = null;

    // eg: http://ip:port/rootName/
    private static final String URL_FORMAT = "{}://{}/{}";

    public static <R> R send(ConnectStringParser parser, Function<String, R> function) throws Exception {
        Exception finallyException = null;
        if (lastConnectUrl != null) {
            try {
                return function.apply(lastConnectUrl);
            } catch (Exception e) {
                finallyException = e;
            }
        }
        for (Host host : parser.getHosts()) {
            try {
                String url = StringUtils.format(URL_FORMAT, parser.getProtocolName(), host.getEndpoint(), parser.getRootName());
                R result = function.apply(url);
                lastConnectUrl = url;
                return result;
            } catch (Exception e) {
                finallyException = e;
            }
        }
        lastConnectUrl = null;
        if (finallyException != null) {
            throw finallyException;
        }
        return null;
    }

}
