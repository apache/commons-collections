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
# Bloom Filters Part 3: Unusual Usage and Advanced Implementations

In the previous post we discussed the Apache Commons CollectionⓇ implementation of Bloom filters and showed how to use them to answer the most basic questions.  In this post we will look at some unusual usage patterns and some advanced implementations.

## Unusual Usage Patterns

These usage patterns are unusual in the sense that they are not commonly seen in code bases.  However, it is important for developers to realize the types of questions that can be answered with Bloom filters.

### Finding filter in intersection of sets

If you have two Bloom filters and you want to know if there are elements that are in both, it is possible to create a single Bloom filter to answer the question.  The solution is to create a Bloom filter that is the intersection of the two original Bloom filters.  To do this we are going to use the `BitMapExtractor.processBitmapPairs()` function.  This function takes a `BitMapExtractor` and a `LongBiPredicate` as arguments.  The `LongBiPredicate` takes two long values, performs some function and returns true if the processing should continue and false otherwise.

In this example the `LongBiPredicate` creates a new bitmap by performing a bitwise “and” on the `BitMap` pairs presented by the `BitMapExtractor.processBitmapPairs()` function.  The class has a method to present the result as a `BitMapExtractor`.

```java
import org.apache.commons.collections4.bloomfilter.BitMap;
import org.apache.commons.collections4.bloomfilter.BitMapExtractor;
import org.apache.commons.collections4.bloomfilter.LongBiPredicate;
import org.apache.commons.collections4.bloomfilter.Shape;

/**
 * Calculates the intersection of two Bloom filters
 */
class Intersection implements LongBiPredicate {
    private long[] newMaps;
    private int idx;

    /**
     * Creates an intersection for a specified shape.
     * @param shape the shape of the Bloom filters being compared.
     */
	Intersection(Shape shape) {
		newMaps = new long[BitMap.numberOfBitMaps(shape.getNumberOfBits())];
        idx = 0;
	}

    /**
     * Implements the LongBiPredicate test. 
     * @param a one BitMap
     * @param b the other BitMap
     * @return true always.
     */
	public boolean test(long a, long b) {
        newMaps[idx++] = a & b;
        return true;
    }

    /**
     * Returns the intersection as a BitMapExtractor.
     * @return the the intersection as a BitMapExtractor.
     */
    BitMapExtractor asExtractor() {
        return BitMapExtractor.fromBitMapArray(newMaps);
    }
}
```
Now we can use the `Intersecton` class to merge the two Bloom filters into a new filter as follows:

```java
import Intersection;
import org.apache.commons.collections4.bloomfilter.BloomFilter;
import org.apache.commons.collections4.bloomfilter.LongBiPredicate;
import org.apache.commons.collections4.bloomfilter.Shape;

class IntersectionExample {

    public static main(String[] args) {
        /* start setup */
        
        // get an array of Bloom filters to check
        BloomFilter[] candidateFilters = getBloomFilterCandidates();

        // use two populated Bloom filters.
        BloomFilter collection1 = populateCollection1();
        BloomFilter collection2 = populateCollection2();

        /* end setup */

        // create the Intersection instance
        Intersection intersection = new Intersection(collection1.getShape());

        // populate the intersection instance with data from the 2 filters.
        collection1.processBitMapPairs(collection2, intersection);

        // create a new Bloom filter from the intersection instance.
        BloomFilter collection1And2 = new BloomFilter(collection1.getShape());
        collection1And2.merge(intersection.asExtractor());

        // now do the search for filters that are in both collections.
        for (BloomFilter target : candidateFilters) {
            if (collection1And2.contains(target)) {
                // do something interesting
            }
        }
    }
}
```

### Sharding by Bloom filter
In processing large volumes of data it is often desirable to fragment or shard the data into different repositories.  Bloom filters provide a quick method for sharding the data.

Let’s assume there are multiple locations to store files, and we want to distribute the data across the cluster in a way that minimizes collisions.  The following code is an example of how to implement this.

```java
import org.apache.commons.collections4.bloomfilter.BloomFilter;
import org.apache.commons.collections4.bloomfilter.SetOperations;
import org.apache.commons.collections4.bloomfilter.Shape;
import org.apache.commons.collections4.bloomfilter.SimpleBloomFilter;
import Storage; // a class that implements the actual storage I/O

class ShardingExample {

    private BloomFilter[] gatekeeper;
    private Storage[] storage;

     /**
     * Constructor.  Assumes 10K items in filter with false positive rate of 0.01
     * @param storage The storage locations to store the objects in.
     */
    public ShardingExample(Storage[] storage) {
        this(storage, Shape.fromNP(10000, 0.01));
    }

    /**
     * Constructor. 
     * @param storage The storage locations to store the objects in.
     * @param shape The shape for the gatekeeper Bloom filters.
     */
    public ShardingExample(Storage[] storage, Shape shape) {
        gatekeeper = new BloomFilter[storage.length];
        this.storage = storage;
        for (int i = 0; i < gatekeeper.length; i++) {
            gatekeeper[i] = new SimpleBloomFilter(shape);
        }
    }

    /**
     * Creates the BloomFilter for the key.
     * @param objectKey The key to hash.
     * @return the BloomFilter for the key.
     */
    private BloomFilter createBloomFilter(Object objectKey) {
        byte[] bytes = SerializationUtils.serialize(o);
        long[] hash = MurmurHash3.hash128(bytes);
        BloomFilter bloomFilter = new SimpleBloomFilter(gatekeeper[0].getShape());
        bloomFilter.merge(new EnhancedDoubleHasher(hash[0], hash[1]));
        return bloomFilter;
    }

    
    /**
     * Write an object into the filter.
     * @param itemKey The item key for the storage layer.
     * @param itemToStore The item to store.
     */
    public void write(Object itemKey, Object itemToStore) {
        // create the Bloom filter to for key
        BloomFilter itemBloomFilter = createBloomFilter(itemKey);

        // find the storage to insert into.
       int selected = 0;
       int hammingValue = Integer.MAX_INT;
       for (int i = 0; i < gatekeeper.length; i++) {
           int hamming = SetOperations.hammingDistance(gatekeeper[i], itemBloomFilter);
           if (hamming < hammingValue) {
               selected = i;
               hammingValue = hamming;
           }
       }

       // insert the data.
       storage[selected].write(itemKey, itemToStore);
       gatekeeper[selected].merge(itemBloomFilter);
    }

    /**
     * Reads the item from the storage.  
     * @param itemKey The key to look for.
     * @return The stored object or null if not found. 
     */
    public Object read(Object itemKey) {
        // create the Bloom filter to look for
        BloomFilter itemBloomFilter = createBloomFilter(itemKey);
        
        // assumes storage returns null if key not found.
        for (int i = 0; i < gatekeeper.length; i++) {
            if (gatekeeper[i].contains(itemBloomFilter)) {
                Object itemThatWasStored = storage[i].read(itemKey);
                if (itemThatWasStored != null) {
                    return itemThatWasStored;
                }
            }
        }
        return null;
    }
}
```

However, the issue with this solution is that once the filters are saturated, the search begins to degrade.  To solve this problem a new, empty filter can be added when one of the existing filters approaches saturation.  When a filter reaches saturation, remove it from consideration for insert but let it respond to read requests.  This solution achieves a balanced Bloom filter distribution and does not exceed the false positive threshold.  An example of a test for saturation is:
```java
    if (bloomfilter.getShape().estimateMaxN() <= bloomfilter.estimateN()) {
        // handle saturated case.
    }
```
The above calculation is dependent upon `BloomFilter.cardinality()` function, so it is advisable to use BloomFilters that track cardinality or can calculate cardinality quickly.

## Counting Bloom Filters

Standard Bloom filters do not have a mechanism to remove items.  One of the solutions for this is to convert each bit to a counter<span><a class="footnote-ref" href="#fn1">1</a></span>. The counter and index together are commonly called a cell.  As items are added to the filter, the values of the cells associated with the enabled bits are incremented.  When an item is removed the values of the cells associated with the enabled bits are decremented.  This solution supports removal of items at the expense of making the filter many times larger than a bit map based one.

The counting Bloom filter also has a couple of operations not found in other Bloom filters:
 * Counting Bloom filters can be added together so that the sum of their counts is achieved.
 * Counting Bloom filters can be subtracted so that the difference of their counts is achieved.
 * Counting Bloom filters can report the maximum number of times another Bloom filter might have been merged into it.  This is an upper bound estimate and may include false positives.

There are several error conditions with counting Bloom filters that are not found in other cases.
 * Incrementing the counter past the maximum value that can be stored in a cell.
 * Decrementing the counter past 0.
 * Removing a Bloom filter that was not added.  This condition can decrement a cell that had an initial value of zero leading to decrementing error.  But in other cases, when all the enabled bits in the Bloom filter have cells in the counting filter it is undetectable, but will lead to unexpected results for subsequent operations.

### Apache Commons Collections Implementation

The Apache Commons Collections Bloom filter package contains a counting Bloom filter implementation.  To support the `CountingBloomFilter` there are `CellExtractor` and `CellPredicate` interfaces.

The `CellExtractor` extends the `IndexExtractor` by producing the index for every cell with a count greater than 0.  The interface defines several new methods:
 * `processCells(CellPredicate consumer)` that will call the `CellPredicate` for each populated cell in the producer.
 * `from(IndexExtractor indexExtractor)` A method to produce a `CellExtractor` from an `IndexExtractor` where each index that appears in the `IndexExtractor` is a cell with a value of one (1).  Since the `CellExtractor` makes a guarantee of uniqueness for the index, duplicate indices are summed together. 

The `CellPredicate` is a functional consumer interface that accepts two integer values, the index and the cell value, for each populated cell.

The `CountingBloomFilter` interface defines several new methods:
 * `add(CellExtractor other)` - adds the cells of the extractor to the cells of the counting Bloom filter.  
 * `subtract(CellExtractor other)` - subtracts the cells of the  extractor from the cells of the counting Bloom filter.
 * `remove(BitMapExtractor other)`, `remove(BloomFilter)`, `remove(Hasher)`, and `remove(IndexExtractor)` - these methods decrement the associated cells by 1.  This is the inverse of the `merge()` methods.
 * `isValid()` verifies that all cells are valid.  If `false` the `CountingBloomFilter` is no longer accurate and functions may yield unpredictable results.
 * `getMaxCell()` defines the maximum value of a cell before it becomes invalid.

The only provided implementation, `ArrayCountingBloomFilter`, uses a simple array of integers to track the counts.

## Element Decay Bloom filters - for streaming data

Element decay Bloom filters are effectively counting Bloom filters that automatically decrement some of the counts.  Technically speaking counting Bloom filters can be used for streaming data, but to do so one would have to figure out how to remove filters when they were too old to be considered any longer.  There are several approaches to this problem; here we discuss two: Creating layers of filters based on some quantized time unit (a temporal layered Bloom filter), and the stable Bloom filter.

### Stable Bloom filter

The stable Bloom filter<span><a class="footnote-ref" href="#fn2">2</a></span> is a form of counting Bloom filter that automatically degrades the cells so that items are “forgotten” after a period of time.  Each cell has a maximum value defined by the stable Bloom filter shape.  When a bit is turned on, the cell is set to the maximum value.  The process for an insert is:
 1. Randomly select a number of cells and if the value is greater than zero decrement it.
 2. For each bit to be enabled, set the cell to the maximum value.

After a period of time the number of enabled cells becomes stable, hence the name of the filter.  This filter will detect duplicates for recently seen items.  However, it also introduces a false negative rate, so unlike other Bloom filters this one does not guarantee that if the target is not in the filter it has not been seen.

The stable filter works well in environments where inserts occur at a fairly fixed rate; it does not handle bursty environments very well.

There is no implementation of a stable Bloom filter in commons collections.

### Layered Bloom Filter

The layered Bloom filter<span><a class="footnote-ref" href="#fn3">3</a></span> creates a list of filters.  Items are added to a target filter and, after a specified condition is met, a new filter is added to the list and it becomes the target.  Filters are removed from the list based on other specified conditions.

For example, a layered Bloom filter could comprise a list of Bloom filters, where every 10 merges a new target is created and old target filters are removed one minute after its last merge.  This provides a one minute windowing and guarantees that no Bloom filter in the list will contain more than 10 other filters.  This type of filter handles bursty rates better than the stable filter.

The layered filter also has the capability to locate the layer in which a filter was added.  This gives the user the ability to look back in time, if necessary.

The layered filter can also be used in situations where the actual number of items is unknown when the Bloom filter is defined.  By using a layered filter that adds a target whenever the saturation of the current target reaches 1, the false positive rate for the entire filter does not rise above the value calculated for the shape.

#### Apache Commons Collections Implementation

The Apache Commons Collections Bloom filter package contains a layered Bloom filter implementation.  To support the `LayeredBloomFilter` there are a `LayerManager` class and a `BloomFilterExtractor` interface.

The `LayerManager` handles all of the manipulation of the collection of Bloom filters that comprise the `LayeredBloomFilter`. It is constructed by a `Builder` that requires:
 * a `Supplier` of Bloom filters that will create new empty Bloom filters as required.  
 * a `Predicate` that takes a `LayerManager` and determines if a new layer should be added.
 * a `Consumer` that takes a `Deque` of the types of Bloom filters provided by the `Supplier` noted above.

These three properties ensure that the `LayerManager` will produce filters when required, and remove them when necessary.

The `BloomFilterExtractor` is a functional interface that functions much like the `CellExtractor` in the `CountingBloomFilter`.  The `BloomFilterExtractor` has methods:
* `processBloomFilters(Predicate<BloomFilter> consumer)` - that will call the `Predicate` for each Bloom filter layer.
* `processBloomFilterPairs(BloomFilterExtractor other, BiPredicate<BloomFilter, BloomFilter> funcindexExtractor)` - that wil lapply the BiPredicate to every pair of Bloom filters.
* Methods to produce a `BloomFilterExtractor` from a collection of Bloom filters.
* `flatten()` - a method to merge all the filters in the list into a single filter.


The `LayeredBloomFilter` class defines several new methods:
* `contains(BloomFilterExtractor others)` - returns true if the layered filter contains all of the other filters.
* `find(BitmapExtractor)`, `find(BloomFilter)`, `find(Hasher)`, and `find(IndexExtractor)` - returns an array of ints identifying which layers match the pattern.
* `get(int layer)` - returns the Bloom filter for the layer.
* `getDepth()` - returns the number of layers in the filter.
* `next()` - forces the the creation of a new layer.

#### Layered Bloom Filter Example

In this example we are building a layered Bloom filter that will 
* Retain information for a specified period of time: `layerExpiry`.
* Create a new layer when  
  * the current layer becomes saturates: `numberOfItems`; or
  * a timer has expired `layerExpiry / layerCount`.

This construct has the interesting effect of growing and shrinking the number of layers as demand grows or shrinks.  If there is a steady flow of items that does not exceed \\( \frac{numberOfItems}{layerExpiry / layerCount} \\) then there will be `layerCount` layers.  If the rate of entities is higher, then the number of layers will grow to handle the rate.  When the rate decreases the number of layers will decrease as the items age out `(layerExpiry)`.  If the rate drops to zero there will be one empty Bloom filter layer.  However, the number of layers will not shrink unless `isEmpty()` or one of the `merge()` or `contains()` methods are called.

This implementation is not thread safe and has the effect that the item is removed from the filter after `layerExpiry` has elapsed even if it was seen again. If the desired effect is to retain the reference until after the last time the item was seen then the check needs to verify that the item was seen in or near the last filter.

```java
package org.example;

import org.apache.commons.collections4.bloomfilter.BloomFilter;
import org.apache.commons.collections4.bloomfilter.Hasher;
import org.apache.commons.collections4.bloomfilter.LayerManager;
import org.apache.commons.collections4.bloomfilter.LayeredBloomFilter;
import org.apache.commons.collections4.bloomfilter.Shape;
import org.apache.commons.collections4.bloomfilter.SimpleBloomFilter;
import org.apache.commons.collections4.bloomfilter.WrappedBloomFilter;

import java.time.Duration;
import java.util.Deque;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class LayeredExample {
    // the layered Bloom filter.
    private final LayeredBloomFilter<TimestampedBloomFilter> bloomFilter;
    // the expiry time for a layer
    private final Duration layerExpiry;
    // the expected number of layers.
    private final int layerCount;

    /**
     * @param numberOfItems the expected number of item in a layer.
     * @param falsePositiveRate the acceptable false positive rate for the filter.
     * @param layerCount the number of expected layers.
     * @param layerExpiry The length of time a layer should exist
     */
    public LayeredExample(int numberOfItems, double falsePositiveRate, int layerCount, Duration layerExpiry) {
        this.layerCount = layerCount;
        this.layerExpiry = layerExpiry;
        Shape shape = Shape.fromNP(numberOfItems, falsePositiveRate);

        // we will create a new Bloom filter (advance) every time the active filter in the layered filter becomes full
        Predicate<LayerManager<TimestampedBloomFilter>> advance = LayerManager.ExtendCheck.advanceOnCount(numberOfItems);
        //  or when the next window should be started.
        advance = advance.or(new TimerPredicate());

        // the cleanup for the LayerManager determines when to remove a layer.
        // always remove the empty target.
        Consumer<Deque<TimestampedBloomFilter>> cleanup = LayerManager.Cleanup.removeEmptyTarget();
        // remove any expired layers from the front of the list.
        cleanup = cleanup.andThen(
                lst -> {
                    if (!lst.isEmpty()) {
                        long now = System.currentTimeMillis();
                        Iterator<TimestampedBloomFilter> iter = lst.iterator();
                        while (iter.hasNext()) {
                            if (iter.next().expires < now) {
                                iter.remove();
                            } else {
                                break;
                            }
                        }
                    }
                });
        
        LayerManager.Builder<TimestampedBloomFilter> builder = LayerManager.builder();
        // the layer manager for the Bloom filter, performs automatic cleanup and advance when necessary.
        LayerManager<TimestampedBloomFilter> layerManager = builder.setExtendCheck(advance)
                .setCleanup(cleanup)
                // create a new TimestampedBloomFilter when needed.
                .setSupplier(() -> new TimestampedBloomFilter(shape, layerExpiry.toMillis()))
                .build();
        // create the layered bloom filter.
        bloomFilter = new LayeredBloomFilter<TimestampedBloomFilter>(shape, layerManager);
    }

    // merge a hasher into the bloom filter.
    public LayeredExample merge(Hasher hasher) {
        bloomFilter.merge(hasher);
        return this;
    }

    /**
     * Returns true if {@code bf} is found in the layered filter.
     *
     * @param bf the filter to look for.
     * @return true if found.
     */
    public boolean contains(BloomFilter bf) {
        return bloomFilter.contains(bf);
    }

    /**
     * Returns true if {@code bf} is found in the layered filter.
     *
     * @param hasher the hasher representation to search for.
     * @return true if found.
     */
    public boolean contains(Hasher hasher) {
        return bloomFilter.contains(hasher);
    }
    
    /**
     * @return true if there are no entities being tracked for the principal.
     */
    public boolean isEmpty() {
        bloomFilter.cleanup(); // forces clean
        return bloomFilter.isEmpty();
    }

    /**
     * A cleanup() predicate that triggers when a new layer should be created based on time
     * and the desired number of layers.
     */
    class TimerPredicate implements Predicate<LayerManager<TimestampedBloomFilter>> {
        long expires;

        TimerPredicate() {
            expires = System.currentTimeMillis() + layerExpiry.toMillis() / layerCount;
        }

        @Override
        public boolean test(LayerManager o) {
            long now = System.currentTimeMillis();
            if (expires > now) {
                return false;
            }
            expires = now + layerExpiry.toMillis() / layerCount;
            return true;
        }
    }
    
    /**
     * A Bloom filter implementation that has a timestamp indicating when it expires.
     * Used as the Bloom filter for the layer in the LayeredBloomFilter
     */
    static class TimestampedBloomFilter extends WrappedBloomFilter {
        long expires;

        TimestampedBloomFilter(Shape shape, long ttl) {
            super(new SimpleBloomFilter(shape));
            expires = System.currentTimeMillis() + ttl;
        }

        private TimestampedBloomFilter(TimestampedBloomFilter other) {
            super(other.getWrapped().copy());
            this.expires = other.expires;
        }

        @Override
        public TimestampedBloomFilter copy() {
            return new TimestampedBloomFilter(this);
        }
    }
}
```

## Review

In this post we covered some unusual uses for Bloom filters as well as a couple of interesting unusual Bloom filters.  In the next post, we will introduce the reference Bloom filter, and delve into multidimensional Bloom filters.  We will also show how multidimensional Bloom filters can be used to search encrypted data without decrypting.

## Footnotes
<span>
<ol class="footnotes>">
<li><a id='fn1'></a>
Fan, Li; Cao, Pei; Almeida, Jussara; Broder, Andrei (2000), "Summary Cache: A Scalable Wide-Area Web Cache Sharing Protocol" (PDF), IEEE/ACM Transactions on Networking, 8 (3): 281–293, CiteSeerX 10.1.1.41.1487, doi:10.1109/90.851975, S2CID 4779754, archived from the original (PDF) on 2017-09-22, retrieved 2018-07-30. A preliminary version appeared at SIGCOMM '98.
</li>
<li><a id='fn2'></a>
Deng, Fan; Rafiei, Davood (2006), "Approximately Detecting Duplicates for Streaming Data using Stable Bloom Filters", Proceedings of the ACM SIGMOD Conference (PDF), pp. 25–36
</li>
<li><a id='fn2'></a>
Zhiwang, Cen; Jungang, Xu; Jian, Sun (2010), "A multi-layer Bloom filter for duplicated URL detection", Proc. 3rd International Conference on Advanced Computer Theory and Engineering (ICACTE 2010), vol. 1, pp. V1-586-V1-591, doi:10.1109/ICACTE.2010.5578947, ISBN 978-1-4244-6539-2, S2CID 3108985
</li>
</ol>
</span>
