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

package org.dromara.hodor.common.compress;

/**
 * EncType
 *
 * @author tomgs
 * @since 1.0
 */
public enum EncType {

    PLAIN(0),
    ZIP(1);

    private final int type;

    EncType(int type) {
        this.type = type;
    }

    public static EncType of(int type) {
        for (EncType encType : EncType.values()) {
            if (encType.type == type) {
                return encType;
            }
        }
        throw new IllegalArgumentException("not found enc type:" + type);
    }

    public int getType() {
        return type;
    }

}
