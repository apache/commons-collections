/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/Attic/TestMap.java,v 1.11 2002/02/22 22:21:50 morgand Exp $
 * $Revision: 1.11 $
 * $Date: 2002/02/22 22:21:50 $
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
import java.util.Collection;
import java.util.Map;
import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
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
 * @version $Id: TestMap.java,v 1.11 2002/02/22 22:21:50 morgand Exp $
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
            assertTrue("Map must contain key for a mapping in the map.", 
                       fm.containsKey(keys[i]));
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

    // TODO: test entrySet().clear()
    // TODO: test entrySet().add() throws OperationNotSupported
    // TODO: test entrySet().addAll() throws OperationNotSupported
    // TODO: test entrySet().contains(Object)
    // TODO: test entrySet().containsAll(Collection)
    // TODO: test entrySet().equals(Object)
    // TODO: test entrySet().hashCode()
    // TODO: test entrySet().toArray()
    // TODO: test entrySet().toArray(Object[] a)
    // TODO: test entrySet().remove(Object)
    // TODO: test entrySet().removeAll(Collection)
    // TODO: test entrySet().retainAll(Collection)

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
            
            assertTrue("Entry key from entry set iterator must exist in map",
                       m.containsKey(entry.getKey()));
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
        // TODO: test set and map reflect the same contents
    }

    /**
     *  Tests whether the map's entrySet() is backed by the map by making sure
     *  a remove from the map is reflected in the entrySet.  This test does nothing
     *  if add/remove modifications are not supported.
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
            //TODO: test set and map reflect the same contents
        }
    }

    // TODO: test entrySet() changes after Map.remove
    // TODO: test entrySet() changes after Map.clear
    // TODO: test entrySet() changes after Map.putAll

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

    // TODO: test map changes after entrySet().remove
    // TODO: test map changes after entrySet().removeAll
    // TODO: test map changes after entrySet().retainAll

    public void testMapEquals() {
        // XXX finish me
    }

    public void testMapGet() {
        // XXX finish me
    }

    public void testMapHashCode() {
        // XXX finish me
    }

    public void testMapKeySet() {
        // XXX finish me
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


    public String getCanonicalEmptyMapName(Map map) {
        StringBuffer retval = new StringBuffer();
        retval.append("data/test/");
        String mapName = map.getClass().getName();
        mapName = mapName.substring(mapName.lastIndexOf(".")+1,mapName.length());
        retval.append(mapName);
        retval.append(".emptyMap.");
        retval.append(COLLECTIONS_VERSION);
        retval.append(".obj");
        return retval.toString();
    }

    public String getCanonicalFullMapName(Map map) {
        StringBuffer retval = new StringBuffer();
        retval.append("data/test/");
        String mapName = map.getClass().getName();
        mapName = mapName.substring(mapName.lastIndexOf(".")+1,mapName.length());
        retval.append(mapName);
        retval.append(".fullMap.");
        retval.append(COLLECTIONS_VERSION);
        retval.append(".obj");
        return retval.toString();
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
        
        writeExternalFormToDisk((Serializable) map, getCanonicalEmptyMapName(map));
        */

        // test to make sure the canonical form has been preserved
        if (!(makeEmptyMap() instanceof Serializable)) return;
        Map map = (Map) readExternalFormFromDisk(getCanonicalEmptyMapName(makeEmptyMap()));
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
        
        writeExternalFormToDisk((Serializable) map, getCanonicalFullMapName(map));
        */

        // test to make sure the canonical form has been preserved
        if (!(makeFullMap() instanceof Serializable)) return;
        Map map = (Map) readExternalFormFromDisk(getCanonicalFullMapName(makeFullMap()));
        assertEquals("Map is the right size",map.size(), getSampleKeys().length);
    }

    /*
        // optional operation
public void testMapClear() {
    // XXX finish me
}

    // optional operation
    public void testMapPut() {
        // XXX finish me
    }

    // optional operation
    public void testMapPutAll() {
        // XXX finish me
    }

    // optional operation
    public void testMapRemove() {
        // XXX finish me
    }

    public void testMapValues() {
        // XXX finish me
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
