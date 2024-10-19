package org.apache.commons.collections4.iterators;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnwindingIteratorTest {

    @Test
    public void simpleTest() {
        List<Iterator<String>> lst = new ArrayList<>();
        lst.add(Arrays.asList( "Hello", "World").iterator());
        lst.add(Arrays.asList("it", "is", "good", "to", "be", "here").iterator());
        List<String> expected = Arrays.asList("Hello", "World", "it", "is", "good", "to", "be", "here");

        Iterator<String> iter = new UnwindingIterator<>(lst.iterator());
        List<String> actual = new ArrayList<>();
        iter.forEachRemaining(actual::add);
        assertEquals(actual, expected);
    }

    @Test
    public void mixedTest() {
        List<List<Number>> lst = new ArrayList<>();

        List<Integer> iList = Arrays.asList(1, 3);
        lst.add(Arrays.asList(3.14f, Math.sqrt(2.0)));

        Iterator<Iterator<Number>> toBeUnwound = new Iterator<Iterator<Number>>() {
            List<List<Number>> lst = Arrays.asList(
                    Arrays.asList(1, 3),
                    Arrays.asList(3.14F, Math.sqrt(2.0))
            );
            Iterator<List<Number>> lstIter = lst.iterator();

            @Override
            public boolean hasNext() {
                return lstIter.hasNext();
            }

            @Override
            public Iterator<Number> next() {
                return lstIter.next().iterator();
            }
        };

        List<Number> expected = Arrays.asList(1, 3, 3.14f, Math.sqrt(2.0));

        Iterator<Number> iter = new UnwindingIterator<>(toBeUnwound);
        List<Number> actual = new ArrayList<>();
        iter.forEachRemaining(actual::add);
        assertEquals(actual, expected);
    }

}
