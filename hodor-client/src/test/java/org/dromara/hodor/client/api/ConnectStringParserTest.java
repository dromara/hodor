package org.dromara.hodor.client.api;

import org.dromara.hodor.common.Host;
import org.dromara.hodor.common.connect.ConnectStringParser;
import org.junit.Test;

/**
 * @author tomgs
 * @since 1.0
 */
public class ConnectStringParserTest {

    @Test
    public void testConnectStringParser() {
        String connectString = "http://localhost:8081,localhost:8082,localhost:8083/hodor";
        ConnectStringParser parser = new ConnectStringParser(connectString);
        System.out.println(parser.getProtocolName());
        System.out.println(parser.getRootName());
        System.out.println(parser.getHosts());

        for (Host host : parser.getHosts()) {
            System.out.println(host.getEndpoint());
        }
    }

}
