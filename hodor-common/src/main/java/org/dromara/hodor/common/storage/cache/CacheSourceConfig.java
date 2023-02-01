package org.dromara.hodor.common.storage.cache;

import java.util.Map;
import lombok.Data;

/**
 * cache source config
 *
 * @author tomgs
 * @since 2021/8/16
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
