/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/decorators/Attic/SequencedSet.java,v 1.2 2003/08/31 17:24:46 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
 *
 */
package org.apache.commons.collections.decorators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * <code>SequencedSet</code> decorates another <code>Set</code>
 * to ensure that the order of addition is retained and used by the iterator.
 * <p>
 * If an object is added to the Set for a second time, it will remain in the
 * original position in the iteration.
 * <p>
 * The order can be observed via the iterator or toArray methods.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.2 $ $Date: 2003/08/31 17:24:46 $
 * 
 * @author Stephen Colebourne
 * @author Henning P. Schmiedehausen
 */
public class SequencedSet extends AbstractSetDecorator implements Set {

    /** Internal list to hold the sequence of objects */
    protected final List setOrder = new ArrayList();

    /**
     * Factory method to create an unmodifiable set.
     * 
     * @param set  the set to decorate, must not be null
     * @throws IllegalArgumentException if set is null
     */
    public static Set decorate(Set set) {
        return new SequencedSet(set);
    }

    /**
     * Constructor that wraps (not copies).
     * 
     * @param set  the set to decorate, must not be null
     * @throws IllegalArgumentException if set is null
     */
    protected SequencedSet(Set set) {
        super(set);
        setOrder.addAll(set);
    }

    //-----------------------------------------------------------------------
    public void clear() {
        collection.clear();
        setOrder.clear();
    }

    public Iterator iterator() {
        return new SequencedSetIterator(setOrder.iterator(), collection);
    }

    public boolean add(Object object) {
        if (collection.contains(object)) {
            // re-adding doesn't change order
            return collection.add(object);
        } else {
            // first add, so add to both set and list
            boolean result = collection.add(object);
            setOrder.add(object);
            return result;
        }
    }

    public boolean addAll(Collection coll) {
        boolean result = false;
        for (Iterator it = coll.iterator(); it.hasNext();) {
            Object object = it.next();
            result = result | add(object);
        }
        return result;
    }

    public boolean remove(Object object) {
        boolean result = collection.remove(object);
        setOrder.remove(object);
        return result;
    }

    public boolean removeAll(Collection coll) {
        boolean result = false;
        for (Iterator it = coll.iterator(); it.hasNext();) {
            Object object = it.next();
            result = result | remove(object);
        }
        return result;
    }

    public boolean retainAll(Collection coll) {
        boolean result = collection.retainAll(coll);
        if (result == false) {
            return false;
        } else if (collection.size() == 0) {
            setOrder.clear();
        } else {
            for (Iterator it = setOrder.iterator(); it.hasNext();) {
                Object object = (Object) it.next();
                if (collection.contains(object) == false) {
                    it.remove();
                }
            }
        }
        return result;
    }

    public Object[] toArray() {
        return setOrder.toArray();
    }

    public Object[] toArray(Object a[]) {
        return setOrder.toArray(a);
    }

    /**
     * Internal iterator handle remove.
     */
    protected static class SequencedSetIterator extends AbstractIteratorDecorator {
        
        /** Object we iterate on */
        protected final Collection set;
        /** Last object retrieved */
        protected Object last;

        private SequencedSetIterator(Iterator iterator, Collection set) {
            super(iterator);
            this.set = set;
        }

        public Object next() {
            last = iterator.next();
            return last;
        }

        public void remove() {
            set.remove(last);
            iterator.remove();
            last = null;
        }
    }

}
