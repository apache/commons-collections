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
import java.util.HashMap;

/**
 * An implementation of {@link Bag} that is backed by a {@link
 * HashMap}.
 *
 * @since 2.0
 * @author Chuck Burdick
 **/
public class HashBag extends DefaultMapBag implements Bag {

   /**
    *  Constructs a new empty <Code>HashBag</Code>.
    */
   public HashBag() {
      setMap(new HashMap());
   }

   /**
    * New {@link Bag} containing all the members of the given
    * collection.
    * @see #addAll
    **/
   public HashBag(Collection c) {
      this();
      addAll(c);
   }
}


