
# License
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

# Use Cases for Bloom filter implementation.

# Definitions

_Shape_: The length of the bloom filter as well as the number of hashing functions for each entry into the filter.  Shape also defines the hashing algorithm used.

# Bloom Filter

## URI indexing

In this use case a list of URIs is developed.  Let's look at two (2) distinct versions of this.

### Exclusion list

In this case the URIs are URIs that are prohibited.  The complete list of prohibited URIs is on a server and an application calls the server to determine if a URI is prohibited.

A Bloom filter containing all the bad URIs is built by the server and sent to the client.  Built means that the Server has determined the appropriate _Shape_ and has hashed and merged all the URIs into a single Bloom filter.  Sent means the Bloom filter and the _Shape_ are sent to the client. 

The client receives and stores the Bloom filter and the _Shape_.  When a URI is entered the client uses the _Shape_ to build a Bloom filter based on the single URI.  It then performs the match between the newly constructed Bloom filter and the server Bloom filter (e.g. testFilter & serverFilter == testFilter).  

If the test returns "true" then the URI may be in the bad list and the client makes an "expensive" network call the the server to validate the presence of the URI in the server maintained list. 

If the test returns "false" then the URI is OK to use.

Similar use cases involve things like unique filters where the list of things seen does not have to be scanned unless the Bloom filter indicates that it may have been seen already.

### Resource location

In this case the URIs represent resources that may be stored on a storage node in a system.  For purposes of this discussion we will assume a master control node manages all the storage nodes, however this need not bee the case.  The system may be a cache or it may be something like a Hadoop file store.  The _Shape_ of the Bloom filter is defined at the system level and all nodes use the same _Shape_.  Each node builds a Bloom filter comprising all the URIs that it holds and reports that to the control node.

The control node creates a list of all the Bloom filters from the storage nodes.  When a request for a URI comes in to the control node it performs a match check against each node in the list.  If the node matches then the control contacts the storage node associated with the Bloom filter and asks for the contents associated with the URI.  

If the storage node has the URI it returns the content and the control node passes that back to the caller.  If the storage node does not have  the content then the control node continues checking the Bloom filters in the list until either a storage node returns the request content or the end of the list is reached and the control node reports that it does not have the content.

## Sharding / Storage allocation

In this case there is a storage system that comprises a number of "buckets" that share a maximum storage capability.  The goal of this Bloom filter solution is to allocate objects into the storage and be able to quickly find them again.

The storage system defines the _Shape_ of the bloom filter based on the storage limit of the buckets.  It creates a number of "buckets" and an empty Bloom filter associated with each one.

When a request to store an object is made a Bloom filter is created for that object.  The Bloom filter must have the same _Shape_ as the storage system.  The filter can be created either by a client or by the storage system.

The distance (Hamming, cosine, jaccard or other) is calculated between each "bucket" Bloom filter and the object Bloom filter.  The "bucket" with the lowest distance is selected and the object is added to the bucket.

Adding an object to the bucket means storing the object and updating the bucket Bloom filter by merging the object Bloom filter with it.

When a "bucket" reaches maximum storage capability it is removed from consideration for further inserts,  but remains in the list for searches.  An new empty "bucket" and Bloom filter can be added to the list without modification to the algorithm.

Searching for an object is the same as the URI indexing, resource location use case above.

Note that selecting the nearest N "buckets" a redundant storage can be achieved. without modification to the retrieval strategy.
 

### With updates

Standard Bloom filters do not support deletion.  There are two general solutions to the problem.

1. Accept that as objects are removed from a "bucket" the Bloom filter will report more and more false positives.  When the number of false positives becomes to great the Bloom filter is recalculated from all the objects in the "bucket". 

2. Use counting Bloom filters, (or similar) that allow deletion.

Counting Bloom filters were introduced by Fan et al.[2].  The counting Bloom filter maintains not bit flag indicating that a bit was enable but a count of the number of times that bit was seen to be enabled.  

In the example above each "bucket" would have a counting Bloom filter of the specified _Shape_.  Every time the object Bloom filter is merged into the "bucket" Bloom filter the count for the bits are incremented.  

When an object is removed associated object Bloom filter is subtracted from the "bucket" Bloom filter.  The result is a Bloom filter that can accept deletions. When the last instance of an object is removed from the "bucket" the effects of its Bloom filter have been removed from the "bucket" Bloom filter.


## Query optimization

In this case Bloom filters are utilized within a query engine to determine which joins to perform first.  As with all other cases the _Shape_ of the filter is determined by the system and used throughout the query processing.  We also assume a UUID for each row or object that the query engine handles.

This case is a little harder to explain as we are jumping into the middle of a query process.  The query could be a SQL query, SPARQL query, or other non-relational query where more than one join is being performed.  In order to optimize the execution time the engine wants to perform the joins that return the fewest solutions first, so as to minimize the comparisons needed for joins where one side of the join is large.

Sub-queries are performed.  The contents of each sub-query are considered as like the "buckets" in the Sharding use case.  Each sub-query builds a Bloom filter comprising its contents.

The estimation of the size of the sub-query can be calculated as:

     -(_Shape_.numberOfBits / _Shape_.numberOfHashFunctions) * log(1 -filter.cardinality() / _Shape_.NumberOfBits)

The estimation of the size of the union between the buckets behind filter1 and filter2 is:       

    -(_Shape_.NumberOfBits() / _Shape_.NumberOfHashFunctions) * Math.log(1 - ( filter1 | filter2 ).cardinality() / _Shape_.NumberOfBits)

The estimation of the size of the intersection between between the buckets behind filter1 and filte2 is:

    estimateSize(filter1) - estimateUnionSize(filter1, filter2) + estimateSize(filter2);

Based on the type of join between the sub-queries the calculation for size of the union or intersection is performed.  The join with the fewest result is performed. A Bloom filter for the result of the join is prepared and the operation continues anew until all joins are performed.

This is an overly simplified example as there are a number of other factors involved in determining the best join to perform but should provide enough information to understand how this use case impacts the Bloom filter design.

## Duplicate detection in streaming data.

This strategy uses a bloom filter called a Stable Bloom Filter first introduced by Deng & Rafiei.[3]

The Stable Bloom filter ages data out of the system as new data is added, effectively provided in sliding window over the data set.  It also has the effect of adding false negatives to the result.

The use case is fairly simple: Objects in a stream are hashed and a Bloom filter constructed.  The Bloom filter is matched() to the Stable Bloom Filter.  If it matches then a duplicate is detected and the object is removed from the stream.

If it is matched to the Stable Bloom Filter then it is merged() and some internal calculations occur wherein the Stable Bloom Filter evicts some earlier data.

The internal representation of the Stable Bloom Filter is closer to the Counting filter with its list of integers.  It has some extra _Shape_ properties that describe the eviction process.


## Object storage with minimal keys

In this case Bloom filters are created for objects to be stored, but those filters are associated with the object while they are stored, similar to an associative array.  The Bloom filter is created using the searchable properties from the object (keys).  The Bloom filter is then used as the key to a Map that maps the filter to the object, or list of objects that have the same filter.

Any the values for subset of the keys can be used to locate objects that have all of the properties by performing a standard Bloom filter match.  For example using geonames data we construct Bloom filters comprising the feature class, feature code, country code, admni1 code, admin2 code, admin3 code and admin4 code for each entry.  We then add the Bloom filter and entry in the map.

If we want to find all Hotels in Andorra we can do that by constructing a filter with the values "HTL" (hotel feature code) and "AD" (Andorra country code) and search the Map keys for matches.  To account for false positives each object returned would have to be checked to ensure that is actually is a Hotel in Andorra.

This structure can further be paired with the Sharding strategy above to create a distributed storage system.


# References

[1] http://crystal.uta.edu/~mcguigan/cse6350/papers/Bloom.pdf
[2] http://www.ece.eng.wayne.edu/~sjiang/ECE7995-07-fall/slides/summary-cache.pdf
[3] https://webdocs.cs.ualberta.ca/~drafiei/papers/DupDet06Sigmod.pdf
