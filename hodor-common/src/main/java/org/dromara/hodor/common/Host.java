package org.dromara.hodor.common;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.hodor.common.utils.StringUtils;

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

        List<String> hostSplit = StringUtils.splitToList(endpoint, ":");
        if (hostSplit.size() == 1) {
            return Host.builder().endpoint(endpoint).ip(hostSplit.get(0)).port(80).build();
        }
        if (hostSplit.size() != 2) {
            throw new IllegalArgumentException("endpoint format is illegal.");

        }
        return Host.builder().endpoint(endpoint).ip(hostSplit.get(0)).port(Integer.parseInt(hostSplit.get(1))).build();
    }

}
