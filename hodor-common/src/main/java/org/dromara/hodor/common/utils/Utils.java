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

package org.dromara.hodor.common.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

/**
 * Utils
 * </p>
 * Manage third-party tools in a unified manner
 *
 * @author tomgs
 * @since 1.0
 */
public class Utils {

    public static class Assert extends cn.hutool.core.lang.Assert {

    }

    public static class Https extends HttpUtil {

    }

    public static class Collections extends CollUtil {

    }

    public static class Reflections extends ReflectUtil {

    }

    public static class Beans extends BeanUtil {

    }

    public static class Jsons extends JSONUtil {

    }

}
