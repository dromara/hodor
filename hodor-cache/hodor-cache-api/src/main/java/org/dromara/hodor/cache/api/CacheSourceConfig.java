package org.dromara.hodor.cache.api;

import java.util.Map;
import lombok.Data;

/**
 * cache source config
 *
 * @author tomgs
 * @since 1.0
 */
@Data
public class CacheSourceConfig {

    private String type;

    private String serverAddresses;

    private String dataPath;

    private String dbPath;

    private int maximumSize;

    private int timeout;

    private Map<String, Object> cacheRawConfig;

}
