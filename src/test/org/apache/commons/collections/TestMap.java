/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/Attic/TestMap.java,v 1.17 2002/06/18 03:17:34 mas Exp $
 * $Revision: 1.17 $
 * $Date: 2002/06/18 03:17:34 $
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
 * The forces at work here are similar to those in {@link TestCollection}.
 * If your class implements the full Map interface, including optional
 * operations, simply extend this class, and implement the {@link
 * #makeEmptyMap()} method.
 * <p>
 * On the other hand, if your map implemenation is wierd, you may have to
 * override one or more of the other protected methods.  They're described
 * below.<P>
 *
 * <B>Entry Population Methods</B><P>
 *
 * Override these methods if your map requires special entries:
 *
 * <UL>
 * <LI>{@link #getSampleKeys}
 * <LI>{@link #getSampleValues}
 * <LI>{@link #getNewSampleValues}
 * <LI>{@link #getOtherKeys}
 * <LI>{@link #getOtherValues}
 * </UL>
 *
 * <B>Supported Operation Methods</B><P>
 *
 * Override these methods if your map doesn't support certain operations:
 *
 * <UL>
 * <LI> {@link #useDuplicateValues}
 * <LI> {@link #useNullKey}
 * <LI> {@link #useNullValue}
 * <LI> {@link #isAddRemoveModifiable}
 * <LI> {@link #isChangeable}
 * </UL>
 *
 * <B>Fixture Methods</B><P>
 *
 * For tests on modification operations (puts and removes), fixtures are used
 * to verify that that operation results in correct state for the map and its
 * collection views.  Basically, the modification is performed against your
 * map implementation, and an identical modification is performed against
 * a <I>confirmed</I> map implementation.  A confirmed map implementation is
 * something like <Code>java.util.HashMap</Code>, which is known to conform
 * exactly to the {@link Map} contract.  After the modification takes place
 * on both your map implementation and the confirmed map implementation, the
 * two maps are compared to see if their state is identical.  The comparison
 * also compares the collection views to make sure they're still the same.<P>
 *
 * The upshot of all that is that <I>any</I> test that modifies the map in
 * <I>any</I> way will verify that <I>all</I> of the map's state is still
 * correct, including the state of its collection views.  So for instance
 * if a key is removed by the map's key set's iterator, then the entry set 
 * is checked to make sure the key/value pair no longer appears.<P>
 *
 * The {@link #map} field holds an instance of your collection implementation.
 * The {@link #entrySet}, {@link #keySet} and {@link #values} fields hold
 * that map's collection views.  And the {@link #confirmed} field holds
 * an instance of the confirmed collection implementation.  The 
 * {@link #resetEmpty} and {@link #resetFull} methods set these fields to 
 * empty or full maps, so that tests can proceed from a known state.<P>
 *
 * After a modification operation to both {@link #map} and 
 * {@link #confirmed}, the {@link #verify} method is invoked to compare the
 * results.  You may want to override {@link #verify} to perform additional
 * verifications.  For instance, {@link TestDoubleOrderedMap} would want 
 * override its {@link #verify} method to verify that the values are unique
 * and in ascending order.<P>
 *  
 * <B>Other Notes</B><P>
 *
 * If your {@link Map} fails one of these tests by design, you may still use
 * this base set of cases.  Simply override the test case (method) your {@link
 * Map} fails and/or the methods that define the assumptions used by the test
 * cases.  For example, if your map does not allow duplicate values, override
 * {@link #useDuplicateValues()} and have it return <code>false</code>
 *
 * @author Michael Smith
 * @author Rodney Waldhoff
 * @author Paul Jack
 * @version $Id: TestMap.java,v 1.17 2002/06/18 03:17:34 mas Exp $
 */
public abstract class TestMap extends TestObject {

    // These instance variables are initialized with the reset method.
    // Tests for map methods that alter the map (put, putAll, remove) 
    // first call reset() to create the map and its views; then perform
    // the modification on the map; perform the same modification on the
    // confirmed; and then call verify() to ensure that the map is equal
    // to the confirmed, that the already-constructed collection views
    // are still equal to the confirmed's collection views.


    /** Map created by reset(). */
    protected Map map;

    /** Entry set of map created by reset(). */
    protected Set entrySet;

    /** Key set of map created by reset(). */
    protected Set keySet;

    /** Values collection of map created by reset(). */
    protected Collection values;

    /** HashMap created by reset(). */
    protected HashMap confirmed;


    public TestMap(String testName) {
        super(testName);
    }


    /**
     *  Override if your map does not allow a <code>null</code> key.  The
     *  default implementation returns <code>true</code>
     **/
    protected boolean useNullKey() {
        return true;
    }

    /**
     *  Override if your map does not allow <code>null</code> values.  The
     *  default implementation returns <code>true</code>.
     **/
    protected boolean useNullValue() {
        return true;
    }

    /**
     *  Override if your map does not allow duplicate values.  The default
     *  implementation returns <code>true</code>.
     **/
    protected boolean useDuplicateValues() {
        return true;
    }

    /**
     *  Override if your map allows its mappings to be changed to new values.
     *  The default implementation returns <code>true</code>.
     **/
    protected boolean isChangeable() {
        return true;
    }

    /**
     *  Override if your map does not allow add/remove modifications.  The
     *  default implementation returns <code>true</code>.
     **/
    protected boolean isAddRemoveModifiable() {
        return true;
    }

    /**
     *  Returns the set of keys in the mappings used to test the map.  This
     *  method must return an array with the same length as {@link
     *  #getSampleValues()} and all array elements must be different. The
     *  default implementation constructs a set of String keys, and includes a
     *  single null key if {@link #useNullKey()} returns <code>true</code>.
     **/
    protected Object[] getSampleKeys() {
        Object[] result = new Object[] {
            "blah", "foo", "bar", "baz", "tmp", "gosh", "golly", "gee", 
            "hello", "goodbye", "we'll", "see", "you", "all", "again",
            "key",
            "key2",
            (useNullKey()) ? null : "nonnullkey"
        };
        return result;
    }


    protected Object[] getOtherKeys() {
        return TestCollection.getOtherNonNullStringElements();
    }

    protected Object[] getOtherValues() {
        return TestCollection.getOtherNonNullStringElements();
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
    protected Object[] getSampleValues() {
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
    protected Object[] getNewSampleValues() {
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
    protected void addSampleMappings(Map m) {

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
    protected abstract Map makeEmptyMap();

    /**
     *  Return a new, populated map.  The mappings in the map should match the
     *  keys and values returned from {@linke #getSampleKeys()} and {@link
     *  #getSampleValues()}.  The default implementation uses makeEmptyMap()
     *  and calls {@link #addSampleMappings()} to add all the mappings to the
     *  map.
     **/
    protected Map makeFullMap() {
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
    public void testMapIsEmpty() {
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
    public void testMapSize() {
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
    public void testMapClear() {
        if (!isAddRemoveModifiable()) return;

        resetEmpty();
        map.clear();
        confirmed.clear();
        verify();
        
        resetFull();
        map.clear();
        confirmed.clear();
        verify();
    }


    /**
     *  Tests Map.containsKey(Object) by verifying it returns false for all
     *  sample keys on a map created using makeEmptyMap() and returns true for
     *  all sample keys returned on a map created using makeFullMap()
     **/
    public void testMapContainsKey() {
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
    public void testMapContainsValue() {
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

        resetEmpty();

	Object[] keys = getSampleKeys();
	Object[] values = getSampleValues();
	Object[] newValues = getNewSampleValues();

        for(int i = 0; i < keys.length; i++) {
            Object o = map.put(keys[i], values[i]);
            confirmed.put(keys[i], values[i]);
            verify();
	    assertTrue("First map.put should return null", o == null);
	    assertTrue("Map should contain key after put", 
		       map.containsKey(keys[i]));
	    assertTrue("Map should contain value after put", 
		       map.containsValue(values[i]));
	}
	
	for(int i = 0; i < keys.length; i++) {
	    Object o = map.put(keys[i], newValues[i]);
            confirmed.put(keys[i], newValues[i]);
            verify();
	    assertEquals("Second map.put should return previous value",
			 values[i], o);
	    assertTrue("Map should still contain key after put",
		       map.containsKey(keys[i]));
	    assertTrue("Map should contain new value after put",
		       map.containsValue(newValues[i]));

	    // if duplicates are allowed, we're not guarunteed that the value
	    // no longer exists, so don't try checking that.
	    if(!useDuplicateValues()) {
		assertTrue("Map should not contain old value after second put",
			   !map.containsValue(values[i]));
	    }
	}
    }

    /**
     *  Tests Map.putAll(Collection)
     **/
    public void testMapPutAll() {
        if (!isAddRemoveModifiable()) return;

        resetEmpty();

        Map m2 = makeFullMap();

        map.putAll(m2);
        confirmed.putAll(m2);
        verify();

        resetEmpty();

	m2 = new HashMap();
	Object[] keys = getSampleKeys();
	Object[] values = getSampleValues();
	for(int i = 0; i < keys.length; i++) {
	    m2.put(keys[i], values[i]);
	}

	map.putAll(m2);
        confirmed.putAll(m2);
        verify();
    }

    /**
     *  Tests Map.remove(Object)
     **/
    public void testMapRemove() {
        if (!isAddRemoveModifiable()) return;

        Map m = makeEmptyMap();
	Object[] keys = getSampleKeys();
	Object[] values = getSampleValues();
	for(int i = 0; i < keys.length; i++) {
	    Object o = m.remove(keys[i]);
	    assertTrue("First map.remove should return null", o == null);
	}

        resetFull();

	for(int i = 0; i < keys.length; i++) {
	    Object o = map.remove(keys[i]);
            confirmed.remove(keys[i]);
            verify();

	    assertEquals("map.remove with valid key should return value",
			 values[i], o);
	}

        Object[] other = getOtherKeys();
        m = makeFullMap();
        int size = m.size();
        for (int i = 0; i < other.length; i++) {
            Object o = m.remove(other[i]);
            assertEquals("map.remove for nonexistent key should return null",
                         o, null);
            assertEquals("map.remove for nonexistent key should not " +
                         "shrink map", size, m.size());
        }
    }


    /**
     * Marker interface, indicating that a TestMap subclass
     * can test put(Object,Object) operations.
     */
    public interface SupportsPut {

    }


    /**
     *  Utility methods to create an array of Map.Entry objects
     *  out of the given key and value arrays.<P>
     *
     *  @param keys    the array of keys
     *  @param values  the array of values
     *  @return an array of Map.Entry of those keys to those values
     */
    private Map.Entry[] makeEntryArray(Object[] keys, Object[] values) {
        Map.Entry[] result = new Map.Entry[keys.length];
        for (int i = 0; i < keys.length; i++) {
            result[i] = new DefaultMapEntry(keys[i], values[i]);
        }
        return result;
    }


    /**
     *  Bulk test {@link Map#entrySet}.  This method runs through all of
     *  the tests in {@link TestSet}.  
     *  After modification operations, {@link #verify} is invoked to ensure
     *  that the map and the other collection views are still valid.
     *
     *  @return a {@link TestSet} instance for testing the map's entry set
     */
    public BulkTest bulkTestMapEntrySet() {
        return new TestMapEntrySet();
    }

    class TestMapEntrySet extends TestSet {
        public TestMapEntrySet() {
            super("");
        }

        // Have to implement manually; entrySet doesn't support addAll
        protected Object[] getFullElements() {
            Object[] k = getSampleKeys();
            Object[] v = getSampleValues();
            return makeEntryArray(k, v);
        }
        
        // Have to implement manually; entrySet doesn't support addAll
        protected Object[] getOtherElements() {
            Object[] k = getOtherKeys();
            Object[] v = getOtherValues();
            return makeEntryArray(k, v);
        }
        
        protected Set makeEmptySet() {
            return makeEmptyMap().entrySet();
        }
        
        protected Set makeFullSet() {
            return makeFullMap().entrySet();
        }
        
        protected boolean supportsAdd() {
            // Collection views don't support add operations.
            return false;
        }
        
        protected boolean supportsRemove() {
            // Entry set should only support remove if map does
            return isAddRemoveModifiable();
        }
        
        protected void resetFull() {
            TestMap.this.resetFull();
            collection = map.entrySet();
            TestMapEntrySet.this.confirmed = 
                TestMap.this.confirmed.entrySet();
        }
        
        protected void resetEmpty() {
            TestMap.this.resetEmpty();
            collection = map.entrySet();
            TestMapEntrySet.this.confirmed = 
                TestMap.this.confirmed.entrySet();
        }
        
        protected void verify() {
            super.verify();
            TestMap.this.verify();
        }
    }


    /**
     *  Bulk test {@link Map#keySet}.  This method runs through all of
     *  the tests in {@link TestSet}.  
     *  After modification operations, {@link #verify} is invoked to ensure
     *  that the map and the other collection views are still valid.
     *
     *  @return a {@link TestSet} instance for testing the map's key set
     */
    public BulkTest bulkTestMapKeySet() {
        return new TestMapKeySet();
    }

    class TestMapKeySet extends TestSet {
        public TestMapKeySet() {
            super("");
        }
        protected Object[] getFullElements() {
            return getSampleKeys();
        }
        
        protected Object[] getOtherElements() {
            return getOtherKeys();
        }
        
        protected Set makeEmptySet() {
            return makeEmptyMap().keySet();
        }
        
        protected Set makeFullSet() {
            return makeFullMap().keySet();
        }
        
        protected boolean supportsAdd() {
            return false;
        }
        
        protected boolean supportsRemove() {
            return isAddRemoveModifiable();
        }
        
        protected void resetEmpty() {
            TestMap.this.resetEmpty();
            collection = map.keySet();
            TestMapKeySet.this.confirmed = TestMap.this.confirmed.keySet();
        }
        
        protected void resetFull() {
            TestMap.this.resetFull();
            collection = map.keySet();
            TestMapKeySet.this.confirmed = TestMap.this.confirmed.keySet();
        }
        
        protected void verify() {
            super.verify();
            TestMap.this.verify();
        }
    }


    /**
     *  Bulk test {@link Map#values}.  This method runs through all of
     *  the tests in {@link TestCollection}.  
     *  After modification operations, {@link #verify} is invoked to ensure
     *  that the map and the other collection views are still valid.
     *
     *  @return a {@link TestCollection} instance for testing the map's 
     *    values collection
     */
    public BulkTest bulkTestMapValues() {
        return new TestMapValues();
    }

    class TestMapValues extends TestCollection {
        public TestMapValues() {
            super("");
        }

        protected Object[] getFullElements() {
            return getSampleValues();
        }
        
        protected Object[] getOtherElements() {
            return getOtherValues();
        }
        
        protected Collection makeCollection() {
            return makeEmptyMap().values();
        }
        
        protected Collection makeFullCollection() {
            return makeFullMap().values();
        }
        
        protected boolean supportsAdd() {
            return false;
        }
        
        protected boolean supportsRemove() {
            return isAddRemoveModifiable();
        }
        
        protected Collection makeConfirmedCollection() {
            // never gets called, reset methods are overridden
            return null;
        }
        
        protected Collection makeConfirmedFullCollection() {
            // never gets called, reset methods are overridden
            return null;
        }
        
        protected void resetFull() {
            TestMap.this.resetFull();
            collection = map.values();
            TestMapValues.this.confirmed = TestMap.this.confirmed.values();
        }
        
        protected void resetEmpty() {
            TestMap.this.resetEmpty();
            collection = map.values();
            TestMapValues.this.confirmed = TestMap.this.confirmed.values();
        }
        
        protected void verify() {
            super.verify();
            TestMap.this.verify();
        }
    }


    /**
     *  Resets the {@link #map}, {@link #entrySet}, {@link #keySet},
     *  {@link #values} and {@link #confirmed} fields to empty.
     */
    protected void resetEmpty() {
        this.map = makeEmptyMap();
        views();
        this.confirmed = new HashMap();
    }


    /**
     *  Resets the {@link #map}, {@link #entrySet}, {@link #keySet},
     *  {@link #values} and {@link #confirmed} fields to full.
     */
    protected void resetFull() {
        this.map = makeFullMap();
        views();
        this.confirmed = new HashMap();
        Object[] k = getSampleKeys();
        Object[] v = getSampleValues();
        for (int i = 0; i < k.length; i++) {
            confirmed.put(k[i], v[i]);
        }
    }


    /**
     *  Resets the collection view fields.
     */
    private void views() {
        this.keySet = map.keySet();
        this.values = map.values();
        this.entrySet = map.entrySet();
    }


    /**
     *  Verifies that {@link #map} is still equal to {@link #confirmed}.
     *  This method checks that the map is equal to the HashMap, 
     *  <I>and</I> that the map's collection views are still equal to
     *  the HashMap's collection views.  An <Code>equals</Code> test
     *  is done on the maps and their collection views; their size and
     *  <Code>isEmpty</Code> results are compared; their hashCodes are
     *  compared; and <Code>containsAll</Code> tests are run on the 
     *  collection views.
     */
    protected void verify() {
        Bag bag1 = new HashBag(confirmed.values());
        Bag bag2 = new HashBag(values);

        assertEquals("Map should still equal HashMap", confirmed, map);
        assertEquals("Map's entry set should still equal HashMap's", 
                     confirmed.entrySet(), entrySet);
        assertEquals("Map's key set should still equal HashMap's",
                     confirmed.keySet(), keySet);
        assertEquals("Map's values should still equal HashMap's",
                     bag1, bag2);
        
        int size = confirmed.size();
        assertEquals("Map should be same size as HashMap", 
                     size, map.size());
        assertEquals("keySet should be same size as HashMap's",
                     size, keySet.size());
        assertEquals("entrySet should be same size as HashMap's",
                     size, entrySet.size());
        assertEquals("values should be same size as HashMap's",
                     size, values.size());
        
        boolean empty = confirmed.isEmpty();
        assertEquals("Map should be empty if HashMap is", 
                     empty, map.isEmpty());
        assertEquals("keySet should be empty if HashMap is", 
                     empty, keySet.isEmpty());
        assertEquals("entrySet should be empty if HashMap is", 
                     empty, entrySet.isEmpty());
        assertEquals("values should be empty if HashMap is", 
                     empty, values.isEmpty());
        
        assertTrue("entrySet should contain all HashMap's elements",
                   entrySet.containsAll(confirmed.entrySet()));
        assertTrue("keySet should contain all HashMap's elements",
                   keySet.containsAll(confirmed.keySet()));
        assertTrue("values should contain all HashMap's elements",
                   values.containsAll(confirmed.values()));
        
        assertEquals("hashCodes should be the same",
                     confirmed.hashCode(), map.hashCode());
        assertEquals("entrySet hashCodes should be the same", 
                     confirmed.entrySet().hashCode(), entrySet.hashCode());
        assertEquals("keySet hashCodes should be the same", 
                     confirmed.keySet().hashCode(), keySet.hashCode());
        assertEquals("values hashCodes should be the same", 
                     bag1.hashCode(), bag2.hashCode());
    }


    /**
     *  Erases any leftover instance variables by setting them to null.
     */
    protected void tearDown() {
        map = null;
        keySet = null;
        entrySet = null;
        values = null;
        confirmed = null;
    }

}
