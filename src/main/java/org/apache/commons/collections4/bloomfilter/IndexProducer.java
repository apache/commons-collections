package org.apache.commons.collections4.bloomfilter;

import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

import org.apache.commons.collections4.bloomfilter.hasher.Hasher;

public interface IndexProducer {

    /**
     * Performs the given action for each {@code index} that represents an enabled bit.
     * Any exceptions thrown by the action are relayed to the caller.
     *
     * @param consumer the action to be performed for each non-zero bit index.
     * @throws NullPointerException if the specified action is null
     */
    void forEachIndex(IntConsumer consumer);

}
