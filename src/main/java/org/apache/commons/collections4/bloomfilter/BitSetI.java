package org.apache.commons.collections4.bloomfilter;

import java.util.stream.IntStream;

public interface BitSetI {

    void and( BitSetI other );
    void andNot( BitSetI other );
    int cardinality();
    boolean get( int idx );
    boolean intersects( BitSetI other);
    boolean isEmpty();
    int nextClearBit( int fromIndex );
    int nextSetBit( int fromIndex );
    void or( BitSetI other );
    int previousClearBit( int fromIndex );
    int previousSetBit( int fromIndex );
    IntStream stream();
    void xor( BitSetI other );
    BitSetI clone();
    boolean bitEquals( BitSetI other );
}
