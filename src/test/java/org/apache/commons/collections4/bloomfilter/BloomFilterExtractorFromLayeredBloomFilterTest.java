/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.bloomfilter;

import org.apache.commons.collections4.bloomfilter.LayerManager.Builder;
import org.apache.commons.lang3.ArrayUtils;

public class BloomFilterExtractorFromLayeredBloomFilterTest extends AbstractBloomFilterExtractorTest {

    @Override
    protected BloomFilterExtractor createUnderTest(final BloomFilter... filters) {
        final Builder<SimpleBloomFilter> builder = LayerManager.<SimpleBloomFilter>builder();
        final BloomFilter bloomFilter0 = ArrayUtils.get(filters, 0);
        final Shape shape0 = bloomFilter0 != null ? bloomFilter0.getShape() : null;
        if (shape0 != null) {
            // Avoid an NPE in test code and let the domain classes decide what to do when there is no supplier set.
            builder.setSupplier(() -> new SimpleBloomFilter(shape0));
        }
        final LayerManager<SimpleBloomFilter> layerManager = builder.setExtendCheck(LayerManager.ExtendCheck.advanceOnPopulated())
                .setCleanup(LayerManager.Cleanup.noCleanup()).get();
        final LayeredBloomFilter underTest = new LayeredBloomFilter(shape0, layerManager);
        for (final BloomFilter bf : filters) {
            underTest.merge(bf);
        }
        return underTest;
    }
}
