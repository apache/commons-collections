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
package org.apache.commons.collections.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Test;

import org.apache.commons.collections.BulkTest;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.list.AbstractTestList;

/**
 * Extension of {@link TestMap} for exercising the {@link ListOrderedMap}
 * implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.7 $ $Date: 2004/01/14 21:34:34 $
 * 
 * @author Henri Yandell
 * @author Stephen Colebourne
 */
public class TestListOrderedMap extends AbstractTestOrderedMap {

    public TestListOrderedMap(String testName) {
        super(testName);
    }

    public static Test suite() {
        return BulkTest.makeSuite(TestListOrderedMap.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestListOrderedMap.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    public Map makeEmptyMap() {
        return ListOrderedMap.decorate(new HashMap());
    }
    
    //-----------------------------------------------------------------------
    public void testGetByIndex() {
        resetEmpty();
        ListOrderedMap lom = (ListOrderedMap) map;
        try {
            lom.get(0);
        } catch (IndexOutOfBoundsException ex) {}
        try {
            lom.get(-1);
        } catch (IndexOutOfBoundsException ex) {}
        
        resetFull();
        lom = (ListOrderedMap) map;
        try {
            lom.get(-1);
        } catch (IndexOutOfBoundsException ex) {}
        try {
            lom.get(lom.size());
        } catch (IndexOutOfBoundsException ex) {}
        
        int i = 0;
        for (MapIterator it = lom.mapIterator(); it.hasNext(); i++) {
            assertSame(it.next(), lom.get(i));
        }
    }

    public void testGetValueByIndex() {
        resetEmpty();
        ListOrderedMap lom = (ListOrderedMap) map;
        try {
            lom.getValue(0);
        } catch (IndexOutOfBoundsException ex) {}
        try {
            lom.getValue(-1);
        } catch (IndexOutOfBoundsException ex) {}
        
        resetFull();
        lom = (ListOrderedMap) map;
        try {
            lom.getValue(-1);
        } catch (IndexOutOfBoundsException ex) {}
        try {
            lom.getValue(lom.size());
        } catch (IndexOutOfBoundsException ex) {}
        
        int i = 0;
        for (MapIterator it = lom.mapIterator(); it.hasNext(); i++) {
            it.next();
            assertSame(it.getValue(), lom.getValue(i));
        }
    }

    public void testIndexOf() {
        resetEmpty();
        ListOrderedMap lom = (ListOrderedMap) map;
        assertEquals(-1, lom.indexOf(getOtherKeys()));
        
        resetFull();
        lom = (ListOrderedMap) map;
        List list = new ArrayList();
        for (MapIterator it = lom.mapIterator(); it.hasNext();) {
            list.add(it.next());
        }
        for (int i = 0; i < list.size(); i++) {
            assertEquals(i, lom.indexOf(list.get(i)));
        }
    }

    public void testRemoveByIndex() {
        resetEmpty();
        ListOrderedMap lom = (ListOrderedMap) map;
        try {
            lom.remove(0);
        } catch (IndexOutOfBoundsException ex) {}
        try {
            lom.remove(-1);
        } catch (IndexOutOfBoundsException ex) {}
        
        resetFull();
        lom = (ListOrderedMap) map;
        try {
            lom.remove(-1);
        } catch (IndexOutOfBoundsException ex) {}
        try {
            lom.remove(lom.size());
        } catch (IndexOutOfBoundsException ex) {}
        
        List list = new ArrayList();
        for (MapIterator it = lom.mapIterator(); it.hasNext();) {
            list.add(it.next());
        }
        for (int i = 0; i < list.size(); i++) {
            Object key = list.get(i);
            Object value = lom.get(key);
            assertEquals(value, lom.remove(i));
            list.remove(i);
            assertEquals(false, lom.containsKey(key));
        }
    }
    
    public BulkTest bulkTestListView() {
        return new TestListView();
    }
    
    public class TestListView extends AbstractTestList {
        
        TestListView() {
            super("TestListView");
        }

        public List makeEmptyList() {
            return ((ListOrderedMap) TestListOrderedMap.this.makeEmptyMap()).asList();
        }
        
        public List makeFullList() {
            return ((ListOrderedMap) TestListOrderedMap.this.makeFullMap()).asList();
        }
        
        public Object[] getFullElements() {
            return TestListOrderedMap.this.getSampleKeys();
        }
        public boolean isAddSupported() {
            return false;
        }
        public boolean isRemoveSupported() {
            return false;
        }
        public boolean isSetSupported() {
            return false;
        }
        public boolean isNullSupported() {
            return TestListOrderedMap.this.isAllowNullKey();
        }

    }

}
