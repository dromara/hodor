/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.hodor.common;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.dromara.hodor.common.utils.TypedMapWrapper;
import org.junit.Assert;
import org.junit.Test;

/**
 * TypeMapWrapperTest
 *
 * @author tomgs
 * @since 1.0
 */
public class TypeMapWrapperTest {

    @Test
    public void testConvertInstance() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 123312L);
        map.put("name", "test");
        map.put("age", 18);

        TypedMapWrapper<String, Object> typedMapWrapper = new TypedMapWrapper<>(map);
        Demo demo = typedMapWrapper.convertInstance(Demo.class);
        Assert.assertEquals(123312L, (long) demo.getId());
    }

    @Data
    public static class Demo {

        private Long id;

        private String name;

        private Integer age;
    }
}
