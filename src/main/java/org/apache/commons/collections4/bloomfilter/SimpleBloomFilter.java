package org.apache.commons.collections4.bloomfilter;

import java.util.function.Function;

import org.apache.commons.collections4.bloomfilter.hasher.DynamicHasher;
import org.apache.commons.collections4.bloomfilter.hasher.Hasher;
import org.apache.commons.collections4.bloomfilter.hasher.Shape;
import org.apache.commons.collections4.bloomfilter.hasher.function.Murmur128x64Cyclic;

/**
 * A bloom filter that uses a BitSetBloomFilter to create a Bloom filter that
 * can merge instances of a specific class.
 *
 * @param <T> The Class to merge.
 * @since 4.5
 */
public class SimpleBloomFilter<T> extends BitSetBloomFilter implements BloomFilter {

    /**
     * The function that converts the instance of T to the SimpleBuilder.
     * <p>
     * If the object T is to be considered as a single item in the filter then
     * function must create the {@code SimpleBuilder} and only call a single {@code with()}
     * method.</p>
     * <p>
     * If the object T is to be considered as several items then the function must
     * create the {@code SimpleBuilder} and call the {@code with()} method once for each item.</p>
     */
    private Function<T,SimpleBuilder> func;

    /**
     * Constructs a SimpleBloomFilter from the shape and function.  This constructor
     * creates an empty Bloom filter.
     * @param shape the Shape of the Bloom filter.
     * @param func a Function to convert T to a SimpleBuilder.
     * @see #func
     */
    public SimpleBloomFilter(Shape shape, Function<T,SimpleBuilder> func) {
        super(shape);
        this.func = func;
    }

    /**
     * Constructs a SimpleBloomFilter from the shape, function and a data object.
     * This constructor creates an Bloom filter populated with the data from the
     * {@code data} parameter.
     * @param shape the Shape of the Bloom filter.
     * @param func a Function to convert T to a SimpleBuilder.
     * @param data the data object to populate the filter with.
     * @see #func
     */
    public SimpleBloomFilter(Shape shape, Function<T,SimpleBuilder> func, T data) {
        this(shape, func);
        this.merge( data );
    }

    /**
     * Merges a data object into this filter.
     *
     * <p>Note: This method should return {@code true} even if no additional bit indexes were
     * enabled. A {@code false} result indicates that this filter is not ensured to contain
     * the {@code data}.
     *
     * @param data the data to merge.
     * @return true if the merge was successful
     * @throws IllegalArgumentException if the shape of the other filter does not match
     * the shape of this filter
     */
    public boolean merge( T data ) {
        return this.merge( this.func.apply( data ).build() );
    }

    /**
     * A Hasher.Builder for the SimpleBloom filter.
     * This builder uses the Murmur 128 x64 cyclic hash.
     *
     * @see Murmur128x64Cyclic
     */
    public static class SimpleBuilder extends DynamicHasher.Builder implements Hasher.Builder {

        public SimpleBuilder() {
            super(new Murmur128x64Cyclic());
        }
    }
}
