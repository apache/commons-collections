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
 * @version $Revision: 1.19 $ $Date: 2004/06/02 22:12:14 $
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
