/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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
package org.apache.commons.collections.functors;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;

/**
 * Transformer implementation calls the transformer whose predicate returns true,
 * like a switch statement.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.2 $ $Date: 2004/01/14 21:43:09 $
 *
 * @author Stephen Colebourne
 */
public class SwitchTransformer implements Transformer, Serializable {

    /** Serial version UID */


    /** The tests to consider */
    private final Predicate[] iPredicates;
    /** The matching transformers to call */
    private final Transformer[] iTransformers;
    /** The default transformer to call if no tests match */
    private final Transformer iDefault;

    /**
     * Factory method that performs validation and copies the parameter arrays.
     * 
     * @param predicates  array of predicates, cloned, no nulls
     * @param transformers  matching array of transformers, cloned, no nulls
     * @param defaultTransformer  the transformer to use if no match, null means nop
     * @return the <code>chained</code> transformer
     * @throws IllegalArgumentException if array is null
     * @throws IllegalArgumentException if any element in the array is null
     */
    public static Transformer getInstance(Predicate[] predicates, Transformer[] transformers, Transformer defaultTransformer) {
        FunctorUtils.validate(predicates);
        FunctorUtils.validate(transformers);
        if (predicates.length != transformers.length) {
            throw new IllegalArgumentException("The predicate and transformer arrays must be the same size");
        }
        if (predicates.length == 0) {
            return (defaultTransformer == null ? ConstantTransformer.NULL_INSTANCE : defaultTransformer);
        }
        predicates = FunctorUtils.copy(predicates);
        transformers = FunctorUtils.copy(transformers);
        return new SwitchTransformer(predicates, transformers, defaultTransformer);
    }

    /**
     * Create a new Transformer that calls one of the transformers depending 
     * on the predicates. 
     * <p>
     * The Map consists of Predicate keys and Transformer values. A transformer 
     * is called if its matching predicate returns true. Each predicate is evaluated
     * until one returns true. If no predicates evaluate to true, the default
     * transformer is called. The default transformer is set in the map with a 
     * null key. The ordering is that of the iterator() method on the entryset 
     * collection of the map.
     * 
     * @param predicatesAndTransformers  a map of predicates to transformers
     * @return the <code>switch</code> transformer
     * @throws IllegalArgumentException if the map is null
     * @throws IllegalArgumentException if any transformer in the map is null
     * @throws ClassCastException  if the map elements are of the wrong type
     */
    public static Transformer getInstance(Map predicatesAndTransformers) {
        Transformer[] transformers = null;
        Predicate[] preds = null;
        if (predicatesAndTransformers == null) {
            throw new IllegalArgumentException("The predicate and transformer map must not be null");
        }
        if (predicatesAndTransformers.size() == 0) {
            return ConstantTransformer.NULL_INSTANCE;
        }
        // convert to array like this to guarantee iterator() ordering
        Transformer defaultTransformer = (Transformer) predicatesAndTransformers.remove(null);
        int size = predicatesAndTransformers.size();
        if (size == 0) {
            return (defaultTransformer == null ? ConstantTransformer.NULL_INSTANCE : defaultTransformer);
        }
        transformers = new Transformer[size];
        preds = new Predicate[size];
        int i = 0;
        for (Iterator it = predicatesAndTransformers.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            preds[i] = (Predicate) entry.getKey();
            transformers[i] = (Transformer) entry.getValue();
            i++;
        }
        return new SwitchTransformer(preds, transformers, defaultTransformer);
    }
    
    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     * 
     * @param predicates  array of predicates, not cloned, no nulls
     * @param transformers  matching array of transformers, not cloned, no nulls
     * @param defaultTransformer  the transformer to use if no match, null means nop
     */
    public SwitchTransformer(Predicate[] predicates, Transformer[] transformers, Transformer defaultTransformer) {
        super();
        iPredicates = predicates;
        iTransformers = transformers;
        iDefault = (defaultTransformer == null ? ConstantTransformer.NULL_INSTANCE : defaultTransformer);
    }

    /**
     * Execute the transformer whose predicate returns true.
     */
    public Object transform(Object input) {
        for (int i = 0; i < iPredicates.length; i++) {
            if (iPredicates[i].evaluate(input) == true) {
                return iTransformers[i].transform(input);
            }
        }
        return iDefault.transform(input);
    }
    
}
