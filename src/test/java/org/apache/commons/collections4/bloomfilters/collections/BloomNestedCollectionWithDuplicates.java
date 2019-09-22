package org.apache.commons.collections4.bloomfilters.collections;

import org.apache.commons.collections4.bloomfilters.FilterConfig;
import org.junit.Before;

public class BloomNestedCollectionWithDuplicates extends AbstractBloomCollectionWithDuplicatesTest {


    FilterConfig gateConfig = new FilterConfig( 25, 5 );
    FilterConfig bucketConfig = new FilterConfig( 5, 5 );
    BloomNestedCollection<String> bloomC;

    @Before
    public void setup() {
        super.setup( new BloomNestedCollection<String>(FUNC, 3, gateConfig, bucketConfig ), gateConfig );
    }

}
