package org.apache.commons.collections4.bloomfilter;

import java.util.stream.IntStream;

public interface BloomFilter {
    boolean match(BloomFilter other);
    IntStream stream();

//    void and( BloomFilter other );
//    void andNot( BloomFilter other );
//    int cardinality();
//    boolean get( int idx );
//    boolean intersects( BloomFilter other);
//    boolean isEmpty();
//    int nextClearBit( int fromIndex );
//    int nextSetBit( int fromIndex );
//    void or( BloomFilter other );
//    int previousClearBit( int fromIndex );
//    int previousSetBit( int fromIndex );
//    IntStream stream();
//    void xor( BloomFilter other );
//    BloomFilter clone();
//    boolean bitEquals( BloomFilter other );
}
