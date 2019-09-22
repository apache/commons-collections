package org.apache.commons.collections4.bloomfilters.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.collections4.bloomfilters.StandardBloomFilter;
import org.apache.commons.collections4.bloomfilters.FilterConfig;
import org.apache.commons.collections4.bloomfilters.ProtoBloomFilter;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for collections that do not accept duplicates
 *
 */
public class BloomCollectionTestNoDups extends AbstractBloomCollectionNoDuplicatesTest {

    public BloomCollectionTestNoDups() {
        super( 2L, 2L );
    }
    
    @Before
    public void setup() {
        FilterConfig filterConfig = new FilterConfig(5,5);
        super.setup( new BloomCollection<String>( new HashSet<String>(), filterConfig, FUNC ), filterConfig );
    }
    
}
