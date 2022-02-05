package org.apache.commons.collections4.bloomfilter;

@FunctionalInterface
public interface LongBiFunction {

    /**
     * A function that takes to long arguments and returns a boolean.
     * @param x the first long argument.
     * @param y the second long argument.
     * @return true or false.
     */
    boolean test( long x, long y);

}
