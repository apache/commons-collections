package org.apache.commons.collections4.bloomfilters.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.function.Consumer;

import org.apache.commons.collections4.bloomfilters.collections.CollectionStats.Action;
import org.apache.commons.collections4.bloomfilters.collections.CollectionStats.ActionMapper;
import org.apache.commons.collections4.bloomfilters.collections.CollectionStats.Change;
import org.junit.Before;
import org.junit.Test;

public class CollectionStatsTest {

    private CollectionStats stats;

    @Before
    public void setup() {
        stats = new CollectionStats();
    }

    @Test
    public void addConsumer() {
        CountingAction countingAction = new CountingAction();
        stats.addConsumer( countingAction );
        stats.insert();
        assertEquals( 1, countingAction.insertCount );
        stats.delete();
        assertEquals( 1, countingAction.deleteCount );
        stats.delete(3);
        assertEquals( 4, countingAction.deleteCount );
        stats.clear();
        assertEquals( 1, countingAction.clearCount );

    }


    @Test
    public void clear() {
        stats.insert();
        stats.delete();

        assertEquals( 1, stats.getInsertCount());
        assertEquals( 1, stats.getDeleteCount());

        CountingAction countingAction = new CountingAction();
        stats.addConsumer(countingAction);
        stats.clear();

        assertEquals( 0, stats.getInsertCount());
        assertEquals( 0, stats.getDeleteCount());
        assertEquals( 1, countingAction.clearCount );
    }

    @Test
    public void delete() {
        assertEquals( 0, stats.getDeleteCount() );
        stats.delete();
        assertEquals( 1, stats.getDeleteCount() );
    }

    @Test
    public void delete_long() {
        assertEquals( 0, stats.getDeleteCount() );
        stats.delete( 4 );
        assertEquals( 4, stats.getDeleteCount() );
    }

    @Test
    public void getActionMapper() {
        ActionMapper mapper = stats.getActionMapper();
        assertEquals( 0, stats.getInsertCount() );
        assertEquals( 0, stats.getDeleteCount() );
        mapper.accept( new Action( Change.delete, 1 ));
        assertEquals( 0, stats.getInsertCount() );
        assertEquals( 1, stats.getDeleteCount() );
        mapper.accept( new Action( Change.insert, 1 ));
        assertEquals( 0, stats.getInsertCount() );
        assertEquals( 1, stats.getDeleteCount() );
        stats.insert();
        mapper.accept( new Action( Change.clear, 1 ));
        assertEquals( 1, stats.getInsertCount() );
        assertEquals( 1, stats.getDeleteCount() );
    }

    @Test
    public void getDeleteCount() {
        assertEquals( 0, stats.getDeleteCount() );
        stats.delete();
        assertEquals( 1, stats.getDeleteCount() );
    }

    @Test
    public void getFilterCount() {
        assertEquals( 0, stats.getFilterCount() );
        stats.insert();
        assertEquals( 1, stats.getFilterCount() );
        stats.delete();
        assertEquals( 0, stats.getFilterCount() );
        stats.insert();
        assertEquals( 1, stats.getFilterCount() );
        stats.insert();
        assertEquals( 2, stats.getFilterCount() );
        stats.clear();
        assertEquals( 0, stats.getFilterCount() );
    }

    @Test
    public void getTxtCount() {
        assertEquals( 0, stats.getTxnCount() );
        stats.delete();
        assertEquals( 1, stats.getTxnCount() );
        stats.insert();
        assertEquals( 2, stats.getTxnCount() );
        stats.delete(2);
        assertEquals( 4, stats.getTxnCount() );
        stats.clear();
        assertEquals( 0, stats.getTxnCount() );
    }

    @Test
    public void insert() {
        assertEquals( 0, stats.getInsertCount() );
        stats.insert();
        assertEquals( 1, stats.getInsertCount() );
    }

    @Test
    public void sameValues() {
        CollectionStats stats2 = new CollectionStats();
        assertTrue( stats.sameValues(stats2));
        stats.insert();
        assertFalse( stats.sameValues(stats2));
        stats2.insert();
        assertTrue( stats.sameValues(stats2));
        stats.delete();
        assertFalse( stats.sameValues(stats2));
        stats2.delete();
        assertTrue( stats.sameValues(stats2));
    }

    private class CountingAction implements Consumer<Action> {

        int clearCount;
        int insertCount;
        int deleteCount;

        @Override
        public void accept(Action action) {
            switch( action.getChange() ) {
            case clear:
                clearCount += action.getCount();
                break;
            case insert:
                insertCount += action.getCount();
                break;
            case delete:
                deleteCount += action.getCount();
                break;
            default:
                throw new IllegalStateException( "Invalid action "+action.getChange());
            }
        }

    }
}
