/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/TestAll.java,v 1.21 2002/03/01 18:36:21 morgand Exp $
 * $Revision: 1.21 $
 * $Date: 2002/03/01 18:36:21 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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

package org.apache.commons.collections;

import org.apache.commons.collections.comparators.*;
import junit.framework.*;

/**
 * Entry point for all Collections tests.
 * @author Rodney Waldhoff
 * @version $Id: TestAll.java,v 1.21 2002/03/01 18:36:21 morgand Exp $
 */
public class TestAll extends TestCase {
    public TestAll(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(TestArrayIterator.suite());
        suite.addTest(TestArrayIterator2.suite());
        suite.addTest(TestArrayStack.suite());
        suite.addTest(TestBeanMap.suite());
        suite.addTest(TestCollectionUtils.suite());
        suite.addTest(TestComparableComparator.suite());
        suite.addTest(TestCursorableLinkedList.suite());
        suite.addTest(TestDoubleOrderedMap.suite());
        suite.addTest(TestExtendedProperties.suite());
        suite.addTest(TestFastArrayList.suite());
        suite.addTest(TestFastArrayList1.suite());
        suite.addTest(TestFastHashMap.suite());
        suite.addTest(TestFastHashMap1.suite());
        suite.addTest(TestFastTreeMap.suite());
        suite.addTest(TestFastTreeMap1.suite());
        suite.addTest(TestFilterIterator.suite());
        suite.addTest(TestFilterListIterator.suite());
        suite.addTest(TestHashBag.suite());
        suite.addTest(TestLRUMap.suite());
        suite.addTest(TestMultiHashMap.suite());
        suite.addTest(TestSequencedHashMap.suite());
        suite.addTest(TestSingletonIterator.suite());
        suite.addTest(TestTreeBag.suite());
        return suite;
    }
        
    public static void main(String args[]) {
        String[] testCaseName = { TestAll.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }
}
