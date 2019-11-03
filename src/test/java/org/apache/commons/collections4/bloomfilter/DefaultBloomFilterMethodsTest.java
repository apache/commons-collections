package org.apache.commons.collections4.bloomfilter;

import java.util.BitSet;
import java.util.function.IntConsumer;

import org.apache.commons.collections4.bloomfilter.BloomFilter.Shape;
import org.apache.commons.collections4.bloomfilter.hasher.StaticHasher;

public class DefaultBloomFilterMethodsTest extends BloomFilterTest {

    @Override
    protected BloomFilter createFilter(Hasher hasher, Shape shape) {
        return new BF( hasher, shape );
    }

    @Override
    protected BloomFilter createEmptyFilter(Shape shape) {
        return new BF( shape );
    }

    /**
     * A testing class that implements only the abstract methods from BloomFilter.
     *
     */
    private static class BF extends BloomFilter {

        /**
         * The bitset that defines this BloomFilter.
         */
        private BitSet bitSet;

        /**
         * Constructs a BitSetBloomFilter from a hasher and a shape.
         *
         * @param hasher the Hasher to use.
         * @param shape the desired shape of the filter.
         */
        public BF(Hasher hasher, Shape shape) {
            this(shape);
            verifyHasher(hasher);
            hasher.getBits(shape).forEachRemaining((IntConsumer) bitSet::set);
        }

        /**
         * Constructs an empty BitSetBloomFilter.
         *
         * @param shape the desired shape of the filter.
         */
        public BF(Shape shape) {
            super(shape);
            this.bitSet = new BitSet();
        }

        @Override
        public long[] getBits() {
            return bitSet.toLongArray();
        }

        @Override
        public StaticHasher getHasher() {
            return new StaticHasher(bitSet.stream().iterator(), getShape());
        }

        @Override
        public void merge(BloomFilter other) {
            verifyShape(other);
            bitSet.or(BitSet.valueOf(other.getBits()));
        }

        @Override
        public void merge(Hasher hasher) {
            verifyHasher( hasher );
            hasher.getBits(getShape()).forEachRemaining((IntConsumer) bitSet::set);
        }

    }

}
