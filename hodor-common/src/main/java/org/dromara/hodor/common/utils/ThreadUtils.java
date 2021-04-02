/*
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.dromara.hodor.common.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

/**
 * thread utils.
 *
 * @author huangxiaofeng
 * @author tomgs
 */
public class ThreadUtils {

    /**
     * sleep current thread.
     *
     * @param timeUnit the time unit
     * @param time     the time
     */
    public static void sleep(final TimeUnit timeUnit, final int time) {
        try {
            timeUnit.sleep(time);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 获取异常堆栈信息
     *
     * @param e 异常
     * @return 堆栈信息
     */
    public static String getStackTraceInfo(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            e.printStackTrace(pw);
            return sw.toString();
        } catch(Exception e1) {
            return StringUtils.EMPTY;
        }
    }

    /**
     * 获取当前线程栈Class信息
     *
     * @return class info
     */
    public static String getClassInfo() {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement stackTraceElement;
        if (stackTrace.length < 3) {
            stackTraceElement = stackTrace[stackTrace.length - 1];
        } else {
            stackTraceElement = stackTrace[3];
        }
        if (stackTraceElement != null) {
            sb.append(getSimpleClassName(stackTraceElement.getClassName())).append("#");
            sb.append(stackTraceElement.getMethodName()).append(":");
            sb.append(stackTraceElement.getLineNumber()).append(StringUtils.SPACE);
        }
        return sb.toString();
    }

    private static String getSimpleClassName(String className) {
        return className.substring(className.lastIndexOf(".") + 1);
    }

    public static int availableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

}
