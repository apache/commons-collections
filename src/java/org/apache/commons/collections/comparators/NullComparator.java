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
package org.apache.commons.collections.comparators;

import java.io.Serializable;
import java.util.Comparator;

/**
 * A Comparator that will compare nulls to be either lower or higher than
 * other objects.
 *
 * @since Commons Collections 2.0
 * @version $Revision: 1.11 $ $Date: 2004/01/14 21:43:18 $ 
 *
 * @author Michael A. Smith
 */
public class NullComparator implements Comparator, Serializable {

    /**
     *  The comparator to use when comparing two non-<code>null</code> objects.
     **/
    private Comparator nonNullComparator;

    /**
     *  Specifies whether a <code>null</code> are compared as higher than
     *  non-<code>null</code> objects.
     **/
    private boolean nullsAreHigh;

    /** 
     *  Construct an instance that sorts <code>null</code> higher than any
     *  non-<code>null</code> object it is compared with. When comparing two
     *  non-<code>null</code> objects, the {@link ComparableComparator} is
     *  used.
     **/
    public NullComparator() {
        this(ComparableComparator.getInstance(), true);
    }

    /**
     *  Construct an instance that sorts <code>null</code> higher than any
     *  non-<code>null</code> object it is compared with.  When comparing two
     *  non-<code>null</code> objects, the specified {@link Comparator} is
     *  used.
     *
     *  @param nonNullComparator the comparator to use when comparing two
     *  non-<code>null</code> objects.  This argument cannot be
     *  <code>null</code>
     *
     *  @exception NullPointerException if <code>nonNullComparator</code> is
     *  <code>null</code>
     **/
    public NullComparator(Comparator nonNullComparator) {
        this(nonNullComparator, true);
    }

    /**
     *  Construct an instance that sorts <code>null</code> higher or lower than
     *  any non-<code>null</code> object it is compared with.  When comparing
     *  two non-<code>null</code> objects, the {@link ComparableComparator} is
     *  used.
     *
     *  @param nullsAreHigh a <code>true</code> value indicates that
     *  <code>null</code> should be compared as higher than a
     *  non-<code>null</code> object.  A <code>false</code> value indicates
     *  that <code>null</code> should be compared as lower than a
     *  non-<code>null</code> object.
     **/
    public NullComparator(boolean nullsAreHigh) {
        this(ComparableComparator.getInstance(), nullsAreHigh);
    }
    
    /**
     *  Construct an instance that sorts <code>null</code> higher or lower than
     *  any non-<code>null</code> object it is compared with.  When comparing
     *  two non-<code>null</code> objects, the specified {@link Comparator} is
     *  used.
     *
     *  @param nonNullComparator the comparator to use when comparing two
     *  non-<code>null</code> objects. This argument cannot be
     *  <code>null</code>
     *
     *  @param nullsAreHigh a <code>true</code> value indicates that
     *  <code>null</code> should be compared as higher than a
     *  non-<code>null</code> object.  A <code>false</code> value indicates
     *  that <code>null</code> should be compared as lower than a
     *  non-<code>null</code> object.
     *
     *  @exception NullPointerException if <code>nonNullComparator</code> is
     *  <code>null</code>
     **/
    public NullComparator(Comparator nonNullComparator, boolean nullsAreHigh) {
        this.nonNullComparator = nonNullComparator;
        this.nullsAreHigh = nullsAreHigh;
        
        if(nonNullComparator == null) {
            throw new NullPointerException("null nonNullComparator");
        }
    }

    /**
     *  Perform a comparison between two objects.  If both objects are
     *  <code>null</code>, a <code>0</code> value is returned.  If one object
     *  is <code>null</code> and the other is not, the result is determined on
     *  whether the Comparator was constructed to have nulls as higher or lower
     *  than other objects.  If neither object is <code>null</code>, an
     *  underlying comparator specified in the constructor (or the default) is
     *  used to compare the non-<code>null</code> objects.
     *
     *  @param o1 the first object to compare
     *
     *  @param o2 the object to compare it to.
     *
     *  @return <code>-1</code> if <code>o1</code> is "lower" than (less than,
     *  before, etc.) <code>o2</code>; <code>1</code> if <code>o1</code> is
     *  "higher" than (greater than, after, etc.) <code>o2</code>; or
     *  <code>0</code> if <code>o1</code> and <code>o2</code> are equal.
     **/
    public int compare(Object o1, Object o2) {
        if(o1 == o2) { return 0; }
        if(o1 == null) { return (this.nullsAreHigh ? 1 : -1); }
        if(o2 == null) { return (this.nullsAreHigh ? -1 : 1); }
        return this.nonNullComparator.compare(o1, o2);
    }

    /**
     *  Implement a hash code for this comparator that is consistent with
     *  {@link #equals(Object)}.
     *
     *  @return a hash code for this comparator.
     **/
    public int hashCode() {
        return (nullsAreHigh ? -1 : 1) * nonNullComparator.hashCode();
    }

    /**
     *  Determines whether the specified object represents a comparator that is
     *  equal to this comparator.
     *
     *  @param obj  the object to compare this comparator with.
     *
     *  @return <code>true</code> if the specified object is a NullComparator
     *  with equivalent <code>null</code> comparison behavior
     *  (i.e. <code>null</code> high or low) and with equivalent underlying
     *  non-<code>null</code> object comparators.
     **/
    public boolean equals(Object obj) {
        if(obj == null) { return false; }
        if(obj == this) { return true; }
        if(!obj.getClass().equals(this.getClass())) { return false; }

        NullComparator other = (NullComparator)obj;
	
        return ((this.nullsAreHigh == other.nullsAreHigh) &&
                (this.nonNullComparator.equals(other.nonNullComparator)));
    }

    private static final long serialVersionUID = -5820772575483504339L;
}
