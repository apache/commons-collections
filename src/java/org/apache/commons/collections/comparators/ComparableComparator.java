/* 
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/comparators/ComparableComparator.java,v 1.10 2003/05/16 15:08:45 scolebourne Exp $
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
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

package org.apache.commons.collections.comparators;

import java.io.Serializable;
import java.util.Comparator;

/**
 * A {@link Comparator Comparator} that compares 
 * {@link Comparable Comparable} objects.
 * <p />
 * This Comparator is useful, for example,
 * for enforcing the natural order in custom implementations
 * of SortedSet and SortedMap.
 * <p />
 * Note: In the 2.0 and 2.1 releases of Commons Collections, 
 * this class would throw a {@link ClassCastException} if
 * either of the arguments to {@link #compare compare}
 * were <code>null</code>, not {@link Comparable Comparable},
 * or for which {@link Comparable#compareTo compareTo} gave
 * inconsistent results.  This is no longer the case.  See
 * {@link #compare} for details.
 *
 * @since Commons Collections 2.0
 * @version $Revision: 1.10 $ $Date: 2003/05/16 15:08:45 $
 *
 * @author bayard@generationjava.com
 *
 * @see java.util.Collections#reverseOrder
 */
public class ComparableComparator implements Comparator, Serializable {

    /**
     *  Return a shared instance of a ComparableComparator.  Developers are
     *  encouraged to use the comparator returned from this method instead of
     *  constructing a new instance to reduce allocation and GC overhead when
     *  multiple comparable comparators may be used in the same VM.
     **/
    public static ComparableComparator getInstance() {
        return instance;
    }

    public ComparableComparator() {
    }

    /**
     * Compare the two {@link Comparable Comparable} arguments.
     * This method is equivalent to:
     * <pre>(({@link Comparable Comparable})o1).{@link Comparable#compareTo compareTo}(o2)</pre>
     * @throws NullPointerException when <i>o1</i> is <code>null</code>, 
     *         or when <code>((Comparable)o1).compareTo(o2)</code> does
     * @throws ClassCastException when <i>o1</i> is not a {@link Comparable Comparable}, 
     *         or when <code>((Comparable)o1).compareTo(o2)</code> does
     */
    public int compare(Object o1, Object o2) {
        return ((Comparable)o1).compareTo(o2);
    }

    /**
     * Implement a hash code for this comparator that is consistent with
     * {@link #equals}.
     *
     * @return a hash code for this comparator.
     * @since Commons Collections 3.0
     */
    public int hashCode() {
        return "ComparableComparator".hashCode();
    }

    /**
     * Returns <code>true</code> iff <i>that</i> Object is 
     * is a {@link Comparator Comparator} whose ordering is 
     * known to be equivalent to mine.
     * <p>
     * This implementation returns <code>true</code>
     * iff <code><i>that</i>.{@link Object#getClass getClass()}</code>
     * equals <code>this.getClass()</code>.  Subclasses may want to override
     * this behavior to remain consistent with the {@link Comparator#equals}
     * contract.
     * @since Commons Collections 3.0
     */
    public boolean equals(Object that) {
        return (this == that) || 
               ((null != that) && (that.getClass().equals(this.getClass())));
    }

    private static final ComparableComparator instance = 
        new ComparableComparator();

    private static final long serialVersionUID=-291439688585137865L;

}
