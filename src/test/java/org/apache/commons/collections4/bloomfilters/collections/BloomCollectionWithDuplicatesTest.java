package org.apache.commons.collections4.bloomfilters.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.collections4.bloomfilters.BloomFilter;
import org.apache.commons.collections4.bloomfilters.FilterConfig;
import org.apache.commons.collections4.bloomfilters.ProtoBloomFilter;
import org.junit.Before;
import org.junit.Test;

public class BloomCollectionWithDuplicatesTest extends AbstractBloomCollectionWithDuplicatesTest {

    
    @Before
    public void setup() {
        FilterConfig filterConfig = new FilterConfig(5,5);
        super.setup( new BloomCollection<String>( new ArrayList<String>(), filterConfig, FUNC ), filterConfig );
    }

   
    
}
