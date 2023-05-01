package com.dromara.hodor.common;

import org.dromara.hodor.common.IdGenerator;
import org.junit.Test;

/**
 * id generator test
 *
 * @author tomgs
 * @since 1.0
 */
public class IdGeneratorTest {

    @Test
    public void testNextId() {
        long id = IdGenerator.defaultGenerator().nextId();
        System.out.println(id); //750732770336051200

        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            IdGenerator.defaultGenerator().nextId();
        }
        long end = System.currentTimeMillis();
        System.out.println((end - start));
    }

}
