/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/java/org/apache/commons/collections/keyvalue/DefaultKeyValue.java,v 1.2 2004/01/08 22:37:30 scolebourne Exp $
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
package org.apache.commons.collections.keyvalue;

import java.util.Map;

import org.apache.commons.collections.KeyValue;

/**
 * A mutable KeyValue pair that does not implement MapEntry.
 * <p>
 * Note that a <code>DefaultKeyValue</code> instance may not contain
 * itself as a key or value.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.2 $ $Date: 2004/01/08 22:37:30 $
 * 
 * @author James Strachan
 * @author Michael A. Smith
 * @author Neil O'Toole
 * @author Stephen Colebourne
 */
public class DefaultKeyValue extends AbstractKeyValue {

    /**
     * Constructs a new pair with a null key and null value.
     */
    public DefaultKeyValue() {
        super(null, null);
    }

    /**
     * Constructs a new pair with the specified key and given value.
     *
     * @param key  the key for the entry, may be null
     * @param value  the value for the entry, may be null
     */
    public DefaultKeyValue(final Object key, final Object value) {
        super(key, value);
    }

    /**
     * Constructs a new pair from the specified KeyValue.
     *
     * @param pair  the pair to copy, must not be null
     * @throws NullPointerException if the entry is null
     */
    public DefaultKeyValue(final KeyValue pair) {
        super(pair.getKey(), pair.getValue());
    }

    /**
     * Constructs a new pair from the specified MapEntry.
     *
     * @param entry  the entry to copy, must not be null
     * @throws NullPointerException if the entry is null
     */
    public DefaultKeyValue(final Map.Entry entry) {
        super(entry.getKey(), entry.getValue());
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the key.
     *
     * @param key  the new key
     * @return the old key
     * @throws IllegalArgumentException if key is this object
     */
    public Object setKey(final Object key) {
        if (key == this) {
            throw new IllegalArgumentException("DefaultKeyValue may not contain itself as a key.");
        }

        final Object old = this.key;
        this.key = key;
        return old;
    }

    /** 
     * Sets the value.
     *
     * @return the old value of the value
     * @param value the new value
     * @throws IllegalArgumentException if value is this object
     */
    public Object setValue(final Object value) {
        if (value == this) {
            throw new IllegalArgumentException("DefaultKeyValue may not contain itself as a value.");
        }

        final Object old = this.value;
        this.value = value;
        return old;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a new <code>Map.Entry</code> object with key and value from this pair.
     * 
     * @return a MapEntry instance
     */
    public Map.Entry toMapEntry() {
        return new DefaultMapEntry(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this Map Entry with another Map Entry.
     * <p>
     * Returns true if the compared object is also a <code>DefaultKeyValue</code>,
     * and its key and value are equal to this object's key and value.
     * 
     * @param obj  the object to compare to
     * @return true if equal key and value
     */
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof DefaultKeyValue == false) {
            return false;
        }

        DefaultKeyValue other = (DefaultKeyValue) obj;
        return 
            (getKey() == null ? other.getKey() == null : getKey().equals(other.getKey())) &&
            (getValue() == null ? other.getValue() == null : getValue().equals(other.getValue()));
    }

    /**
     * Gets a hashCode compatible with the equals method.
     * <p>
     * Implemented per API documentation of {@link java.util.Map.Entry#hashCode()},
     * however subclasses may override this.
     * 
     * @return a suitable hash code
     */
    public int hashCode() {
        return (getKey() == null ? 0 : getKey().hashCode()) ^
               (getValue() == null ? 0 : getValue().hashCode());
    }

}
