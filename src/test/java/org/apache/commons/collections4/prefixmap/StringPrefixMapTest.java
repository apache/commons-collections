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
package org.apache.commons.collections4.prefixmap;

import org.apache.commons.collections4.PrefixMap;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class StringPrefixMapTest extends AbstractPrefixMapTests {

    @Override
    PrefixMap<String> createPrefixMap(boolean caseSensitive) {
        return new StringPrefixMap<>(caseSensitive);
    }

    @Test
    public void testCaseINSensitiveLookup(){
        Map<String, String> prefixMap = new HashMap<>();
        prefixMap.put("ABC",    "Result ABC");
        prefixMap.put("ABCD",    "Result ABCD");
        // The ABCDE is missing !!!
        prefixMap.put("ABCDEF",  "Result ABCDEF");
        prefixMap.put("你", "Hello in Chinese");

        PrefixMap<String> prefixLookup = new StringPrefixMap<>(false);
        prefixLookup.putAll(prefixMap);

        // ----------------------------------------------------
        // Shortest Match
        checkShortest(prefixLookup, "MisMatch", null);

        checkShortest(prefixLookup, "你好",     "Hello in Chinese");

        // Same case
        checkShortest(prefixLookup, "A",       null);
        checkShortest(prefixLookup, "AB",      null);
        checkShortest(prefixLookup, "ABC",     "Result ABC");
        checkShortest(prefixLookup, "ABCD",    "Result ABC");
        checkShortest(prefixLookup, "ABCDE",   "Result ABC");
        checkShortest(prefixLookup, "ABCDEF",  "Result ABC");

        checkShortest(prefixLookup, "A-",      null);
        checkShortest(prefixLookup, "AB-",     null);
        checkShortest(prefixLookup, "ABC-",    "Result ABC");
        checkShortest(prefixLookup, "ABCD-",   "Result ABC");
        checkShortest(prefixLookup, "ABCDE-",  "Result ABC");
        checkShortest(prefixLookup, "ABCDEF-", "Result ABC");
        
        checkShortest(prefixLookup, "ABC\\t",  "Result ABC");
        checkShortest(prefixLookup, "ABC€",    "Result ABC");
        checkShortest(prefixLookup, "ABCD€",   "Result ABC");

        // Different case
        checkShortest(prefixLookup, "a",       null);
        checkShortest(prefixLookup, "ab",      null);
        checkShortest(prefixLookup, "abc",     "Result ABC");
        checkShortest(prefixLookup, "abcd",    "Result ABC");
        checkShortest(prefixLookup, "abcde",   "Result ABC");
        checkShortest(prefixLookup, "abcdef",  "Result ABC");

        checkShortest(prefixLookup, "a-",      null);
        checkShortest(prefixLookup, "ab-",     null);
        checkShortest(prefixLookup, "abc-",    "Result ABC");
        checkShortest(prefixLookup, "abcd-",   "Result ABC");
        checkShortest(prefixLookup, "abcde-",  "Result ABC");
        checkShortest(prefixLookup, "abcdef-", "Result ABC");

        checkShortest(prefixLookup, "abc\\t",  "Result ABC");
        checkShortest(prefixLookup, "abc€",    "Result ABC");
        checkShortest(prefixLookup, "abcd€",   "Result ABC");

        // ----------------------------------------------------
        // Longest Match
        checkLongest(prefixLookup, "MisMatch", null);

        checkLongest(prefixLookup, "你好",     "Hello in Chinese");

        // Same case
        checkLongest(prefixLookup, "A",       null);
        checkLongest(prefixLookup, "AB",      null);
        checkLongest(prefixLookup, "ABC",     "Result ABC");
        checkLongest(prefixLookup, "ABCD",    "Result ABCD");
        checkLongest(prefixLookup, "ABCDE",   "Result ABCD");
        checkLongest(prefixLookup, "ABCDEF",  "Result ABCDEF");

        checkLongest(prefixLookup, "A-",      null);
        checkLongest(prefixLookup, "AB-",     null);
        checkLongest(prefixLookup, "ABC-",    "Result ABC");
        checkLongest(prefixLookup, "ABCD-",   "Result ABCD");
        checkLongest(prefixLookup, "ABCDE-",  "Result ABCD");
        checkLongest(prefixLookup, "ABCDEF-", "Result ABCDEF");

        checkLongest(prefixLookup, "ABC\\t",  "Result ABC");
        checkLongest(prefixLookup, "ABC€",    "Result ABC");
        checkLongest(prefixLookup, "ABCD€",   "Result ABCD");

        // Different case
        checkLongest(prefixLookup, "a",       null);
        checkLongest(prefixLookup, "ab",      null);
        checkLongest(prefixLookup, "abc",     "Result ABC");
        checkLongest(prefixLookup, "abcd",    "Result ABCD");
        checkLongest(prefixLookup, "abcde",   "Result ABCD");
        checkLongest(prefixLookup, "abcdef",  "Result ABCDEF");

        checkLongest(prefixLookup, "a-",      null);
        checkLongest(prefixLookup, "ab-",     null);
        checkLongest(prefixLookup, "abc-",    "Result ABC");
        checkLongest(prefixLookup, "abcd-",   "Result ABCD");
        checkLongest(prefixLookup, "abcde-",  "Result ABCD");
        checkLongest(prefixLookup, "abcdef-", "Result ABCDEF");

        checkLongest(prefixLookup, "abc\\t",  "Result ABC");
        checkLongest(prefixLookup, "abc€",    "Result ABC");
        checkLongest(prefixLookup, "abcd€",   "Result ABCD");

        // ----------------------------------------------------
        // Contains
        checkContains(prefixLookup, "MisMatch", false);

        checkContains(prefixLookup, "你",      true);
        checkContains(prefixLookup, "你好",    false);

        // Same case
        checkContains(prefixLookup, "A",       false);
        checkContains(prefixLookup, "AB",      false);
        checkContains(prefixLookup, "ABC",     true);
        checkContains(prefixLookup, "ABCD",    true);
        checkContains(prefixLookup, "ABCDE",   false);
        checkContains(prefixLookup, "ABCDEF",  true);

        checkContains(prefixLookup, "A-",      false);
        checkContains(prefixLookup, "AB-",     false);
        checkContains(prefixLookup, "ABC-",    false);
        checkContains(prefixLookup, "ABCD-",   false);
        checkContains(prefixLookup, "ABCDE-",  false);
        checkContains(prefixLookup, "ABCDEF-", false);

        checkContains(prefixLookup, "ABC\\t",  false);
        checkContains(prefixLookup, "ABC€",    false);
        checkContains(prefixLookup, "ABCD€",   false);

        // Different case
        checkContains(prefixLookup, "a",       false);
        checkContains(prefixLookup, "ab",      false);
        checkContains(prefixLookup, "abc",     true);
        checkContains(prefixLookup, "abcd",    true);
        checkContains(prefixLookup, "abcde",   false);
        checkContains(prefixLookup, "abcdef",  true);

        checkContains(prefixLookup, "a-",      false);
        checkContains(prefixLookup, "ab-",     false);
        checkContains(prefixLookup, "abc-",    false);
        checkContains(prefixLookup, "abcd-",   false);
        checkContains(prefixLookup, "abcde-",  false);
        checkContains(prefixLookup, "abcdef-", false);

        checkContains(prefixLookup, "abc\\t",  false);
        checkContains(prefixLookup, "abc€",    false);
        checkContains(prefixLookup, "abcd€",   false);
    }

    @Test
    public void testCaseSensitiveLookup(){
        Map<String, String> prefixMap = new HashMap<>();
        prefixMap.put("ABC",    "Result ABC");
        prefixMap.put("ABCD",    "Result ABCD");
        // The ABCDE is missing !!!
        prefixMap.put("ABCDEF",  "Result ABCDEF");
        prefixMap.put("你", "Hello in Chinese");

        PrefixMap<String> prefixLookup = new StringPrefixMap<>(true);
        prefixLookup.putAll(prefixMap);

        // ----------------------------------------------------
        // Shortest Match
        checkShortest(prefixLookup, "MisMatch", null);

        checkLongest(prefixLookup, "你好",     "Hello in Chinese");

        // Same case
        checkShortest(prefixLookup, "A",       null);
        checkShortest(prefixLookup, "AB",      null);
        checkShortest(prefixLookup, "ABC",     "Result ABC");
        checkShortest(prefixLookup, "ABCD",    "Result ABC");
        checkShortest(prefixLookup, "ABCDE",   "Result ABC");
        checkShortest(prefixLookup, "ABCDEF",  "Result ABC");

        checkShortest(prefixLookup, "A-",      null);
        checkShortest(prefixLookup, "AB-",     null);
        checkShortest(prefixLookup, "ABC-",    "Result ABC");
        checkShortest(prefixLookup, "ABCD-",   "Result ABC");
        checkShortest(prefixLookup, "ABCDE-",  "Result ABC");
        checkShortest(prefixLookup, "ABCDEF-", "Result ABC");

        checkShortest(prefixLookup, "ABC\\t",  "Result ABC");
        checkShortest(prefixLookup, "ABC€",    "Result ABC");

        // Different case
        checkShortest(prefixLookup, "a",       null);
        checkShortest(prefixLookup, "ab",      null);
        checkShortest(prefixLookup, "abc",     null);
        checkShortest(prefixLookup, "abcd",    null);
        checkShortest(prefixLookup, "abcde",   null);
        checkShortest(prefixLookup, "abcdef",  null);

        checkShortest(prefixLookup, "a-",      null);
        checkShortest(prefixLookup, "ab-",     null);
        checkShortest(prefixLookup, "abc-",    null);
        checkShortest(prefixLookup, "abcd-",   null);
        checkShortest(prefixLookup, "abcde-",  null);
        checkShortest(prefixLookup, "abcdef-", null);

        checkShortest(prefixLookup, "abc\\t",  null);
        checkShortest(prefixLookup, "abc€",    null);

        // ----------------------------------------------------
        // Longest Match
        checkLongest(prefixLookup, "MisMatch", null);

        checkLongest(prefixLookup, "你好",     "Hello in Chinese");

        // Same case
        checkLongest(prefixLookup, "A",       null);
        checkLongest(prefixLookup, "AB",      null);
        checkLongest(prefixLookup, "ABC",     "Result ABC");
        checkLongest(prefixLookup, "ABCD",    "Result ABCD");
        checkLongest(prefixLookup, "ABCDE",   "Result ABCD");
        checkLongest(prefixLookup, "ABCDEF",  "Result ABCDEF");

        checkLongest(prefixLookup, "A-",      null);
        checkLongest(prefixLookup, "AB-",     null);
        checkLongest(prefixLookup, "ABC-",    "Result ABC");
        checkLongest(prefixLookup, "ABCD-",   "Result ABCD");
        checkLongest(prefixLookup, "ABCDE-",  "Result ABCD");
        checkLongest(prefixLookup, "ABCDEF-", "Result ABCDEF");

        checkLongest(prefixLookup, "ABC\\t",  "Result ABC");
        checkLongest(prefixLookup, "ABC€",    "Result ABC");

        // Different case
        checkLongest(prefixLookup, "a",       null);
        checkLongest(prefixLookup, "ab",      null);
        checkLongest(prefixLookup, "abc",     null);
        checkLongest(prefixLookup, "abcd",    null);
        checkLongest(prefixLookup, "abcde",   null);
        checkLongest(prefixLookup, "abcdef",  null);

        checkLongest(prefixLookup, "a-",      null);
        checkLongest(prefixLookup, "ab-",     null);
        checkLongest(prefixLookup, "abc-",    null);
        checkLongest(prefixLookup, "abcd-",   null);
        checkLongest(prefixLookup, "abcde-",  null);
        checkLongest(prefixLookup, "abcdef-", null);

        checkLongest(prefixLookup, "abc\\t",  null);
        checkLongest(prefixLookup, "abc€",    null);

        // ----------------------------------------------------
        // Contains
        checkContains(prefixLookup, "MisMatch", false);

        checkContains(prefixLookup, "你",      true);
        checkContains(prefixLookup, "你好",    false);

        // Same case
        checkContains(prefixLookup, "A",       false);
        checkContains(prefixLookup, "AB",      false);
        checkContains(prefixLookup, "ABC",     true);
        checkContains(prefixLookup, "ABCD",    true);
        checkContains(prefixLookup, "ABCDE",   false);
        checkContains(prefixLookup, "ABCDEF",  true);

        checkContains(prefixLookup, "A-",      false);
        checkContains(prefixLookup, "AB-",     false);
        checkContains(prefixLookup, "ABC-",    false);
        checkContains(prefixLookup, "ABCD-",   false);
        checkContains(prefixLookup, "ABCDE-",  false);
        checkContains(prefixLookup, "ABCDEF-", false);

        checkContains(prefixLookup, "ABC\\t",  false);
        checkContains(prefixLookup, "ABC€",    false);

        // Different case
        checkContains(prefixLookup, "a",       false);
        checkContains(prefixLookup, "ab",      false);
        checkContains(prefixLookup, "abc",     false);
        checkContains(prefixLookup, "abcd",    false);
        checkContains(prefixLookup, "abcde",   false);
        checkContains(prefixLookup, "abcdef",  false);

        checkContains(prefixLookup, "a-",      false);
        checkContains(prefixLookup, "ab-",     false);
        checkContains(prefixLookup, "abc-",    false);
        checkContains(prefixLookup, "abcd-",   false);
        checkContains(prefixLookup, "abcde-",  false);
        checkContains(prefixLookup, "abcdef-", false);

        checkContains(prefixLookup, "abc\\t",  false);
        checkContains(prefixLookup, "abc€",    false);
    }

}
