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

/**
 * This class is a simple placeholder to hold the end part of a path
 * under construction in a {@link SequencesComparator SequencesComparator}.
 * <p>
 * A snake is an internal structure used in Eugene W. Myers algorithm
 * (<a href="http://www.cis.upenn.edu/~bcpierce/courses/dd/papers/diff.ps">
 * An O(ND) Difference Algorithm and Its Variations</a>).
 *
 * @since 4.0
 * @version $Id$
 */
public class Snake {

    /** Start index. */
    private final int start;

    /** End index. */
    private final int end;

    /** Diagonal number. */
    private final int diag;

    /**
     * Simple constructor. Creates a new instance of Snake with default indices.
     */
    public Snake() {
        start = -1;
        end   = -1;
        diag  =  0;
    }

    /**
     * Simple constructor. Creates a new instance of Snake with specified indices.
     *
     * @param start  start index of the snake
     * @param end  end index of the snake
     * @param diag  diagonal number
     */ 
    public Snake(final int start, final int end, final int diag) {
        this.start = start;
        this.end   = end;
        this.diag  = diag;
    }

    /**
     * Get the start index of the snake.
     *
     * @return start index of the snake
     */
    public int getStart() {
        return start;
    }

    /**
     * Get the end index of the snake.
     *
     * @return end index of the snake
     */
    public int getEnd() {
        return end;
    }

    /**
     * Get the diagonal number of the snake.
     *
     * @return diagonal number of the snake
     */  
    public int getDiag() {
        return diag;
    }

}
