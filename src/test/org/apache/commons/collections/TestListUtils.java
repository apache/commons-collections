/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/TestListUtils.java,v 1.15 2003/11/16 00:05:47 scolebourne Exp $
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
package org.apache.commons.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.Test;

import org.apache.commons.collections.list.PredicatedList;

/**
 * Tests for ListUtils.
 * 
 * @version $Revision: 1.15 $ $Date: 2003/11/16 00:05:47 $
 * 
 * @author Stephen Colebourne
 * @author Neil O'Toole
 * @author Matthew Hawthorne
 */
public class TestListUtils extends BulkTest {

    public TestListUtils(String name) {
        super(name);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestListUtils.class);
    }

    public void testNothing() {
    }
    
    public void testpredicatedList() {
        Predicate predicate = new Predicate() {
            public boolean evaluate(Object o) {
                return o instanceof String;
            }
        };
        List list =
        ListUtils.predicatedList(new ArrayStack(), predicate);
        assertTrue("returned object should be a PredicatedList",
            list instanceof PredicatedList);
        try {
            list =
            ListUtils.predicatedList(new ArrayStack(), null);
            fail("Expecting IllegalArgumentException for null predicate.");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            list =
            ListUtils.predicatedList(null, predicate);
            fail("Expecting IllegalArgumentException for null list.");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public BulkTest bulkTestTypedList() {
        return new TestTypedCollection("") {

            public Collection typedCollection() {
                Class type = getType();
                return ListUtils.typedList(new ArrayList(), type);
            }

            public BulkTest bulkTestAll() {
                return new AbstractTestList("") {
                    public List makeEmptyList() {
                        return (List)typedCollection();
                    }

                    public Object[] getFullElements() {
                        return getFullNonNullStringElements();
                    }

                    public Object[] getOtherElements() {
                        return getOtherNonNullStringElements();
                    }

                };
            }
        };
    }


    public void testLazyList() {
        List list = ListUtils.lazyList(new ArrayList(), new Factory() {

            private int index;

            public Object create() {
                index++;
                return new Integer(index);
            }
        });

        assertNotNull((Integer)list.get(5));
        assertEquals(6, list.size());

        assertNotNull((Integer)list.get(5));
        assertEquals(6, list.size());
    }

	public void testEquals() {
		Collection data = Arrays.asList( new String[] { "a", "b", "c" });
		
		List a = new ArrayList( data );
		List b = new ArrayList( data );
		
        assertEquals(true, a.equals(b));
        assertEquals(true, ListUtils.isEqualList(a, b));
        a.clear();
        assertEquals(false, ListUtils.isEqualList(a, b));
        assertEquals(false, ListUtils.isEqualList(a, null));
        assertEquals(false, ListUtils.isEqualList(null, b));
        assertEquals(true, ListUtils.isEqualList(null, null));
	}
	
	public void testHashCode() {
		Collection data = Arrays.asList( new String[] { "a", "b", "c" });
			
		List a = new ArrayList( data );
		List b = new ArrayList( data );
		
        assertEquals(true, a.hashCode() == b.hashCode());
        assertEquals(true, a.hashCode() == ListUtils.hashCodeForList(a));
        assertEquals(true, b.hashCode() == ListUtils.hashCodeForList(b));
        assertEquals(true, ListUtils.hashCodeForList(a) == ListUtils.hashCodeForList(b));
        a.clear();
        assertEquals(false, ListUtils.hashCodeForList(a) == ListUtils.hashCodeForList(b));
        assertEquals(0, ListUtils.hashCodeForList(null));
	}	
	
}
