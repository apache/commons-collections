/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/primitives/Attic/TestAbstractShortArrayList.java,v 1.2 2002/06/21 04:01:31 mas Exp $
 * $Revision: 1.2 $
 * $Date: 2002/06/21 04:01:31 $
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

package org.apache.commons.collections.primitives;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.collections.TestList;
import java.util.List;

/**
 * @version $Revision: 1.2 $ $Date: 2002/06/21 04:01:31 $
 * @author Rodney Waldhoff
 */
public abstract class TestAbstractShortArrayList extends TestList {

    //------------------------------------------------------------ Conventional

    public TestAbstractShortArrayList(String testName) {
        super(testName);
    }

    //---------------------------------------------------------------- Abstract

    abstract protected AbstractShortArrayList createList();

    //------------------------------------------------------- TestList interface

    public List makeEmptyList() {
        return createList();
    }

    //------------------------------------------------------------------- Tests

    public void testAddGet() {
        AbstractShortArrayList list = createList();
        for(short i=0;i<100;i++) {
            list.addShort(i);
        }
        for(int i=0;i<100;i++) {
            assertEquals((short)i,list.getShort(i));
        }
    }

    public void testAddGetLargeValues() {
        AbstractShortArrayList list = createList();
        for(short i=128;i<256;i++) {
            list.addShort(i);
        }
        for(int i=0;i<128;i++) {
            assertEquals((short)(i+128),list.getShort(i));
        }
    }

    /**
     *  Returns an array of Short objects for testing.
     */
    protected Object[] getFullElements() {
        Short[] result = new Short[19];
        for (int i = 0; i < result.length; i++) {
            result[i] = new Short((short)(i + 19));
        }
        return result;
    }


    /**
     *  Returns an array of Short objects for testing.
     */
    protected Object[] getOtherElements() {
        Short[] result = new Short[16];
        for (int i = 0; i < result.length; i++) {
            result[i] = new Short((short)(i + 48));
        }
        return result;
    }

    // TODO:  Create canonical primitive lists in CVS

    public void testCanonicalEmptyCollectionExists() {
    }


    public void testCanonicalFullCollectionExists() {
    }

    public void testEmptyListCompatibility() {
    }

    public void testFullListCompatibility() {
    }

    // TODO: Make primitive lists fail fast

    public void testCollectionIteratorFailFast() {
    }

    public void testListSubListFailFastOnAdd() {
    }

    public void testListSubListFailFastOnRemove() {
    }




}

