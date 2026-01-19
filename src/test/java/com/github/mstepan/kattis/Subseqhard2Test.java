package com.github.mstepan.kattis;

import static com.github.mstepan.kattis.Subseqhard2.countInterestingSequences;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class Subseqhard2Test {

    @Test
    void singleElementEqualsTarget() {
        int[] arr = {47};
        int res = countInterestingSequences(arr);
        assertEquals(1, res);
    }

    @Test
    void singleElementNotTarget() {
        int[] arr = {46};
        int res = countInterestingSequences(arr);
        assertEquals(0, res);
    }

    @Test
    void noSubarrayMatchesTarget() {
        int[] arr = {1, 2, 3};
        int res = countInterestingSequences(arr);
        assertEquals(0, res);
    }

    @Test
    void multipleMatchesIncludingZerosAndFortySeven() {
        // Array: [20, 27, 47, 0, 47]
        // Matching contiguous subarrays that sum to 47:
        // [20,27], [47], [47,0], [0,47], [47] => total 5
        int[] arr = {20, 27, 47, 0, 47};
        int res = countInterestingSequences(arr);
        assertEquals(5, res);
    }

    @Test
    void handlesNegativeNumbers() {
        // [10, -10, 47] -> [10,-10,47], [47] => 2
        int[] arr = {10, -10, 47};
        int res = countInterestingSequences(arr);
        assertEquals(2, res);
    }

    @Test
    void manyOverlappingMatches() {
        // arr = [1, 46, 1, -1, 47, 0, 0]
        // Matches (contiguous) that sum to 47:
        // [1,46], [46,1], [47], [47,0], [47,0,0], [1,-1,47], [1,-1,47,0], [1,-1,47,0,0],
        // [1,46,1,-1]
        // total 9
        int[] arr = {1, 46, 1, -1, 47, 0, 0};
        int res = countInterestingSequences(arr);
        assertEquals(9, res);
    }

    @Test
    void largeValuesDoNotOverflow() {
        int[] arr = {Integer.MAX_VALUE, -Integer.MAX_VALUE + 47};
        int res = countInterestingSequences(arr);
        assertEquals(1, res);
    }
}
