/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/primitives/Attic/TestAbstractLongArrayList.java,v 1.10 2003/11/16 22:15:11 scolebourne Exp $
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002-2003 The Apache Software Foundation.  All rights
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

package org.apache.commons.collections.primitives;

import java.util.List;

import org.apache.commons.collections.list.AbstractTestList;

/**
 * @version $Revision: 1.10 $ $Date: 2003/11/16 22:15:11 $
 * @author Rodney Waldhoff
 * @deprecated this should have been removed along with the others
 */
public abstract class TestAbstractLongArrayList extends AbstractTestList {

    //------------------------------------------------------------ Conventional

    public TestAbstractLongArrayList(String testName) {
        super(testName);
    }

    //---------------------------------------------------------------- Abstract

    abstract protected AbstractLongList createList();

    //------------------------------------------------------- TestList interface

    public List makeEmptyList() {
        return createList();
    }

    //------------------------------------------------------------------- Tests

    public void testAddGet() {
        AbstractLongList list = createList();
        for(long i=0L;i<1000L;i++) {
            list.addLong(i);
        }
        for(int i=0;i<1000;i++) {
            assertEquals((long)i,list.getLong(i));
        }
    }

    public void testAddGetLargeValues() {
        AbstractLongList list = createList();
        for(long i=0L;i<1000L;i++) {
            long value = ((long)(Integer.MAX_VALUE));
            value += i;
            list.addLong(value);
        }
        for(long i=0L;i<1000L;i++) {
            long value = ((long)(Integer.MAX_VALUE));
            value += i;
            assertEquals(value,list.getLong((int)i));
        }
    }


    /**
     *  Returns an array of Long objects for testing.
     */
    protected Object[] getFullElements() {
        Long[] result = new Long[19];
        for (int i = 0; i < result.length; i++) {
            result[i] = new Long(i + 19);
        }
        return result;
    }


    /**
     *  Returns an array of Long objects for testing.
     */
    protected Object[] getOtherElements() {
        Long[] result = new Long[16];
        for (int i = 0; i < result.length; i++) {
            result[i] = new Long(i + 48);
        }
        return result;
    }

    public void testCanonicalEmptyCollectionExists() {
    }


    public void testCanonicalFullCollectionExists() {
    }

    public void testEmptyListCompatibility() {
    }

    public void testFullListCompatibility() {
    }

    public void testCollectionIteratorFailFast() {
    }

    public void testListSubListFailFastOnAdd() {
    }

    public void testListSubListFailFastOnRemove() {
    }



}

