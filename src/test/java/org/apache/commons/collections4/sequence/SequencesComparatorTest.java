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
package org.apache.commons.collections4.sequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SequencesComparatorTest {

    private List<String> before;
    private List<String> after;
    private int[]        length;

    @Test
    public void testLength() {
        for (int i = 0; i < before.size(); ++i) {
            final SequencesComparator<Character> comparator =
                    new SequencesComparator<>(sequence(before.get(i)),
                            sequence(after.get(i)));
            assertEquals(length[i], comparator.getScript().getModifications());
        }
    }

    @Test
    public void testExecution() {
        final ExecutionVisitor<Character> ev = new ExecutionVisitor<>();
        for (int i = 0; i < before.size(); ++i) {
            ev.setList(sequence(before.get(i)));
            new SequencesComparator<>(sequence(before.get(i)),
                    sequence(after.get(i))).getScript().visit(ev);
            assertEquals(after.get(i), ev.getString());
        }
    }

    @Test
    public void testMinimal() {
        final String[] shadokAlph = new String[] {
            "GA",
            "BU",
            "ZO",
            "MEU"
        };
        final List<String> sentenceBefore = new ArrayList<>();
        final List<String> sentenceAfter  = new ArrayList<>();
        sentenceBefore.add(shadokAlph[0]);
        sentenceBefore.add(shadokAlph[2]);
        sentenceBefore.add(shadokAlph[3]);
        sentenceBefore.add(shadokAlph[1]);
        sentenceBefore.add(shadokAlph[0]);
        sentenceBefore.add(shadokAlph[0]);
        sentenceBefore.add(shadokAlph[2]);
        sentenceBefore.add(shadokAlph[1]);
        sentenceBefore.add(shadokAlph[3]);
        sentenceBefore.add(shadokAlph[0]);
        sentenceBefore.add(shadokAlph[2]);
        sentenceBefore.add(shadokAlph[1]);
        sentenceBefore.add(shadokAlph[3]);
        sentenceBefore.add(shadokAlph[2]);
        sentenceBefore.add(shadokAlph[2]);
        sentenceBefore.add(shadokAlph[0]);
        sentenceBefore.add(shadokAlph[1]);
        sentenceBefore.add(shadokAlph[3]);
        sentenceBefore.add(shadokAlph[0]);
        sentenceBefore.add(shadokAlph[3]);

        final Random random = new Random(4564634237452342L);

        for (int nbCom = 0; nbCom <= 40; nbCom+=5) {
            sentenceAfter.clear();
            sentenceAfter.addAll(sentenceBefore);
            for (int i = 0; i<nbCom; i++) {
                if (random.nextInt(2) == 0) {
                    sentenceAfter.add(random.nextInt(sentenceAfter.size() + 1),
                                      shadokAlph[random.nextInt(4)]);
                } else {
                    sentenceAfter.remove(random.nextInt(sentenceAfter.size()));
                }
            }

            final SequencesComparator<String> comparator =
                    new SequencesComparator<>(sentenceBefore, sentenceAfter);
            assertTrue(comparator.getScript().getModifications() <= nbCom);
        }
    }

    @Test
    public void testShadok() {
        final int lgMax = 5;
        final String[] shadokAlph = new String[] {
            "GA",
            "BU",
            "ZO",
            "MEU"
        };
        List<List<String>> shadokSentences = new ArrayList<>();
        for (int lg=0; lg<lgMax; ++lg) {
            final List<List<String>> newTab = new ArrayList<>();
            newTab.add(new ArrayList<String>());
            for (final String element : shadokAlph) {
                for (final List<String> sentence : shadokSentences) {
                    final List<String> newSentence = new ArrayList<>(sentence);
                    newSentence.add(element);
                    newTab.add(newSentence);
                }
            }
            shadokSentences = newTab;
        }

        final ExecutionVisitor<String> ev = new ExecutionVisitor<>();

        for (final List<String> element : shadokSentences) {
            for (final List<String> shadokSentence : shadokSentences) {
                ev.setList(element);
                new SequencesComparator<>(element,
                        shadokSentence).getScript().visit(ev);

                final StringBuilder concat = new StringBuilder();
                for (final String s : shadokSentence) {
                    concat.append(s);
                }
                assertEquals(concat.toString(), ev.getString());
            }
        }
    }

    private List<Character> sequence(final String string) {
        final List<Character> list = new ArrayList<>();
        for (int i = 0; i < string.length(); ++i) {
            list.add(Character.valueOf(string.charAt(i)));
        }
        return list;
    }

    private class ExecutionVisitor<T> implements CommandVisitor<T> {

        private List<T> v;
        private int index;

        public void setList(final List<T> array) {
            v = new ArrayList<>(array);
            index = 0;
        }

        @Override
        public void visitInsertCommand(final T object) {
            v.add(index++, object);
        }

        @Override
        public void visitKeepCommand(final T object) {
            ++index;
        }

        @Override
        public void visitDeleteCommand(final T object) {
            v.remove(index);
        }

        public String getString() {
            final StringBuilder buffer = new StringBuilder();
            for (final T c : v) {
                buffer.append(c);
            }
            return buffer.toString();
        }

    }

    @BeforeEach
    public void setUp() {

        before = Arrays.asList(
            "bottle",
            "nematode knowledge",
            "",
            "aa",
            "prefixed string",
            "ABCABBA",
            "glop glop",
            "coq",
            "spider-man");

        after = Arrays.asList(
            "noodle",
            "empty bottle",
            "",
            "C",
            "prefix",
            "CBABAC",
            "pas glop pas glop",
            "ane",
            "klingon");

        length = new int[] {
            6,
            16,
            0,
            3,
            9,
            5,
            8,
            6,
            13
        };

    }

    @AfterEach
    public void tearDown() {
        before = null;
        after  = null;
        length = null;
    }

}
