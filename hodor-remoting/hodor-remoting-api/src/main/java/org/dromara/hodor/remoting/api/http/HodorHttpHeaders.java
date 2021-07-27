package org.dromara.hodor.remoting.api.http;

import com.google.common.collect.HashMultimap;
import java.util.Map;
import java.util.Set;

/**
 * hodor http headers
 *
 * @author tomgs
 * @date 2021/7/27 10:42
 * @since 1.0
 */
public class HodorHttpHeaders {

    private final HashMultimap<String, String> headers = HashMultimap.create();

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public Set<Map.Entry<String, String>> entries() {
        return headers.entries();
    }

    public Set<String> get(String key) {
        return headers.get(key);
    }

}
