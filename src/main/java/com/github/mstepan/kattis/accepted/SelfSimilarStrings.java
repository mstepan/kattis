package com.github.mstepan.kattis.accepted;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Self-Similar Strings
 *
 * <p>https://open.kattis.com/problems/selfsimilarstrings
 */
public class SelfSimilarStrings {

    public static int selfSimilarIndex(String str) {
        Objects.requireNonNull(str, "'str' is null");

        int maxSelfSimIdx = 0;

        for (int len = 1; len < str.length(); len++) {

            Map<String, Integer> subCntMap = new HashMap<>();

            for (int start = 0; start <= str.length() - len; start++) {
                String sub = str.substring(start, start + len);
                subCntMap.compute(sub, (notUsedKey, cnt) -> cnt == null ? 1 : cnt + 1);
            }

            if (allCountsAtLeast(subCntMap, 2)) {
                maxSelfSimIdx = len;
            } else {
                break;
            }
        }

        return maxSelfSimIdx;
    }

    private static boolean allCountsAtLeast(Map<String, Integer> subMap, int threashold) {
        for (Map.Entry<String, Integer> entry : subMap.entrySet()) {
            if (entry.getValue() < threashold) {
                return false;
            }
        }

        return true;
    }

    public static void run(BufferedReader in) throws IOException {
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println(selfSimilarIndex(line));
        }
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
