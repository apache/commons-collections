/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//collections/src/test/org/apache/commons/collections/map/TestCompositeMap.java,v 1.1 2003/12/14 21:42:55 psteitz Exp $
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
package org.apache.commons.collections.map;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.Assert;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

/**
 * Extension of {@link AbstractTestMap} for exercising the 
 * {@link CompositeMap} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.1 $ $Date: 2003/12/14 21:42:55 $
 *
 * @author Brian McCallister
 */
public class TestCompositeMap extends AbstractTestMap {
    /** used as a flag in MapMutator tests */
    private boolean pass = false;
    
    public TestCompositeMap(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return new TestSuite(TestCompositeMap.class);
    }
    
    public void setUp() throws Exception {
        super.setUp();
        this.pass = false;
    }
    
    public static void main(String args[]) {
        String[] testCaseName = {TestCompositeMap.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }
    
    public Map makeEmptyMap() {
        CompositeMap map = new CompositeMap();
        map.addComposited(new HashMap());
        map.setMutator(new CompositeMap.MapMutator() {
            public void resolveCollision(CompositeMap composite,
            Map existing,
            Map added,
            Collection intersect) {
                // Do nothing
            }
            
            public Object put(CompositeMap map, Map[] composited, Object key, Object value) {
                return composited[0].put(key, value);
            }
            
            public void putAll(CompositeMap map, Map[] composited, Map t) {
                composited[0].putAll(t);
            }
            
        });
        return map;
    }
    
    private Map buildOne() {
        HashMap map = new HashMap();
        map.put("1", "one");
        map.put("2", "two");
        return map;
    }
    
    public Map buildTwo() {
        HashMap map = new HashMap();
        map.put("3", "three");
        map.put("4", "four");
        return map;
    }
    
    public void testGet() {
        CompositeMap map = new CompositeMap(buildOne(), buildTwo());
        Assert.assertEquals("one", map.get("1"));
        Assert.assertEquals("four", map.get("4"));
    }
    
    public void testAddComposited() {
        CompositeMap map = new CompositeMap(buildOne(), buildTwo());
        HashMap three = new HashMap();
        three.put("5", "five");
        map.addComposited(three);
        assertTrue(map.containsKey("5"));
        try {
            map.addComposited(three);
            fail("Expecting IllegalArgumentException.");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }
    
    public void testRemoveComposited() {
        CompositeMap map = new CompositeMap(buildOne(), buildTwo());
        HashMap three = new HashMap();
        three.put("5", "five");
        map.addComposited(three);
        assertTrue(map.containsKey("5"));
        
        map.removeComposited(three);
        assertFalse(map.containsKey("5"));
        
        map.removeComposited(buildOne());
        assertFalse(map.containsKey("2"));
        
    }
    
    public void testRemoveFromUnderlying() {
        CompositeMap map = new CompositeMap(buildOne(), buildTwo());
        HashMap three = new HashMap();
        three.put("5", "five");
        map.addComposited(three);
        assertTrue(map.containsKey("5"));
        
        //Now remove "5"
        three.remove("5");
        assertFalse(map.containsKey("5"));
    }
    
    public void testRemoveFromComposited() {
        CompositeMap map = new CompositeMap(buildOne(), buildTwo());
        HashMap three = new HashMap();
        three.put("5", "five");
        map.addComposited(three);
        assertTrue(map.containsKey("5"));
        
        //Now remove "5"
        map.remove("5");
        assertFalse(three.containsKey("5"));
    }
    
    public void testResolveCollision() {
        CompositeMap map = new CompositeMap(buildOne(), buildTwo(), 
            new CompositeMap.MapMutator() {
            public void resolveCollision(CompositeMap composite,
            Map existing,
            Map added,
            Collection intersect) {
                pass = true;
            }
            
            public Object put(CompositeMap map, Map[] composited, Object key, 
                Object value) {
                throw new UnsupportedOperationException();
            }
            
            public void putAll(CompositeMap map, Map[] composited, Map t) {
                throw new UnsupportedOperationException();
            }
        });
        
        map.addComposited(buildOne());
        assertTrue(pass);
    }
    
    public void testPut() {
        CompositeMap map = new CompositeMap(buildOne(), buildTwo(), 
            new CompositeMap.MapMutator() {
            public void resolveCollision(CompositeMap composite,
            Map existing,
            Map added,
            Collection intersect) {
                throw new UnsupportedOperationException();
            }
            
            public Object put(CompositeMap map, Map[] composited, Object key, 
                Object value) {
                pass = true;
                return "foo";
            }
            
            public void putAll(CompositeMap map, Map[] composited, Map t) {
                throw new UnsupportedOperationException();
            }
        });
        
        map.put("willy", "wonka");
        assertTrue(pass);
    }
    
    public void testPutAll() {
        CompositeMap map = new CompositeMap(buildOne(), buildTwo(), 
            new CompositeMap.MapMutator() {
            public void resolveCollision(CompositeMap composite,
            Map existing,
            Map added,
            Collection intersect) {
                throw new UnsupportedOperationException();
            }
            
            public Object put(CompositeMap map, Map[] composited, Object key,
                Object value) {
                throw new UnsupportedOperationException();
            }
            
            public void putAll(CompositeMap map, Map[] composited, Map t) {
                pass = true;
            }
        });
        
        map.putAll(null);
        assertTrue(pass);
    }
}

