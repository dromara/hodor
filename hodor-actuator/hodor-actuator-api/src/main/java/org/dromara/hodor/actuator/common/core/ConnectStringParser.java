package org.dromara.hodor.actuator.common.core;

import cn.hutool.core.lang.Assert;
import java.util.ArrayList;
import org.dromara.hodor.common.Host;

/**
 * connect string parser
 *
 * @author tomgs
 * @since 2021/8/24
 */
public class ConnectStringParser {

    private static final int DEFAULT_PORT = 46367;

    private final String protocolName;

    private final String rootName;

    private final ArrayList<Host> hosts = new ArrayList<>();

    /**
     * @throws IllegalArgumentException for an invalid chroot path.
     */
    public ConnectStringParser(String connectString) {
        Assert.notBlank(connectString, "hodor client connect string must be not blank.");
        // parse connect protocol
        int index = connectString.indexOf("://");
        if (index >= 0) {
            this.protocolName = connectString.substring(0, index);
            connectString = connectString.substring(index + 3);
        } else {
            this.protocolName = null;
        }

        // parse out chroot, if any
        int off = connectString.indexOf('/');
        if (off >= 0) {
            String rootName = connectString.substring(off + 1);
            // ignore "/" chroot spec, same as null
            if (rootName.length() == 1) {
                this.rootName = null;
            } else {
                this.rootName = rootName;
            }
            connectString = connectString.substring(0, off);
        } else {
            this.rootName = null;
        }

        String[] hostsList = connectString.split(",");
        for (String host : hostsList) {
            int port = DEFAULT_PORT;
            int pidx = host.lastIndexOf(':');
            if (pidx >= 0) {
                // otherwise : is at the end of the string, ignore
                if (pidx < host.length() - 1) {
                    port = Integer.parseInt(host.substring(pidx + 1));
                }
                host = host.substring(0, pidx);
            }
            hosts.add(Host.builder().ip(host).port(port).build());
        }
    }

    public String getProtocolName() {
        return protocolName;
    }

    public String getRootName() {
        return rootName;
    }

    public ArrayList<Host> getHosts() {
        return hosts;
    }

}
