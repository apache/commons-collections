/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/Attic/TestMap.java,v 1.16 2002/05/28 06:51:03 mas Exp $
 * $Revision: 1.16 $
 * $Date: 2002/05/28 06:51:03 $
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

import junit.framework.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;

/**
 * Tests base {@link java.util.Map} methods and contracts.
 * <p>
 * If your class implements the full Map interface, including optional
 * operations, simply extend this class, and implement the {@link
 * #makeEmptyMap()} method.
 * <p>
 * If your {@link Map} fails one of these tests by design, you may still use
 * this base set of cases.  Simply override the test case (method) your {@link
 * Map} fails and/or the methods that define the assumptions used by the test
 * cases.  For example, if your map does not allow duplicate values, override
 * {@link useDuplicateValues()} and have it return <code>false</code>
 *
 * @author Michael Smith
 * @author Rodney Waldhoff
 * @version $Id: TestMap.java,v 1.16 2002/05/28 06:51:03 mas Exp $
 */
public abstract class TestMap extends TestObject {

    public TestMap(String testName) {
        super(testName);
    }
    /**
     *  Override if your map does not allow a <code>null</code> key.  The
     *  default implementation returns <code>true</code>
     **/
    public boolean useNullKey() {
        return true;
    }

    /**
     *  Override if your map does not allow <code>null</code> values.  The
     *  default implementation returns <code>true</code>.
     **/
    public boolean useNullValue() {
        return true;
    }

    /**
     *  Override if your map does not allow duplicate values.  The default
     *  implementation returns <code>true</code>.
     **/
    public boolean useDuplicateValues() {
        return true;
    }

    /**
     *  Override if your map allows its mappings to be changed to new values.
     *  The default implementation returns <code>true</code>.
     **/
    public boolean isChangeable() {
        return true;
    }

    /**
     *  Override if your map does not allow add/remove modifications.  The
     *  default implementation returns <code>true</code>.
     **/
    public boolean isAddRemoveModifiable() {
        return true;
    }

    /**
     *  Returns the set of keys in the mappings used to test the map.  This
     *  method must return an array with the same length as {@link
     *  #getSampleValues()} and all array elements must be different. The
     *  default implementation constructs a set of String keys, and includes a
     *  single null key if {@link #useNullKey()} returns <code>true</code>.
     **/
    public Object[] getSampleKeys() {
        Object[] result = new Object[] {
            "blah", "foo", "bar", "baz", "tmp", "gosh", "golly", "gee", 
            "hello", "goodbye", "we'll", "see", "you", "all", "again",
            "key",
            "key2",
            (useNullKey()) ? null : "nonnullkey"
        };
        return result;
    }

    /**
     *  Returns the set of values in the mappings used to test the map.  This
     *  method must return an array with the same length as {@link
     *  #getSampleKeys()}.  The default implementation contructs a set of
     *  String values and includes a single null value if {@link
     *  #useNullValue()} returns <code>true</code>, and includes two values
     *  that are the same if {@link #useDuplicateValues()} returns
     *  <code>true</code>.
     **/
    public Object[] getSampleValues() {
        Object[] result = new Object[] {
            "blahv", "foov", "barv", "bazv", "tmpv", "goshv", "gollyv", "geev",
            "hellov", "goodbyev", "we'llv", "seev", "youv", "allv", "againv",
            (useNullValue()) ? null : "nonnullvalue",
            "value",
            (useDuplicateValues()) ? "value" : "value2",
        };
        return result;
    }

    /**
     *  Returns a the set of values that can be used to replace the values
     *  returned from {@link #getSampleValues()}.  This method must return an
     *  array with the same length as {@link #getSampleValues()}.  The values
     *  returned from this method should not be the same as those returned from
     *  {@link #getSampleValues()}.  The default implementation constructs a
     *  set of String values and includes a single null value if {@link
     *  #useNullValue()} returns <code>true</code>, and includes two values
     *  that are the same if {@link #useDuplicateValues()} returns
     *  <code>true</code>.  
     **/
    public Object[] getNewSampleValues() {
        Object[] result = new Object[] {
            (useNullValue()) ? null : "newnonnullvalue",
            "newvalue",
            (useDuplicateValues()) ? "newvalue" : "newvalue2",
            "newblahv", "newfoov", "newbarv", "newbazv", "newtmpv", "newgoshv", 
            "newgollyv", "newgeev", "newhellov", "newgoodbyev", "newwe'llv", 
            "newseev", "newyouv", "newallv", "newagainv",
        };
        return result;
    }

    /**
     *  Helper method to add all the mappings described by {@link
     *  #getSampleKeys()} and {@link #getSampleValues()}.
     **/
    public void addSampleMappings(Map m) {

        Object[] keys = getSampleKeys();
        Object[] values = getSampleValues();
        
        for(int i = 0; i < keys.length; i++) {
            try {
                m.put(keys[i], values[i]);
            } catch (NullPointerException exception) {
                assertTrue("NullPointerException only allowed to be thrown " +
                           "if either the key or value is null.", 
                           keys[i] == null || values[i] == null);
                
                assertTrue("NullPointerException on null key, but " +
                           "useNullKey is not overridden to return false.", 
                           keys[i] == null || !useNullKey());
                
                assertTrue("NullPointerException on null value, but " +
                           "useNullValue is not overridden to return false.",
                           values[i] == null || !useNullValue());
                
                assertTrue("Unknown reason for NullPointer.", false);
            }
        }
        assertEquals("size must reflect number of mappings added.",
                     keys.length, m.size());
    }

    /**
     * Return a new, empty {@link Map} to be used for testing. 
     */
    public abstract Map makeEmptyMap();

    /**
     *  Return a new, populated map.  The mappings in the map should match the
     *  keys and values returned from {@linke #getSampleKeys()} and {@link
     *  #getSampleValues()}.  The default implementation uses makeEmptyMap()
     *  and calls {@link #addSampleMappings()} to add all the mappings to the
     *  map.
     **/
    public Map makeFullMap() {
        Map m = makeEmptyMap();
        addSampleMappings(m);
        return m;
    }

    public Object makeObject() {
        return makeEmptyMap();
    }

    /**
     *  Test to ensure the test setup is working properly.  This method checks
     *  to ensure that the getSampleKeys and getSampleValues methods are
     *  returning results that look appropriate.  That is, they both return a
     *  non-null array of equal length.  The keys array must not have any
     *  duplicate values, and may only contain a (single) null key if
     *  useNullKey() returns true.  The values array must only have a null
     *  value if useNullValue() is true and may only have duplicate values if
     *  useDuplicateValues() returns true.  
     **/
    public void testSampleMappings() {
      Object[] keys = getSampleKeys();
      Object[] values = getSampleValues();
      Object[] newValues = getNewSampleValues();

      assertTrue("failure in test: Must have keys returned from " +
                 "getSampleKeys.", keys != null);

      assertTrue("failure in test: Must have values returned from " +
                 "getSampleValues.", values != null);

      // verify keys and values have equivalent lengths (in case getSampleX are
      // overridden)
      assertEquals("failure in test: not the same number of sample " +
                   "keys and values.",  keys.length, values.length);
      
      assertEquals("failure in test: not the same number of values and new values.",
                   values.length, newValues.length);

      // verify there aren't duplicate keys, and check values
      for(int i = 0; i < keys.length - 1; i++) {
          for(int j = i + 1; j < keys.length; j++) {
              assertTrue("failure in test: duplicate null keys.",
                         (keys[i] != null || keys[j] != null));
              assertTrue("failure in test: duplicate non-null key.",
                         (keys[i] == null || keys[j] == null || 
                          (!keys[i].equals(keys[j]) && 
                           !keys[j].equals(keys[i]))));
          }
          assertTrue("failure in test: found null key, but useNullKey " +
                     "is false.", keys[i] != null || useNullKey());
          assertTrue("failure in test: found null value, but useNullValue " +
                     "is false.", values[i] != null || useNullValue());
          assertTrue("failure in test: found null new value, but useNullValue " +
                     "is false.", newValues[i] != null || useNullValue());
          assertTrue("failure in test: values should not be the same as new value",
                     values[i] != newValues[i] && 
                     (values[i] == null || !values[i].equals(newValues[i])));
      }
    }
    
    // tests begin here.  Each test adds a little bit of tested functionality.
    // Many methods assume previous methods passed.  That is, they do not
    // exhaustively recheck things that have already been checked in a previous
    // test methods.  

    /**
     *  Test to ensure that makeEmptyMap and makeFull returns a new non-null
     *  map with each invocation.  
     **/
    public void testMakeMap() {
        Map em = makeEmptyMap();
        assertTrue("failure in test: makeEmptyMap must return a non-null map.",
                   em != null);
        
        Map em2 = makeEmptyMap();
        assertTrue("failure in test: makeEmptyMap must return a non-null map.",
                   em != null);

        assertTrue("failure in test: makeEmptyMap must return a new map " +
                   "with each invocation.", em != em2);

        Map fm = makeFullMap();
        assertTrue("failure in test: makeFullMap must return a non-null map.",
                   fm != null);
        
        Map fm2 = makeFullMap();
        assertTrue("failure in test: makeFullMap must return a non-null map.",
                   fm != null);

        assertTrue("failure in test: makeFullMap must return a new map " +
                   "with each invocation.", fm != fm2);
    }

    /**
     *  Tests Map.isEmpty()
     **/
    public void testIsEmpty() {
        Map em = makeEmptyMap();
        assertEquals("Map.isEmpty() should return true with an empty map", 
                     true, em.isEmpty());

        Map fm = makeFullMap();
        assertEquals("Map.isEmpty() should return false with a non-empty map",
                     false, fm.isEmpty());
    }

    /**
     *  Tests Map.size()
     **/
    public void testSize() {
        Map em = makeEmptyMap();
        assertEquals("Map.size() should be 0 with an empty map",
                     0, em.size());

        Map fm = makeFullMap();
        assertEquals("Map.size() should equal the number of entries in the map",
                     getSampleKeys().length, fm.size());
    }

    /**
     *  Tests {@link Map#clear()}.  If the map {@link #isAddRemoveModifiable()
     *  can add and remove elements}, then {@link Map#size()} and {@link
     *  Map#isEmpty()} are used to ensure that map has no elements after a call
     *  to clear.  If the map does not support adding and removing elements,
     *  this method checks to ensure clear throws an
     *  UnsupportedOperationException.  This method checks that the both maps
     *  returned by makeEmptyMap and makeFullMap have correct behavior.
     **/
    public void testClear() {
        Map em = makeEmptyMap();
        try {
            em.clear();
            assertTrue("Map must throw UnsupportedOperationException if the " +
                       "map does not support removing elements", 
                       isAddRemoveModifiable());
            assertEquals("size() must return zero after clear.", 
                         0, em.size());
            assertEquals("isEmpty() must return true after clear.", 
                         true, em.isEmpty());
        } catch (UnsupportedOperationException exception) {
            assertTrue("Map must not throw UnsupportedOperationException if the " +
                       "map supports removing elements", !isAddRemoveModifiable());
        }

        Map fm = makeFullMap();
        try {
            fm.clear();
            assertTrue("Map must throw UnsupportedOperationException if the " +
                       "map does not support removing elements", 
                       isAddRemoveModifiable());
            assertEquals("size() must return zero after clear.", 
                         0, fm.size());
            assertEquals("isEmpty() must return true after clear.", 
                         true, fm.isEmpty());
        } catch (UnsupportedOperationException exception) {
            assertTrue("Map must not throw UnsupportedOperationException if the " +
                       "map supports removing elements", !isAddRemoveModifiable());
        }
    }

    public void testFailFastIterator() {
        Map fm = makeFullMap();

        Iterator iterator = fm.keySet().iterator();
        try {
            fm.remove(getSampleKeys()[0]);
        } catch (UnsupportedOperationException e) {
            return;
        }

        try {
            iterator.next();
            fail("Iterators typically throw ConcurrentModificationExceptions when underlying collection is modified.");
        } catch (ConcurrentModificationException e) {

        }
    }

    /**
     *  Tests:
     *  <ul>
     *  <li> Map.entrySet().isEmpty()
     *  <li> Map.entrySet().size()
     *  </ul>
     **/
    public void testEntrySetIsEmpty() {
        Map em = makeEmptyMap();
        Set es = em.entrySet();
        
        assertEquals("entrySet() must return an empty set when map is empty.", 
                     em.isEmpty(), es.isEmpty());
        assertEquals("entrySet() must return a set with the same size as " +
                     "the map.", em.size(), es.size());

        Map fm = makeEmptyMap();
        Set fs = fm.entrySet();
        
        assertEquals("entrySet() must return a non-empty set when map is not empty.", 
                     fm.isEmpty(), fs.isEmpty());
        assertEquals("entrySet() must return a set with the same size as " +
                     "the map.", fm.size(), fs.size());
    }

    /**
     *  Tests Map.containsKey(Object) by verifying it returns false for all
     *  sample keys on a map created using makeEmptyMap() and returns true for
     *  all sample keys returned on a map created using makeFullMap()
     **/
    public void testContainsKey() {
        Object[] keys = getSampleKeys();

        Map em = makeEmptyMap();

        for(int i = 0; i < keys.length; i++) {
            assertTrue("Map must not contain key when map is empty", 
                       !em.containsKey(keys[i]));
        }

        Map fm = makeFullMap();

        for(int i = 0; i < keys.length; i++) {
            assertTrue("Map must contain key for a mapping in the map. " +
		       "Missing: " + keys[i], fm.containsKey(keys[i]));
        }
    }

    /**
     *  Tests Map.containsValue(Object) by verifying it returns false for all
     *  sample alues on a map created using makeEmptyMap() and returns true for
     *  all sample values returned on a map created using makeFullMap.
     **/
    public void testContainsValue() {
        Object[] values = getSampleValues();

        Map em = makeEmptyMap();

        for(int i = 0; i < values.length; i++) {
            assertTrue("Empty map must not contain value", 
                       !em.containsValue(values[i]));
        }

        Map fm = makeFullMap();

        for(int i = 0; i < values.length; i++) {
            assertTrue("Map must contain value for a mapping in the map.", 
                       fm.containsValue(values[i]));
        }
    }

    /**
     *  Test to ensure that Map.entrySet() returns a non-null set.
     **/
    public void testEntrySet() {
        Map em = makeEmptyMap();
        Set es = em.entrySet();
        
        assertTrue("entrySet() must return a non-null set.", es != null);

        Map fm = makeEmptyMap();
        Set fs = fm.entrySet();
        
        assertTrue("entrySet() must return a non-null set.", fs != null);
    }
    
    /**
     *  Tests:
     *  <ul>
     *  <li> Map.entrySet().contains(Object)
     *  <li> Map.entrySet().containsAll(Collection)
     *  </ul>
     *
     *  Note:  This test relies on a working commons.collections.DefaultMapEntry class.
     **/
    public void testEntrySetContainsProperMappings() {
        Object[] keys = getSampleKeys();
        Object[] values = getSampleValues();
        Map.Entry[] entries = new Map.Entry[keys.length];
        HashSet mappings = new HashSet();

        for(int i = 0; i < keys.length; i++) {
            entries[i] = new DefaultMapEntry(keys[i], values[i]);
            mappings.add(entries[i]);
        }

        // test an empty map
        Map em = makeEmptyMap();
        Set es = em.entrySet();

        for(int i = 0; i < keys.length; i++) {
            assertEquals("entrySet().contains(Object) must return false when map " +
                         "is empty", false, es.contains(entries[i]));
        }

        assertEquals("entrySet().containsAll(Collection) must return false when the " +
                     "map is empty", false, es.containsAll(mappings));


        Map fm = makeFullMap();
        Set fs = fm.entrySet();

        for(int i = 0; i < keys.length; i++) {
            assertEquals("entrySet().contains(Object) must return true when map " +
                         "contains the mapping", true, fs.contains(entries[i]));
        }
        assertEquals("entrySet().containsAll(Collection) must return true when the " +
                     "map contains the mapping", true, fs.containsAll(mappings));

        try {
            es.containsAll((Collection)null);
            fail("entrySet().containsAll(null) should " +
                 "throw a NullPointerException");
        } catch (NullPointerException exception) {
            // expected
        }
        try {
            fs.containsAll((Collection)null);
            fail("entrySet().containsAll(null) should " +
                 "throw a NullPointerException");
        } catch (NullPointerException exception) {
            // expected
        }
    }

    /**
     *  Tests Map.entrySet().clear() using Map.isEmpty() and
     *  Map.entrySet().isEmpty().  
     **/
    public void testEntrySetClear() {
        if (!isAddRemoveModifiable()) return;
        Map m = makeFullMap();
        Set set = m.entrySet();
        set.clear();
        assertTrue("entrySet should be empty after clear", set.isEmpty());
        assertTrue("map should be empty after entrySet.clear()", m.isEmpty());
    }


    /**
     *  Tests Map.entrySet().add(Object);
     **/
    public void testEntrySetAdd() {
        Map m = makeFullMap();
        Set set = m.entrySet();
        try {
            set.add(new Object());
            fail("entrySet().add should raise UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }


    /**
     *  Tests Map.entrySet().addAll(Collection);
     **/
    public void testEntrySetAddAll() {
        Map m = makeFullMap();
        Set set = m.entrySet();
        try {
            set.addAll(java.util.Collections.singleton(new Object()));
            fail("entrySet().addAll(Collection) should raise " +
		 "UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    /**
     *  Tests Map.entrySetContainsAll(Collection)
     **/
    public void testEntrySetContainsAll() {
        Map m = makeFullMap();
        Set set = m.entrySet();

        java.util.ArrayList list = new java.util.ArrayList();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            list.add(new DefaultMapEntry(entry.getKey(), entry.getValue()));

	    assertTrue("entrySet().containsAll failed", set.containsAll(list));
        }

        list.add(new Object());
        assertTrue("entrySet().containsAll failed", !set.containsAll(list));
    }


    /**
     *  Tests entrySet().equals(Object)
     **/
    public void testEntrySetEquals() {
        Map m = makeFullMap();
        Map m2 = new HashMap(m);
        assertTrue("Equal maps should have equal entrySets", 
		   m.entrySet().equals(m2.entrySet()));

        assertTrue("entrySet.equals(null) returned true", 
		   !m.entrySet().equals(null));
        assertTrue("Unequal maps should have unequal entrySets", 
		   !m.entrySet().equals(Collections.EMPTY_SET));
    }


    /**
     *  Test entrySet().hashCde()
     **/
    public void testEntrySetHashCode() {
        Map m = makeFullMap();
        Map m2 = new HashMap(m);
        Set set = m.entrySet();
        Set set2 = m2.entrySet();
        assertTrue("hashCode of equal entrySets should be same", 
		   set.hashCode() == set2.hashCode());
    }


    /**
     *  Test entrySet().toArray() and entrySet().toArray(Object[])
     **/
    public void testEntrySetToArray() {
        Map m = makeFullMap();
        Set set = m.entrySet();
        Object[] a = set.toArray();
        assertTrue("entrySet.toArray() should be same size as map", 
		   a.length == m.size());

        a = set.toArray(new Object[0]);
        assertTrue("entrySet.toArray(new Object[0]) should be same size " +
		   "as map", a.length == m.size());

        a = new Object[m.size() * 2];
        a[m.size()] = new Object();
        a = set.toArray(a);
        assertTrue("entrySet.toArray(new Object[m.size * 2]) should set " +
		   "last element to null", a[m.size()] == null);

        a = set.toArray(new Map.Entry[0]);
        assertTrue("entrySet.toArray(new Map.Entry[0]) should return " +
		   "instanceof Map.Entry[]", a instanceof Map.Entry[]);

        try {
            a = set.toArray(new String[0]);
            fail("entrySet.toArray(new String[]) should raise " +
		 "ArrayStoreException.");
        } catch (ArrayStoreException e) {
            // expected
        }
        
    }

    /**
     *  Tests entrySet().remove(Object)
     **/
    public void testEntrySetRemove2() {
        if (!isAddRemoveModifiable()) return;

        Map m = makeFullMap();
        Set set = m.entrySet();

        boolean r = set.remove(null);
        assertTrue("entrySet.remove(null) should return false", !r);

        r = set.remove("Not a Map.Entry");
        assertTrue("entrySet.remove should return false for non-Map.Entry", 
		   !r);

	m = makeEmptyMap();
	set = m.entrySet();

	Object[] keys = getSampleKeys();
	Object[] values = getSampleValues();

	for(int i = 0; i < keys.length; i++) {
	    // remove on all elements should return false because the map is
	    // empty.
	    r = set.remove(new DefaultMapEntry(keys[i], values[i]));
	    assertTrue("entrySet.remove for nonexistent entry should " +
		       "return false", !r);
	}

	// reset to full map to check actual removes
	m = makeFullMap();
	set = m.entrySet();

        int size = m.size();
        Map.Entry entry = (Map.Entry)set.iterator().next();
        r = set.remove(entry);
        assertTrue("entrySet.remove for internal entry should return true", r);
        assertTrue("entrySet.size should shrink after successful remove", 
		   set.size() == size - 1);
        assertTrue("map size should shrink after succuessful entrySet.remove", 
		   m.size() == size - 1);
        entrySetEqualsMap(set, m);

        size--;
        entry = (Map.Entry)set.iterator().next();
        entry = new DefaultMapEntry(entry.getKey(), entry.getValue());
        r = set.remove(entry);
        assertTrue("entrySet.remove for external entry should return true", r);
        assertTrue("entrySet.size should shrink after successful remove", 
		   set.size() == size - 1);
        assertTrue("map size should shrink after succuessful entrySet.remove",
		   m.size() == size - 1);
        assertTrue("After remove, entrySet should not contain element", 
		   !set.contains(entry));
        entrySetEqualsMap(set, m);
        r = set.remove(entry);
        assertTrue("second entrySet.remove should return false", !r);
    }


    /**
     *  Tests entrySet().removeAll() and entrySet().retainAll()
     **/
    public void testEntrySetBulkRemoveOperations() {
	if (!isAddRemoveModifiable()) return;

        Map m = makeFullMap();
        Set set = m.entrySet();
        Map m2 = new HashMap(m);
        Set set2 = m2.entrySet();

        Object[] entries = set2.toArray();
        Collection c = Arrays.asList(entries).subList(2, 5);
        boolean r = set.removeAll(c);
        set2.removeAll(c);
        assertTrue("entrySet().removeAll() returned false", r);
        assertTrue("entrySet().removeAll() failed", m2.equals(m));
        assertTrue("entrySet().removeAll() returned true", !set.removeAll(c));

        m = makeFullMap();
        set = m.entrySet();
        m2 = new HashMap(m);
        set2 = m2.entrySet();
        entries = set2.toArray();
        c = Arrays.asList(entries).subList(2, 5);
        r = set.retainAll(c);
        set2.retainAll(c);
        assertTrue("entrySet().retainAll returned false", r);
        assertTrue("entrySet().retainAll() failed", m2.equals(m));
        assertTrue("entrySet().retainAll returned true", !set.retainAll(c));
    }


    /**
     *  Tests:
     *  <ul>
     *  <li> Map.entrySet().iterator()
     *  <li> Map.entrySet().iterator().hasNext()
     *  <li> Map.entrySet().iterator().next()
     *  </ul>
     **/
    public void testEntrySetIterator() {
        Map em = makeEmptyMap();
        Set es = em.entrySet();
        Iterator eiter = es.iterator();

        assertEquals("entrySet().iterator().hasNext() must return false " +
                     "when then the map is empty.", 
                     false, eiter.hasNext());

        // note: we make a new map to test for this because some impls in the
        // past have required a call to hasMoreElements before a call to next
        // for it to work properly.  By using a new map, we make sure this test
        // will catch those broken impls.
        em = makeEmptyMap();
        es = em.entrySet();
        eiter = es.iterator();
        
        try {
            eiter.next();
            fail("entrySet().iterator().next() must throw a NoSuchElementException " +
                 "when the map is empty");
        } catch (NoSuchElementException exception) {
            // expected
        }


        Map fm = makeFullMap();

        Set fs = fm.entrySet();

        Object[] keys = getSampleKeys();
        Object[] values = getSampleValues();
        boolean[] found = new boolean[keys.length];

        Iterator iter = fs.iterator();

        assertTrue("entrySet().iterator() must return a non-null " +
                   "iterator.", iter != null);

        while(iter.hasNext()) {
            Object obj = iter.next();
            assertTrue("Null is not allowed to be returned from the " +
                       "entrySet().iterator()'s next().", obj != null);
            assertTrue("Objects returned from entrySet().iterator() must be " +
                       "instances of Map.Entry.", obj instanceof Map.Entry);
                
            Map.Entry entry = (Map.Entry)obj;
            Object key = entry.getKey();
            Object value = entry.getValue();

            assertTrue("the key for an entry returned from the entry " +
                       "set's iterator can only be null if useNullKey " +
                       "is true.",
                       key != null || (key == null && useNullKey()));
            
            assertTrue("the value for an entry returned from the entry " +
                       "set's iterator can only be null if useNullValue " +
                       "is true.",
                       value != null || (value == null && useNullValue()));

            for(int i = 0; i < keys.length; i++) {
                if((key == null && keys[i] == null) ||
                   (key != null && key.equals(keys[i]))) {
                    assertTrue("entrySet().iterator() must not return " +
                               "multiple entries with the same key.", 
                               !found[i]);
                        
                    found[i] = true;

                    assertTrue
                        ("value of entry returned from iterator " +
                         "must be the value for the added mapping.",
                         (value == null && values[i] == null) ||
                         (value != null && value.equals(values[i])));
                }
            }
        }
        for(int i = 0; i < found.length; i++) {
            assertTrue("must find all added elements through entrySet's " +
                       "iterator().", found[i]);
        }
    }
  
    /**
     *  Tests Map.entrySet().iterator().remove()
     **/
    public void testEntrySetIteratorRemove() {
        Map m = makeFullMap();
        Set s = m.entrySet();
        Iterator iter = s.iterator();

        try {
            iter.remove();
            fail("Entry set iterator must not allow a call to remove " +
                 "before any calls to next");
        } catch (IllegalStateException exception) {
            // expected exception provided add/remove modifiable
            assertTrue("iterator should throw UnsupportedOperationException " +
                       "if remove is not allowed from the entrySet().iterator()",
                       isAddRemoveModifiable());
        } catch (UnsupportedOperationException exception) {
            assertTrue("iterator should not throw UnsupportedOperationException " +
                       "if the map supports adding and removing elements",
                       !isAddRemoveModifiable());
        }

        while(iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();

            assertTrue("Entry key from entry set iterator must exist in map: " +
		       entry, m.containsKey(entry.getKey()));
            try {
                iter.remove();
                // note: we do not check that the mapping was actually removed
                // from the map because some classes do not have their
                // entrySet().iterator() backed by the map.  That test occurs
                // below in testEntrySetIteratorRemoveCausesMapModification
            } catch (UnsupportedOperationException exception) {
                assertTrue("iterator should not throw UnsupportedOperationException " +
                           "if the map supports adding and removing elements",
                           !isAddRemoveModifiable());
            }

            try {
                iter.remove();
                fail("Entry set iterator must not allow two calls to " +
                     "remove without a call to next.");
            } catch (IllegalStateException exception) {
                // expected exception provided add/remove modifiable
                assertTrue("iterator should throw UnsupportedOperationException " +
                           "if remove is not allowed from the entrySet().iterator()",
                           isAddRemoveModifiable());
            } catch (UnsupportedOperationException exception) {
                assertTrue("iterator should not throw UnsupportedOperationException " +
                           "if the map supports adding and removing elements",
                           !isAddRemoveModifiable());
            }
        }
    }

    /**
     *  utility method to ensure that a set of Map.Entry objects matches those
     *  found in the specified map.
     **/
    protected void entrySetEqualsMap(Set set, Map m) {
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            assertTrue("map should contain key found in entrySet", 
		       m.containsKey(key));
            Object v2 = m.get(key);
            assertTrue("map should contain entry found in entrySet", 
		       (value == null) ? v2 == null : value.equals(v2));
        }
    }


    /**
     *  Tests whether the map's entrySet() is backed by the map by making sure
     *  a put in the map is reflected in the entrySet.  This test does nothing
     *  if add/remove modifications are not supported.
     **/
    public void testEntrySetChangesWithMapPut() {
        if(!isAddRemoveModifiable()) return;

        Map m = makeEmptyMap();

        // test insert reflected in entry set
        Set s = m.entrySet();
        addSampleMappings(m);
        assertEquals("entrySet() must only be empty if map is empty.",
                     m.isEmpty(), s.isEmpty());
        assertEquals("entrySet() must adjust size when map changes.",
                     m.size(), s.size());

        entrySetEqualsMap(s, m);
    }

    /**
     *  Tests whether the map's entrySet() is backed by the map by making sure
     *  a remove from the map is reflected in the entrySet.  This test does
     *  nothing if add/remove modifications are not supported.
     **/
    public void testEntrySetChangesWithMapRemove() {
        if(!isAddRemoveModifiable()) return;

        Map m = makeFullMap();
        Set s = m.entrySet();

        Object[] keys = getSampleKeys();
        Object[] values = getSampleValues();

        for(int i = 0; i < keys.length; i++) {
            m.remove(keys[i]);
            assertEquals("entrySet() must only be empty if map is empty.",
                         m.isEmpty(), s.isEmpty());
            assertEquals("entrySet() must adjust size when map changes.",
                         m.size(), s.size());
            entrySetEqualsMap(s, m);
        }
    }


    /**
     *  Tests whether the map's entrySet() is backed by the map by making sure
     *  a clear on the map is reflected in the entrySet.  This test does
     *  nothing if add/remove modifications are not supported.
     **/
    public void testEntrySetChangesWithMapClear() {
        if (!isAddRemoveModifiable()) return;

        Map m = makeFullMap();
        Set s = m.entrySet();
        m.clear();
        assertTrue("entrySet() must be empty after map.clear()", s.isEmpty());
    }


    /**
     *  Tests whether the map's entrySet() is backed by the map by making sure
     *  a putAll on the map is reflected in the entrySet.  This test does
     *  nothing if add/remove modifications are not supported.
     **/
    public void testEntrySetChangesWithMapPutAll() {
        if (!isAddRemoveModifiable()) return;

        Map m = makeFullMap();
        Set s = m.entrySet();

        Map m2 = new HashMap();
        m2.put("1", "One");
        m2.put("2", "Two");
        m2.put("3", "Three");

        m.putAll(m2);
        entrySetEqualsMap(s, m);
    }


    /**
     *  Tests whether the map's entrySet() is backed by the map by making sure
     *  a remove from the entrySet's iterator is reflected in the map. This
     *  test does nothing if add/remove modifications are not supported.
     **/
    public void testEntrySetIteratorRemoveCausesMapModification() {
        if(!isAddRemoveModifiable()) return;
        
        Map m = makeFullMap();
        Set s = m.entrySet();
        Iterator iter = s.iterator();
        
        while(iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            
            try {
                iter.remove();
                assertTrue("Entry key from entry set iterator must " +
                           "no longer exist in map",
                           !m.containsKey(entry.getKey()));
            } catch (UnsupportedOperationException exception) {
                // isAddRemoveModifiable is true -- we've checked that above
                fail("iterator should not throw UnsupportedOperationException " +
                     "if the map supports adding and removing elements");
            }
        }
    }

    /**
     *  Tests Map.equals(Object)
     **/
    public void testMapEquals() {
        Map m = makeEmptyMap();
        assertTrue("Empty maps unequal.", m.equals(new HashMap()));

        m = makeFullMap();
        Map m2 = new HashMap();
        m2.putAll(m);
        assertTrue("Full maps unequal.", m.equals(m2));

	// modify the HashMap created from the full map and make sure this
	// change results in map.equals() to return false.
        Iterator iter = m2.keySet().iterator();
        iter.next();
        iter.remove();
        assertTrue("Different maps equal.", !m.equals(m2));

        assertTrue("equals(null) returned true.", !m.equals(null));
        assertTrue("equals(new Object()) returned true.", 
		   !m.equals(new Object()));
    }

    /**
     *  Tests Map.get(Object)
     **/
    public void testMapGet() {
        Map m = makeEmptyMap();

        Object[] keys = getSampleKeys();
        Object[] values = getSampleValues();

        for (int i = 0; i < keys.length; i++) {
            assertTrue("Empty map.get() should return null.", 
		       m.get(keys[i]) == null);
        }

        m = makeFullMap();
        for (int i = 0; i < keys.length; i++) {
	    assertEquals("Full map.get() should return value from mapping.", 
			 values[i], m.get(keys[i]));
        }
    }

    /**
     *  Tests Map.hashCode()
     **/
    public void testMapHashCode() {
        Map m = makeEmptyMap();
	Map m2 = new HashMap();
        assertTrue("Empty maps have different hashCodes.", 
		   m.hashCode() == m2.hashCode());

        m = makeFullMap();
        m2.putAll(m);
        assertTrue("Equal maps have different hashCodes.", 
		   m.hashCode() == m2.hashCode());
    }

    /**
     *  Tests Map.toString().  Since the format of the string returned by the
     *  toString() method is not defined in the Map interface, there is no
     *  common way to test the results of the toString() method.  Thereforce,
     *  it is encouraged that Map implementations override this test with one
     *  that checks the format matches any format defined in its API.  This
     *  default implementation just verifies that the toString() method does
     *  not return null.
     **/
    public void testMapToString() {
        Map m = makeEmptyMap();
        String s = m.toString();
        assertTrue("Empty map toString() should not return null", s != null);
    }

    /**
     *  Tests Map.keySet()
     **/
    public void testMapKeySet() {
        Map m = makeFullMap();
        Map m2 = new HashMap(m);
        assertTrue("Equal maps have unequal keySets.", 
		   m.keySet().equals(m2.keySet()));
    }
    
    //-------TEST AGAINST OPTIONAL OPERATIONS, ENABLE IN TEST SUBCLASSES

    public void testMapSupportsNullValues() {

        if ((this instanceof TestMap.SupportsPut) == false) {
            return;
        }

        Map map = makeEmptyMap();
        map.put(new Integer(1),"foo");
        
        assertTrue("no null values in Map",map.containsValue(null) == false);

        map.put(new Integer(2),null);

        assertTrue("null value in Map",map.containsValue(null));
        assertTrue("key to a null value",map.containsKey(new Integer(2)));
    }

    public void testMultiplePuts() {

        if ((this instanceof TestMap.SupportsPut) == false) {
            return;
        }

        Map map = makeEmptyMap();
        map.put(new Integer(4),"foo");
        map.put(new Integer(4),"bar");
        map.put(new Integer(4),"foo");
        map.put(new Integer(4),"bar");

        assertTrue("same key different value",map.get(new Integer(4)).equals("bar"));
    }


    public void testCapacity() {

        if ((this instanceof TestMap.SupportsPut) == false) {
            return;
        }

        Map map = makeEmptyMap();
        map.put(new Integer(1),"foo");
        map.put(new Integer(2),"foo");
        map.put(new Integer(3),"foo");
        map.put(new Integer(1),"foo");
        
        assertTrue("size of Map should be 3, but was " + map.size(), map.size() == 3);
    }

    public void testEntrySetRemove() {

        if ((this instanceof TestMap.EntrySetSupportsRemove) == false ||
            (this instanceof TestMap.SupportsPut) == false) {
            return;
        }

        Map map = makeEmptyMap();
        map.put("1","1");
        map.put("2","2");
        map.put("3","3");

        Object o = map.entrySet().iterator().next();
        // remove one of the key/value pairs
        Set set = map.entrySet();
        set.remove(o);
        assertTrue(set.size() == 2);
        // try to remove it again, to make sure 
        // the Set is not confused by missing entries
        set.remove(o);

        assertTrue("size of Map should be 2, but was " + map.size(), map.size() == 2);

    }

    public void testEntrySetContains() {

        if ((this instanceof TestMap.SupportsPut) == false) {
            return;
        }

        Map map = makeEmptyMap();
        map.put("1","1");
        map.put("2","2");
        map.put("3","3");

        Set set = map.entrySet();
        Object o = set.iterator().next();
        assertTrue("entry set should contain valid element",set.contains(o));

        // create a fresh entry mapped to existing values
        DefaultMapEntry goodEntry  = new DefaultMapEntry("2","2");
        assertTrue("entry set should recognize externally constructed MapEntry objects",
                   set.contains(goodEntry));

        // make a bogus entry
        DefaultMapEntry badEntry = new DefaultMapEntry("4","4");
        assertTrue("entry set should not contain a bogus element",
                   set.contains(badEntry) == false);
        

    }

    public void testEmptyMapSerialization() 
    throws IOException, ClassNotFoundException {
        Map map = makeEmptyMap();
        if (!(map instanceof Serializable)) return;
        
        byte[] objekt = writeExternalFormToBytes((Serializable) map);
        Map map2 = (Map) readExternalFormFromBytes(objekt);

        assertTrue("Both maps are empty",map.isEmpty()  == true);
        assertTrue("Both maps are empty",map2.isEmpty() == true);
    }

    public void testFullMapSerialization() 
    throws IOException, ClassNotFoundException {
        Map map = makeFullMap();
        if (!(map instanceof Serializable)) return;
        
        byte[] objekt = writeExternalFormToBytes((Serializable) map);
        Map map2 = (Map) readExternalFormFromBytes(objekt);

        assertEquals("Both maps are same size",map.size(), getSampleKeys().length);
        assertEquals("Both maps are same size",map2.size(),getSampleKeys().length);
    }

    /**
     * Compare the current serialized form of the Map
     * against the canonical version in CVS.
     */
    public void testEmptyMapCompatibility() throws IOException, ClassNotFoundException {
        /**
         * Create canonical objects with this code
        Map map = makeEmptyMap();
        if (!(map instanceof Serializable)) return;
        
        writeExternalFormToDisk((Serializable) map, getCanonicalEmptyCollectionName(map));
        */

        // test to make sure the canonical form has been preserved
        if (!(makeEmptyMap() instanceof Serializable)) return;
        Map map = (Map) readExternalFormFromDisk(getCanonicalEmptyCollectionName(makeEmptyMap()));
        assertTrue("Map is empty",map.isEmpty()  == true);
    }

        /**
     * Compare the current serialized form of the Map
     * against the canonical version in CVS.
     */
    public void testFullMapCompatibility() throws IOException, ClassNotFoundException {
        /**
         * Create canonical objects with this code
        Map map = makeFullMap();
        if (!(map instanceof Serializable)) return;
        
        writeExternalFormToDisk((Serializable) map, getCanonicalFullCollectionName(map));
        */

        // test to make sure the canonical form has been preserved
        if (!(makeFullMap() instanceof Serializable)) return;
        Map map = (Map) readExternalFormFromDisk(getCanonicalFullCollectionName(makeFullMap()));
        assertEquals("Map is the right size",map.size(), getSampleKeys().length);
    }

    /**
     *  Tests Map.put(Object, Object)
     **/
    public void testMapPut() {
        if (!isAddRemoveModifiable()) return;

        Map m = makeEmptyMap();

	Object[] keys = getSampleKeys();
	Object[] values = getSampleValues();
	Object[] newValues = getNewSampleValues();

	for(int i = 0; i < keys.length; i++) {
	    Object o = m.put(keys[i], values[i]);
	    assertTrue("First map.put should return null", o == null);
	    assertTrue("Map should contain key after put", 
		       m.containsKey(keys[i]));
	    assertTrue("Map should contain value after put", 
		       m.containsValue(values[i]));
	}
	
	for(int i = 0; i < keys.length; i++) {
	    Object o = m.put(keys[i], newValues[i]);
	    assertEquals("Second map.put should return previous value",
			 values[i], o);
	    assertTrue("Map should still contain key after put",
		       m.containsKey(keys[i]));
	    assertTrue("Map should contain new value after put",
		       m.containsValue(newValues[i]));

	    // if duplicates are allowed, we're not guarunteed that the value
	    // no longer exists, so don't try checking that.
	    if(!useDuplicateValues()) {
		assertTrue("Map should not contain old value after second put",
			   !m.containsValue(values[i]));
	    }
	}
    }

    /**
     *  Tests Map.putAll(Collection)
     **/
    public void testMapPutAll() {
        if (!isAddRemoveModifiable()) return;

        Map m = makeEmptyMap();
	Map m2 = makeFullMap();

	m.putAll(m2);

        assertTrue("Maps should be equal after putAll", m.equals(m2));

	// repeat test with a different map implementation

	m2 = new HashMap();
	Object[] keys = getSampleKeys();
	Object[] values = getSampleValues();
	for(int i = 0; i < keys.length; i++) {
	    m2.put(keys[i], values[i]);
	}
	
	m = makeEmptyMap();
	m.putAll(m2);
	
	assertTrue("Maps should be equal after putAll", m.equals(m2));
    }

    /**
     *  Tests Map.remove(Object)
     **/
    public void testMapRemove() {
        if (!isAddRemoveModifiable()) return;

        Map m = makeEmptyMap();
	Object[] keys = getSampleKeys();
	for(int i = 0; i < keys.length; i++) {
	    Object o = m.remove(keys[i]);
	    assertTrue("First map.remove should return null", o == null);
	}

	m = makeFullMap();
	int startSize = m.size();

	Object[] values = getSampleValues();

	for(int i = 0; i < keys.length; i++) {
	    Object o = m.remove(keys[i]);

	    assertEquals("map.remove with valid key should return value",
			 values[i], o);
	    assertEquals("map.remove should reduce size by one",
			 (startSize - i) - 1, m.size());
	}
    }

    /**
     *  Tests Map.values()
     **/
    public void testMapValues() {
        Map m = makeFullMap();

        // since Collection.equals is reference-based, have to do
        // this the long way...

	Object[] values = getSampleValues();

	Collection c = m.values();
	
	assertEquals("values() should have same size as map", 
		     m.size(), c.size());

	assertEquals("values() should have same number of sample values",
		     values.length, c.size());

	boolean[] matched = new boolean[values.length];

	Iterator iter = c.iterator();
	while(iter.hasNext()) {
	    Object o = iter.next();
	    boolean found = false;

	    for(int i = 0; i < values.length; i++) {
		// skip values already matched
		if(matched[i]) continue;
		
		if((o == null && values[i] == null) ||
		   (o != null && o.equals(values[i]))) {
		    matched[i] = true;
		    found = true;
		    break;
		}
	    }

	    if(!found) {
		// no match for this element
		fail("values() returned an unexpected value");
	    }
	}

	for(int i = 0; i < matched.length; i++) {
	    if(!matched[i]) {
		fail("values() did not return all values from map");
	    }
	}
    }

    /**
     * Marker interface, indicating that a TestMap subclass
     * can test put(Object,Object) operations.
     */
    public interface SupportsPut {

    }

    public interface EntrySetSupportsRemove {

    }

}
