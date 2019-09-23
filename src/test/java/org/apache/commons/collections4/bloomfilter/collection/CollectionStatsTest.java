/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.bloomfilter.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.function.Consumer;

import org.apache.commons.collections4.bloomfilter.collection.CollectionStatistics;
import org.apache.commons.collections4.bloomfilter.collection.CollectionStatistics.Action;
import org.apache.commons.collections4.bloomfilter.collection.CollectionStatistics.ActionMapper;
import org.apache.commons.collections4.bloomfilter.collection.CollectionStatistics.Change;
import org.junit.Before;
import org.junit.Test;

public class CollectionStatsTest {

    private CollectionStatistics stats;

    @Before
    public void setup() {
        stats = new CollectionStatistics();
    }

    @Test
    public void addConsumer() {
        CountingAction countingAction = new CountingAction();
        stats.addConsumer( countingAction );
        CountingAction countingAction2 = new CountingAction();
        stats.addConsumer( countingAction2 );
        stats.insert();
        assertEquals( 1, countingAction.insertCount );
        assertEquals( 1, countingAction2.insertCount );
        stats.delete();
        assertEquals( 1, countingAction.deleteCount );
        assertEquals( 1, countingAction2.deleteCount );
        stats.delete(3);
        assertEquals( 4, countingAction.deleteCount );
        assertEquals( 4, countingAction2.deleteCount );
        stats.clear();
        assertEquals( 1, countingAction.clearCount );
        assertEquals( 1, countingAction2.clearCount );

    }

    @Test
    public void asInt() {
        assertEquals( Integer.MAX_VALUE, CollectionStatistics.asInt( Long.MAX_VALUE ));
        assertEquals( Integer.MIN_VALUE, CollectionStatistics.asInt( Long.MIN_VALUE ));
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
        stats.delete( Long.MAX_VALUE - 3 );
        assertEquals( Long.MAX_VALUE, stats.getDeleteCount() );
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
        stats.delete( Long.MAX_VALUE-3 );
        assertEquals( Long.MAX_VALUE, stats.getTxnCount() );
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
        CollectionStatistics stats2 = new CollectionStatistics();
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
