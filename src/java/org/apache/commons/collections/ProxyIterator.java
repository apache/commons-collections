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

import java.util.Iterator;

/** A Proxy {@link Iterator Iterator} which delegates its methods to a proxy instance.
  *
  * @since 1.0
  * @see ProxyListIterator
  * @version $Revision: 1.6.2.1 $ $Date: 2004/05/22 12:14:02 $
  *
  * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
  * @deprecated this class has been moved to the iterators subpackage
  */

public class ProxyIterator
extends org.apache.commons.collections.iterators.ProxyIterator {
    
    /**
     *  Constructs a new <Code>ProxyIterator</Code> that will not function
     *  until {@link #setIterator(Iterator)} is called.
     */
    public ProxyIterator() {
        super();
    }
    
    /**
     *  Constructs a new <Code>ProxyIterator</Code> that will use the
     *  given iterator.
     *
     *  @param iterator  the underyling iterator
     */
    public ProxyIterator( Iterator iterator ) {
        super(iterator);
    }

}
