/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.commons.collections;

import java.util.*;

/** A default implementation of {@link Map.Entry Map.Entry}
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  */
  
public class DefaultMapEntry implements Map.Entry {
    
    private Object              key;
    private Object              value;

    protected static final int HASH_CODE_SEED = 123456789;
    
    
    public DefaultMapEntry() {
    }

    public DefaultMapEntry(Object key, Object value) {
        this.key = key;
        this.value = value;
    }

    public boolean equals(Object o) {
        if ( ! (o instanceof DefaultMapEntry ) )
            return false;
        DefaultMapEntry that = (DefaultMapEntry) o;

        if ( ( this.key == null && that.key == null )
            || ( this.key != null && this.key.equals( that.key ) ) )
        {
            if ( ( this.value == null && that.value == null )
                || ( this.value != null && this.value.equals( that.value ) ) )
            {
                return true;
            }
        }
        return false;
    }

    public boolean equals( DefaultMapEntry that ) {
        if ( ( key == null && that.key == null ) || ( key != null && key.equals( that.key ) ) ) {
            return ( value == null && value == null ) || ( value != null && value.equals( that.value ) );
        }
        return false;
    }
    
    public int hashCode() {
        int answer = HASH_CODE_SEED;
        if ( key != null ) {
            answer ^= key.hashCode();
        }
        if ( value != null ) {
            answer ^= value.hashCode();
        }
        return answer;
    }


    // Map.Entry interface
    //-------------------------------------------------------------------------
    public Object getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    // Properties
    //-------------------------------------------------------------------------    
    public void setKey(Object key) {
        this.key = key;
    }
    
    /** Note that this method only sets the local reference inside this object and
      * does not modify the original Map.
      *
      * @return the old value of the value
      * @param value the new value
      */
    public Object setValue(Object value) {
        Object answer = this.value;
        this.value = value;
        return answer;
    }

}
