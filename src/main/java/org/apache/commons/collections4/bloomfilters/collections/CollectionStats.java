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
package org.apache.commons.collections4.bloomfilters.collections;

import java.util.function.Consumer;

/**
 * Statistics for a collection.
 * <p>
 * The statistics track the number of inserted and deleted items in the filter.
 * From this it is possible to calculate the higher rate of false positives that
 * occur when a bloom filter has too many inserts or a non counting bloom filter
 * has any deletes.
 * </p>
 * <p>
 * Statistics are usually retrieved CollectionConfig object
 * <p>
 * In addition the {@code CollectionStats} will notify @{code
 * Consumer&lt;Action>} when any change to the collection occurs.
 * </p>
 */
public final class CollectionStats {
    enum Change {
        insert, delete, clear
    };

    private long filterInserts;
    private long filterDeletes;
    private Consumer<Action> changeNotification;
    private ActionMapper actionMapper;

    /**
     * Constructor.
     */
    public CollectionStats() {
        this(null);
    }

    /**
     * Constructor with change notification.
     *
     * @param changeNotification a {@code Consumer} to be notified when the
     *                           collection changes.
     */
    public CollectionStats(Consumer<Action> changeNotification) {
        filterInserts = 0;
        filterDeletes = 0;
        this.changeNotification = changeNotification;
        this.actionMapper = new ActionMapper();
    }

    /**
     * Adds a consumer to the change notification list.
     *
     * @param consumer the consumer to add.
     */
    public synchronized void addConsumer(Consumer<Action> consumer) {
        if (changeNotification == null) {
            changeNotification = consumer;
        } else {
            changeNotification = changeNotification.andThen(consumer);
        }
    }

    /**
     * Verify the values of the collection stats are the same.
     * @param other the other collection stats to compare to.
     * @return true if the values are the same.
     */
    public boolean sameValues( CollectionStats other )
    {
        return this.filterInserts == other.filterInserts &&
                this.filterDeletes == other.filterDeletes;
    }

    // notify there is a change.
    private synchronized void notifyChange(Change change, long count) {
        if (changeNotification != null) {
            changeNotification.accept(new Action(change, count));
        }
    }

    /**
     * Get the ActionMapper for this collection.
     * @return the ActionMapper instance for this collection.
     */
    public synchronized ActionMapper getActionMapper() {
        return actionMapper;
    }

    /**
     * Clear all the data. Does not reset the change notification list. Does notify
     * the notification list that the data has been reset.
     */
    public void clear() {
        filterInserts = 0;
        filterDeletes = 0;
        notifyChange(Change.clear, 1);
    }

    /**
     * Present a long as an int value. If the long is greater than Integer.MAX_VALUE
     * then return Integer.MAX_VALUE. If it is less than Integer.MIN_VALUE return
     * Integer.MIN_VALUE.
     *
     * @param l the long to convert.
     * @return the integer version.
     */
    public static int asInt(long l) {
        return (l > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (l < Integer.MIN_VALUE) ? Integer.MIN_VALUE : (int) l;
    }

    /**
     * Get the number filters. This is the number of inserts less the number of
     * deletes.
     *
     * @return the filter count.
     */
    public long getFilterCount() {
        return filterInserts - filterDeletes;
    }

    /**
     * Increment the number of inserts. Will only count inserts to Long.MAX_VALUE,
     * after that value no further incrementing is performed. A change notification
     * is always generated.
     */
    public void insert() {
        if (filterInserts < Long.MAX_VALUE) {
            filterInserts++;
        }
        notifyChange(Change.insert, 1);
    }

    /**
     * Increment the number of deletes. Will only count deletes to Long.MAX_VALUE,
     * after that value no further incrementing is performed. A change notification
     * is always generated.
     */
    public void delete() {
        delete(1);
    }

    /**
     * Increment the number of deletes. Will only count deletes to Long.MAX_VALUE,
     * after that value no further incrementing is performed. A change notification
     * is always generated.
     *
     * @param count the number of deletes to increment by.
     */
    public void delete(long count) {
        if (filterDeletes < Long.MAX_VALUE - count) {
            filterDeletes += count;
        } else {
            filterDeletes = Long.MAX_VALUE;
        }
        notifyChange(Change.delete, count);
    }

    /**
     * Get the insert count.
     *
     * @return the insert count
     */
    public long getInsertCount() {
        return filterInserts;
    }

    /**
     * Get the delete count.
     *
     * @return the delete count.
     */
    public long getDeleteCount() {
        return filterDeletes;
    }

    /**
     * Get the transaction count. This is the sum of the number of deletes and the
     * number of inserts. Will return Long.MAX_VALUE if the transaction count
     * exceeds Long.MAX_VALUE;
     *
     * @return the transaction count.
     */
    public long getTxnCount() {
        if (filterInserts < (Long.MAX_VALUE - filterDeletes)) {
            return filterInserts + filterDeletes;
        }
        return Long.MAX_VALUE;
    }

    /**
     * Definition of an action takeen on the collection.
     */
    public static class Action {
        private Change change;
        private long count;

        /**
         * Constructor.
         *
         * @param change the type of Change that triggered the action.
         * @param count  the number of changes.
         */
        public Action(Change change, long count) {
            this.change = change;
            this.count = count;
        }

        /**
         * Get the change type for the action.
         *
         * @return the change type.
         */
        public Change getChange() {
            return change;
        }

        /**
         * Get teh number of changes of the specifed type.
         *
         * @return the number of changes.
         */
        public long getCount() {
            return count;
        }
    }

    /**
     * Maps an Action into this statistics. This class is used when there is a
     * collection of BloomFilterGated collections. An instance of this class is
     * added as a consumer of action to the sub collections so that notifications
     * flow to the containing status.
     *
     */
    public class ActionMapper implements Consumer<Action> {

        /**
         * Do not allow public construction.
         */
        private ActionMapper() {
        }

        @Override
        public void accept(Action action) {
            switch (action.getChange()) {
            case delete:
                delete(action.getCount());
                break;
            case insert: // ignore -- insert handled by local merge
            case clear: // ignore -- clear only occurs at top level.
            default:
                break;
            }
        }

    }
}
