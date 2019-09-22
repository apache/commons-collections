package org.apache.commons.collections4.bloomfilters.collections;

import org.apache.commons.collections4.bloomfilters.FilterConfig;
import org.apache.commons.collections4.bloomfilters.collections.BloomNestedCollection.BloomHashSetFactory;
import org.junit.Before;

public class BloomNestedCollectionNoDuplicatesSameConfig extends AbstractBloomCollectionNoDuplicatesTest {


    FilterConfig gateConfig = new FilterConfig( 25, 5 );
    FilterConfig bucketConfig = new FilterConfig( 25, 5 );

    public BloomNestedCollectionNoDuplicatesSameConfig() {
        super( 1L, 1L );
    }

    @Before
    public void setup() {
        super.setup( new BloomNestedCollection<String>(FUNC, 3, gateConfig, new BloomHashSetFactory<String>(FUNC, bucketConfig) ), gateConfig );
    }

}
