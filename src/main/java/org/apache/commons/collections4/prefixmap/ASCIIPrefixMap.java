/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.prefixmap;

import org.apache.commons.collections4.PrefixMap;

import java.io.Serializable;
import java.lang.reflect.Array;

/**
 * The ASCIIPrefixMap is an implementation of PrefixMap where the assumption is that the
 * stored prefixes only contain characters that are in the human readable range of the ASCII encoding.
 * @param <V> The type of the value that is to be stored.
 */
public class ASCIIPrefixMap<V extends Serializable> implements PrefixMap<V>, Serializable {

    private class PrefixTrie implements Serializable {
        private PrefixTrie[] childNodes;
        private boolean      caseSensitive;
        private int          charIndex;
        private V            theValue;

        PrefixTrie(boolean caseSensitive) {
            this(caseSensitive, 0);
        }

        PrefixTrie(boolean caseSensitive, int charIndex) {
            this.caseSensitive = caseSensitive;
            this.charIndex = charIndex;
        }

        @SuppressWarnings("unchecked") // Creating the array of generics is tricky
        V add(String prefix, V value) {
            V previousValue = theValue;
            if (charIndex == prefix.length()) {
                theValue = value;
                return previousValue;
            }

            char myChar = prefix.charAt(charIndex); // This will give us the ASCII value of the char
            if (myChar < 32 || myChar > 126) {
                throw new IllegalArgumentException("Only readable ASCII is allowed as key !!!");
            }

            if (childNodes == null) {
                childNodes = (PrefixTrie[]) Array.newInstance(PrefixTrie.class, 128);
            }

            if (caseSensitive) {
                if (childNodes[myChar] == null) {
                    childNodes[myChar] = new PrefixTrie(true, charIndex + 1);
                }
                previousValue = childNodes[myChar].add(prefix, value);
            } else {
                // If case INsensitive we build the tree
                // and we link the same child to both the
                // lower and uppercase entries in the child array.
                char lower = Character.toLowerCase(myChar);
                char upper = Character.toUpperCase(myChar);

                if (childNodes[lower] == null) {
                    childNodes[lower] = new PrefixTrie(false, charIndex + 1);
                }
                previousValue = childNodes[lower].add(prefix, value);
                childNodes[upper] = childNodes[lower];
            }
            return previousValue;
        }

        boolean containsPrefix(String prefix) {
            if (charIndex == prefix.length()) {
                return theValue != null;
            }

            if (childNodes == null) {
                return false;
            }

            char myChar = prefix.charAt(charIndex); // This will give us the ASCII value of the char
            if (myChar < 32 || myChar > 126) {
                return false; // Cannot store these, so is false.
            }

            PrefixTrie child = childNodes[myChar];
            if (child == null) {
                return false;
            }

            return child.containsPrefix(prefix);
        }

        V getShortestMatch(String input) {
            if (theValue != null ||
                charIndex == input.length() ||
                childNodes == null) {
                return theValue;
            }

            char myChar = input.charAt(charIndex); // This will give us the ASCII value of the char
            if (myChar < 32 || myChar > 126) {
                return null; // Cannot store these, so this is where it ends.
            }

            PrefixTrie child = childNodes[myChar];
            if (child == null) {
                return null;
            }

            return child.getShortestMatch(input);
        }

        V getLongestMatch(String input) {
            if (charIndex == input.length() || childNodes == null) {
                return theValue;
            }

            char myChar = input.charAt(charIndex); // This will give us the ASCII value of the char
            if (myChar < 32 || myChar > 126) {
                return theValue; // Cannot store these, so this is where it ends.
            }

            PrefixTrie child = childNodes[myChar];
            if (child == null) {
                return theValue;
            }

            V returnValue = child.getLongestMatch(input);
            return (returnValue == null) ? theValue : returnValue;
        }

        public void clear() {
            childNodes = null;
            theValue = null;
        }
    }

    private PrefixTrie prefixPrefixTrie;
    private int size = 0;

    public ASCIIPrefixMap(boolean caseSensitive) {
        prefixPrefixTrie = new PrefixTrie(caseSensitive);
    }

    @Override
    public boolean containsPrefix(String prefix) {
        return prefixPrefixTrie.containsPrefix(prefix);
    }

    @Override
    public V put(String prefix, V value) {
        if (prefix == null) {
            throw new NullPointerException("The prefix may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }
        V previousValue = prefixPrefixTrie.add(prefix, value);
        if (previousValue == null) {
            size++;
        }
        return previousValue;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        prefixPrefixTrie.clear();
    }

    @Override
    public V getShortestMatch(String input) {
        return prefixPrefixTrie.getShortestMatch(input);
    }

    @Override
    public V getLongestMatch(String input) {
        return prefixPrefixTrie.getLongestMatch(input);
    }

}
