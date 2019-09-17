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
package org.apache.commons.collections4.bloomfilters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Objects;

import org.junit.Test;
import org.apache.commons.collections4.bloomfilters.ProtoBloomFilter.Builder;
import org.apache.commons.collections4.bloomfilters.ProtoBloomFilter.Hash;

public class BuilderTest {

	private static final Hash HELLO_HASH = new Hash( 3871253994707141660L, -6917270852172884668L );
	
	@Test
	public void updateTest_byte()
	{
		Builder builder = new Builder().update( (byte) 0x1 );
		assertEquals( 1, builder.hashes.size() );
		assertEquals( new Hash( 8849112093580131862L, 8613248517421295493L), builder.hashes.iterator().next());
	}
	
	@Test
	public void updateTest_byteArray()
	{
		Builder builder = new Builder().update( "Hello".getBytes());
		assertEquals( 1, builder.hashes.size() );
		assertEquals( HELLO_HASH, builder.hashes.iterator().next());
	}
	
	@Test
	public void updateTest_ByteBuffer()
	{
		Builder builder = new Builder().update( ByteBuffer.wrap( "Hello".getBytes()));
		assertEquals( 1, builder.hashes.size() );
		assertEquals( HELLO_HASH, builder.hashes.iterator().next());
	}
	
	@Test
	public void updateTest_ProtoBloomFilter()
	{
		ProtoBloomFilter pbf = new Builder().update("Hello").build();
		Builder builder = new Builder().update(pbf);
		assertEquals( 1, builder.hashes.size() );
		assertEquals( HELLO_HASH, builder.hashes.iterator().next());
	}
	
	@Test
	public void updateTest_String()
	{
		Builder builder = new Builder().update("Hello");
		assertEquals( 1, builder.hashes.size() );
		assertEquals( HELLO_HASH, builder.hashes.iterator().next());		
	}
	
	@Test
	public void buildTest_byte()
	{
		ProtoBloomFilter pbf = new Builder().build( (byte) 0x1 );
		assertEquals( 1, pbf.getItemCount() );
		assertEquals( new Hash( 8849112093580131862L, 8613248517421295493L), pbf.hashes.iterator().next());
	}
	
	@Test
	public void buildTest_byteArray()
	{
		ProtoBloomFilter pbf = new Builder().build( "Hello".getBytes());
		assertEquals( 1, pbf.getItemCount() );
		assertEquals( HELLO_HASH, pbf.hashes.iterator().next());
	}
	
	@Test
	public void buildTest_ByteBuffer()
	{/*
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

		ProtoBloomFilter pbf = new Builder().build( ByteBuffer.wrap( "Hello".getBytes()));
		assertEquals( 1, pbf.getItemCount() );
		assertEquals( HELLO_HASH, pbf.hashes.iterator().next());
	}
	
	@Test
	public void buildTest_ProtoBloomFilter()
	{
		ProtoBloomFilter pbf1 = new Builder().update("Hello").build();
		ProtoBloomFilter pbf = new Builder().build(pbf1);
		assertEquals( 1, pbf.getItemCount() );
		assertEquals( HELLO_HASH, pbf.hashes.iterator().next());
	}
	
	@Test
	public void buildTest_String()
	{
		ProtoBloomFilter pbf = new Builder().build("Hello");
		assertEquals( 1, pbf.getItemCount() );
		assertEquals( HELLO_HASH, pbf.hashes.iterator().next());		
	}
	
	@Test
	public void updateTest_LongString()
	{
		Builder builder = new Builder().update("Now is the time for all good men to come to the aid of their country");
		assertEquals( 1, builder.hashes.size() );
		assertEquals( new Hash(-1735186738861022201L, -4338573967658373034L ), builder.hashes.iterator().next());		
	}
}
