/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/TestMultiHashMap.java,v 1.2 2002/02/22 02:18:50 mas Exp $
 * $Revision: 1.2 $
 * $Date: 2002/02/22 02:18:50 $
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

import java.util.*;

import junit.framework.Test;
import junit.framework.TestCase;
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
}
