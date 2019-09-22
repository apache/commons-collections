package org.apache.commons.collections4.bloomfilters.collections;

import org.apache.commons.collections4.bloomfilters.FilterConfig;
import org.apache.commons.collections4.bloomfilters.collections.BloomNestedCollection.BloomArrayListFactory;
import org.junit.Before;

public class BloomNestedCollectionWithDuplicatesDiffConfig extends AbstractBloomCollectionWithDuplicatesTest {


    FilterConfig gateConfig = new FilterConfig( 25, 5 );
    FilterConfig bucketConfig = new FilterConfig( 5, 5 );
    
    public BloomNestedCollectionWithDuplicatesDiffConfig() {
        super( 2L, 3L );
    }
    @Before
    public void setup() {        
        super.setup( new BloomNestedCollection<String>(FUNC, 3, gateConfig, new BloomArrayListFactory<String>(FUNC, bucketConfig) ), gateConfig );
    }

}
