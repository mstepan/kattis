package com.github.mstepan.kattis.accepted;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Bell Ringing
 *
 * <p>https://open.kattis.com/problems/bells
 */
public class Bells {

    public static void run(BufferedReader in) throws IOException {
        String line = in.readLine();

        int n = Integer.parseInt(line);

        if (n < 1 || n > 8) {
            throw new IllegalArgumentException(
                    "n is out of range, expected [1...8] but found %d".formatted(n));
        }

        var result = generatePerfectSequence(n);

        for (String singleSeq : result) {
            System.out.println(singleSeq);
        }
    }

    private static final List<String> ONE_SEQ = List.of("1");

    private static final List<String> TWO_SEQ = List.of("1 2", "2 1");

    /**
     * Use approach similar to how the binary Gray codes generated. Started from "1 2", "2 1" and
     * gracefully adding new values, like '3' in this case.
     */
    private static List<String> generatePerfectSequence(int n) {
        assert n >= 1 && n <= 8;

        if (n == 1) {
            return ONE_SEQ;
        }

        if (n == 2) {
            return TWO_SEQ;
        }

        List<String> sequence = TWO_SEQ;

        for (int i = 3; i <= n; ++i) {
            sequence = nextSetOfSequences(sequence, i);
        }

        return sequence;
    }

    private static List<String> nextSetOfSequences(List<String> sequence, int newBellValue) {

        // "1 2", "2 1"

        List<String> result = new ArrayList<>();
        Direction direction = Direction.RIGHT_TO_LEFT;

        for (String prevVal : sequence) {
            result.addAll(nextSequenceValues(prevVal, newBellValue, direction));
            direction = direction.invert();
        }

        return Collections.unmodifiableList(result);
    }

    private static List<String> nextSequenceValues(String prevVal, int value, Direction direction) {

        char[] arr = seqToArray(prevVal, value);

        List<String> result = new ArrayList<>();

        if (direction == Direction.RIGHT_TO_LEFT) {
            arr[arr.length - 1] = (char) ('0' + value);

            result.add(toSequence(arr));

            for (int pos = arr.length - 1; pos > 0; --pos) {
                swap(arr, pos - 1, pos);
                result.add(toSequence(arr));
            }

        } else {
            shiftRight(arr);
            arr[0] = (char) ('0' + value);

            result.add(toSequence(arr));

            for (int pos = 0; pos < arr.length - 1; ++pos) {
                swap(arr, pos, pos + 1);
                result.add(toSequence(arr));
            }
        }

        return result;
    }

    private static void shiftRight(char[] arr) {
        for (int i = arr.length - 1; i > 0; --i) {
            arr[i] = arr[i - 1];
        }
    }

    private static char[] seqToArray(String prevVal, int length) {

        char[] arr = new char[length];

        for (int i = 0, idx = 0; i < prevVal.length(); ++i) {
            char ch = prevVal.charAt(i);

            if (Character.isDigit(ch)) {
                arr[idx] = ch;
                ++idx;
            }
        }

        return arr;
    }

    private static String toSequence(char[] arr) {

        StringBuilder res = new StringBuilder(arr.length);
        for (int i = 0; i < arr.length; ++i) {

            if (i != 0) {
                res.append(" ");
            }
            res.append(arr[i]);
        }

        return res.toString();
    }

    private static void swap(char[] arr, int from, int to) {
        assert arr != null;
        assert from >= 0 && from < arr.length;
        assert to >= 0 && to < arr.length;

        if (from == to) {
            return;
        }

        char temp = arr[from];
        arr[from] = arr[to];
        arr[to] = temp;
    }

    enum Direction {
        RIGHT_TO_LEFT,
        LEFT_TO_RIGHT;

        Direction invert() {
            if (this == RIGHT_TO_LEFT) {
                return LEFT_TO_RIGHT;
            }

            return RIGHT_TO_LEFT;
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
