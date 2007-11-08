package org.apache.commons.collections;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;

public abstract class AbstractDecoratedCollectionTest<C> {
    /**
     * The {@link Collection} being decorated.
     */
    protected Collection<C> original;
    /**
     * The Collection under test that decorates {@link #original}.
     */
    protected Collection<C> decorated;
    
    @Before
    public void setUpDecoratedCollection() throws Exception {
        original = new ArrayList<C>();
    }
}
