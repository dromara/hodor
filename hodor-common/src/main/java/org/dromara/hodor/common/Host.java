package org.dromara.hodor.common;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.hodor.common.utils.StringUtils;

/**
 * host info
 *
 * @author tomgs
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Host {

    private String endpoint;

    private String ip;

    private int port;

    public static Host of(String endpoint) {
        if (endpoint == null) {
            throw new IllegalArgumentException("endpoint must not be null.");
        }

        List<String> hostSplit = StringUtils.splitToList(endpoint, ":");
        if (hostSplit.size() == 1) {
            return Host.builder().endpoint(endpoint).ip(hostSplit.get(0)).port(80).build();
        }
        if (hostSplit.size() != 2) {
            throw new IllegalArgumentException("endpoint format is illegal.");
        }
        return Host.builder().endpoint(endpoint).ip(hostSplit.get(0)).port(Integer.parseInt(hostSplit.get(1))).build();
    }

    public static Host.HostBuilder builder() {
        return new Host.HostBuilder();
    }

    public static class HostBuilder {
        private String endpoint;

        private String ip;

        private int port;

        HostBuilder() {
        }

        public Host.HostBuilder endpoint(final String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Host.HostBuilder ip(final String ip) {
            this.ip = ip;
            return this;
        }

        public Host.HostBuilder port(final int port) {
            this.port = port;
            return this;
        }

        public Host build() {
            if (this.endpoint == null) {
                this.endpoint = this.ip + ":" + this.port;
            }
            return new Host(this.endpoint, this.ip, this.port);
        }

        public String toString() {
            return "Host.HostBuilder(endpoint=" + this.endpoint + ", ip=" + this.ip + ", port=" + this.port + ")";
        }
    }

}
