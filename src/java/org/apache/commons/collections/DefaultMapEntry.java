/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/DefaultMapEntry.java,v 1.2 2002/02/10 08:07:42 jstrachan Exp $
 * $Revision: 1.2 $
 * $Date: 2002/02/10 08:07:42 $
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

import java.util.Map;

/** A default implementation of {@link Map.Entry Map.Entry}
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @author <a href="mailto:michael@iammichael.org">Michael Smith</a>
  */
  
public class DefaultMapEntry implements Map.Entry {
    
    private Object key;
    private Object value;
    
    public DefaultMapEntry() {
    }

    public DefaultMapEntry(Object key, Object value) {
        this.key = key;
        this.value = value;
    }

    /**
     *  Implemented per API documentation of {@link Map.Entry#equals(Object)}
     **/
    public boolean equals(Object o) {
        if( o == null ) return false;
        if( o == this ) return true;        

        if ( ! (o instanceof Map.Entry ) )
            return false;
        Map.Entry e2 = (Map.Entry)o;    
        return ((getKey() == null ?
                 e2.getKey() == null : getKey().equals(e2.getKey())) &&
                (getValue() == null ?
                 e2.getValue() == null : getValue().equals(e2.getValue())));
    }
     
     
    /**
     *  Implemented per API documentation of {@link Map.Entry#hashCode()}
     **/
    public int hashCode() {
        return ( ( getKey() == null ? 0 : getKey().hashCode() ) ^
                 ( getValue() == null ? 0 : getValue().hashCode() ) ); 
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
