/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/keyvalue/TestUnmodifiableMapEntry.java,v 1.1 2003/12/05 20:23:57 scolebourne Exp $
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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.Unmodifiable;

/**
 * Test the UnmodifiableMapEntry class.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $ $Date: 2003/12/05 20:23:57 $
 * 
 * @author Neil O'Toole
 */
public class TestUnmodifiableMapEntry extends AbstractTestMapEntry {

    public TestUnmodifiableMapEntry(String testName) {
        super(testName);

    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestUnmodifiableMapEntry.class);
    }

    public static Test suite() {
        return new TestSuite(TestUnmodifiableMapEntry.class);
    }

    //-----------------------------------------------------------------------
    /**
     * Make an instance of Map.Entry with the default (null) key and value.
     * Subclasses should override this method to return a Map.Entry
     * of the type being tested.
     */
    public Map.Entry makeMapEntry() {
        return new UnmodifiableMapEntry(null, null);
    }

    /**
     * Make an instance of Map.Entry with the specified key and value.
     * Subclasses should override this method to return a Map.Entry
     * of the type being tested.
     */
    public Map.Entry makeMapEntry(Object key, Object value) {
        return new UnmodifiableMapEntry(key, value);
    }

    //-----------------------------------------------------------------------
    /**
     * Subclasses should override this method.
     *
     */
    public void testConstructors() {
        // 1. test key-value constructor
        Map.Entry entry = new UnmodifiableMapEntry(key, value);
        assertSame(key, entry.getKey());
        assertSame(value, entry.getValue());

        // 2. test pair constructor
        KeyValue pair = new DefaultKeyValue(key, value);
        entry = new UnmodifiableMapEntry(pair);
        assertSame(key, entry.getKey());
        assertSame(value, entry.getValue());

        // 3. test copy constructor
        Map.Entry entry2 = new UnmodifiableMapEntry(entry);
        assertSame(key, entry2.getKey());
        assertSame(value, entry2.getValue());

        assertTrue(entry instanceof Unmodifiable);
    }

    public void testAccessorsAndMutators() {
        Map.Entry entry = makeMapEntry(key, value);

        assertSame(key, entry.getKey());
        assertSame(value, entry.getValue());

        // check that null doesn't do anything funny
        entry = makeMapEntry(null, null);
        assertSame(null, entry.getKey());
        assertSame(null, entry.getValue());
    }

    public void testSelfReferenceHandling() {
        // block
    }

    public void testUnmodifiable() {
        Map.Entry entry = makeMapEntry();
        try {
            entry.setValue(null);
            fail();

        } catch (UnsupportedOperationException ex) {}
    }

}
