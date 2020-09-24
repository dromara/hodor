package org.dromara.hodor.core;

import lombok.Data;

/**
 * host info
 *
 * @author tomgs
 * @since 2020/9/24
 */
@Data
public class Host {

    private String endpoint;

    private String ip;

    private int port;
}
