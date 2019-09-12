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
import static org.junit.Assert.assertTrue;

import java.util.BitSet;

import org.junit.Test;

public class HashTest {


	@Test
	public void populateTest() {
		Hash hash = new Hash( 1, 2 );
		// n = 1
		// p = 0.091848839 (1 in 11)
		// m = 5 (1B)
		// k = 3
		FilterConfig fc = new FilterConfig( 1, 11 );
		BitSet bs = new BitSet();
		
		assertEquals( 1, hash.h1());
		assertEquals( 2, hash.h2());
		
		hash.populate( bs, fc );
		// there are 3 iterations so 
		 // 1 mod 5 = 1
		 // 3 mod 5 = 3
		 // 5 mod 5 = 0
		 // so bits 1,3, and 0 should be on
		 assertEquals( 3, bs.cardinality() );
		 assertTrue( bs.get(0));
		 assertTrue( bs.get(1));
		 assertTrue( bs.get(3));
	
	}
}
