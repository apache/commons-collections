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

import java.util.function.Consumer;

/**
 * Statistics for a collection.
 * <p>
 * The statistics track the number of inserted and deleted items in the filter.
 * From this it is possible to calculate the higher rate of false positives that
 * occur when a Bloom filter has too many inserts or a non counting Bloom filter
 * has any deletes.
 * </p>
 * <p>
 * Statistics are usually retrieved from a {@link BloomFilterGatedConfiguration} object
 * <p>
 * In addition the {@code BloomCollectionStatistics} will notify @{code
 * Consumer&lt;Action&gt;} when any change to the collection occurs.
 * </p>
 *
 * @since 4.5
 */
public final class BloomFilterGatedStatistics {
    /**
     * The type of change being recorded.  Used in an Action.
     *
     * @since 4.5
     */
    enum Change {
        INSERT, DELETE, CLEAR
    };

    /**
     * The number of inserts that have been made into the bloom filter.
     */
    private long filterInserts;

    /**
     * The number of deletes that have been made into the bloom filter.
     */
    private long filterDeletes;

    /**
     * A consumer of {@code Action} that respond to {@code Change}.
     */
    private Consumer<Action> changeNotification;

    /**
     * An implementation of {@code ActionMapper} that uses the {@code Action} to modify
     * the values of this {@code BloomCollectionStatistics} instance. This is primarily
     * used for nested collection implementations to record changes that occur in the
     * nested collections.
     */
    private ActionMapper actionMapper;

    /**
     * Construct a {@code BloomCollectionStatistics} without a registered change notification consumer.
     */
    public BloomFilterGatedStatistics() {
        this(null);
    }

    /**
     * Construct a {@code BloomCollectionStatistics} with a registered change notification
     * consumer.
     *
     * @param changeNotification a {@code Consumer} to be notified when the collection
     * changes.
     */
    public BloomFilterGatedStatistics(Consumer<Action> changeNotification) {
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
     * Verifies the values of the {@code BloomCollectionStatistics} are the same.
     *
     * @param other the other {@code BloomCollectionStatistics} to compare to.
     * @return true if the values are the same.
     */
    public boolean sameValues(BloomFilterGatedStatistics other) {
        return this.filterInserts == other.filterInserts && this.filterDeletes == other.filterDeletes;
    }

    /**
     * Notifies of a change.
     *
     * @param change The {@code Change} to notify about.
     * @param count The number of {@code Change} events that are being reported.
     */
    private synchronized void notifyChange(Change change, long count) {
        if (changeNotification != null) {
            changeNotification.accept(new Action(change, count));
        }
    }

    /**
     * Gets the @{code ActionMapper} for this collection.
     *
     * @return the @{code ActionMapper} instance for this collection.
     */
    public synchronized ActionMapper getActionMapper() {
        return actionMapper;
    }

    /**
     * Clears all the data. Does not reset the change notification list. Does notify
     * the notification list that the data has been reset.
     */
    public void clear() {
        filterInserts = 0;
        filterDeletes = 0;
        notifyChange(Change.CLEAR, 1);
    }

    /**
     * Converts a long value into an int value. If the long is greater than
     * Integer.MAX_VALUE then return Integer.MAX_VALUE. If it is less than
     * Integer.MIN_VALUE return Integer.MIN_VALUE.
     *
     * @param lValue the long value to convert.
     * @return the integer version.
     */
    public static int clampToInteger(long lValue) {
        if (lValue > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (lValue < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) lValue;
    }

    /**
     * Gets the number filters. This is the number of inserts less the number of
     * deletes.
     *
     * @return the filter count.
     */
    public long getFilterCount() {
        return filterInserts - filterDeletes;
    }

    /**
     * Increments the number of inserts. Will only count inserts to Long.MAX_VALUE, after
     * that value no further incrementing is performed. A change notification is always
     * generated.
     */
    public void insert() {
        if (filterInserts < Long.MAX_VALUE) {
            filterInserts++;
        }
        notifyChange(Change.INSERT, 1);
    }

    /**
     * Increments the number of deletes. Will only count deletes to Long.MAX_VALUE,
     * after that value no further incrementing is performed. A change notification
     * is always generated.
     */
    public void delete() {
        delete(1);
    }

    /**
     * Increments the number of deletes. Will only count deletes to Long.MAX_VALUE, after
     * that value no further incrementing is performed. A change notification is always
     * generated.
     *
     * @param count the number of deletes to increment by.
     */
    public void delete(long count) {
        if (filterDeletes < Long.MAX_VALUE - count) {
            filterDeletes += count;
        } else {
            filterDeletes = Long.MAX_VALUE;
        }
        notifyChange(Change.DELETE, count);
    }

    /**
     * Gets the insert count.
     *
     * @return the insert count
     */
    public long getInsertCount() {
        return filterInserts;
    }

    /**
     * Gets the delete count.
     *
     * @return the delete count.
     */
    public long getDeleteCount() {
        return filterDeletes;
    }

    /**
     * Gets the transaction count. This is the sum of the number of deletes and the number
     * of inserts. Will return Long.MAX_VALUE if the transaction count exceeds
     * Long.MAX_VALUE;
     *
     * @return the transaction count.
     */
    public long getTransactionCount() {
        if (filterInserts < (Long.MAX_VALUE - filterDeletes)) {
            return filterInserts + filterDeletes;
        }
        return Long.MAX_VALUE;
    }

    /**
     * Definition of an action taken on the collection.
     *
     * @since 4.5
     */
    public static class Action {
        /**
         * The change type that is being reported.
         */
        private Change change;

        /**
         * The number of changes being reported.
         */
        private long count;

        /**
         * Constructor.
         *
         * @param change the type of {@code Change} that triggered the action.
         * @param count  the number of changes.
         */
        public Action(Change change, long count) {
            this.change = change;
            this.count = count;
        }

        /**
         * Gets the {@code Change} type for the action.
         *
         * @return the change type.
         */
        public Change getChange() {
            return change;
        }

        /**
         * Gets the number of changes of the specified type.
         *
         * @return the number of changes.
         */
        public long getCount() {
            return count;
        }
    }

    /**
     * Maps an {@code Action} into this statistics. This class is used when there is a
     * collection of {@code BloomFilterGated} collections. An instance of this class is
     * added as a consumer of action to the sub collections so that notifications flow to
     * the containing status.
     *
     * @since 4.5
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
            case DELETE:
                delete(action.getCount());
                break;
            case INSERT: // ignore -- insert handled by local merge
            case CLEAR: // ignore -- clear only occurs at top level.
            default:
                break;
            }
        }

    }
}
