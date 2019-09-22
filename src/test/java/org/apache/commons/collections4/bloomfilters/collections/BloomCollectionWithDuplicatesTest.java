package org.apache.commons.collections4.bloomfilters.collections;

import java.util.ArrayList;
import org.apache.commons.collections4.bloomfilters.FilterConfig;
import org.junit.Before;

public class BloomCollectionWithDuplicatesTest extends AbstractBloomCollectionWithDuplicatesTest {


    public BloomCollectionWithDuplicatesTest()
    {
        super( 3L, 3L );
    }
    @Before
    public void setup() {
        FilterConfig filterConfig = new FilterConfig(5,5);
        super.setup( new BloomCollection<String>( new ArrayList<String>(), filterConfig, FUNC ), filterConfig );
    }



}
