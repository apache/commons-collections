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
package org.apache.commons.collections;



/** <p><code>SingletonIterator</code> is an {@link java.util.Iterator Iterator} over a single 
  * object instance.</p>
  *
  * @since 2.0
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @version $Revision: 1.8.2.1 $
  * @deprecated this class has been moved to the iterators subpackage
  */
public class SingletonIterator 
extends org.apache.commons.collections.iterators.SingletonIterator {
    
    /**
     *  Constructs a new <Code>SingletonIterator</Code>.
     *
     *  @param object  the single object to return from the iterator
     */
    public SingletonIterator(Object object) {
        super(object);
    }

}
