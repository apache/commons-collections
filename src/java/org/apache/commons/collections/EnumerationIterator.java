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

import java.util.Collection;
import java.util.Enumeration;

/** Adapter to make {@link Enumeration Enumeration} instances appear
  * to be {@link java.util.Iterator Iterator} instances.
  *
  * @since 1.0
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
  * @deprecated this class has been moved to the iterators subpackage
  */
public class EnumerationIterator
extends org.apache.commons.collections.iterators.EnumerationIterator {
    
    /**
     *  Constructs a new <Code>EnumerationIterator</Code> that will not
     *  function until {@link #setEnumeration(Enumeration)} is called.
     */
    public EnumerationIterator() {
        super();
    }

    /**
     *  Constructs a new <Code>EnumerationIterator</Code> that provides
     *  an iterator view of the given enumeration.
     *
     *  @param enumeration  the enumeration to use
     */
    public EnumerationIterator( Enumeration enumeration ) {
        super(enumeration);
    }

    /**
     *  Constructs a new <Code>EnumerationIterator</Code> that will remove
     *  elements from the specified collection.
     *
     *  @param enumeration  the enumeration to use
     *  @param collection  the collection to remove elements from
     */
    public EnumerationIterator( Enumeration enumeration, Collection collection ) {
        super(enumeration, collection);
    }

}
