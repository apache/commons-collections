/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/SetUtils.java,v 1.6 2002/10/12 22:15:19 scolebourne Exp $
 * $Revision: 1.6 $
 * $Date: 2002/10/12 22:15:19 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
package org.apache.commons.collections;


import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;


/**
 *  Provides static utility methods and decorators for {@link Set} 
 *  and {@link SortedSet} instances.
 *
 *  @author Paul Jack
 *  @version $Id: SetUtils.java,v 1.6 2002/10/12 22:15:19 scolebourne Exp $
 *  @since 2.1
 */
public class SetUtils {


    /**
     *  Prevents instantiation.
     */
    private SetUtils() {
    }


    static class PredicatedSet extends CollectionUtils.PredicatedCollection
    implements Set {

        public PredicatedSet(Set set, Predicate p) {
            super(set, p);
        }

    }


    static class PredicatedSortedSet extends PredicatedSet 
    implements SortedSet {

        public PredicatedSortedSet(SortedSet s, Predicate p) {
            super(s, p);
        }

        public SortedSet subSet(Object o1, Object o2) {
            SortedSet sub = getSortedSet().subSet(o1, o2);
            return new PredicatedSortedSet(sub, predicate);
        }

        public SortedSet headSet(Object o1) {
            SortedSet sub = getSortedSet().headSet(o1);
            return new PredicatedSortedSet(sub, predicate);
        }

        public SortedSet tailSet(Object o1) {
            SortedSet sub = getSortedSet().tailSet(o1);
            return new PredicatedSortedSet(sub, predicate);
        }

        public Object first() {
            return getSortedSet().first();
        }

        public Object last() {
            return getSortedSet().last();
        }

        public Comparator comparator() {
            return getSortedSet().comparator();
        }

        private SortedSet getSortedSet() {
            return (SortedSet)collection;
        }

    }

    /**
     *  Returns a predicated set backed by the given set.  Only objects
     *  that pass the test in the given predicate can be added to the set.
     *  It is important not to use the original set after invoking this 
     *  method, as it is a backdoor for adding unvalidated objects.
     *
     *  @param set  the set to predicate
     *  @param p  the predicate for the set
     *  @return  a predicated set backed by the given set
     */
    public static Set predicatedSet(Set set, Predicate p) {
        return new PredicatedSet(set, p);
    }



    /**
     *  Returns a predicated sorted set backed by the given sorted set.  
     *  Only objects that pass the test in the given predicate can be added
     *  to the sorted set.
     *  It is important not to use the original sorted set after invoking this 
     *  method, as it is a backdoor for adding unvalidated objects.
     *
     *  @param set  the sorted set to predicate
     *  @param p  the predicate for the sorted set
     *  @return  a predicated sorted set backed by the given sorted set
     */
    public static SortedSet predicatedSortedSet(SortedSet set, Predicate p) {
        return new PredicatedSortedSet(set, p);
    }

}
