package org.apache.commons.collections4.bloomfilter;

import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

import org.apache.commons.collections4.bloomfilter.hasher.Hasher;

/**
 * Interface that produces bit map long values in a Bloom filter.
 *
 */
public interface BitMapProducer {

    /**
     * Performs the given action for each bit map {@code long} that comprise the Bloom filter.
     * Any exceptions thrown by the action are relayed to the caller.
     *
     * @param consumer the action to be performed for each bit map long
     * @throws NullPointerException if the specified action is null
     */
    void forEachBitMap(LongConsumer consumer);

}
