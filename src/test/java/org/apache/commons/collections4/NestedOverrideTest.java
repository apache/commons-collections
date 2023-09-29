package org.apache.commons.collections4;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NestedOverrideTest {
    private int basicRuns = 0;
    private int zeroRunsA = 0;
    private int zeroRunsB = 0;
    private int zeroRunsC = 0;
    private int zeroRunsC2 = 0;
    private int zeroRunsD = 0;
    private int oneRuns = 0;
    private int twoRunsA = 0;
    private int twoRunsB = 0;
    private int threeRuns = 0;

    @AfterAll
    public void checkAllRan() {
        assertEquals(1, basicRuns);
        assertEquals(5, zeroRunsA);
        assertEquals(2, zeroRunsB);
        assertEquals(1, zeroRunsC);
        assertEquals(1, zeroRunsC2);
        assertEquals(1, zeroRunsD);
        assertEquals(1, oneRuns);
        assertEquals(1, twoRunsA);
        assertEquals(1, twoRunsB);
        assertEquals(1, threeRuns);
    }

    @Test
    public void normal() {
        basicRuns++;
    }

    // test class zero, confirm that @Nested can work as override
    @Nested
    public class Zero {
        public class TestBase {
            @Test
            public void testZero() {
                zeroRunsA++;
            }
        }

        @Nested
        public class Check0A extends TestBase {
            // should run original testZero (A)
        }

        @Nested
        public class Check0B extends TestBase {
            // should also run original testZero (A)

            @Test
            public void testZeroAlternate() {
                zeroRunsB++;
            }
        }
    }

    @Nested
    public class ZeroSub extends Zero {
        @Nested
        public class Check0A extends Zero.Check0A {
            // should run original testZero (A)
        }

        @Nested
        public class Check0B extends Zero.Check0B {
            // should also run testZero and testZeroAlternate

            @Test
            @Override
            public void testZeroAlternate() {
                zeroRunsC++;
            }
        }

        @Nested
        public class Check0B2 extends Zero.Check0B {
            // should also run testZero and testZeroAlternate

            @Test
            public void testZeroNew() {
                zeroRunsC2++;
            }
        }

        @Nested
        public class Check0C {
            // should just run testZeroNew

            @Test
            public void testZeroNew() {
                zeroRunsD++;
            }
        }
    }


    @Nested
    public class One {
        @NestedOverridable
        public class Check1 {
            @Test
            public void testOne() {
                oneRuns++;
            }
        }
    }

    public abstract class Two {
        @NestedOverridable
        public class Check2 {
            @Test
            public void testTwo() {
                fail("shouldn't run");
            }
        }
    }

    @Nested
    public class TwoSubA extends Two {
        @NestedOverride(Two.Check2.class)
        public class Check2A extends Two.Check2 {
            @Test
            @Override
            public void testTwo() {
                twoRunsA++;
            }
        }
    }

    @Nested
    public class TwoSubB extends Two {
        @NestedOverride(Two.Check2.class)
        public class Check2B {
            @Test
            public void testTwoB() {
                twoRunsB++;
            }
        }
    }

    public abstract class Three {
        @NestedOverridable
        public class Check3 {
            @Test
            public void test3A() {
                fail("shouldn't run");
            }
        }
    }

    public abstract class ThreeSub extends Three {
        @NestedOverride(Three.Check3.class)
        public class Check3B {
            @Test
            public void test3B() {
                fail("shouldn't run");
            }
        }
    }

    @Nested
    public class ThreeSubSub extends ThreeSub {
        @NestedOverride(Three.Check3.class)
        public class Check3C {
            @Test
            public void test3C() {
                threeRuns++;
            }
        }
    }

}
