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
import com.google.common.base.Preconditions;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
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

    public static final String EMPTY_STRING = "";

    public static final char EXTENSION_SEPARATOR = '.';

    public static final char SINGLE_QUOTE = '\'';

    public static final char DOUBLE_QUOTE = '\"';

    public static final String LF = "\n";

    private static final Pattern BROWSWER_PATTERN = Pattern
        .compile(".*Gecko.*|.*AppleWebKit.*|.*Trident.*|.*Chrome.*");

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
        if (data == null) {
            return EMPTY_STRING;
        }
        return toEncodedString(data, StandardCharsets.UTF_8);
    }

    public static String formatURL(String value, Object... args) {
        for (int i = 0; i < args.length; i++) {
            String arg = String.valueOf(args[i]);
            try {
                String encodeArg = URLEncoder.encode(arg, "UTF-8");
                args[i] = encodeArg;
            } catch (UnsupportedEncodingException e) {
                //ignore
            }
        }
        return String.format(value, args);
    }

    public static String shellQuote(final String s, final char quoteCh) {
        final StringBuilder buf = new StringBuilder(s.length() + 2);
        buf.append(quoteCh);
        for (int i = 0; i < s.length(); i++) {
            final char ch = s.charAt(i);
            if (ch == quoteCh) {
                buf.append('\\');
            }
            buf.append(ch);
        }
        buf.append(quoteCh);
        return buf.toString();
    }

    public static boolean isFromBrowser(final String userAgent) {
        if (userAgent == null) {
            return false;
        }

        return BROWSWER_PATTERN.matcher(userAgent).matches();
    }

    // Using the charset canonical name for String/byte[] conversions is much
    // more efficient due to use of cached encoders/decoders.
    private static final String UTF8_CSN = StandardCharsets.UTF_8.name();

    /**
     * Priority of the StringUtils shutdown hook.
     */
    private static final int SHUTDOWN_HOOK_PRIORITY = 0;

    /**
     * Decode a specific range of bytes of the given byte array to a string
     * using UTF8.
     *
     * @param bytes  The bytes to be decoded into characters
     * @param offset The index of the first byte to decode
     * @param length The number of bytes to decode
     * @return The decoded string
     */
    public static String bytes2String(byte[] bytes, int offset, int length) {
        try {
            return new String(bytes, offset, length, UTF8_CSN);
        } catch (UnsupportedEncodingException e) {
            // should never happen!
            throw new IllegalArgumentException("UTF8 encoding is not supported", e);
        }
    }

    /**
     * Decode a specific range of bytes of the given byte array to a string
     * using UTF8.
     *
     * @param bytes The bytes to be decoded into characters
     * @return The decoded string
     */
    public static String bytes2String(byte[] bytes) {
        return bytes2String(bytes, 0, bytes.length);
    }

    /**
     * Converts a string to a byte array using UTF8 encoding.
     */
    public static byte[] string2Bytes(String str) {
        try {
            return str.getBytes(UTF8_CSN);
        } catch (UnsupportedEncodingException e) {
            // should never happen!
            throw new IllegalArgumentException("UTF8 decoding is not supported", e);
        }
    }

    /**
     * Return a message for logging.
     * @param prefix prefix keyword for the message
     * @param msg content of the message
     * @return a message for logging
     */
    public static String toStartupShutdownString(String prefix, String... msg) {
        StringBuilder b = new StringBuilder(prefix);
        b.append("\n/************************************************************");
        for (String s : msg) {
            b.append("\n").append(prefix).append(s);
        }
        b.append("\n************************************************************/");
        return b.toString();
    }

    public static String appendIfNotPresent(String str, char c) {
        Preconditions.checkNotNull(str, "Input string is null");
        return str.isEmpty() || str.charAt(str.length() - 1) != c ? str + c : str;
    }

    public static Optional<String> ofBlankable(String str) {
        if (isBlank(str)) {
            return Optional.empty();
        }
        return Optional.of(str);
    }
}
