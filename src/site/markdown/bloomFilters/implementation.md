<!---
 Licensed to the Apache Software Foundation (ASF) under one or more
 contributor license agreements.  See the NOTICE file distributed with
 this work for additional information regarding copyright ownership.
 The ASF licenses this file to You under the Apache License, Version 2.0
 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->
# Bloom Filters Part 2: Apache Commons CollectionsⓇ Bloom Filter Implementation

Previously I wrote about Bloom filters, what they are, why we use them, and what statistics they can provide.  In this post I am going to cover the Apache Commons Collections Bloom filter implementation in Java and explain the logic behind some of the design decisions.

We need to realize that there are several pain points in Bloom filter usage.  First, the amount of time it takes to create the number of hashes needed for each filter.  Second, the trade-off between storing the bits as a bit vector (which for sparsely populated filters can be wasteful) or storing the index of the enabled bits (which for densely populated filters can be very large).  Adjacent to the internal storage question is how to serialize the Bloom filter.  Consideration of these pain points drove design decisions in the development of the Apache Commons Collections Bloom filter implementation.

## Shape

Last time I spoke of the shape of the filter.  In Commons Collections, `Shape` is a class that defines the number of bits in the filter (`m`) and the number of hashes (`k`) used for each object insertion.

Bloom filters with the same shape can be merged and compared.  The code does not verify that the shape is the same, as the comparison would increase processing time.  However, every Bloom filter has a reference to its `Shape` and the test can be performed if desired.

The `Shape` is constructed from one of five combinations of parameters specified in `fromXX` methods:
 * `fromKM` - when the number of hash functions and the number of bits is known.
 * `fromNM` - when the number of items and the number of bits is known.
 * `fromNMK` - when the number of items, the number of bits and the number of hash functions is known.  In this case the number of items is used to verify that the probability is within range.
 * `fromNP` - when the number of items and the probability of false positives is known.
 * `fromPMK` - when the probability of false positives, number of bits, and number of hash functions is known.  In this case the probability is used to verify that the maximum number of elements will result in a probability that is valid.

## The Extractors

The question of how to efficiently externally represent the internal representation of a Bloom filter led to the development of two “extractors”.   One that logically represents an ordered collection of bit map longs, and the other that represents a collection of enabled bits indices.  In both cases the extractor feeds each value to a function - called a predicate - that does some operation with the value and returns true to continue processing, or false to stop processing.

The extractors allow implementation of different internal storage as long as implementation can produce one or both of the standard producers.  Each producer has a static method to convert from the other type so only one implementation is required of the internal storage.

### BitMapExtractor

The `BitMapExtractor` is an interface that defines objects that can produce bit map vectors.  Bit map vectors are vectors of long values where the bits are enabled as per the `BitMap` class.  All Bloom filters implement this interface.  The `BitMapExtractor` has static methods to create an extractor from an array of long values, as well as from an `IndexExtractor`.  The interface has default implementations that convert the extractor into an array of long values, and one that executes a LongPredicate for each bit map.

The method `processBitMaps(LongPredicate)` is the standard access method for the extractor.  Each long value in the bit map is passed to the predicate in order.  Processing continues until the last bit map is processed or the `LongPredicate` returns false.

### IndexExtractor

The `IndexExtractor` produces the index values for the enabled bits in a Bloom filter.  All Bloom filters implement this interface.  The `IndexExtractor` has static methods to create the extractor from an array of integers or a `BitMapExtractor`.  The interface also has default implementations to convert the extractor to an array of integers and one that executes an `IntPredicate` on each index.

The method `processIndices(IntPredicate)` is the standard access method for the extractor.  Each int value in the extractor is passed to the predicate.  Processing continues until the last int is processed or the `IntPredicate` returns false.  The order and uniqueness of the values is not guaranteed.

## The Hashers

### Hasher

The `Hasher` interface defines a method that accepts a `Shape` and returns an `IndexrExtractor`. Calling `hasher.indices(shape)` will yield an `IndexExtractor` that will produce the `shape.numberOfHashFunctions()` integers.

The `Hasher` provides a clean separation between new data being hashed and the Bloom filter.  It also provides a mechanism to add the same item to multiple Bloom filters even if they have different shapes.  This can be an important consideration when using Bloom filters in multiple systems with different requirements.

Any current hashing process used by an existing Bloom filter implementation can be duplicated by a hasher.  The hasher only needs to create an `IndexExtractor` for an arbitrary shape.  In fact, the testing code contains `Hasher` implementations that produce sequential values.

### EnhancedDoubleHasher

In the previous post I described a hashing technique where the hash value is split into two values.  One is used for the initial value, the other is used to increment the values as additional hashes are required.  The Commons Collections implementation adds code to ensure that the increment is changed by a tetrahedral value on every iteration.  This change addresses issues with the incrementer initially being zero or very small.

The constructor accepts either two longs or a byte array from a hashing function.  Users are expected to select a hashing algorithm to create an initial hash for the item.  The `EnhancedDoubleHasher` is then constructed from that hash value.  In many cases the Murmur3 hash, available in the Apache Commons CodecⓇ library, is sufficient and very fast.  The result is a `Hasher` that represents the hashed item.  This hasher can now be passed to any Bloom filter and the `Shape` of the filter will determine the number of hashes created.

## The Bloom Filters

### Bloom Filter

Now we come to the Bloom filter.  As noted above the `BloomFilter` interface implements both the `IndexExtractor` and the `BitMapExtractor`.  The two extractors are the external representations of the internal representation of the Bloom filter.  Bloom filter implementations are free to store bit values in any way deemed fit.  The requirements for bit value storage are:
 * Must be able to produce an `IndexExtractor` or `BitMapExtractor`.
 * Must be able to clear the values.  Reset the cardinality to zero.
 * Must specify if the filter prefers (is faster creating) the `IndexExtractor` (Sparse characteristic) or the `BitMapExtractor` (Not sparse characteristic).
 * Must be able to merge hashers, Bloom filters, index extractors, and bit map extractors.  When handling extractors it is often the case that an implementation will convert one type of extractor to the other for merging.  The BloomFilter interface has default implementations for Bloom filter and hasher merging.
 * Must be able to determine if the filter contains hashers, Bloom filters, index extractors, and bit map extractors. The BloomFilter interface has default implementations for bit map extractor, Bloom filter and hasher checking.
 * Must be able to make a deep copy of itself.
 * Must be able to produce its `Shape`.

Several implementations of Bloom filter are provided.  We will start by focusing on the `SimpleBloomFilter` and the `SparseBloomFilter`.

### SimpleBloomFilter

The `SimpleBloomFilter` implements its storage as an on-heap array of longs.  This implementation is suitable for many applications.  It can also serve as a good model for how to implement bit map storage in specific off-heap situations.

### SparseBloomFilter
The `SparseBloomFilter` implements its storage as a TreeSet of Integers.  Since each bit is randomly selected across the entire [0,shape.numberOfBits) range the Sparse Bloom filter only makes sense when \\( \frac{2m}{64} \lt kn \\).  The reasoning being that every bit map in the array is equivalent to 2 integers in the sparse filter.  The number of integers is the number of hash functions times the number of items – this is an overestimation due to the number of hash collisions that will occur over the number of bits.

### Other

The helper classes included in the package make it easy to implement new Bloom filters, for example it should be fairly simple to implement a Bloom filter on a `ByteBuffer`, or `LongBuffer`, or one that uses a compressed bit vector.

## Common Usage Patterns

### Populating a Bloom filter

In most cases a shape is determined; in this example it is determined by the number of items to go in and the acceptable false positive rate, and then a number of items are added to the filter.

```java
import org.apache.commons.codec.digest.MurmurHash3;
import org.apache.commons.collections4.bloomfilter.BloomFilter;
import org.apache.commons.collections4.bloomfilter.EnhancedDoubleHasher;
import org.apache.commons.collections4.bloomfilter.Shape;
import org.apache.commons.lang3.SerializationUtils;

public class PopulatingExample {

    /**
     * Populates a Bloom filter with the items.  Uses a shape that 
     * expects 10K items max and a false positive rate of 0.01.
     * @param items The items to insert.
     * @return the Bloom filter populated with the items.
     */
    public static BloomFilter populate(Object[] items) {
        populate(Shape.fromNP(10000, 0.01), items);
    }

    /**
     * Populates a Bloom filter with the items.  
     * @param shape The shape of the Bloom filter.
     * @param items The items to insert.
     * @return the Bloom filter populated with the items.
     */
    public static BloomFilter populate(Shape shape, Object[] items) {
        BloomFilter collection = new SimpleBloomFilter(shape);
        for (Object o : items) {
            // this example serializes the entire object, actual implementation 
            // may want to serialize specific properties into the hash.
            byte[] bytes = SerializationUtils.serialize(o);
            long[] hash = MurmurHash3.hash128(bytes);
            collection.merge(new EnhancedDoubleHasher(hash[0], hash[1]));
        }
        // collection now contains all the items from the list of items
        return collection;
    }
}
```

### Searching a Bloom filter
When searching a single Bloom filter, it makes sense to use the `Hasher` to search in order to reduce the overhead of creating a Bloom filter for the match.

```java
import org.apache.commons.codec.digest.MurmurHash3;
import org.apache.commons.collections4.bloomfilter.BloomFilter;
import org.apache.commons.collections4.bloomfilter.EnhancedDoubleHasher;
import org.apache.commons.lang3.SerializationUtils;

public class SearchingExample {

    private Bloomfilter collection;

    /**
     * Creates the example from a populated collection.
     * @param collection the collection to search.
     */
    public SearchingExample(BloomFilter collection) {
        this.collection = collection;
    }

    /**
     * Perform the search.
     * @param item the item to look for.
     * @return true if the item is found, false otherwise.
     */
    public boolean search(Object item) {
        // create the hasher to look for.=
        byte[] bytes = SerializationUtils.serialize(o);
        long[] hash = MurmurHash3.hash128(bytes);
        return collection.contains(new EnhancedDoubleHasher(hash[0], hash[1]));
    }
}
```

However, if multiple filters are being checked, and they are all the same shape, then creating a Bloom filter from the hasher and using that is more efficient.

```java
import org.apache.commons.codec.digest.MurmurHash3;
import org.apache.commons.collections4.bloomfilter.BloomFilter;
import org.apache.commons.collections4.bloomfilter.EnhancedDoubleHasher;
import org.apache.commons.collections4.bloomfilter.Shape;
import org.apache.commons.lang3.SerializationUtils;

public abstract class SearchingExample2 {
    
    private BloomFilter[] collections;

    /**
     * Create example from an array of populated Bloom filters that all have the same Shape.
     * @param collections The Bloom filters to search.
     */
    public SearchingExample2(BloomFilter[] collections) {
        this.collections = collections;
    }

    /**
     * Search the filters for matching items.
     * @param item the Item t o search for.
     */
    public void search(Object item) {
        // create the Bloom filter to search for.
        byte[] bytes = SerializationUtils.serialize(o);
        long[] hash = MurmurHash3.hash128(bytes);
        BloomFilter target = new SimpleBloomFilter(collectons[0].getShape());
        target.merge(new EnhancedDoubleHasher(hash[0], hash[1]));

        for (BloomFilter candidate : candidates) {
            if (candidate.contains(target)) {
                doSomethingInteresting(candidate, item);
            }
        }
    }

    /**
     * The interesting thing to do when a match occurs.
     * @param collection the Bloom filter that matched.
     * @param item The item that was being searched for.
     */
    abstract public void doSomethingInteresting(BloomFilter collection, Object item);

}
```

If multiple filters of different shapes are being checked then use the `Hasher` to perform the check.  It will be more efficient than building a Bloom filter for each `Shape`.

## Statistics

The statistics that I mentioned in the previous bloom post are implemented in the `SetOperations` class.  The methods in this class accept `BitMapExtractor` arguments. They can be called with `BloomFilter` implementations or with `BitMapExtractor` implementations generated from arrays of longs or `IndexExtractors` or any other valid `BitMapExtractor` instance.

## Review

In this post we investigated the Apache Commons Collections implementation of Bloom filters and how to use them.  We touched on how they can be used to implement new designs of the standard components.  In the next post we will cover some of the more unusual usages and introduce some unusual implementations.
