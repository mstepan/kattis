package com.github.mstepan.kattis;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * Font
 *
 * <p>https://open.kattis.com/problems/font
 *
 * <p>The problem we're dealing with is related to a complex NP-complete, specifically the Min Set
 * Coverage problem. To tackle this task, we need to count the number of Subset Coverages, which is
 * also an NP-complete problem.
 *
 * <p>Here's a simplified overview of our approach: Generate all possible subsets: We need to create
 * all possible combinations of words and check if any of these subsets cover all characters from
 * 'a' to 'z'.
 *
 * <p>To make this process more efficient, we've implemented a couple of optimizations:
 *
 * <p>1. Pre-computing word coverage masks: We calculate the coverage mask for each word only once
 * and store it in the allWordsMask array.
 *
 * <p>2. Chunking and pre-computing subset coverages: We divide the words into smaller groups
 * (called "chunks") of 8 elements and pre-compute all possible subset coverages for each chunk.
 *
 * <p>This allows us to efficiently generate all possible coverages for the words in each chunk. By
 * using these optimizations, we can make the computation more manageable, even though the
 * underlying problem is NP-complete.
 */
public class Font {

    public static void run(BufferedReader in) throws IOException {
        String line = in.readLine();

        int wordsCount = Integer.parseInt(line.trim());

        String[] allWords = new String[wordsCount];

        for (int i = 0; i < wordsCount; i++) {
            allWords[i] = in.readLine().trim();
        }

        //        long startTime = System.nanoTime();

        int cnt = countPossibleTestCases(allWords);

        //        long endTime = System.nanoTime();
        //        assert cnt == 16515037;
        //        System.out.printf("time: %d ms%n", (endTime - startTime) / 1_000_000);

        System.out.println(cnt);
    }

    private static final int ALPHABET_SIZE = 'z' - 'a' + 1;
    private static final int FULL_COVERAGE_MASK = (1 << ALPHABET_SIZE) - 1;
    private static final int CHUNK_SIZE = 8;

    private static int countPossibleTestCases(String[] allWords) {
        Objects.requireNonNull(allWords);

        int[] allWordsMask = new int[allWords.length];
        for (int i = 0; i < allWordsMask.length; i++) {
            allWordsMask[i] = wordToMask(allWords[i]);
        }

        int chunksCount = Math.ceilDiv(allWords.length, CHUNK_SIZE);
        int[][] chunks = new int[chunksCount][1 << CHUNK_SIZE];

        for (int i = 0; i < chunks.length; i++) {
            int from = i * CHUNK_SIZE;
            int to = Math.min(from + CHUNK_SIZE, allWords.length);
            fillSingleChunkRow(chunks, i, allWordsMask, from, to);
        }

        int testCasesCnt = 0;

        for (int mask = 1; mask < (1 << allWords.length); mask++) {

            int combinedMask = combine(mask, chunks);

            if (combinedMask == FULL_COVERAGE_MASK) {
                ++testCasesCnt;
            }
        }

        return testCasesCnt;
    }

    private static void fillSingleChunkRow(
            int[][] chunks, int chunkRowIdx, int[] allWordsMask, int from, int to) {

        final int length = to - from;

        for (int mask = 1; mask < (1 << length); ++mask) {
            chunks[chunkRowIdx][mask] = combineChunkCell(allWordsMask, mask, from);
        }
    }

    private static int combineChunkCell(int[] allWordsMask, int mask, int offset) {
        int cellValue = 0;

        for (int idx = 0, curMask = 1; curMask <= mask; ++idx, curMask <<= 1) {
            if ((mask & curMask) != 0) {
                cellValue = cellValue | allWordsMask[offset + idx];
            }
        }

        return cellValue;
    }

    private static int wordToMask(String word) {
        assert word != null;

        int mask = 0;

        for (int i = 0; i < word.length(); i++) {
            int offset = word.charAt(i) - 'a';
            mask = mask | (1 << offset);
        }

        return mask;
    }

    private static int combine(int curMask, int[][] chunks) {
        assert curMask > 0;
        assert chunks != null;

        int combined = 0;

        int left = curMask;
        for (int chunkRowIdx = 0; left > 0; ++chunkRowIdx) {

            int chunksMask = left & 0xFF;

            combined = combined | chunks[chunkRowIdx][chunksMask];

            left >>= 8;
        }

        return combined;
    }

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
