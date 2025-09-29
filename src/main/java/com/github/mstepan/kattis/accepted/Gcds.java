package com.github.mstepan.kattis.accepted;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

/**
 * GCDs
 *
 * <p>https://open.kattis.com/problems/gcds
 */
public class Gcds {

    /**
     * Use dynamic programming approach.
     *
     * <p>prev_gcd = {....}
     *
     * <p>cur_gcd = arr[i] | gcd(prev_gcd[i], arr[i])
     *
     * <p>time: O(N* 100)
     *
     * <p>space: O(100)
     */
    private static int countUniqueGdcs(int[] arr) {
        assert arr != null;

        boolean[] gcds = new boolean[101];

        Set<Integer> prev = new HashSet<>();

        for (int val : arr) {
            Set<Integer> cur = new HashSet<>();
            cur.add(val);
            gcds[val] = true;

            for (int prevVal : prev) {
                int newGcd = gcd(prevVal, val);
                cur.add(newGcd);

                gcds[newGcd] = true;
            }

            prev = cur;
        }

        return countSetValues(gcds);
    }

    private static int countSetValues(boolean[] gcds) {

        int cnt = 0;

        for (boolean val : gcds) {
            cnt += val ? 1 : 0;
        }

        return cnt;
    }

    private static int gcd(int a, int b) {
        assert a > 0 && b > 0;

        int first = a;
        int second = b;

        while (second != 0) {
            int rem = first % second;

            first = second;
            second = rem;
        }

        return first;
    }

    public static void run(BufferedReader in) throws IOException {
        int n = Integer.parseInt(in.readLine().trim());

        int[] arr = new int[n];

        for (int i = 0; i < n; i++) {
            arr[i] = Integer.parseInt(in.readLine().trim());
        }

        System.out.println(countUniqueGdcs(arr));
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
