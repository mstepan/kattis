package com.github.mstepan.kattis;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Counting Subsequences (Hard)
 *
 * <p>https://open.kattis.com/problems/subseqhard
 */
public class Subseqhard {

    public static void run(BufferedReader in) throws IOException {
        int testCases = Integer.parseInt(in.readLine());

        for (int i = 0; i < testCases; ++i) {
            String ignoredBlankLine = in.readLine();

            int arrLength = Integer.parseInt(in.readLine());

            int[] arr = new int[arrLength];

            String[] arrValues = in.readLine().trim().split(" ");
            for (int j = 0; j < arrLength; ++j) {
                arr[j] = Integer.parseInt(arrValues[j]);
            }

            int seqCount = countInterestingSequences(arr);

            System.out.println(seqCount);
        }
    }

    /**
     * Use divide-and-conquer technique to find the number of subsequences with 47 as a sum.
     *
     * <p>total number of subsequences = count from left + count from right + count middle one
     *
     * <p>time: O(N*lgN) space: O(lgN)
     */
    static int countInterestingSequences(int[] arr) {
        assert arr != null;

        // it's ok to use recursion here, b/c N = 1_000_000 and log2(N) ~ 20
        return countRec(arr, 0, arr.length - 1);
    }

    private static int countRec(int[] arr, int from, int to) {
        if (from == to) {
            return arr[from] == 47 ? 1 : 0;
        }
        int mid = from + ((to - from) / 2);

        int leftCount = countRec(arr, from, mid);
        int rightCount = countRec(arr, mid + 1, to);

        int midCount = 0;

        Map<Long, Integer> leftMidValues = new HashMap<>();
        long leftSum = 0L;

        for (int i = mid; i >= from; --i) {
            leftSum += arr[i];
            leftMidValues.compute(leftSum, (keyNotUsed, freq) -> freq == null ? 1 : freq + 1);
        }

        long rightSum = 0L;
        for (int i = mid + 1; i <= to; ++i) {
            rightSum += arr[i];

            long expectedLeftValue = 47L - rightSum;

            Integer leftFreq = leftMidValues.get(expectedLeftValue);

            if (leftFreq != null) {
                midCount += leftFreq;
            }
        }

        return leftCount + rightCount + midCount;
    }

    // ============================== DON'T MODIFY ANYTHING BELOW ==============================

    public static void main(String[] args) throws Exception {

        final boolean debugMode = System.getenv("DEBUG") != null;

        if (debugMode) {
            final String inFileName = "in.txt";
            try (InputStream inStream =
                    MethodHandles.lookup()
                            .lookupClass()
                            .getClassLoader()
                            .getResourceAsStream(inFileName)) {
                if (inStream == null) {
                    throw new IllegalStateException(
                            "Can't read input from file '%s'".formatted(inFileName));
                }
                try (BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(inStream, Charset.defaultCharset()))) {
                    System.out.println("==== Debug mode ====");
                    run(in);
                }
            }

        } else {
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(System.in, Charset.defaultCharset()));
            run(in);
        }
    }
}
