/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/TestFastHashMap1.java,v 1.2 2002/02/22 02:18:50 mas Exp $
 * $Revision: 1.2 $
 * $Date: 2002/02/22 02:18:50 $
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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.util.HashMap;
import java.util.Map;

/**
 * Test FastHashMap in <strong>fast</strong> mode.
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @version $Id: TestFastHashMap1.java,v 1.2 2002/02/22 02:18:50 mas Exp $
 */
public class TestFastHashMap1 extends TestFastHashMap
{
    public TestFastHashMap1(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        return new TestSuite(TestFastHashMap1.class);
    }

    public static void main(String args[])
    {
        String[] testCaseName = { TestFastHashMap1.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }

    public Map makeMap() {
        FastHashMap fhm = new FastHashMap();
        fhm.setFast(true);
        return (fhm);
    }

    /**
     *  When the fast hash map is in fast mode, the underlying hash map is
     *  cloned on modification (i.e. on a put).  Because of that, any
     *  previously existing entry set will be representing the old (pre-clone)
     *  map and will not reflect changes made to the map after the clone.  So,
     *  we must override this test.
     **/
    public void testEntrySetChangesWithMapPut() {
    }

    /**
     *  When the fast hash map is in fast mode, the underlying hash map is
     *  cloned on modification (i.e. on a remove).  Because of that, any
     *  previously existing entry set will be representing the old (pre-clone)
     *  map and will not reflect changes made to the map after the clone.  So,
     *  we must override this test.
     **/
    public void testEntrySetChangesWithMapRemove() {
    }

    /**
     *  When the fast hash map is in fast mode, the underlying hash map is
     *  cloned on modification (i.e. on a put).  Because of that, any
     *  previously existing entry set will be representing the old (pre-clone)
     *  map, so changes to the set will not be seen in the map. So, we must
     *  override this test.
     **/
    public void testEntrySetRemoveCausesMapModification() {
    }

    public void setUp()
    {
        map = (HashMap) makeMap();
    }

}
