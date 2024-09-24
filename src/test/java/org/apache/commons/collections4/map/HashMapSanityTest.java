package org.apache.commons.collections4.map;

import java.util.HashMap;
import java.util.Map;

/**
 * A sanity test for the test framework.
 *
 * @param <K> the key type.
 * @param <V> the value type.
 */
public class HashMapSanityTest<K, V> extends AbstractMapTest<K, V> {

    public HashMapSanityTest() {
        super(HashMapSanityTest.class.getSimpleName());
    }

    /**
     * Don't test, just a sanity check for the test framework.
     */
    @Override
    public boolean isTestSerialization() {
        return false;
    }

    @Override
    public Map<K, V> makeObject() {
        return new HashMap<>();
    }

}
