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

/**
 * Creates and loads {@link SortedProperties}.
 *
 * @see SortedProperties
 * @since 4.4
 */
public class SortedPropertiesFactory extends AbstractPropertiesFactory<SortedProperties> {

    /**
     * The singleton instance.
     */
    public static final SortedPropertiesFactory INSTANCE = new SortedPropertiesFactory();

    /**
     * Constructs an instance.
     */
    private SortedPropertiesFactory() {
        // There is only one instance.
    }

    /**
     * Subclasses override to provide customized properties instances.
     *
     * @return a new Properties instance.
     */
    @Override
    protected SortedProperties createProperties() {
        return new SortedProperties();
    }

}
