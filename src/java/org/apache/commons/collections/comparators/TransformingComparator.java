/*
 * $Header$ 
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Commons" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.commons.collections.comparators;

import java.util.Comparator;

import org.apache.commons.collections.Transformer;

/**
 * Decorates another Comparator with transformation behavior. That is, the
 * return value from the transform operation will be passed to the decorated
 * {@link Comparator#compare compare} method.
 * <p />
 * 
 * @since Commons Collections 2.0 (?)
 * @version $Revision$ $Date$
 * 
 * @see org.apache.commons.collections.Transformer
 * @see org.apache.commons.collections.comparators.ComparableComparator
 */
public class TransformingComparator implements Comparator
{
    protected Comparator decorated;
    protected Transformer transformer;

    /**
     * Constructs an instance with the given Transformer and a 
     * {@link ComparableComparator ComparableComparator}.
     * @param transformer what will transform the arguments to 
     *        {@link #compare compare}
     */
    public TransformingComparator(Transformer transformer)
    {
        this(transformer, new ComparableComparator());
    }

    /**
     * Constructs an instance with the given Transformer and Comparator
     * @param transformer what will transform the arguments to {@link #compare compare}
     * @param decorated the decorated Comparator
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

