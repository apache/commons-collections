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
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * An implementation of {@link Bag} that is backed by a {@link
 * TreeMap}. Order will be maintained among the unique representative
 * members.
 *
 * @since 2.0
 * @author Chuck Burdick
 **/
public class TreeBag extends DefaultMapBag implements SortedBag, Bag {

   /**
    *  Constructs a new empty <Code>TreeBag</Code>.
    */
   public TreeBag() {
      setMap(new TreeMap());
   }

   /**
    * New {@link Bag} that maintains order on its unique
    * representative members according to the given {@link
    * Comparator}.
    **/
   public TreeBag(Comparator c) {
      setMap(new TreeMap(c));
   }

   /**
    * New {@link Bag} containing all the members of the given
    * collection.
    * @see #addAll
    **/
   public TreeBag(Collection c) {
      this();
      addAll(c);
   }

   public Object first() {
      return ((SortedMap)getMap()).firstKey();
   }

   public Object last() {
      return ((SortedMap)getMap()).lastKey();
   }

   public Comparator comparator() {
      return ((SortedMap)getMap()).comparator();
   }
}






