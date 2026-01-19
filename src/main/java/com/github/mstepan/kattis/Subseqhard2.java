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
public class Subseqhard2 {

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
     * Use prefix-sum in a more clever way.
     *
     * <p>sum(arr[i....j]) == 47 if
     *
     * <p>==> prefix[i] - prefix[j-1] == 47 and
     *
     * <p>==> prefix[j-1] = prefix[i] - 47
     *
     * <p>prefix[j-1] - is previously seen prefix, so we can just store in inside HashMap
     *
     * <p>time: O(N) space: O(N)
     */
    static int countInterestingSequences(int[] arr) {
        assert arr != null;

        Map<Long, Integer> prevPrefixes = new HashMap<>();

        // 0-sum frequency should be always present to work correctly
        prevPrefixes.put(0L, 1);

        int resultCounter = 0;

        long prefixSum = 0L;
        for (int val : arr) {
            prefixSum += val;

            Integer prevFreq = prevPrefixes.get(prefixSum - 47L);

            if (prevFreq != null) {
                resultCounter += prevFreq;
            }

            prevPrefixes.compute(prefixSum, (keyNotUsed, freq) -> freq == null ? 1 : freq + 1);
        }

        return resultCounter;
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
