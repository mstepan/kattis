package com.github.mstepan.kattis.accepted;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Bus Numbers
 *
 * <p>https://open.kattis.com/problems/busnumbers2
 */
public class Busnumbers2 {

    /**
     * time: O(N ^ 2/3)
     *
     * <p>space: O(N)
     */
    public static Optional<Integer> findBiggestBusNumber(int upperBoundary) {

        final int last = (int) Math.cbrt(upperBoundary);

        Map<Integer, Integer> reprMap = new HashMap<>();

        for (int first = 1; first <= last; ++first) {
            for (int second = first; second <= last; ++second) {

                int curValue = first * first * first + second * second * second;
                if (curValue <= upperBoundary) {
                    reprMap.compute(curValue, (key, cnt) -> cnt == null ? 1 : cnt + 1);
                } else {
                    break;
                }
            }
        }

        int largestBusNumber = -1;

        for (Map.Entry<Integer, Integer> entry : reprMap.entrySet()) {
            if (entry.getValue() == 2) {
                largestBusNumber = Math.max(largestBusNumber, entry.getKey());
            }
        }

        return largestBusNumber == -1 ? Optional.empty() : Optional.of(largestBusNumber);
    }

    public static void run(BufferedReader in) throws IOException {
        String line = in.readLine().trim();

        int upperBoundary = Integer.parseInt(line);

        Optional<Integer> maybeBusNumber = findBiggestBusNumber(upperBoundary);
        maybeBusNumber.ifPresentOrElse(System.out::println, () -> System.out.println("none"));
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
