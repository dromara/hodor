package com.dromara.hodor.common;

import org.dromara.hodor.common.utils.HashUtils;
import org.junit.Test;

/**
 * @author tomgs
 * @since 1.0
 */
public class HashUtilsTest {

    @Test
    public void testHash() {
        System.out.println(HashUtils.hash("123131"));
        System.out.println(HashUtils.hash("123132"));
        System.out.println(HashUtils.hash("123133"));
        System.out.println(HashUtils.hash("123134"));
    }

}
