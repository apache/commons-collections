/*
 * Copyright 1999-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections.iterators;

import junit.framework.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Tests the SingletonIterator to ensure that the next() method will actually
 * perform the iteration rather than the hasNext() method.
 *
 * @author James Strachan
 * @version $Id: TestSingletonIterator.java,v 1.1.2.1 2004/05/22 12:14:04 scolebourne Exp $
 */
public class TestSingletonIterator extends TestIterator {

    private static final Object testValue = "foo";
    
    public static Test suite() {
        return new TestSuite(TestSingletonIterator.class);
    }
    
    public TestSingletonIterator(String testName) {
        super(testName);
    }
    
    /**
     * Returns null. SingletonIterators can never be empty;
     * they always have exactly one element.
     * 
     * @return null
     */
    public Iterator makeEmptyIterator() {
        return null;
    }

    public Iterator makeFullIterator() {
        return new SingletonIterator( testValue );
    }

    /**
     * Return a new, empty {@link Object} to used for testing.
     */
    public Object makeObject() {
        return makeFullIterator();
    }
    
    /**
     * Whether or not we are testing an iterator that can be
     * empty.  SingletonIterators are never empty;
     * 
     * @return false
     */
    public boolean supportsEmptyIterator() {
        return false;
    }

    public void testIterator() {
        Iterator iter = (Iterator) makeObject();
        assertTrue( "Iterator has a first item", iter.hasNext() );
        
        Object iterValue = iter.next();
        assertEquals( "Iteration value is correct", testValue, iterValue );
        
        assertTrue("Iterator should now be empty", ! iter.hasNext() );

	try {
	    Object testValue = iter.next();
	} 
        catch (Exception e) {
	  assertTrue("NoSuchElementException must be thrown", 
		 e.getClass().equals((new NoSuchElementException()).getClass()));
	}
    }
}

