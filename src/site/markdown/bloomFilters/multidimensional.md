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
# Bloom Filters Part 4: Bloom filters for indexing

In many cases Bloom filters are used as gatekeepers; that is, they are queried before attempting a longer operation to see if the longer operation should be executed.  However, there is another type of Bloom filter: the reference type.  The reference type contains the hashed values for the properties of a single object.  For example a Person object might have the fields  name, date of birth, address, and phone number.  Each of those could be hashed and combined into a single Bloom filter.  That Bloom filter could then be said to be a reference to the person.  Reference Bloom filters tend to be fully or nearly fully saturated.

We can use reference Bloom filters to index data by storing the Bloom filter along with a record identifier that can be used to retrieve the data.  The simplest solution is create a list of reference Bloom filters and their associated record identifiers and then perform the linear search for matches.  For every Bloom filter that matches the search, return the associated record identifier.

Searching can be performed by creating a target Bloom filter with partial data, for example name and date of birth from the person example, and then searching through the list as described above.  The associated records either have the name and birthdate or are false positives and need to be filtered out during retrieval.

## Multidimensional Bloom Filters

The description above is a multidimensional Bloom filter.  A multidimensional Bloom filter is simply a collection of searchable filters, the simplest implementation being a list.  In fact, for fewer than 10K filters the list is the fastest possible solution.  There are two basic reasons for this:
  * Bloom filter comparisons are extremely fast taking on approximately five (5) machine instructions for the simple comparison.
 * Bloom filters do not have an obvious natural order that can be used to reduce the search space without incurring significant overhead.  The amount of overhead often overwhelms  the advantage of the index.

There are, however, several multidimensional Bloom filter algorithms among them are: Bloofi, Flat Bloofi, BF-Trie, Hamming Skip List, Sharded List, and Natural Bloofi.

### Bloofi
Bloofi is a technique that uses a B+-tree structure where the inner nodes are merges of the Bloom filters below and the leaf nodes contain the actual Bloom filters.<span><a class="footnote-ref" href="#fn1">1</a></span>  This technique works well for Bloom filters that are not densely populated (i.e. low saturation) and are designed with a very small false positive rate.

Bloofi is extremely fast when searching; however, inserting often requires updates to multiple inner nodes.  Bloofi supports deletion, but deletion can also generate updates to multiple inner nodes.

### Flat Bloofi
Flat-Bloofi, expands each bit into a bit vector representing which Bloom filters in the index contain the specific bit.  Conceptually this is a bit matrix, with the columns being the bits in the Bloom filter and the rows the bitMaps for the indexed Bloom filters.  During insert, the Bloom filter being inserted is given an index number (row).  In each bit vector associated with each enabled bit in the Bloom filter (column) the index number bit is enabled.  During a search, all of the bit vectors associated with the enabled bits in the target Bloom filter (columns) are “and”ed together.  The enabled bits in the resulting bit vector represent the index numbers of the matching Bloom filters.  Implementations typically utilize the internal bit structure of native data types to compactly represent the matrix.

This solution is consistently among the fastest solutions of all solutions presented here.  It is often the fastest or second fastest.  It supports deletion through the addition of a list of deleted rows and reuse of space in the vectors.  

### BF-Trie

BF-Trie creates a trie based on the byte values in the filters.  It has the same insert, delete, and update characteristics you would expect from a trie structure.

During searching there is an expansion factor that has to be taken into account.  Every zero in the target filter yields two possible solutions.  For example the byte 0xFA has 4 potential matches (see table below) that have to be included in the search.  This is performed by exploring multiple paths through the trie while finding the solution.

| code | matching pattern |
| ---- | -----------------|
| 0xFA | 1111 1010 |
| 0XFB | 1111 1011 |
| 0xFE | 1111 1110 |
| 0xFF | 1111 1111|


### Hamming Skip List

Conceptually the Hamming skip list is an implementation of a two segment key.  This index arises from the observation that no target can match a filter with a lower hamming value.  In addition, if the Bloom filter bit vector is interpreted as a very large unsigned integer no target can match a filter of a lower value.  Since we have a binary representation of the very large unsigned integer we can calculate \\( log_2 \\) of the value.  Now we construct a skip list based on the hamming value on the first level, and the \\( log_2 \\) value on the second.  As an alternative, an index in a standard relational DB can be used.  During the search all solutions where filter hamming value is greater than or equal to the target hamming value and the filter \\( log_2 \\) value is greater than or equal to the target \\( log_2 \\) value are returned.

This solution suffers from the clustering of hamming values.  Most hamming values will cluster around the number of values that were merged times the number of hash functions \\( (n \times k) \\) adjusted for the expected collision rate.  So the hamming value only provides a strong selector when the hamming value of the target is close to the saturation of the indexed filter.  The \\( log_2 \\) index is fairly evenly distributed in the upper range and really only provides a strong selector when the hamming value is low but upper bits are enabled.

The Hamming Skip List is a good simple implementation for architectures that use relational databases or other environments where multi-segmented numerical indexes are present.

### Sharded List

A sharded list is a collection of lists of Bloom filters and builds upon the sharding solution presented in part 3 of this series.  In this instance, as a Bloom filter is added to the index the filter is hashed and a Bloom filter created from that hash.  The Bloom filter’s Bloom filter is then used to determine which list to add the filter to.  When a collection reaches capacity (as defined by the Shape of the Bloom filter’s Bloom filter), it is removed from consideration for further inserts and a new empty list created and added to the collection for insert consideration.

This solution utilizes the rapidity of the standard list solution, while providing a mechanism to handle more than 10K filters at a time.

### Natural Bloofi

Natural Bloofi uses a Tree structure like Bloofi does except that each node in the tree is a filter that was inserted into the index.<span><a class="footnote-ref" href="#fn2">2</a></span>  Natural Bloofi operates like the sharded list except that if the Bloom filter for a node is contained by a node in the list then it is made a child of that node.  If the Bloom filter node contains a node in the list, then it becomes the parent of that node.  This yields a flat Bloofi tree where the more saturated filters are closer to the root.

## Encrypted Indexing

The idea of using Bloom filters for indexing encrypted data is not a new idea.<span><a class="footnote-ref" href="#fn3">3</a></span><span><a class="footnote-ref" href="#fn4">4</a></span><span><a class="footnote-ref" href="#fn5">5</a></span><span><a class="footnote-ref" href="#fn6">6</a></span>    The salient points are that Bloom filters are a very effective one way hash with matching capabilities.  The simplest solution is to create a reference Bloom filter comprising the plain text of the columns that are to be indexed.  Encrypt the data.  Send the encrypted data and the Bloom filter to the storage engine.  The storage engine stores the encrypted data as a blob and indexes the Bloom filter with a reference to the stored blob.

When searching such an index, the desired plain text values are hashed into a Bloom filter and sent to the storage engine.  The engine finds all the matching Bloom filters and returns the encrypted blobs associated with them.  The client then decrypts the blobs and removes any false positives.

The technique ensures that the plain text data never leaves the client’s system and guarantees that the service has no access to the plain text of the stored data.

## Review

In this section we discussed multidimensional Bloom filters and presented several implementations.  We also explored the idea of an encrypted database where the data in transit and at rest is encrypted or at least strongly hashed.

I hope that over the course of this series you have developed a deeper understanding of Bloom filters, their construction and how they can be applied to various technical problems.

<span>
<ol class="footnotes>">
<li><a id='fn1'></a>
Adina Crainiceanu. 2013. Bloofi: a hierarchical Bloom filter index with applications to distributed data provenance. In Proceedings of the 2nd International Workshop on Cloud Intelligence. ACM, New York, NY, USA. https://doi.org/10.1145/2501928.2501931
</li>
<li><a id='fn2'></a>
Adina Crainiceanu and Daniel Lemire. 2015. Bloofi: Multidimensional Bloom filters. Information Systems 54, C (Dec. 2015), 311–324. https://doi.org/10.1016/j.is.2015.01.002
</li>
<li><a id='fn3'></a>
Yan-Cheng Chang and Michael Mitzenmacher. Privacy Preserving Keyword Searches on Remote Encrypted Data. Accessed on 18-Dec-2019. 2004. url: https://eprint.iacr.org/2004/051.pdf.
</li>
<li><a id='fn4'></a>
Steven M Bellovin and William R Cheswick”. Privacy-Enchanced Searched Using Encrypted Bloom Filters”. Accessed on 18-Dec-2019. 2004. url: https://mice.cs.columbia.edu/getTechreport.php?techreportID=483.
</li>
<li><a id='fn5'></a>
Eu-Jin Goh. Secure Indexes. Accessed on 18-Dec-2019. 2004. url: https://crypto.stanford.edu/~eujin/papers/secureindex/ secureindex.pdf.
</li>
<li><a id='fn6'></a>
Arisa Tajima, Hiroki Sato, and Hayato Yamana. Privacy-Preserving Join Processing over outsourced private datasets with Fully Homomorphic Encryption and Bloom Filters. Accessed on 18-Dec-2019. 2018. url: https://db-event.jpn.org/deim2018/data/papers/201.pdf
</li>
</ol>
</span>
