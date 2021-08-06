package com.dromara.hodor.common;

import java.util.ArrayList;
import java.util.List;
import org.dromara.hodor.common.utils.CopySets;
import org.junit.Test;

/**
 * copy sets test
 *
 * @author tomgs
 * @since 2020/9/2
 */
public class CopySetsTest {

    @Test
    public void testUseCopySets1() {
        List<String> nodes = new ArrayList<>();
        nodes.add("127.0.0.1:8081");
        nodes.add("127.0.0.1:8082");
        nodes.add("127.0.0.1:8083");
        List<List<String>> copySets = CopySets.buildCopySets(nodes, 2, 2);
        System.out.println(copySets); // [[127.0.0.1, 127.0.0.2], [127.0.0.2, 127.0.0.3], [127.0.0.1, 127.0.0.3]]
    }

    @Test
    public void testUseCopySets2() {
        List<String> nodes = new ArrayList<>();
        nodes.add("127.0.0.1");
        nodes.add("127.0.0.2");
        nodes.add("127.0.0.3");
        List<List<String>> copySets = CopySets.buildCopySets(nodes, 2, 2);
        System.out.println(copySets); // [[127.0.0.1, 127.0.0.2], [127.0.0.2, 127.0.0.3], [127.0.0.1, 127.0.0.3]]
    }

    @Test
    public void testUseCopySets3() {
        List<String> nodes = new ArrayList<>();
        nodes.add("127.0.0.1");
        nodes.add("127.0.0.2");
        nodes.add("127.0.0.3");
        nodes.add("127.0.0.4");
        nodes.add("127.0.0.5");
        nodes.add("127.0.0.6");
        nodes.add("127.0.0.7");
        nodes.add("127.0.0.8");
        nodes.add("127.0.0.9");
        List<List<String>> copySets = CopySets.buildCopySets(nodes, 3, 4);
        System.out.println(copySets);
    }

}
