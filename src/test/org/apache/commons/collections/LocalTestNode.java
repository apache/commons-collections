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
package org.apache.commons.collections;

/**
 * Class LocalTestNode, a helper class for TestDoubleOrderedMap
 *
 * @version $Revision$ $Date$
 *
 * @author Marc Johnson (marcj at users dot sourceforge dot net)
 */
class LocalTestNode<K extends Comparable<K>, V extends Comparable<V>> implements Comparable<LocalTestNode<K, V>> {

    private K key;
    private V value;

    @SuppressWarnings("boxing")
    static LocalTestNode<Integer, String> createLocalTestNode(final int key) {
        return new LocalTestNode<Integer, String>(key, String.valueOf(key));
    }

    /**
     * construct a LocalTestNode
     *
     * @param key value used to create the key and value
     */
    private LocalTestNode(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * @param key the unique key associated with the current node.
     */
    void setKey(K key) {
        this.key = key;
    }

    /**
     * @return the unique key associated with the current node
     */
    K getKey() {
        return key;
    }

    /**
     * @param value the unique value associated with the current node.
     */
    void setValue(V value) {
        this.value = value;
    }

    /**
     * @return the unique value associated with the current node
     */
    V getValue() {
        return value;
    }

    /**
     * Method compareTo
     *
     * @param other
     *
     * @return a negative integer, zero, or a positive integer
     *  as this object is less than, equal to, or greater than the specified object.
     */
    public int compareTo(LocalTestNode<K, V> other) {

        int rval = getKey().compareTo(other.getKey());

        if (rval == 0) {
            rval = getValue().compareTo(other.getValue());
        }

        return rval;
    }

    /**
     * Method equals
     *
     * @param o
     *
     * @return true if equal
     */
    @Override
    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }

        if (!(o.getClass().equals(this.getClass()))) {
            return false;
        }

        LocalTestNode node = (LocalTestNode) o;

        return (getKey().equals(node.getKey())
                && getValue().equals(node.getValue()));
    }

    /**
     * @return hash code
     */
    @Override
    public int hashCode() {
        return getKey().hashCode() ^ getValue().hashCode();
    }
}
