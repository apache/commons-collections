/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections4.sequence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ReplacementsFinderTest {

    static Stream<Arguments> sequences() {
        return Stream.of(
            Arguments.of("abcd", "abcd", ""),
            Arguments.of("xabc", "yabc", "0:[x]->[y]"),
            Arguments.of("abxcd", "abycd", "2:[x]->[y]"),
            Arguments.of("axbxc", "aybyc", "1:[x]->[y] 1:[x]->[y]"),
            Arguments.of("abc", "abd", "2:[c]->[d]"),
            Arguments.of("abcd", "abc", "3:[d]->[]"),
            Arguments.of("abc", "abcd", "3:[]->[d]"),
            Arguments.of("abc", "", "0:[a, b, c]->[]"),
            Arguments.of("", "xyz", "0:[]->[x, y, z]"),
            Arguments.of("a", "b", "0:[a]->[b]"));
    }

    private static List<Character> sequence(final String string) {
        final List<Character> list = new ArrayList<>(string.length());
        for (int i = 0; i < string.length(); i++) {
            list.add(Character.valueOf(string.charAt(i)));
        }
        return list;
    }

    /**
     * The replacements reported for a whole script must transform the first sequence into the second one.
     */
    @ParameterizedTest
    @MethodSource("sequences")
    void testReplacementsSpanWholeScript(final String left, final String right, final String expected) {
        final List<Character> first = sequence(left);
        final List<String> reported = new ArrayList<>();
        final List<Character> rebuilt = new ArrayList<>();
        final int[] cursor = {0};
        new SequencesComparator<>(first, sequence(right)).getScript().visit(new ReplacementsFinder<Character>(
            (skipped, from, to) -> {
                reported.add(skipped + ":" + from + "->" + to);
                rebuilt.addAll(first.subList(cursor[0], cursor[0] + skipped));
                cursor[0] += skipped + from.size();
                rebuilt.addAll(to);
            }));
        rebuilt.addAll(first.subList(cursor[0], first.size()));
        assertEquals(expected, String.join(" ", reported));
        assertEquals(sequence(right), rebuilt);
    }

}
