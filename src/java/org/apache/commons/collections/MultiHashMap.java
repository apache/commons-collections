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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
/** 
 * <code>MultiHashMap</code> is the default implementation of the 
 * {@link org.apache.commons.collections.MultiMap MultiMap} interface.
 * A <code>MultiMap</code> is a Map with slightly different semantics.
 * Instead of returning an Object, it returns a Collection.
 * So for example, you can put( key, new Integer(1) ); 
 * and then a Object get( key ); will return you a Collection 
 * instead of an Integer.
 *
 * @since 2.0
 * @author Christopher Berry
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @author Steve Downey
 * @author Stephen Colebourne
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
            for ( int ii=0; ii < values.length; ii++ ) {
                returnList.add( values[ii] );
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
