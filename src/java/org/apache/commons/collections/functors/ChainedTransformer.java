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
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections.Transformer;

/**
 * Transformer implementation that chains the specified closures together.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.4 $ $Date: 2004/01/14 21:43:09 $
 *
 * @author Stephen Colebourne
 */
public class ChainedTransformer implements Transformer, Serializable {

    /** Serial version UID */
    static final long serialVersionUID = 3514945074733160196L;

    /** The transformers to call in turn */
    private final Transformer[] iTransformers;

    /**
     * Factory method that performs validation and copies the parameter array.
     * 
     * @param transformers  the transformers to chain, copied, no nulls
     * @return the <code>chained</code> transformer
     * @throws IllegalArgumentException if the transformers array is null
     * @throws IllegalArgumentException if any transformer in the array is null
     */
    public static Transformer getInstance(Transformer[] transformers) {
        FunctorUtils.validate(transformers);
        if (transformers.length == 0) {
            return NOPTransformer.INSTANCE;
        }
        transformers = FunctorUtils.copy(transformers);
        return new ChainedTransformer(transformers);
    }
    
    /**
     * Create a new Transformer that calls each transformer in turn, passing the 
     * result into the next transformer. The ordering is that of the iterator()
     * method on the collection.
     * 
     * @param transformers  a collection of transformers to chain
     * @return the <code>chained</code> transformer
     * @throws IllegalArgumentException if the transformers collection is null
     * @throws IllegalArgumentException if any transformer in the collection is null
     */
    public static Transformer getInstance(Collection transformers) {
        if (transformers == null) {
            throw new IllegalArgumentException("Transformer collection must not be null");
        }
        if (transformers.size() == 0) {
            return NOPTransformer.INSTANCE;
        }
        // convert to array like this to guarantee iterator() ordering
        Transformer[] cmds = new Transformer[transformers.size()];
        int i = 0;
        for (Iterator it = transformers.iterator(); it.hasNext();) {
            cmds[i++] = (Transformer) it.next();
        }
        FunctorUtils.validate(cmds);
        return new ChainedTransformer(cmds);
    }

    /**
     * Factory method that performs validation.
     * 
     * @param transformer1  the first transformer, not null
     * @param transformer2  the second transformer, not null
     * @return the <code>chained</code> transformer
     * @throws IllegalArgumentException if either transformer is null
     */
    public static Transformer getInstance(Transformer transformer1, Transformer transformer2) {
        if (transformer1 == null || transformer2 == null) {
            throw new IllegalArgumentException("Transformers must not be null");
        }
        Transformer[] transformers = new Transformer[] { transformer1, transformer2 };
        return new ChainedTransformer(transformers);
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     * 
     * @param transformers  the transformers to chain, not copied, no nulls
     */
    public ChainedTransformer(Transformer[] transformers) {
        super();
        iTransformers = transformers;
    }

    /**
     * Execute a list of transformers.
     * 
     * @param object  the input object passed to each transformer
     */
    public Object transform(Object object) {
        for (int i = 0; i < iTransformers.length; i++) {
            object = iTransformers[i].transform(object);
        }
        return object;
    }
    
}
