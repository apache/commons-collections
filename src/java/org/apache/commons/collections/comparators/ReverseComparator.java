package org.apache.commons.collections.comparators;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
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

import java.io.Serializable;
import java.util.Comparator;

/**
 * Reverses the order of another comparator.
 * 
 * @since 2.0
 * @author bayard@generationjava.com
 * @author <a href="mailto:mas@apache.org">Michael A. Smith</a>
 * @version $Id: ReverseComparator.java,v 1.9 2003/01/07 23:05:33 rwaldhoff Exp $
 */
public class ReverseComparator implements Comparator,Serializable {

    private Comparator comparator;

    /**
     * Creates a comparator that compares objects based on the inverse of their
     * natural ordering.  Using this Constructor will create a ReverseComparator
     * that is functionaly identical to the Comparator returned by
     * java.util.Collections.<b>reverseOrder()</b>.
     * 
     * @see java.util.Collections#reverseOrder()
     */
    public ReverseComparator() {
        this(null);
    }

    /**
     * Creates a reverse comparator that inverts the comparison
     * of the passed in comparator.  If you pass in a null,
     * the ReverseComparator defaults to reversing the
     * natural order, as per 
     * java.util.Collections.<b>reverseOrder()</b>.
     * 
     * @param comparator Comparator to reverse
     */
    public ReverseComparator(Comparator comparator) {
        if(comparator != null) {
            this.comparator = comparator;
        } else {
            this.comparator = ComparableComparator.getInstance();
        }
    }

    public int compare(Object o1, Object o2) {
        return comparator.compare(o2, o1);
    }

    /**
     * Implement a hash code for this comparator that is consistent with
     * {@link #equals}.
     * 
     * @since Collections 2.2
     */
    public int hashCode() {
        return "ReverseComparator".hashCode() ^ comparator.hashCode();
    }

    /**
     * Returns <code>true</code> iff <i>that</i> Object is 
     * is a {@link Comparator} whose ordering is known to be 
     * equivalent to mine.
     * <p>
     * This implementation returns <code>true</code>
     * iff <code><i>that</i>.{@link Object#getClass getClass()}</code>
     * equals <code>this.getClass()</code>, and the underlying 
     * comparators are equal.  Subclasses may want to override
     * this behavior to remain consistent with the 
     * {@link Comparator.equals} contract.
     * 
     * @since Collections 2.2
     */
    public boolean equals(Object that) {
        if(this == that) {
            return true;
        } else if(null == that) {
            return false;
        } else if(that.getClass().equals(this.getClass())) {
            ReverseComparator thatrc = (ReverseComparator)that;
            return comparator.equals(thatrc.comparator);
        } else {
            return false;
        }
    }

    // use serialVersionUID from Collections 2.0 for interoperability
    private static final long serialVersionUID = 2858887242028539265L;;
}
