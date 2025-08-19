package com.github.mstepan.kattis;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * Font
 *
 * <p>https://open.kattis.com/problems/font
 */
public class Font {

    public static void run(BufferedReader in) throws IOException {
        String line = in.readLine();

        int wordsCount = Integer.parseInt(line.trim());

        String[] allWords = new String[wordsCount];

        for (int i = 0; i < wordsCount; i++) {
            allWords[i] = in.readLine().trim();
        }

        int cnt = countPossibleTestCases(allWords);

        System.out.println(cnt);
    }

    private static final int ALPHABET_SIZE = 'z' - 'a' + 1;
    private static final int FULL_COVERAGE_MASK = (1 << ALPHABET_SIZE) - 1;

    private static int countPossibleTestCases(String[] allWords) {
        Objects.requireNonNull(allWords);

        int[] allWordsMask = new int[allWords.length];

        for (int i = 0; i < allWordsMask.length; i++) {
            allWordsMask[i] = wordToMask(allWords[i]);
        }

        int testCasesCnt = 0;

        for (int mask = 1; mask < (1 << allWords.length); mask++) {
            int combinedMask = combine(mask, allWordsMask);

            if (combinedMask == FULL_COVERAGE_MASK) {
                ++testCasesCnt;
            }
        }

        return testCasesCnt;
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

    private static int combine(int usedWordMask, int[] allWordsMask) {
        assert usedWordMask > 0;
        assert allWordsMask != null;

        int combined = 0;

        for (int i = 0, left = usedWordMask; left != 0; ++i) {
            int curMask = (1 << i);

            if ((left & curMask) != 0) {
                combined = combined | allWordsMask[i];
                left = left ^ curMask;
            }
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
