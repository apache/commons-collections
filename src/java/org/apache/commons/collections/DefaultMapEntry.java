/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/DefaultMapEntry.java,v 1.15 2003/12/05 20:23:57 scolebourne Exp $
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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

/**
 * A default implementation of {@link java.util.Map.Entry}
 *
 * @since Commons Collections 1.0
 * @version $Revision: 1.15 $ $Date: 2003/12/05 20:23:57 $
 * 
 * @author James Strachan
 * @author Michael A. Smith
 * @author Neil O'Toole
 * @author Stephen Colebourne
 * 
 * @deprecated Use the version in the keyvalue subpackage.
 */
public class DefaultMapEntry implements Map.Entry {
    
    /** The key */
    private Object key;
    /** The value */
    private Object value;
    
    /**
     * Constructs a new <code>DefaultMapEntry</code> with a null key
     * and null value.
     */
    public DefaultMapEntry() {
        super();
    }

    /**
     * Constructs a new <code>DefaultMapEntry</code> with the given
     * key and given value.
     *
     * @param entry  the entry to copy, must not be null
     * @throws NullPointerException if the entry is null
     */
    public DefaultMapEntry(Map.Entry entry) {
        super();
        this.key = entry.getKey();
        this.value = entry.getValue();
    }

    /**
     * Constructs a new <code>DefaultMapEntry</code> with the given
     * key and given value.
     *
     * @param key  the key for the entry, may be null
     * @param value  the value for the entry, may be null
     */
    public DefaultMapEntry(Object key, Object value) {
        super();
        this.key = key;
        this.value = value;
    }

    // Map.Entry interface
    //-------------------------------------------------------------------------
    /**
     * Gets the key from the Map Entry.
     *
     * @return the key 
     */
    public Object getKey() {
        return key;
    }

    /**
     * Sets the key stored in this Map Entry.
     * <p>
     * This Map Entry is not connected to a Map, so only the local data is changed.
     *
     * @param key  the new key
     */
    public void setKey(Object key) {
        this.key = key;
    }
    
    /**
     * Gets the value from the Map Entry.
     *
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /** 
     * Sets the value stored in this Map Entry.
     * <p>
     * This Map Entry is not connected to a Map, so only the local data is changed.
     *
     * @param value  the new value
     * @return the previous value
     */
    public Object setValue(Object value) {
        Object answer = this.value;
        this.value = value;
        return answer;
    }

    // Basics
    //-----------------------------------------------------------------------
    /**
     * Compares this Map Entry with another Map Entry.
     * <p>
     * Implemented per API documentation of {@link java.util.Map.Entry#equals(Object)}
     * 
     * @param obj  the object to compare to
     * @return true if equal key and value
     */
    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Map.Entry == false) {
            return false;
        }
        Map.Entry other = (Map.Entry) obj;
        return
            (getKey() == null ? other.getKey() == null : getKey().equals(other.getKey())) &&
            (getValue() == null ? other.getValue() == null : getValue().equals(other.getValue()));
    }
     
    /**
     * Gets a hashCode compatible with the equals method.
     * <p>
     * Implemented per API documentation of {@link java.util.Map.Entry#hashCode()}
     * 
     * @return a suitable hashcode
     */
    public final int hashCode() {
        return (getKey() == null ? 0 : getKey().hashCode()) ^
               (getValue() == null ? 0 : getValue().hashCode()); 
    }

    /**
     * Written to match the output of the Map.Entry's used in 
     * a {@link java.util.HashMap}. 
     * @since 3.0
     */
    public String toString() {
        return ""+getKey()+"="+getValue();
    }

}
