/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
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
 */
package org.apache.commons.collections.iterators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.Unmodifiable;

/**
 * Tests the UnmodifiableIterator.
 * 
 * @version $Revision: 1.2 $ $Date: 2004/01/14 21:34:26 $
 * 
 * @author Stephen Colebourne
 */
public class TestUnmodifiableIterator extends AbstractTestIterator {

    protected String[] testArray = { "One", "Two", "Three" };
    protected List testList = new ArrayList(Arrays.asList(testArray));

    public static Test suite() {
        return new TestSuite(TestUnmodifiableIterator.class);
    }

    public TestUnmodifiableIterator(String testName) {
        super(testName);
    }

    public Iterator makeEmptyIterator() {
        return UnmodifiableIterator.decorate(Collections.EMPTY_LIST.iterator());
    }

    public Iterator makeFullIterator() {
        return UnmodifiableIterator.decorate(testList.iterator());
    }

    public boolean supportsRemove() {
        return false;
    }

    //-----------------------------------------------------------------------
    public void testIterator() {
        assertTrue(makeEmptyIterator() instanceof Unmodifiable);
    }
    
    public void testDecorateFactory() {
        Iterator it = makeFullIterator();
        assertSame(it, UnmodifiableIterator.decorate(it));
        
        it = testList.iterator();
        assertTrue(it != UnmodifiableIterator.decorate(it));
        
        try {
            UnmodifiableIterator.decorate(null);
            fail();
        } catch (IllegalArgumentException ex) {}
    }

}
