/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/list/TestTransformedList.java,v 1.2 2003/11/16 22:15:09 scolebourne Exp $
 * ====================================================================
 *
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
package org.apache.commons.collections.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.collection.TestTransformedCollection;

/**
 * Extension of {@link TestList} for exercising the {@link TransformedList}
 * implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.2 $ $Date: 2003/11/16 22:15:09 $
 * 
 * @author Stephen Colebourne
 */
public class TestTransformedList extends AbstractTestList {
    
    public TestTransformedList(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestTransformedList.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestTransformedList.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    public Collection makeConfirmedCollection() {
        return new ArrayList();
    }

    protected Collection makeConfirmedFullCollection() {
        List list = new ArrayList();
        list.addAll(Arrays.asList(getFullElements()));
        return list;
    }
    
    public List makeEmptyList() {
        return TransformedList.decorate(new ArrayList(), TestTransformedCollection.NOOP_TRANSFORMER);
    }

    protected List makeFullList() {
        List list = new ArrayList();
        list.addAll(Arrays.asList(getFullElements()));
        return TransformedList.decorate(list, TestTransformedCollection.NOOP_TRANSFORMER);
    }
    
    public void testTransformedList() {
        List list = TransformedList.decorate(new ArrayList(), TestTransformedCollection.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(0, list.size());
        Object[] els = new Object[] {"1", "3", "5", "7", "2", "4", "6"};
        for (int i = 0; i < els.length; i++) {
            list.add(els[i]);
            assertEquals(i + 1, list.size());
            assertEquals(true, list.contains(new Integer((String) els[i])));
            assertEquals(false, list.contains(els[i]));
        }
        
        assertEquals(false, list.remove(els[0]));
        assertEquals(true, list.remove(new Integer((String) els[0])));
        
        list.clear();
        for (int i = 0; i < els.length; i++) {
            list.add(0, els[i]);
            assertEquals(i + 1, list.size());
            assertEquals(new Integer((String) els[i]), list.get(0));
        }
        
        list.set(0, "22");
        assertEquals(new Integer(22), list.get(0));
        
        ListIterator it = list.listIterator();
        it.next();
        it.set("33");
        assertEquals(new Integer(33), list.get(0));
        it.add("44");
        assertEquals(new Integer(44), list.get(1));
        
        List adds = new ArrayList();
        adds.add("1");
        adds.add("2");
        list.clear();
        list.addAll(adds);
        assertEquals(new Integer(1), list.get(0));
        assertEquals(new Integer(2), list.get(1));
        
        adds.clear();
        adds.add("3");
        list.addAll(1, adds);
        assertEquals(new Integer(1), list.get(0));
        assertEquals(new Integer(3), list.get(1));
        assertEquals(new Integer(2), list.get(2));
    }
}
