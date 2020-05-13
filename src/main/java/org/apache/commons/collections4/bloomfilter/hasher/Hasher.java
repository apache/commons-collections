/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.bloomfilter.hasher;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.PrimitiveIterator;

/**
 * A Hasher represents items of arbitrary byte size as a byte representation of
 * fixed size (a hash). The hash representations can be used to create indexes
 * for a Bloom filter.
 *
 * <p>The hash for each item is created using a hash function; use of different
 * seeds allows generation of different hashes for the same item. The hashes can
 * be dynamically converted into the bit index representation used by a Bloom
 * filter. The shape of the Bloom filter defines the number of indexes per item
 * and the range of the indexes. The hasher can generate the correct number of
 * indexes in the range required by the Bloom filter for each item it
 * represents.
 *
 * <p>Note that the process of generating hashes and mapping them to a Bloom
 * filter shape may create duplicate indexes. The hasher may generate fewer than
 * the required number of hash functions per item if duplicates have been
 * removed. Implementations of {@code iterator()} may return duplicate values
 * and may return values in a random order. See implementation javadoc notes as
 * to the guarantees provided by the specific implementation.
 *
 * <p>Hashers have an identity based on the hashing algorithm used.
 *
 * @since 4.5
 */
public interface Hasher {

    /**
     * A builder to build a hasher.
     *
     * <p>A hasher represents one or more items of arbitrary byte size. The builder
     * contains methods to collect byte representations of items. Each method to add
     * to the builder will add an entire item to the final hasher created by the
     * {@link #build()} method.</p>
     *
     * @param <H> the Hasher implementation that the builder will build.
     * @param <B> the Builder implementation that is returned from the {@code with()} methods.
     * @since 4.5
     */
    interface Builder<H extends Hasher, B extends Hasher.Builder<H, ?>> {

        /**
         * Builds the hasher from all the items.
         *
         * <p>This method will clear the builder for future use.
         *
         * @return the fully constructed hasher
         */
        H build();

        /**
         * Adds a byte array item to the hasher.
         *
         * @param item the item to add
         * @return a reference to this object
         */
        B with(byte[] item);

        /**
         * Adds a character sequence item to the hasher using the specified {@code charset}
         * encoding.
         *
         * @param item the item to add
         * @param charset the character set
         * @return a reference to this object
         */
        default B with(CharSequence item, Charset charset) {
            return with(item.toString().getBytes(charset));
        }

        /**
         * Adds a character sequence item to the hasher. Each 16-bit character is
         * converted to 2 bytes using little-endian order.
         *
         * @param item the item to add
         * @return a reference to this object
         */
        default B withUnencoded(CharSequence item) {
            int length = item.length();
            final byte[] bytes = new byte[length * 2];
            for (int i = 0; i < length; i++) {
                final char ch = item.charAt(i);
                bytes[i * 2] = (byte) ch;
                bytes[i * 2 + 1] = (byte) (ch >>> 8);
            }
            return with(bytes);
        }

        /**
         * Adds an integer into the hasher.  The integer is converted into 4 bytes
         * through the use of ByteBuffer.putInt.
         * @param data the integer to add.
         * @return a reference to this object
         * @see ByteBuffer#putInt
         */
        default B with(int data) {
            return with( ByteBuffer.allocate(Integer.BYTES).putInt( data ).array());
        }

        /**
         * Adds a long into the hasher.  The long is converted into 8 bytes
         * through the use of ByteBuffer.putLong.
         * @param data the long to add.
         * @return a reference to this object
         * @see ByteBuffer#putLong
         */
        default B with(long data) {
            return with( ByteBuffer.allocate(Long.BYTES).putLong( data ).array());
        }

        /**
         * Adds a double into the hasher.  The double is converted into 8 bytes
         * through the use of ByteBuffer.putDouble.
         * @param data the double to add.
         * @return a reference to this object
         * @see ByteBuffer#putDouble
         */
        default B with(double data) {
            return with( ByteBuffer.allocate(Double.BYTES).putDouble( data ).array());
        }

        /**
         * Adds a float into the hasher.  The float is converted into 4 bytes
         * through the use of ByteBuffer.putFloat.
         * @param data the float to add.
         * @return a reference to this object
         * @see ByteBuffer#putFloat
         */
        default B with(float data) {
            return with( ByteBuffer.allocate(Float.BYTES).putFloat( data ).array());
        }

        /**
         * Adds a char into the hasher.  The char is converted into 2 bytes
         * through the use of ByteBuffer.putChar.
         * @param data the char to add.
         * @return a reference to this object
         * @see ByteBuffer#putChar
         */
        default  B with(char data) {
            return with( ByteBuffer.allocate(Character.BYTES).putChar( data ).array());
        }

        /**
         * Adds a short into the hasher.  The short is converted into 2 bytes
         * through the use of ByteBuffer.putShort.
         * @param data the short to add.
         * @return a reference to this object
         * @see ByteBuffer#putShort
         */
        default B with(short data) {
            return with( ByteBuffer.allocate(Short.BYTES).putShort( data ).array());
        }

        /**
         * Adds a BigInteger into the hasher.
         * @param data the BigInteger to add.
         * @return a reference to this object
         */
        default B with(BigInteger data) {
            return with( data.toByteArray() );
        }

        /**
         * Adds a BigDecimal into the hasher.
         * The scale of the BigDecimal is placed in the buffer followed by the
         * byte array for the unscaled value.
         * @param data the BigDecimal to add.
         * @return a reference to this object
         */
        default B with(BigDecimal data) {
            byte[] value = data.unscaledValue().toByteArray();
            return with( ByteBuffer.allocate( Integer.BYTES+value.length )
                    .putInt( data.scale() ).put( value ).array() );
        }

    }

    /**
     * Gets an iterator of integers that are the bits to enable in the Bloom
     * filter based on the shape.
     *
     * <p>The iterator will create indexes within the range defined by the number of bits in
     * the shape. The total number of indexes will respect the number of hash functions per item
     * defined by the shape. However the count of indexes may not be a multiple of the number of
     * hash functions if the implementation has removed duplicates.
     *
     * <p>No guarantee is made as to order of values.
     *
     * @param shape the shape of the desired Bloom filter
     * @return the iterator of integers
     * @throws IllegalArgumentException if the hasher cannot generate indexes for
     * the specified @{@code shape}
     */
    PrimitiveIterator.OfInt iterator(Shape shape);

    /**
     * Gets the identify of the hash function used by the the hasher.
     *
     * @return the identity of the hash function
     */
    HashFunctionIdentity getHashFunctionIdentity();
}
