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

import java.util.HashSet;
import java.util.Iterator;
import org.apache.commons.collections.Predicate;

/** A FilterIterator which only returns "unique" Objects.  Internally,
  * the Iterator maintains a Set of objects it has already encountered,
  * and duplicate Objects are skipped.
  *
  * @author Morgan Delagrange
  * @version $Id: UniqueFilterIterator.java,v 1.2.2.1 2004/05/22 12:14:04 scolebourne Exp $
  * @since 2.1
  */

public class UniqueFilterIterator extends FilterIterator {
       
    //-------------------------------------------------------------------------
    
    /**
     *  Constructs a new <Code>UniqueFilterIterator</Code>.
     *
     *  @param iterator  the iterator to use
     */
    public UniqueFilterIterator( Iterator iterator ) {
        super( iterator, new UniquePredicate() );
    }

    private static class UniquePredicate implements Predicate {

        HashSet set = new HashSet();

        public boolean evaluate(Object object) {
            return set.add(object);       
        }

    }

}
