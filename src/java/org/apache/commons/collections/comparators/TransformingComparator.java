/*
 * Copyright 2001-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections.comparators;
import java.util.Comparator;

import org.apache.commons.collections.Transformer;

/**
 * Decorates another Comparator with transformation behavior. That is, the
 * return value from the transform operation will be passed to the decorated
 * <CODE>Comparator#compare</CODE> method.
 * <p>
 * @see org.apache.commons.collections.Transformer
 * @see org.apache.commons.collections.comparators.ComparableComparator
 */
public class TransformingComparator implements Comparator
{
    protected Comparator decorated;
    protected Transformer transformer;

    /**
     * Constructs an instance with the given Transformer and a ComparableComparator.
     * @param transformer what will transform the instance.
     */
    public TransformingComparator(Transformer transformer)
    {
        this(transformer, new ComparableComparator());
    }

    /**
     * Constructs an instance with the given Transformer and Comparator
     * @param decorated  the decorated Comparator
     * @param getterName    the getter name
     */
    public TransformingComparator(Transformer transformer, Comparator decorated)
    {
        this.decorated = decorated;
        this.transformer = transformer;
    }

    /**
     * Returns the result of comparing the values from the transform operation.
     * @return the result of comparing the values from the transform operation
     */
    public int compare(Object o1, Object o2)
    {
        Object value1 = this.transformer.transform(o1);
        Object value2 = this.transformer.transform(o2);
        return this.decorated.compare(value1, value2);
    }

}

