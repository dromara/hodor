package org.dromara.hodor.common;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * host info
 *
 * @author tomgs
 * @since 2020/9/24
 */
@Data
@Builder
@EqualsAndHashCode
public class Host {

    private String endpoint;

    private String ip;

    private int port;

    public static Host of(String endpoint) {
        if (endpoint == null) {
            throw new IllegalArgumentException("endpoint must not be null.");
        }

        String[] split = endpoint.split(":");
        if (split.length != 2) {
            throw new IllegalArgumentException("endpoint format is illegal.");
        }

        return Host.builder().endpoint(endpoint).ip(split[0]).port(Integer.parseInt(split[1])).build();
    }

}
