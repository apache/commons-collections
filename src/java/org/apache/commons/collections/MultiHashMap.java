/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/MultiHashMap.java,v 1.4 2002/06/12 03:59:15 mas Exp $
 * $Revision: 1.4 $
 * $Date: 2002/06/12 03:59:15 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
import java.io.*;

/** see MultiMap for details of an important semantic difference
 * between this and a typical HashMap
 *
 * @since 2.0
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
