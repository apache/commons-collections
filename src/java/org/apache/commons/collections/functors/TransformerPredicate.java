/*
 *  Copyright 2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;

import org.apache.commons.collections.FunctorException;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;

/**
 * Predicate implementation that returns the result of a transformer.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.4 $ $Date: 2004/02/18 00:59:20 $
 *
 * @author Stephen Colebourne
 */
public final class TransformerPredicate implements Predicate, Serializable {

    /** Serial version UID */
    static final long serialVersionUID = -2407966402920578741L;
    
    /** The transformer to call */
    private final Transformer iTransformer;
    
    /**
     * Factory to create the predicate.
     * 
     * @return the transformer
     * @throws IllegalArgumentException if the transformer is null
     */
    public static Predicate getInstance(Transformer transformer) {
        if (transformer == null) {
            throw new IllegalArgumentException("The transformer to call must not be null");
        }
        return new TransformerPredicate(transformer);
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     */
    public TransformerPredicate(Transformer transformer) {
        super();
        iTransformer = transformer;
    }

    /**
     * Return the predicate result.
     */
    public boolean evaluate(Object object) {
        Object result = iTransformer.transform(object);
        if (result instanceof Boolean == false) {
            throw new FunctorException(
                "Transformer must return an instanceof Boolean, it was a "
                    + (result == null ? "null object" : result.getClass().getName()));
        }
        return ((Boolean) result).booleanValue();
    }
    
}
