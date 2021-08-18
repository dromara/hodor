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

    private int maximumSize;

    private Map<String, Object> cacheRawConfig;

}