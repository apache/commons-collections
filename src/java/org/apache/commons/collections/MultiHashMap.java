/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.commons.collections;

import java.util.*;
import java.io.*;

/** see MultiMap for details of an important semantic difference
 * between this and a typical HashMap
 *
 * @author Christopher Berry
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 */
public class MultiHashMap extends HashMap implements MultiMap
{
    //----------------- Data
    private static int sCount = 0;
    private String mName = null;
    
    public MultiHashMap()
    {
        super();
        setName();
    }
    
    public MultiHashMap( int initialCapacity )
    {
        super( initialCapacity );
        setName();
    }
    
    public MultiHashMap(int initialCapacity, float loadFactor )
    {
        super( initialCapacity, loadFactor);
        setName();
    }
    
    public MultiHashMap( Map mapToCopy )
    {
        super( mapToCopy );
    }
    
    private void setName()
    {
        sCount++;
        mName = "MultiMap-" + sCount;
    }
    
    public String getName()
    { return mName; }
    
    public Object put( Object key, Object value )
    {
        // NOTE:: put might be called during deserialization !!!!!!
        //        so we must provide a hook to handle this case
        //        This means that we cannot make MultiMaps of ArrayLists !!!
        
        if ( value instanceof ArrayList ) {
            return ( super.put( key, value ) );
        }
        
        ArrayList keyList = (ArrayList)(super.get( key ));
        if ( keyList == null ) {
            keyList = new ArrayList(10);
            
            super.put( key, keyList );
        }
        
        boolean results = keyList.add( value );
        
        return ( results ? value : null );
    }
    
    public boolean containsValue( Object value )
    {
        Set pairs = super.entrySet();
        
        if ( pairs == null )
            return false;
        
        Iterator pairsIterator = pairs.iterator();
        while ( pairsIterator.hasNext() ) {
            Map.Entry keyValuePair = (Map.Entry)(pairsIterator.next());
            ArrayList list = (ArrayList)(keyValuePair.getValue());
            if( list.contains( value ) )
                return true;
        }
        return false;
    }
    
    public Object remove( Object key, Object item )
    {
        ArrayList valuesForKey = (ArrayList) super.get( key );
        
        if ( valuesForKey == null )
            return null;
        
        valuesForKey.remove( item );
        return item;
    }
    
    public void clear()
    {
        Set pairs = super.entrySet();
        Iterator pairsIterator = pairs.iterator();
        while ( pairsIterator.hasNext() ) {
            Map.Entry keyValuePair = (Map.Entry)(pairsIterator.next());
            ArrayList list = (ArrayList)(keyValuePair.getValue());
            list.clear();
        }
        super.clear();
    }
    
    public void putAll( Map mapToPut )
    {
        super.putAll( mapToPut );
    }
    
    public Collection values()
    {
        ArrayList returnList = new ArrayList( super.size() );
        
        Set pairs = super.entrySet();
        Iterator pairsIterator = pairs.iterator();
        while ( pairsIterator.hasNext() ) {
            Map.Entry keyValuePair = (Map.Entry)(pairsIterator.next());
            ArrayList list = (ArrayList)(keyValuePair.getValue());
            
            Object[] values = list.toArray();
            for( int ii=0; ii < values.length; ii++ ) {
                boolean successfulAdd = returnList.add( values[ii] );
            }
        }
        return returnList;
    }
    
    // FIXME:: do we need to implement this??
    // public boolean equals( Object obj ) {}
    
    // --------------- From Cloneable
    public Object clone()
    {
        MultiHashMap obj = (MultiHashMap)(super.clone());
        obj.mName = mName;
        return obj;
    }
    
}
