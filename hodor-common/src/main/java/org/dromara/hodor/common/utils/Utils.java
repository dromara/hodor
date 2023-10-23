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
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.SystemUtil;
import cn.hutool.system.oshi.OshiUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;
import oshi.hardware.GlobalMemory;

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

        public static long validLong(String longStr, String errorMsgTemplate, String... params) {
            long result;
            try {
                result = Long.parseLong(longStr);
            } catch (Exception e) {
                throw new IllegalArgumentException(StrUtil.format(errorMsgTemplate, (Object[]) params));
            }
            return result;
        }

        public static <T> T validParse(Supplier<? extends T> supplier, String errorMsgTemplate, String... params) {
            try {
                return supplier.get();
            } catch (Exception e) {
                throw new IllegalArgumentException(StrUtil.format(errorMsgTemplate, (Object[]) params));
            }
        }
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

        private static final Gson gson = GsonUtils.getGson();

        public static String toJson(Object obj) {
            return gson.toJson(obj);
        }

        public static <T> T toBean(String jsonStr, TypeReference<T> typeReference) {
            return toBean(jsonStr, typeReference.getType());
        }

        public static <T> T toBean(String jsonStr, TypeToken<T> typeToken) {
            return toBean(jsonStr, typeToken.getType());
        }

        public static <T> T toBean(String jsonStr, Type type) {
            return gson.fromJson(jsonStr, type);
        }
    }

    public static class Systems extends SystemUtil {

        public static Double getCpuUsage() {
            return OshiUtil.getCpuInfo().getUsed();
        }

        public static Double getMemUsage() {
            GlobalMemory memory = OshiUtil.getMemory();
            double memoryUsage = (memory.getTotal() - memory.getAvailable()) * 1.0 / memory.getTotal();

            return doubleFormat(memoryUsage);
        }

        public static Double getLoadAverage() {
            double loadAverage;
            try {
                loadAverage = getOperatingSystemMXBean().getSystemLoadAverage();
            } catch (Exception e) {
                loadAverage = OshiUtil.getHardware().getProcessor().getSystemLoadAverage(1)[0];
                if (Double.isNaN(loadAverage)) {
                    return (double) -1;
                }
            }
            return doubleFormat(loadAverage);
        }

        private static double doubleFormat(double rawValue) {
            DecimalFormat df = new DecimalFormat("0.00");
            df.setRoundingMode(RoundingMode.HALF_UP);
            return Double.parseDouble(df.format(rawValue));
        }
    }

    /**
     * date utils
     */
    public static class Dates extends DateUtil {
        public static String PATTEN = "yyyy-MM-dd HH:mm:ss";

        public static String SIMPLE_PATTEN = "yyyyMMddHHmmss";

        public static long betweenMs(String startTime, String endTime) {
            if (StringUtils.isEmpty(endTime) || StringUtils.isEmpty(startTime)) {
                return 0;
            }
            if (endTime.equals(startTime)) {
                return 0;
            }

            SimpleDateFormat sdf = new SimpleDateFormat(PATTEN);
            long processTime = 0;
            if (!(StringUtils.isNotEmpty(endTime) && StringUtils.isNotEmpty(startTime))) {
                return processTime;
            }
            try {
                java.util.Date end = sdf.parse(endTime);
                java.util.Date start = sdf.parse(startTime);
                processTime = (end.getTime() - start.getTime()) / 1000;
            } catch (ParseException e) {
                // ignore
            }
            if (processTime > 1400000000) {
                processTime = 0;
            }
            return processTime;
        }

        public static Date parseByYYYYMMDDPatten(String date) throws ParseException {
            if (StringUtils.isEmpty(date)) {
                return null;
            }
            DateFormat df = new SimpleDateFormat(SIMPLE_PATTEN);
            return df.parse(date);
        }

    }

}
