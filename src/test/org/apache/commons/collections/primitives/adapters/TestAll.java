/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/primitives/adapters/Attic/TestAll.java,v 1.6 2003/04/15 00:11:19 rwaldhoff Exp $
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
 *    any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @version $Revision: 1.6 $ $Date: 2003/04/15 00:11:19 $
 * @author Rodney Waldhoff
 */
public class TestAll extends TestCase {
    public TestAll(String testName) {
        super(testName);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestAll.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        
        suite.addTest(TestCollectionShortCollection.suite());
        suite.addTest(TestShortCollectionCollection.suite());
        suite.addTest(TestShortListList.suite());
        suite.addTest(TestListShortList.suite());
        suite.addTest(TestIteratorShortIterator.suite());
        suite.addTest(TestListIteratorShortListIterator.suite());
        suite.addTest(TestShortIteratorIterator.suite());
        suite.addTest(TestShortListIteratorListIterator.suite());

        suite.addTest(TestCollectionIntCollection.suite());
        suite.addTest(TestIntCollectionCollection.suite());
        suite.addTest(TestIntListList.suite());
        suite.addTest(TestListIntList.suite());
        suite.addTest(TestIteratorIntIterator.suite());
        suite.addTest(TestListIteratorIntListIterator.suite());
        suite.addTest(TestIntIteratorIterator.suite());
        suite.addTest(TestIntListIteratorListIterator.suite());
        
		suite.addTest(TestCollectionLongCollection.suite());
		suite.addTest(TestLongCollectionCollection.suite());
		suite.addTest(TestLongListList.suite());
		suite.addTest(TestListLongList.suite());
		suite.addTest(TestIteratorLongIterator.suite());
		suite.addTest(TestListIteratorLongListIterator.suite());
		suite.addTest(TestLongIteratorIterator.suite());
		suite.addTest(TestLongListIteratorListIterator.suite());

        suite.addTest(TestCollectionFloatCollection.suite());
        suite.addTest(TestFloatCollectionCollection.suite());
        suite.addTest(TestFloatListList.suite());
        suite.addTest(TestListFloatList.suite());
        suite.addTest(TestIteratorFloatIterator.suite());
        suite.addTest(TestListIteratorFloatListIterator.suite());
        suite.addTest(TestFloatIteratorIterator.suite());
        suite.addTest(TestFloatListIteratorListIterator.suite());

        suite.addTest(TestCollectionDoubleCollection.suite());
        suite.addTest(TestDoubleCollectionCollection.suite());
        suite.addTest(TestDoubleListList.suite());
        suite.addTest(TestListDoubleList.suite());
        suite.addTest(TestIteratorDoubleIterator.suite());
        suite.addTest(TestListIteratorDoubleListIterator.suite());
        suite.addTest(TestDoubleIteratorIterator.suite());
        suite.addTest(TestDoubleListIteratorListIterator.suite());

        return suite;
    }
}

