/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/TestEnumerationUtils.java,v 1.2 2003/10/28 18:56:12 ggregory Exp $
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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import junit.framework.Assert;
import junit.framework.Test;

/**
 * Tests EnumerationUtils.
 * 
 * @author <a href="mailto:ggregory@seagullsw.com">Gary Gregory</a>
 * @version $Id: TestEnumerationUtils.java,v 1.2 2003/10/28 18:56:12 ggregory Exp $
 */
public class TestEnumerationUtils extends BulkTest {

    public TestEnumerationUtils(String name) {
        super(name);
    }

    public static final String TO_LIST_FIXTURE = "this is a test";
    
    public void testToListWithStringTokenizer() {
        List expectedList1 = new ArrayList();
        StringTokenizer st = new StringTokenizer(TO_LIST_FIXTURE);
             while (st.hasMoreTokens()) {
                 expectedList1.add(st.nextToken());
             }        
        List expectedList2 = new ArrayList();
        expectedList2.add("this");
        expectedList2.add("is");
        expectedList2.add("a");
        expectedList2.add("test");
        List actualList = EnumerationUtils.toList(new StringTokenizer(TO_LIST_FIXTURE));
        Assert.assertEquals(expectedList1, expectedList2);
        Assert.assertEquals(expectedList1, actualList);
        Assert.assertEquals(expectedList2, actualList);
    }

    public void testToListWithHashtable() {
        Hashtable expected = new Hashtable();
        expected.put("one", new Integer(1));
        expected.put("two", new Integer(2));
        expected.put("three", new Integer(3));
        // validate elements.
        List actualEltList = EnumerationUtils.toList(expected.elements());
        Assert.assertEquals(expected.size(), actualEltList.size());
        Assert.assertTrue(actualEltList.contains(new Integer(1)));
        Assert.assertTrue(actualEltList.contains(new Integer(2)));
        Assert.assertTrue(actualEltList.contains(new Integer(3)));
        List expectedEltList = new ArrayList();
        expectedEltList.add(new Integer(1));
        expectedEltList.add(new Integer(2));
        expectedEltList.add(new Integer(3));
        Assert.assertTrue(actualEltList.containsAll(expectedEltList));

        // validate keys.
        List actualKeyList = EnumerationUtils.toList(expected.keys());
        Assert.assertEquals(expected.size(), actualEltList.size());
        Assert.assertTrue(actualKeyList.contains("one"));
        Assert.assertTrue(actualKeyList.contains("two"));
        Assert.assertTrue(actualKeyList.contains("three"));
        List expectedKeyList = new ArrayList();
        expectedKeyList.add("one");
        expectedKeyList.add("two");
        expectedKeyList.add("three");
        Assert.assertTrue(actualKeyList.containsAll(expectedKeyList));
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestEnumerationUtils.class);
    }

}
