/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003-2004 The Apache Software Foundation.  All rights
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.collection.TestTransformedCollection;

/**
 * Extension of {@link TestMap} for exercising the {@link TransformedMap}
 * implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.4 $ $Date: 2004/01/14 21:34:34 $
 * 
 * @author Stephen Colebourne
 */
public class TestTransformedMap extends AbstractTestMap {
    
    public TestTransformedMap(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestTransformedMap.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { TestTransformedMap.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    public Map makeEmptyMap() {
        return TransformedMap.decorate(new HashMap(), TestTransformedCollection.NOOP_TRANSFORMER, TestTransformedCollection.NOOP_TRANSFORMER);
    }

    public void testTransformedMap() {
        Object[] els = new Object[] {"1", "3", "5", "7", "2", "4", "6"};

        Map map = TransformedMap.decorate(new HashMap(), TestTransformedCollection.STRING_TO_INTEGER_TRANSFORMER, null);
        assertEquals(0, map.size());
        for (int i = 0; i < els.length; i++) {
            map.put(els[i], els[i]);
            assertEquals(i + 1, map.size());
            assertEquals(true, map.containsKey(new Integer((String) els[i])));
            assertEquals(false, map.containsKey(els[i]));
            assertEquals(true, map.containsValue(els[i]));
            assertEquals(els[i], map.get(new Integer((String) els[i])));
        }
        
        assertEquals(null, map.remove(els[0]));
        assertEquals(els[0], map.remove(new Integer((String) els[0])));
        
        map = TransformedMap.decorate(new HashMap(), null, TestTransformedCollection.STRING_TO_INTEGER_TRANSFORMER);
        assertEquals(0, map.size());
        for (int i = 0; i < els.length; i++) {
            map.put(els[i], els[i]);
            assertEquals(i + 1, map.size());
            assertEquals(true, map.containsValue(new Integer((String) els[i])));
            assertEquals(false, map.containsValue(els[i]));
            assertEquals(true, map.containsKey(els[i]));
            assertEquals(new Integer((String) els[i]), map.get(els[i]));
        }

        assertEquals(new Integer((String) els[0]), map.remove(els[0]));
        
        Set entrySet = map.entrySet();
        Map.Entry[] array = (Map.Entry[]) entrySet.toArray(new Map.Entry[0]);
        array[0].setValue("66");
        assertEquals(new Integer(66), array[0].getValue());
        assertEquals(new Integer(66), map.get(array[0].getKey()));
        
        Map.Entry entry = (Map.Entry) entrySet.iterator().next();
        entry.setValue("88");
        assertEquals(new Integer(88), entry.getValue());
        assertEquals(new Integer(88), map.get(entry.getKey()));
    }
}
