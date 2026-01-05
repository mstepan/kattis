package com.github.mstepan.kattis.accepted;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Sound
 *
 * <p>https://open.kattis.com/problems/sound
 */
public class Sound {

    public static void run(BufferedReader in) throws IOException {

        String[] firstLineElems = in.readLine().trim().split(" ");

        int samplesCount = Integer.parseInt(firstLineElems[0]);
        int windowSize = Integer.parseInt(firstLineElems[1]);
        int maxDiff = Integer.parseInt(firstLineElems[2]);

        int[] values = new int[samplesCount];

        String[] valuesArr = in.readLine().trim().split(" ");

        assert valuesArr.length == samplesCount;

        for (int i = 0; i < valuesArr.length; i++) {
            values[i] = Integer.parseInt(valuesArr[i]);
        }

        String result = createSilenceIndexesResult(values, windowSize, maxDiff);
        System.out.println(result);
    }

    /**
     * N - elements count, K - window size
     *
     * <p>time: O(N)
     *
     * <p>Space: O(K)
     */
    private static String createSilenceIndexesResult(int[] values, int windowsSize, int maxDiff) {
        Deque<Integer> ascDeque = new ArrayDeque<>();
        Deque<Integer> descDeque = new ArrayDeque<>();

        for (int i = 0; i < windowsSize; i++) {
            addToDeque(values, ascDeque, i, Order.ASC);
            addToDeque(values, descDeque, i, Order.DESC);
        }

        StringBuilder result = new StringBuilder();

        int diff = calculateDiff(values, ascDeque, descDeque);

        if (diff <= maxDiff) {
            result.append(1).append("\n");
        }

        for (int i = windowsSize; i < values.length; i++) {
            int idxToRemove = i - windowsSize;

            removeFromDeque(ascDeque, idxToRemove);
            removeFromDeque(descDeque, idxToRemove);

            addToDeque(values, ascDeque, i, Order.ASC);
            addToDeque(values, descDeque, i, Order.DESC);

            diff = calculateDiff(values, ascDeque, descDeque);
            if (diff <= maxDiff) {
                result.append(i - windowsSize + 2).append("\n");
            }
        }

        if (result.isEmpty()) {
            return "NONE";
        }
        result.setLength(result.length() - 1);
        return result.toString();
    }

    private static int calculateDiff(
            int[] values, Deque<Integer> ascDeque, Deque<Integer> descDeque) {
        assert !ascDeque.isEmpty() && !descDeque.isEmpty();

        int minIdx = ascDeque.peekFirst();
        int maxIdx = descDeque.peekFirst();

        return values[maxIdx] - values[minIdx];
    }

    enum Order {
        ASC,
        DESC;
    }

    private static void addToDeque(int[] values, Deque<Integer> deque, int idx, Order order) {
        if (order == Order.ASC) {
            while (!deque.isEmpty()) {

                int lastIdx = deque.peekLast();

                if (values[lastIdx] >= values[idx]) {
                    deque.pollLast();
                } else {
                    break;
                }
            }
        } else {
            while (!deque.isEmpty()) {

                int lastIdx = deque.peekLast();

                if (values[lastIdx] <= values[idx]) {
                    deque.pollLast();
                } else {
                    break;
                }
            }
        }

        deque.addLast(idx);
    }

    private static void removeFromDeque(Deque<Integer> deque, int idxToRemove) {
        assert !deque.isEmpty();
        if (deque.peekFirst() == idxToRemove) {
            deque.pollFirst();
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
