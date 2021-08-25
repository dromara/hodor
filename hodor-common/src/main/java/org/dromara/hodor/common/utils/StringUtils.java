/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.hodor.common.utils;

import cn.hutool.core.text.StrSplitter;
import cn.hutool.core.util.StrUtil;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.text.TextStringBuilder;

/**
 * StringUtils .
 *
 * @author xiaoyu
 */
public final class StringUtils extends org.apache.commons.lang3.StringUtils {

    public static final String PATH_SEPARATOR = "/";

    public static final String WINDOWS_FOLDER_SEPARATOR = "\\";

    public static final String TOP_PATH = "..";

    public static final String CURRENT_PATH = ".";

    public static final String UNDER_LINE_SEPARATOR = "_";

    public static final char EXTENSION_SEPARATOR = '.';

    /**
     * 判断字符串是否为空.
     *
     * @param arg arg.
     * @return bool .
     */
    public static boolean isBlank(final String arg) {
        return StrUtil.isBlank(arg);
    }

    /**
     * 判断字符串是不为空.
     *
     * @param arg arg.
     * @return bool .
     */
    public static boolean isNotBlank(final String arg) {
        return !isBlank(arg);
    }

    /**
     * Has length boolean.
     *
     * @param str the str
     * @return the boolean
     */
    public static boolean hasLength(CharSequence str) {
        return (str != null && str.length() > 0);
    }

    public static String format(String format, Object... args) {
        return StrUtil.format(format, args);
    }

    public static List<String> splitPath(String path) {
        return StrSplitter.splitPath(path);
    }

    public static List<String> splitToList(String str, String delimit) {
        return StrSplitter.split(str, delimit, true, false);
    }

    public static List<String> splitLimit(String str, String delimit, int limit) {
        return StrSplitter.split(str, delimit, limit, true, true);
    }

    public static TextStringBuilder getStringBuilder() {
        return new TextStringBuilder();
    }

    public static String decodeString(byte[] data) {
        return toEncodedString(data, StandardCharsets.UTF_8);
    }

}
