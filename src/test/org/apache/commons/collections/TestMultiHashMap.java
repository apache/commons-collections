/*
 * Copyright 1999-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * Unit Tests for <code>MultiHashMap</code>.
 *
 */
public class TestMultiHashMap extends TestMap
{
    public TestMultiHashMap(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        return new TestSuite(TestMultiHashMap.class);
    }

    public static void main(String args[])
    {
        String[] testCaseName = { TestMultiHashMap.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }

    // MutltiHashMap was introduced in Collections 2.x
    public int getCompatibilityVersion() {
        return 2;
    }

    public Map makeEmptyMap() {
        return new MultiHashMap();
    }
    
    //----------------------------
    //          Tests
    //----------------------------
    public void testPutNGet()
    {
        MultiHashMap map = new MultiHashMap();
        loadMap( map );
        checkMap( map );
        
        assertTrue( map.get(new Integer(99)) == null );
        
        map.clear();
        assertTrue( map.size() == 0 );
    }
    
    public void testContainsValue()
    {
        MultiHashMap map = new MultiHashMap();
        loadMap( map );
        
        assertTrue( map.containsValue( "uno" ) );
        assertTrue( map.containsValue( "quatro" ) );
        assertTrue( map.containsValue( "two" ) );
        
        assertTrue( ! map.containsValue( "uggaBugga" ) );
        
        map.clear();
    }
    
    public void testValues()
    {
        MultiHashMap map = new MultiHashMap();
        loadMap( map );
        
        Collection vals = map.values();
        assertTrue( vals.size() == getFullSize() );
        
        map.clear();
    }

    
    static private class MapPair
    {
        MapPair( int key, String val )
        {
            mKey = new Integer( key );
            mValue = val;
        }
        
        Integer mKey = null;
        String mValue = null;
    }
    
    static private MapPair[][] sMapPairs =
    {
        {new MapPair(0,"zero")},
        {new MapPair(1,"one"), new MapPair(1,"ONE"), new MapPair(1,"uno")},
        {new MapPair(2,"two"), new MapPair(2,"two") },
        {new MapPair(3,"three"), new MapPair(3,"THREE"), new MapPair(3,"tres")},
        {new MapPair(4,"four"), new MapPair(4,"quatro")}
    };
    
    private void loadMap( MultiHashMap map )
    {
        // Set up so that we load the keys "randomly"
        // (i.e. we don't want to load int row-order, so that all like keys
        // load together. We want to mix it up...)
        
        int numRows = sMapPairs.length;
        int maxCols = 0;
        for( int ii=0; ii < sMapPairs.length; ii++ ){
            if ( sMapPairs[ii].length > maxCols )
                maxCols = sMapPairs[ii].length;
        }
        for( int ii=0; ii < maxCols; ii++ ){
            for( int jj=0; jj < numRows; jj++ ){
                if ( ii < sMapPairs[jj].length ) {
                    map.put( sMapPairs[jj][ii].mKey, sMapPairs[jj][ii].mValue);
                    //---------------------------------------------------------
                }
            }
        }
        assertTrue( map.size() == sMapPairs.length );
    }
    
    private void checkMap( MultiHashMap map )
    {
        for( int ii=0; ii < sMapPairs.length; ii++ ){
            checkKeyList( map, ii );
        }
    }
    
    private void checkKeyList( MultiHashMap map, int index )
    {
        assertTrue( index < sMapPairs.length );
        Integer key = sMapPairs[index][0].mKey ;
        
        Object obj = map.get( key );
        //--------------------------
        
        assertTrue( obj != null );
        assertTrue( obj instanceof Collection );
        Collection keyList = (Collection)obj;
        
        assertTrue( keyList.size()  == sMapPairs[index].length );
        Iterator iter = keyList.iterator();
        while ( iter.hasNext() ) {
            Object oval = iter.next();
            assertTrue( oval != null );
            assertTrue( oval instanceof String );
            String val = (String)oval;
            boolean foundIt = false;
            for( int ii=0; ii < sMapPairs[index].length; ii++ ){
                if( val.equals( sMapPairs[index][ii].mValue ) )
                    foundIt = true;
            }
            assertTrue( foundIt );
        }
    }
    
    public int getFullSize()
    {
        int len = 0;
        for( int ii=0; ii < sMapPairs.length; ii++ ){
            len += sMapPairs[ii].length;
        }
        return len;
    }
    

    public void testEntrySetIterator() {
    }
    public void testEntrySetContainsProperMappings() {
    }
    public void testEntrySetIteratorHasProperMappings() {
        // override and ignore test -- it will fail when verifying the iterator for
        // the set contains the right value -- we're not returning the value, we're
        // returning a collection.
        // TODO: re-implement this test to ensure the values of the iterator match
        // the proper collection rather than the value the superclass is checking
        // for.
        return;
    }

    // Next methods are overriden because MultiHashMap values are always a
    // collection, and deviate from the Map contract because of this.

    // TODO: implement the tests to ensure that Map.get(Object) returns the
    // appropriate collection of values

    public void testMapGet() {
    }

    public void testMapPut() {
    }

    public void testMapPutAll() {
    }

    public void testMapRemove() {
    }

    public void testMapEquals() {
    }

    public void testMapHashCode() {
    }

    // The verification for the map and its entry set must also be overridden
    // because the values are not going to be the same as the values in the
    // confirmed map (they're going to be collections of values instead).
    public void verifyMap() {
        // TODO: implement test to ensure that map is the same as confirmed if
        // its values were converted into collections.
    }

    public void verifyEntrySet() {
        // TODO: implement test to ensure that each entry is the same as one in
        // the confirmed map, but with the value wrapped in a collection.
    }

    // The verification method must be overridden because MultiHashMap's
    // values() is not properly backed by the map (Bug 9573).

    public void verifyValues() {
        // update the values view to the latest version, then proceed to verify
        // as usual.  
        values = map.values();
        super.verifyValues();
    }
}
