/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/comparators/FixedOrderComparator.java,v 1.3 2003/08/31 12:54:49 scolebourne Exp $
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
package org.apache.commons.collections.comparators;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/** 
 * A Comparator which imposes a specific order on a specific set of Objects.
 * Objects are presented to the FixedOrderComparator in a specified order and
 * subsequent calls to {@link #compare} yield that order.
 * For example:
 * <pre>
 * String[] planets = {"Mercury", "Venus", "Earth", "Mars"};
 * FixedOrderComparator distanceFromSun = new FixedOrderComparator(planets);
 * Arrays.sort(planets);                     // Sort to alphabetical order
 * Arrays.sort(planets, distanceFromSun);    // Back to original order
 * </pre>
 * <p>
 * Once {@link #compare} has been called, the FixedOrderComparator is locked and
 * attempts to modify it yield an UnsupportedOperationException.
 * <p>
 * Instances of FixedOrderComparator are not synchronized.  The class is not
 * thread-safe at construction time, but it is thread-safe to perform
 * multiple comparisons  after all the setup operations are complete.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.3 $ $Date: 2003/08/31 12:54:49 $
 *
 * @author David Leppik
 * @author Stephen Colebourne
 */
public class FixedOrderComparator implements Comparator {

    /** 
     * Behavior when comparing unknown Objects:
     * unknown objects compare as before known Objects.
     */
    public static final int UNKNOWN_BEFORE = 0;

    /** 
     * Behavior when comparing unknown Objects:
     * unknown objects compare as before known Objects.
     */
    public static final int UNKNOWN_AFTER = 1;

    /** 
     * Behavior when comparing unknown Objects:
     * unknown objects cause a IllegalArgumentException to be thrown.
     * This is the default behavior.
     */
    public static final int UNKNOWN_THROW_EXCEPTION = 2;

    /** Internal map of object to position */
    private final Map map = new HashMap();
    /** Counter used in determining the position in the map */
    private int counter = 0;
    /** Is the comparator locked against further change */
    private boolean isLocked = false;
    /** The behaviour in the case of an unknown object */
    private int unknownObjectBehavior = UNKNOWN_THROW_EXCEPTION;

    // Constructors
    //-----------------------------------------------------------------------
    /** 
     * Constructs an empty FixedOrderComparator.
     */
    public FixedOrderComparator() {
        super();
    }

    /** 
     * Constructs a FixedOrderComparator which uses the order of the given array
     * to compare the objects.
     * <p>
     * The array is copied, so later changes will not affect the comparator.
     * 
     * @param items  the items that the comparator can compare in order
     * @throws IllegalArgumentException if the array is null
     */
    public FixedOrderComparator(Object[] items) {
        super();
        if (items == null) {
            throw new IllegalArgumentException("The list of items must not be null");
        }
        for (int i = 0; i < items.length; i++) {
            add(items[i]);
        }
    }

    /** 
     * Constructs a FixedOrderComparator which uses the order of the given list
     * to compare the objects.
     * <p>
     * The list is copied, so later changes will not affect the comparator.
     * 
     * @param items  the items that the comparator can compare in order
     * @throws IllegalArgumentException if the list is null
     */
    public FixedOrderComparator(List items) {
        super();
        if (items == null) {
            throw new IllegalArgumentException("The list of items must not be null");
        }
        for (Iterator it = items.iterator(); it.hasNext();) {
            add(it.next());
        }
    }

    // Bean methods / state querying methods
    //-----------------------------------------------------------------------
    /**
     * Returns true if modifications cannot be made to the FixedOrderComparator.
     * FixedOrderComparators cannot be modified once they have performed a comparison.
     * 
     * @return true if attempts to change the FixedOrderComparator yield an
     *  UnsupportedOperationException, false if it can be changed.
     */
    public boolean isLocked() {
        return isLocked;
    }

    /**
     * Checks to see whether the comparator is now locked against further changes.
     * 
     * @throws UnsupportedOperationException if the comparator is locked
     */
    protected void checkLocked() {
        if (isLocked()) {
            throw new UnsupportedOperationException("Cannot modify a FixedOrderComparator after a comparison");
        }
    }

    /** 
     * Gets the behavior for comparing unknown objects.
     */
    public int getUnkownObjectBehavior() {
        return unknownObjectBehavior;
    }

    /** 
     * Sets the behavior for comparing unknown objects.
     * 
     * @throws UnsupportedOperationException if a comparison has been performed
     * @throws IllegalArgumentException if the unknown flag is not valid
     */
    public void setUnknownObjectBehavior(int unknownObjectBehavior) {
        checkLocked();
        if (unknownObjectBehavior != UNKNOWN_AFTER 
            && unknownObjectBehavior != UNKNOWN_BEFORE 
            && unknownObjectBehavior != UNKNOWN_THROW_EXCEPTION) {
            throw new IllegalArgumentException("Unrecognised value for unknown behaviour flag");    
        }
        this.unknownObjectBehavior = unknownObjectBehavior;
    }

    // Methods for adding items
    //-----------------------------------------------------------------------
    /** 
     * Adds an item, which compares as after all items known to the Comparator.
     * If the item is already known to the Comparator, its old position is
     * replaced with the new position.
     * 
     * @param obj  the item to be added to the Comparator.
     * @return true if obj has been added for the first time, false if
     *  it was already known to the Comparator.
     * @throws UnsupportedOperationException if a comparison has already been made
     */
    public boolean add(Object obj) {
        checkLocked();
        Object position = map.put(obj, new Integer(counter++));
        return (position == null);
    }

    /**
     * Adds a new item, which compares as equal to the given existing item.
     * 
     * @param existingObj  an item already in the Comparator's set of 
     *  known objects
     * @param newObj  an item to be added to the Comparator's set of
     *  known objects
     * @return true if newObj has been added for the first time, false if
     *  it was already known to the Comparator.
     * @throws IllegalArgumentException if existingObject is not in the 
     *  Comparator's set of known objects.
     * @throws UnsupportedOperationException if a comparison has already been made
     */
    public boolean addAsEqual(Object existingObj, Object newObj) {
        checkLocked();
        Integer position = (Integer) map.get(existingObj);
        if (position == null) {
            throw new IllegalArgumentException(existingObj + " not known to " + this);
        }
        Object result = map.put(newObj, position);
        return (result == null);
    }

    // Comparator methods
    //-----------------------------------------------------------------------
    /** 
     * Compares two objects according to the order of this Comparator.
     * <p>
     * It is important to note that this class will throw an IllegalArgumentException
     * in the case of an unrecognised object. This is not specified in the 
     * Comparator interface, but is the most appropriate exception.
     * 
     * @param obj1  the first object to compare
     * @param obj2  the second object to compare
     * @throws IllegalArgumentException if o1 or o2 are not known 
     *  to this Comparator and an alternative behavior has not been set
     *  via {@link #setUnknownObjectBehavior(int)}.
     */
    public int compare(Object obj1, Object obj2) {
        isLocked = true;
        Integer position1 = (Integer) map.get(obj1);
        Integer position2 = (Integer) map.get(obj2);
        if (position1 == null || position2 == null) {
            switch (unknownObjectBehavior) {
                case UNKNOWN_BEFORE :
                    if (position1 == null) {
                        return (position2 == null) ? 0 : -1;
                    } else {
                        return 1;
                    }
                case UNKNOWN_AFTER :
                    if (position1 == null) {
                        return (position2 == null) ? 0 : 1;
                    } else {
                        return -1;
                    }
                case UNKNOWN_THROW_EXCEPTION :
                    Object unknownObj = (position1 == null) ? obj1 : obj2;
                    throw new IllegalArgumentException("Attempting to compare unknown object " + unknownObj);
                default :
                    throw new UnsupportedOperationException("Unknown unknownObjectBehavior: " + unknownObjectBehavior);
            }
        } else {
            return position1.compareTo(position2);
        }
    }

}
