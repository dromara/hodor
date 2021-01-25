package org.dromara.hodor.common.utils;

import java.nio.charset.StandardCharsets;

/**
 * serialize util
 *
 * @author tomgs
 * @since 2020/9/15
 */
public class SerializeUtils {

    private static final GsonUtils INSTANCE = GsonUtils.getInstance();

    public static byte [] serialize(Object obj) {
        return INSTANCE.toJson(obj).getBytes(StandardCharsets.UTF_8);
    }

    public static <T> T deserialize(byte[] byteData, Class<T> cls) {
        return INSTANCE.fromJson(new String(byteData, StandardCharsets.UTF_8), cls);
    }

}
