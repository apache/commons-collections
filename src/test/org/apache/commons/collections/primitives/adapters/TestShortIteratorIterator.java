/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/primitives/adapters/Attic/TestShortIteratorIterator.java,v 1.2 2003/08/31 17:28:38 scolebourne Exp $
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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

package org.apache.commons.collections.primitives.adapters;

import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.iterators.TestIterator;
import org.apache.commons.collections.primitives.ArrayShortList;
import org.apache.commons.collections.primitives.ShortList;

/**
 * @version $Revision: 1.2 $ $Date: 2003/08/31 17:28:38 $
 * @author Rodney Waldhoff
 */
public class TestShortIteratorIterator extends TestIterator {

    // conventional
    // ------------------------------------------------------------------------

    public TestShortIteratorIterator(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestShortIteratorIterator.class);
    }

    // collections testing framework
    // ------------------------------------------------------------------------

    public Object makeObject() {
        return makeFullIterator();
    }
    
    public Iterator makeEmptyIterator() {
        return ShortIteratorIterator.wrap(makeEmptyShortList().iterator());
    }
    
    public Iterator makeFullIterator() {
        return ShortIteratorIterator.wrap(makeFullShortList().iterator());
    }

    protected ShortList makeEmptyShortList() {
        return new ArrayShortList();
    }
    
    protected ShortList makeFullShortList() {
        ShortList list = makeEmptyShortList();
        short[] elts = getFullElements();
        for(int i=0;i<elts.length;i++) {
            list.add((short)elts[i]);
        }
        return list;
    }
    
    public short[] getFullElements() {
        return new short[] { (short)0, (short)1, (short)2, (short)3, (short)4, (short)5, (short)6, (short)7, (short)8, (short)9 };
    }
    
    // tests
    // ------------------------------------------------------------------------


}
