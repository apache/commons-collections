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
package org.apache.commons.collections.comparators.sequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SequencesComparatorTest {

    private List<String> before;
    private List<String> after;
    private int[]        length;

    @Test
    public void testLength() {
        for (int i = 0; i < before.size(); ++i) {
            SequencesComparator<Character> comparator =
                    new SequencesComparator<Character>(sequence(before.get(i)),
                            sequence(after.get(i)));
            Assert.assertEquals(length[i], comparator.getScript().getModifications());
        }
    }

    @Test
    public void testExecution() {
        ExecutionVisitor<Character> ev = new ExecutionVisitor<Character>();
        for (int i = 0; i < before.size(); ++i) {
            ev.setList(sequence(before.get(i)));
            new SequencesComparator<Character>(sequence(before.get(i)),
                    sequence(after.get(i))).getScript().visit(ev);
            Assert.assertEquals(after.get(i), ev.getString());
        }
    }

    @Test
    public void testMinimal() {
        String[] shadokAlph = new String[] {
            new String("GA"),
            new String("BU"),
            new String("ZO"),
            new String("MEU")
        };
        List<String> sentenceBefore = new ArrayList<String>();
        List<String> sentenceAfter  = new ArrayList<String>();
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

        Random random = new Random(4564634237452342L);

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

            SequencesComparator<String> comparator =
                    new SequencesComparator<String>(sentenceBefore, sentenceAfter);
            Assert.assertTrue(comparator.getScript().getModifications() <= nbCom);
        }
    }

    @Test
    public void testShadok() {
        int lgMax = 5;
        String[] shadokAlph = new String[] {
            new String("GA"),
            new String("BU"),
            new String("ZO"),
            new String("MEU")
        };
        List<List<String>> shadokSentences = new ArrayList<List<String>>();
        for (int lg=0; lg<lgMax; ++lg) {
            List<List<String>> newTab = new ArrayList<List<String>>();
            newTab.add(new ArrayList<String>());
            for (int k = 0; k < shadokAlph.length; k++) {
                for (List<String> sentence : shadokSentences) {
                    List<String> newSentence = new ArrayList<String>(sentence);
                    newSentence.add(shadokAlph[k]);
                    newTab.add(newSentence);
                }
            }
            shadokSentences = newTab;
        }

        ExecutionVisitor<String> ev = new ExecutionVisitor<String>();

        for (int i = 0; i < shadokSentences.size(); ++i) {
            for (int j = 0; j < shadokSentences.size(); ++j) {
                ev.setList(shadokSentences.get(i));
                new SequencesComparator<String>(shadokSentences.get(i),
                        shadokSentences.get(j)).getScript().visit(ev);

                StringBuilder concat = new StringBuilder();
                for (final String s : shadokSentences.get(j)) {
                    concat.append(s);
                }
                Assert.assertEquals(concat.toString(), ev.getString());
            }
        }
    }

    private List<Character> sequence(String string) {
        List<Character> list = new ArrayList<Character>();
        for (int i = 0; i < string.length(); ++i) {
            list.add(new Character(string.charAt(i)));
        }
        return list;
    }

    private class ExecutionVisitor<T> implements CommandVisitor<T> {

        private List<T> v;
        private int index;

        public void setList(List<T> array) {
            v = new ArrayList<T>(array);
            index = 0;
        }

        public void visitInsertCommand(T object) {
            v.add(index++, object);
        }

        public void visitKeepCommand(T object) {
            ++index;
        }

        public void visitDeleteCommand(T object) {
            v.remove(index);
        }

        public String getString() {
            StringBuffer buffer = new StringBuffer();
            for (T c : v) {
                buffer.append(c);
            }
            return buffer.toString();
        }

    }

    @Before
    public void setUp() {

        before = Arrays.asList(new String[] {
            "bottle",
            "nematode knowledge",
            "",
            "aa",
            "prefixed string",
            "ABCABBA",
            "glop glop",
            "coq",
            "spider-man"
        });

        after = Arrays.asList(new String[] {
            "noodle",
            "empty bottle",
            "",
            "C",
            "prefix",
            "CBABAC",
            "pas glop pas glop",
            "ane",
            "klingon"
        });

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

    @After
    public void tearDown() {
        before = null;
        after  = null;
        length = null;
    }

}
