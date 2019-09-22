package org.apache.commons.collections4.bloomfilters.collections;

import org.apache.commons.collections4.bloomfilters.FilterConfig;
import org.apache.commons.collections4.bloomfilters.collections.BloomNestedCollection.BloomHashSetFactory;
import org.junit.Before;

/**
 * Tests BloomNestedCollection that does not accept duplicates and that uses different filter configurations
 * for the gate and the buckets.
 */
public class BloomNestedCollectionNoDuplicatesDiffConfig extends AbstractBloomCollectionNoDuplicatesTest {


    FilterConfig gateConfig = new FilterConfig( 25, 5 );
    FilterConfig bucketConfig = new FilterConfig( 5, 5 );

    /**
     * Constructor.
     */
    public BloomNestedCollectionNoDuplicatesDiffConfig() {
        super( 1L, 2L );
    }

    /**
     * setup
     */
    @Before
    public void setup() {
        super.setup( new BloomNestedCollection<String>(FUNC, 3, gateConfig, new BloomHashSetFactory<String>(FUNC, bucketConfig) ), gateConfig );
    }

}
