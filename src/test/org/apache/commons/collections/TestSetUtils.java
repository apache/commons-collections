/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/TestSetUtils.java,v 1.11 2003/10/05 21:23:21 scolebourne Exp $
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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Test;

import org.apache.commons.collections.decorators.PredicatedSet;

/**
 * Tests for SetUtils.
 * 
 * @version $Revision: 1.11 $ $Date: 2003/10/05 21:23:21 $
 * 
 * @author Stephen Colebourne
 * @author Neil O'Toole
 * @author Matthew Hawthorne
 */
public class TestSetUtils extends BulkTest {

    public TestSetUtils(String name) {
        super(name);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestSetUtils.class);
    }

    public void testNothing() {
    }
    
    public void testpredicatedSet() {
        Predicate predicate = new Predicate() {
            public boolean evaluate(Object o) {
                return o instanceof String;
            }
        };
        Set set = SetUtils.predicatedSet(new HashSet(), predicate);
        assertTrue("returned object should be a PredicatedSet",
            set instanceof PredicatedSet);
        try {
            set = SetUtils.predicatedSet(new HashSet(), null);
            fail("Expecting IllegalArgumentException for null predicate.");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            set = SetUtils.predicatedSet(null, predicate);
            fail("Expecting IllegalArgumentException for null set.");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }


    public BulkTest bulkTestTypedSet() {
        return new TestTypedCollection("") {

            public Collection typedCollection() {
                Class type = getType();
                return SetUtils.typedSet(new HashSet(), type);
            }

            public BulkTest bulkTestAll() {
                return new AbstractTestSet("") {
                    public Set makeEmptySet() {
                        return (Set)typedCollection();
                    }

                    public Object[] getFullElements() {
                        return getFullNonNullStringElements();
                    }

                    public Object[] getOtherElements() {
                        return getOtherNonNullStringElements();
                    }
                };
            }
        };
    }
     
    public void testEquals() {
        Collection data = Arrays.asList( new String[] { "a", "b", "c" });
        
        Set a = new HashSet( data );
        Set b = new HashSet( data );
        
        assertEquals(true, a.equals(b));
        assertEquals(true, SetUtils.isEqualSet(a, b));
        a.clear();
        assertEquals(false, SetUtils.isEqualSet(a, b));
        assertEquals(false, SetUtils.isEqualSet(a, null));
        assertEquals(false, SetUtils.isEqualSet(null, b));
        assertEquals(true, SetUtils.isEqualSet(null, null));
    }
    
    public void testHashCode() {
        Collection data = Arrays.asList( new String[] { "a", "b", "c" });
            
        Set a = new HashSet( data );
        Set b = new HashSet( data );
        
        assertEquals(true, a.hashCode() == b.hashCode());
        assertEquals(true, a.hashCode() == SetUtils.hashCodeForSet(a));
        assertEquals(true, b.hashCode() == SetUtils.hashCodeForSet(b));
        assertEquals(true, SetUtils.hashCodeForSet(a) == SetUtils.hashCodeForSet(b));
        a.clear();
        assertEquals(false, SetUtils.hashCodeForSet(a) == SetUtils.hashCodeForSet(b));
        assertEquals(0, SetUtils.hashCodeForSet(null));
    }   

}
