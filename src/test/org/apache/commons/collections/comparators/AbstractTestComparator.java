/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/comparators/AbstractTestComparator.java,v 1.1 2003/10/01 22:14:48 scolebourne Exp $
 * ====================================================================
 *
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
package org.apache.commons.collections.comparators;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.TestObject;

/**
 * Abstract test class for testing the Comparator interface.
 * <p>
 * Concrete subclasses declare the comparator to be tested.
 * They also declare certain aspects of the tests.
 * 
 * @author Stephen Colebourne
 */
public abstract class AbstractTestComparator extends TestObject {

    /**
     * JUnit constructor.
     * 
     * @param testName  the test class name
     */
    public AbstractTestComparator(String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    /**
     * Implement this method to return the comparator to test.
     * 
     * @return the comparator to test
     */
    protected abstract Comparator makeComparator();
    
    /**
     * Implement this method to return a list of sorted objects.
     * 
     * @return sorted objects
     */
    protected abstract List getComparableObjectsOrdered();

    //-----------------------------------------------------------------------
    /**
     * Implements the abstract superclass method to return the comparator.
     * 
     * @return a full iterator
     */
    protected Object makeObject() {
        return makeComparator();
    }

    /**
     * Overrides superclass to block tests.
     */
    public boolean supportsEmptyCollections() {
        return false;
    }

    /**
     * Overrides superclass to block tests.
     */
    public boolean supportsFullCollections() {
        return false;
    }

    /**
     * Overrides superclass to set the compatability to version 2
     * as there were no Comparators in version 1.x.
     */
    protected String getCompatibilityVersion() {
        return "2";
    }

    //-----------------------------------------------------------------------
    /**
     * Reverse the list.
     */
    protected void reverseObjects(List list) {
        Collections.reverse(list);
    }

    /**
     * Randomize the list.
     */
    protected void randomizeObjects(List list) {
        Collections.shuffle(list);
    }

    /**
     * Sort the list.
     */
    protected void sortObjects(List list, Comparator comparator) {
        Collections.sort(list,comparator);

    }

    //-----------------------------------------------------------------------
    /**
     * Test sorting an empty list
     */
    public void testEmptyListSort() {
        List list = new LinkedList();
        sortObjects(list, makeComparator());

        List list2 = new LinkedList();
        
        assertTrue("Comparator cannot sort empty lists",
                   list2.equals(list));
    }

    /**
     * Test sorting a reversed list.
     */
    public void testReverseListSort() {
        Comparator comparator = makeComparator();

        List randomList = getComparableObjectsOrdered();
        reverseObjects(randomList);
        sortObjects(randomList,comparator);

        List orderedList = getComparableObjectsOrdered();

        assertTrue("Comparator did not reorder the List correctly",
                   orderedList.equals(randomList));

    }

    /**
     * Test sorting a random list.
     */
    public void testRandomListSort() {
        Comparator comparator = makeComparator();

        List randomList = getComparableObjectsOrdered();
        randomizeObjects(randomList);
        sortObjects(randomList,comparator);

        List orderedList = getComparableObjectsOrdered();

        /* debug 
        Iterator i = randomList.iterator();
        while (i.hasNext()) {
            System.out.println(i.next());
        }
        */

        assertTrue("Comparator did not reorder the List correctly",
                   orderedList.equals(randomList));

    }

    /**
     * Nearly all Comparators should be Serializable.
     */
    public void testComparatorIsSerializable() {
        Comparator comparator = makeComparator();
        assertTrue("This comparator should be Serializable.",
                   comparator instanceof Serializable);
    }

    public String getCanonicalComparatorName(Object object) {
        StringBuffer retval = new StringBuffer();
        retval.append("data/test/");
        String colName = object.getClass().getName();
        colName = colName.substring(colName.lastIndexOf(".")+1,colName.length());
        retval.append(colName);
        retval.append(".version");
        retval.append(getCompatibilityVersion());
        retval.append(".obj");
        return retval.toString();
    }

    /**
     * Compare the current serialized form of the Comparator
     * against the canonical version in CVS.
     */
    public void testComparatorCompatibility() throws IOException, ClassNotFoundException {
        if(!skipSerializedCanonicalTests()) {
            Comparator comparator = null;
    
            // test to make sure the canonical form has been preserved
            try {
                comparator = (Comparator) readExternalFormFromDisk(getCanonicalComparatorName(makeComparator()));
        	} catch (FileNotFoundException exception) {
    
                boolean autoCreateSerialized = false;
    
        	    if(autoCreateSerialized) {
    	          	comparator = makeComparator();
            		String fileName = getCanonicalComparatorName(comparator);
            		writeExternalFormToDisk((Serializable) comparator, fileName);
            		fail("Serialized form could not be found.  A serialized version " +
            		     "has now been written (and should be added to CVS): " + fileName);
                } else {
                    fail("The Serialized form could be located to test serialization " +
                        "compatibility: " + exception.getMessage());
                }
            }
    
            
            // make sure the canonical form produces the ordering we currently
            // expect
            List randomList = getComparableObjectsOrdered();
            reverseObjects(randomList);
            sortObjects(randomList,comparator);
    
            List orderedList = getComparableObjectsOrdered();
    
            assertTrue("Comparator did not reorder the List correctly",
                       orderedList.equals(randomList));
        }
    }

}
