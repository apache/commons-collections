/*
 *  Copyright 2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections;

/**
 * Defines a functor interface implemented by classes that
 * perform a predicate test on an object. Predicate instances can be used
 * to implement queries or to do filtering. 
 * 
 * @since Commons Collections 1.0
 * @version $Revision: 1.10 $ $Date: 2004/02/18 01:15:42 $
 * 
 * @author James Strachan
 * @author Stephen Colebourne
 */
public interface Predicate {
    
    /**
     * Use the specified parameter to perform a test that returns true or false.
     *
     * @param object  the object to evaluate
     * @return true or false
     * @throws ClassCastException (runtime) if the input is the wrong class
     * @throws IllegalArgumentException (runtime) if the input is invalid
     * @throws FunctorException (runtime) if the predicate encounters a problem
     */
    public boolean evaluate(Object object);
    
}
