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
# Bloom Filters Part 1: An Introduction

Bloom filters are the magical elixir often used to reduce search space and time.  They have other interesting properties that make them applicable in many situations where knowledge of the approximate size of a set, union, or intersection is important, or where searching vast datasets for small matching patterns is desired, and even in cases where it is desirable to search for data without disclosing the data being searched for, or the actual data found, to 3rd parties.

In this series of blog posts we’ll do a deep dive into Bloom filters.  In this, the first post, we will touch on the mathematics behind the filters and work through an example of their use, and explore some of their properties.  In later posts we will explore the Apache Commons Collections® implementation that is due out in version 4.5 of that library, discuss using Bloom filters for data sharding, and explore some of the unusual Bloom filters, like counting Bloom filters, stable Bloom filters, and layered Bloom filters, before diving in to multidimensional Bloom filters and encrypted data indexing.

Bloom filters are probably used on websites and applications you use every day.  They are used to track articles you’ve read, speed up bitcoin clients, detect malicious web sites, and improve the performance of caches.   We will get back to these later.

## What?
But let us start at the beginning.  Bloom filters are a probabilistic data structure frequently used to represent sets of objects.  They were invented by Burton Bloom<span><a class="footnote-ref" href="#fn1">1</a></span> in 1970.  A Bloom filter is an array of bits (bit vector) into which a set of values has been hashed, so some of the bit values are on (value one), or "enabled", and others are off (value zero), or "disabled".

Multiple Bloom filters can be merged together by creating a union of the two bit vectors (V1 or V2).  The resulting Bloom filter (B) is said to contain both items.

The equation for combining can be expressed as: \\( B = V1 \cup V2 \\)

When searching Bloom filters we generate a Bloom filter for the item we are looking for (the target, T), and then calculate the intersection of T with the bit vector of the Bloom filter that may contain the value (the candidate, C).  If the result is equal to T, then a match has been made.  This calculation can yield false positives but never false negatives.

The equation for matching can be expressed as: \\( T \cap C = T \\)

There are several properties that define a Bloom filter: the number of bits in the vector (`m`), the number of items that will be merged into the filter (`n`), the number of hash functions for each item (`k`), and the probability of false positives (`p`).  All of these values are mathematically related. Mitzenmacher and Upfal<span><a class="footnote-ref" href="#fn2">2</a></span> have shown that the relationship between these properties is

\\[ p = \left( 1 - e^{-kn/m} \right) ^k \\]

However, it has been reported  that the false positive rate in real deployments is higher than the value given by this equation.<span><a class="footnote-ref" href="#fn3">3</a></span><span><a class="footnote-ref" href="#fn4">4</a></span> Theoretically it has been proven that the equation offered a lower bound of the false positive rate, and a more accurate false positive rate has been discovered.<span><a class="footnote-ref" href="#fn5">5</a></span>

The net result is that we can describe a Bloom filter with a “shape” and that shape can be derived from combinations of the properties for example from `(p, m, k)`, `(n, p)`, `(k, m)`, `(n, m)`, or `(n, m, k)`.

To compare Bloom filters, they must have the same shape and use the same hashing functions.

## Why?
Bloom filters are often used to reduce the search space. For example, consider an application looking for a file which may occur on one of many systems. Without a Bloom filter, each system must be queried for the existence of the file. Generally this is a lengthy process. However, if a Bloom filter is created for each system, then the query could first check the filter.  If the filter indicates the file might be on the system, then the expensive lookup check is performed.  Because the Bloom filter never returns a false negative, this strategy reduces the search space to only those systems that may contain the file.

Examples of large Bloom filter collections can be found in bioinformatics<span><a class="footnote-ref" href="#fn6">6</a></span><span><a class="footnote-ref" href="#fn7">7</a></span><span><a class="footnote-ref" href="#fn8">8</a></span> where Bloom filters are used to represent gene sequences, and Bloom filter based databases where records are encoded into Bloom filters.<span><a class="footnote-ref" href="#fn9">9</a></span><span><a class="footnote-ref" href="#fn10">10</a></span>

Medium, the digital publishing company, uses Bloom filters to track what articles have been read.<span><a class="footnote-ref" href="#fn11">11</a></span>  Bitcoin uses them to speed up clients.<span><a class="footnote-ref" href="#fn12">12</a></span>  Bloom filters have been used to improve caching performance<span><a class="footnote-ref" href="#fn13">13</a></span> and in detecting malicious websites.<span><a class="footnote-ref" href="#fn14">14</a></span>

## How?
So, let’s work through an example.  Let's assume we want to put 3 items in a filter `(n = 3)` with a 1/5 probability of collision `(p = 1/5 = 0.2)`.  Solving \\( p = \left( 1 - e^{-kn/m} \right) ^k \\) yields  `m=11` and `k=3`.  Thomas Hurst has provided an online calculator<span><a class="footnote-ref" href="#fn15">15</a></span> where you can explore the interactions between the values.

Now that we know the shape of our Bloom filters, let’s populate one.  In this example we will be using a CRC hash; this is not recommended and is only used here for ease of example.  Also, we will be using a naive combinatorial hashing technique that should not be used in real life.

We start by taking the CRC hash for "CAT" which is `FD2615C4`.  The naive combinatorial hashing technique splits the calculated hash into 2 unsigned values.  In this case the CRC value can be interpreted as 2 unsigned ints

| Value        | Use     |
|--------------|----------|
| FD26 = 64806 | increment |
| 15C4 = 5572  | initial  |

We need to generate 3 hash values (`k`) using the naive combinatorial hashing algorithm.  The first hash value is the initial value.  The second hash value is the initial value plus the increment.  The third hash value is the 2nd hash value plus the increment.  This proceeds until the proper number of hash values have been generated.  After we generate the `k` values, take the modulus (remainder after division) of those numbers by the number of bits in the vector (`m`).

| Name | Calculation | Value | Value mod(11) |
|------|-------------|-------|---------------|
| k1  | 5572 | 5572 | 6 |
 | k2 | 5572 + 64608 | 70180 | 0 |
| k3 | 5572 + 64608 + 64608 | 134788 | 5 |


This yields a Bloom filter of `00001100001` or  \\(\\{0,5,6\\}\\).  In the binary form we enumerate the bits from left to right as they would be displayed in a big-endian unsigned integer where the bit n is represented by \\(2^n\\). Performing the same operations on "DOG" and "GUINEA PIG" yields:

| Name | CRC | Bit Set              | Bloom filter |
|------|-----|----------------------|--------------|
| CAT | FD26 15C4 | \\(\\{0,5,6\\}\\)        | 00001100001 |
| DOG | 3560 D2EF | \\(\\{2\\}\\)            | 00000000100 |
| GUINEA PIG | E58C A739 | \\(\\{2,7,10\\}\\)       | 10010000100 |
| Collection | | \\(\\{0,2,5,6,7,10\\}\\) | 10011100101 |

The collection is the union of the other three values.  This represents the set of animals.

The interesting one in this collection is DOG.  When we execute the naive combinatorial hashing algorithm on the DOG hash, every value is 2.  We’ll come back to this in a moment.

If we perform the same calculations on “HORSE”, we get \\(\\{2,5,9\\}\\).  Now to see if HORSE is in our collection we solve \\(\\{2,5,9\\} \cap \\{0,2,5,6,7,10\\} = \\{2,5,9\\} \longrightarrow \\{2,5\\} \ne \\{2,5,9\\}\\) .  So HORSE is not in the collection.

If we only put CAT and GUINEA PIG into the collection, we get the same result for the collection.  But when testing for DOG we get the true statement \\(\\{2\\} \cap \\{0,2,5,6,7,10\\} = \\{2\\}\\). The filter says that DOG is in the collection.  This is an example of a false positive result.

Dog also shows the weakness of the naive combinatorial hashing technique.   A proper implementation can be found in the EnhancedDoubleHashing class in the Apache Commons Collections version 4.5 library.  In this case, a tetrahedral number is added to the increment to reduce the probability of a single bit being selected over the course of the hash.

## Statistics?

Bloom filters lend themselves to several statistics.  The most common is the Hamming value or cardinality.  This is simply the number of bits that are enabled in the vector.  From this value a number of statistics can be calculated.

The hamming distance is the number of bits that have to be flipped to convert one Bloom filter to another.  For example to convert Cat \\(\\{0,5,6\\}\\) to horse \\(\\{2,5,9\\}\\), we have to turn off bits \\(\\{0,6\\}\\) and turn on bits \\(\\{2,9\\}\\) so the hamming distance is 4.  Bloom filters with lower hamming distances are in some sense similar.

Another measure of similarity is the Cosine similarity also known as Orchini similarity, Tucker coefficient of congruence or Ochiai similarity.  To calculate it the cardinality of the intersection (bitwise ‘and’) of the two filters is then divided by the square root of the cardinality of the first filter times the cardinality of the second filter.  The result is a number in the range \\([0,1]\\).

The cosine distance is calculated as `1 - cosine similarity`.


The final measure of similarity that we will cover is the Jaccard similarity also known as the Jaccard Index, Intersection over Union, and Jaccard similarity coefficient.  To calculate the Jaccard index the cardinality of the intersection (bitwise ‘and’)  of the two Bloom filters is calculated.  This value is divided by the cardinality of the union (bitwise ‘or’) of the two Bloom filters.

The Jaccard distance, like the cosine distance, is calculated as `1 - Jaccard similarity`.

The similarity and distance statistics can be used to group similar Bloom filters together; for example when distributing files across a system that uses Bloom filters to determine where the file might be located.  In this case it might make sense to store Bloom filters in the collection that has minimal distance.

In addition to basic similarity and difference, if the shape of the filter is known some information about the data behind the filters can be estimated.  For example the number of items merged into a filter (`n`) can be estimated provided we have the cardinality (`c`), number of bits in the vector (`m`) and the number of hash functions (`k`) used when adding each element.

\\[ n = \frac{-m ln(1 - c/m)}{k} \\]

Estimating the size of the union of two filters is simply calculating `n` for the union (bitwise ‘or’) of the two filters.

Estimating the size of the intersection of two filters is the estimated `n` of the first + the estimated `n` of the second - the estimated union of the two.  There are some tricky edge conditions, such as when one or both of the estimates of `n` is infinite.

## Usage Errors
There are several places that errors can creep into Bloom filter usage.

### Saturation errors

Saturation errors arise from underestimating the number of items that will be placed into the filter.  Let’s define  “saturation” as the number of items merged into a filter divided by the number of items specified in the Shape.  Then once `n` items have been inserted the filter is at a saturation of 1.  As the saturation increases the false positive rate increases.   Using the calculation for the false positive rate noted above, we can calculate the expected false positive rate for the various saturations.  For an interactive version see Thomas Hursts online calculator.

For Bloom filters defined as `k=17` and `n=3` the calculation yields `m=72`, and `p=0.00001`.  As the saturation increases the rates of false positives increase as per the following table:

| Saturation | Probability of false positive |
|------------|-------------------------------|
| 1 | 0.000010 |
| 2 | 0.008898 |
| 3 | 0.115070 |
| 4 | 0.356832 |
| 5 | 0.606726 |


The table shows that the probability of false positives is two orders of magnitude larger when the saturation reaches two.  After five times the estimated number of items have been added the false positive rate is so high as to make the filter useless.

### Hashing errors

A second focus of errors is the generation of the hashes.

If a combinatorial hashing algorithm is used and the number of bits is significantly higher than the midpoint of the hash values then the generated values will be weighted to the lower bits.  For example if byte values were used to set the increment and the initial values but the number of bits was in excess of 255, then the higher valued bits could not be selected on the first hash, and in all cases values far above 255 would be rarely selected.

## Review
So far we have covered what Bloom filters are, why we use them, how to construct and use them, explored a few statistics, and looked at potential problems arising from usage errors.  In the next post we will see how the Apache Commons Collections code implements Bloom filters and look at how to implement the common usage patterns using the library.

## Footnotes

<span>
<ol class="footnotes>">
<li><a id='fn1'></a>
Burton H. Bloom. 1970.   “Space/Time Trade-offs in Hash Coding with Allowable Errors". Commun. ACM, 13, 7 (July 1970), 422–426
</li>
<li><a id='fn2'></a>
Michael Mitzenmacher and Eli Upfal. 2005. Probability and computing: Randomized algorithms and probabilistic analysis. Cambridge University Press, Cambridge, Cambridgeshire, UK. 109–111, 308 pages.
</li>
<li><a id='fn3'></a>
L. L. Gremillion, “Designing a Bloom filter for differential file access,” Communications of the ACM, vol. 25, no. 7, pp. 600-604, 1982. 
</li>
<li><a id='fn4'></a>
J. K. Mullin, “A second look at Bloom filters,” Communications of the ACM, vol. 26, no. 8, 1983
</li>
<li><a id='fn5'></a>
P. Bose, H. Guo, E. Kranakis, “On the false-positive rate of Bloom filters,” Information Processing Letters, vol. 108, no. 4, pp. 210-213, 2008.
</li>
<li><a id='fn6'></a>
Henrik Stranneheim, Max Käller, Tobias Allander, Björn Andersson, Lars Arvestad, and Joakim Lundeberg. 2015. Classification of DNA sequences using Bloom filters. Bioinfomatics 26, 13 (July 2015), 1595–1600. <a href="https://doi.org/10.1093/bioinformatics/btq230">https://doi.org/10.1093/bioinformatics/btq230</a>
</li>
<li><a id='fn7'></a>
Justin Chu, Sara Sadeghi, Anthony Raymond, Shaun D. Jackman, Ka Ming Nip, Richard Mar, Hamid Mohamadi, Yaron S. Butterfield, A. Gordon Robertson, and Inanç Birol. 2014. BioBloom tools: fast, accurate and memory-efficient host species sequence screening using bloom filters.  Bioinfomatics 30, 23 (Dec. 2014), 302–304. <a href="https://doi.org/10.1093/bioinformatics/btu558">https://doi.org/10.1093/bioinformatics/btu558</a>
</li>
<li><a id='fn8'></a>
Brad Solomon and Carl Kingsford. 2016. Fast Search of Thousands of Short-Read Sequencing Experiments. Nature Biotechnology 34, 3 (March 2016), 300–302. <a href="https://doi.org/10.1038/nbt.3442">https://doi.org/10.1038/nbt.3442</a>
</li>
<li><a id='fn9'></a>
Steven M Bellovin and William R Cheswick. 2004. Privacy-Enchanced Searches Using Encrypted Bloom Filters". <a href="https://mice.cs.columbia.edu/getTechreport.php?techreportID=483">https://mice.cs.columbia.edu/getTechreport.php?techreportID=483</a>
</li>
<li><a id='fn10'></a>
Arisa Tajima, Hiroki Sato, and Hayato Yamana. 2018. Privacy-Preserving Join Processing over outsourced private datasets with Fully Homomorphic Encryption and Bloom Filters. <a href="https://db-event.jpn.org/deim2018/data/papers/201.pdf">https://db-event.jpn.org/deim2018/data/papers/201.pdf</a>
</li>
<li><a id='fn11'></a>
Jamie Talbot, Jul 15, 2015, What are Bloom filters? : A tale of code, dinner, and a favour with unexpected consequences., The Medium Blog, <a href="https://blog.medium.com/what-are-bloom-filters-1ec2a50c68ff">https://blog.medium.com/what-are-bloom-filters-1ec2a50c68ff</a>
</li>
<li><a id='fn12'></a>
BitcoinDeveloper, Documentation, https://developer.bitcoin.org/search.html?q=bloom+filter
</li>
<li><a id='fn13'></a>
<a href="https://en.wikipedia.org/wiki/Bloom_filter#Cache_filtering">https://en.wikipedia.org/wiki/Bloom_filter#Cache_filtering</a>
</li>
<li><a id='fn4'></a>
K. Nandhini and R. Balasubramaniam, "Malicious Website Detection Using Probabilistic Data Structure Bloom Filter," 2019 3rd International Conference on Computing Methodologies and Communication (ICCMC), Erode, India, 2019, pp. 311-316, doi: 10.1109/ICCMC.2019.8819818.
</li>
<li><a id='fn15'></a>
Thomas Hurst, Bloom Filter Calculator. <a href="https://hur.st/bloomfilter/">https://hur.st/bloomfilter/</a>
</li>
</ol>
</span> 
