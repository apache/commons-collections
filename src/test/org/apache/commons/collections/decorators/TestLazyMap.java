/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/decorators/Attic/TestLazyMap.java,v 1.3 2003/10/02 23:01:09 scolebourne Exp $
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
package org.apache.commons.collections.decorators;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.AbstractTestMap;

/**
 * Extension of {@link TestMap} for exercising the 
 * {@link LazyMap} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.3 $ $Date: 2003/10/02 23:01:09 $
 * 
 * @author Phil Steitz
 */
public class TestLazyMap extends AbstractTestMap {
    
    public TestLazyMap(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return new TestSuite(TestLazyMap.class);
    }
    
    public static void main(String args[]) {
        String[] testCaseName = { TestLazyMap.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }
    
 //-------------------------------------------------------------------
    
    protected Factory oneFactory = FactoryUtils.constantFactory("One");
    protected Factory nullFactory = FactoryUtils.nullFactory();
    
    protected Map decorateMap(Map map, Factory factory) {
        return LazyMap.decorate(map, factory);
    }
    
    protected Map makeEmptyMap() {
        return decorateMap(new HashMap(), nullFactory);
    }
    
//--------------------------------------------------------------------   
    
    protected Map makeTestMap(Factory factory) {
        return decorateMap(new HashMap(), factory);
    }
    
    public void testMapGet() {
        Map map = makeTestMap(oneFactory);
        assertEquals(0, map.size());
        String s1 = (String) map.get("Five");
        assertEquals("One", s1);
        assertEquals(1, map.size());
        String s2 = (String) map.get(new String(new char[] {'F','i','v','e'}));
        assertEquals("One", s2);
        assertEquals(1, map.size());
        assertSame(s1, s2);
        
        map = makeTestMap(nullFactory);
        Object o = map.get("Five");
        assertEquals(null,o);
        assertEquals(1, map.size());
        
    }       
}