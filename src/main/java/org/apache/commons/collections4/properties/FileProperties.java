/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.collections4.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A class that extend a load method which accept a filename.
 * <p>
 * Use context classloader to load current activated file.
 * </p>
 *
 * @since 4.2
 */
public class FileProperties extends Properties {

    private static final long serialVersionUID = 1L;

    public synchronized Properties load(String fileName) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
        properties.load(inputStream);
        return properties;
    }
}
