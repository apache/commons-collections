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
package org.apache.commons.collections.functors;

import java.io.Serializable;

import org.apache.commons.collections.Predicate;

/**
 * Predicate implementation that returns true if the input is not null.
 *
 * @since Commons Collections 3.0
 * @version $Revision$ $Date$
 *
 * @author Stephen Colebourne
 */
public final class NotNullPredicate<T> implements Predicate<T>, Serializable {

    /** Serial version UID */
    private static final long serialVersionUID = 7533784454832764388L;
    
    /** Singleton predicate instance */
    public static final Predicate<Object> INSTANCE = new NotNullPredicate<Object>();

    /**
     * Factory returning the singleton instance.
     * 
     * @return the singleton instance
     * @since Commons Collections 3.1
     */
    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> getInstance() {
        return (Predicate<T>) INSTANCE;
    }

    /**
     * Restricted constructor.
     */
    private NotNullPredicate() {
        super();
    }

    /**
     * Evaluates the predicate returning true if the object does not equal null.
     * 
     * @param object  the object to evaluate
     * @return true if not null
     */
    public boolean evaluate(T object) {
        return (object != null);
    }

}
