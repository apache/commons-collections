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
package org.apache.commons.collections.set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.collection.CompositeCollection;

import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

/**
 * Extension of {@link AbstractTestSet} for exercising the 
 * {@link CompositeSet} implementation.
 *
 * @since Commons Collections 3.0
 * @version $Revision: 1.2 $ $Date: 2004/01/14 21:34:35 $
 *
 * @author Brian McCallister
 * @author Phil Steitz
 */

public class TestCompositeSet extends AbstractTestSet {
    public TestCompositeSet(String name) {
        super(name);
    }
    
    public static Test suite() {
        return new TestSuite(TestCompositeSet.class);
    }
    
    public Set makeEmptySet() {
        final HashSet contained = new HashSet();
        CompositeSet set = new CompositeSet(contained);
        set.setMutator(new CompositeSet.SetMutator() {
            public void resolveCollision(CompositeSet comp, Set existing, 
                Set added, Collection intersects) {
                throw new IllegalArgumentException();
            }
            
            public boolean add(CompositeCollection composite, 
                Collection[] collections, Object obj) {
                return contained.add(obj);
            }
            
            public boolean addAll(CompositeCollection composite, 
                Collection[] collections, Collection coll) {
                return contained.addAll(coll);
            }
            
            public boolean remove(CompositeCollection composite, 
                Collection[] collections, Object obj) {
                return contained.remove(obj);
            }
        });
        return set;
    }
    
    public Set buildOne() {
        HashSet set = new HashSet();
        set.add("1");
        set.add("2");
        return set;
    }
    
    public Set buildTwo() {
        HashSet set = new HashSet();
        set.add("3");
        set.add("4");
        return set;
    }
    
    public void testContains() {
        CompositeSet set = new CompositeSet(new Set[]{buildOne(), buildTwo()});
        assertTrue(set.contains("1"));
    }
    
    public void testRemoveUnderlying() {
        Set one = buildOne();
        Set two = buildTwo();
        CompositeSet set = new CompositeSet(new Set[]{one, two});
        one.remove("1");
        assertFalse(set.contains("1"));
        
        two.remove("3");
        assertFalse(set.contains("3"));
    }
    
    public void testRemoveComposited() {
        Set one = buildOne();
        Set two = buildTwo();
        CompositeSet set = new CompositeSet(new Set[]{one, two});
        set.remove("1");
        assertFalse(one.contains("1"));
        
        set.remove("3");
        assertFalse(one.contains("3"));
    }
    
    public void testFailedCollisionResolution() {
        Set one = buildOne();
        Set two = buildTwo();
        CompositeSet set = new CompositeSet(new Set[]{one, two});
        set.setMutator(new CompositeSet.SetMutator() {
            public void resolveCollision(CompositeSet comp, Set existing, 
                Set added, Collection intersects) {
            }
            
            public boolean add(CompositeCollection composite, 
                Collection[] collections, Object obj) {
                throw new UnsupportedOperationException();
            }
            
            public boolean addAll(CompositeCollection composite, 
                Collection[] collections, Collection coll) {
                throw new UnsupportedOperationException();
            }
            
            public boolean remove(CompositeCollection composite, 
                Collection[] collections, Object obj) {
                throw new UnsupportedOperationException();
            }
        });
        
        HashSet three = new HashSet();
        three.add("1");
        try {
            set.addComposited(three);
            fail("IllegalArgumentException should have been thrown");
        }
        catch (IllegalArgumentException e) {
            // expected
        }
    }
    
    public void testAddComposited() {
        Set one = buildOne();
        Set two = buildTwo();
        CompositeSet set = new CompositeSet();
        set.addComposited(one, two);
        CompositeSet set2 = new CompositeSet(buildOne());
        set2.addComposited(buildTwo());
        assertTrue(set.equals(set2));
        HashSet set3 = new HashSet();
        set3.add("1");
        set3.add("2");
        set3.add("3");
        HashSet set4 = new HashSet();
        set4.add("4");
        CompositeSet set5 = new CompositeSet(set3);
        set5.addComposited(set4);
        assertTrue(set.equals(set5));
        try {
            set.addComposited(set3);
            fail("Expecting UnsupportedOperationException.");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
    }
}
