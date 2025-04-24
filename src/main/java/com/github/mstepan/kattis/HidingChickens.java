package com.github.mstepan.kattis;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Hiding Chickens
 *
 * <p>https://open.kattis.com/problems/hidingchickens
 */

/*
==== Debug mode ====
time: 1527 ms
15968.180089
 */
public class HidingChickens {

    public static void run(BufferedReader in) throws IOException {
        //
        //        for (int i = 0; i < 20; ++i) {
        //            BigDecimal x = randomBigDecimal();
        //            BigDecimal y = randomBigDecimal();
        //
        //            System.out.printf("%.6f %.6f%n", x, y);
        //        }

        Location rooster = Location.toLocation(in.readLine());

        int locationsCnt = Integer.parseInt(in.readLine().trim());

        Location[] spots = new Location[locationsCnt + 1];
        spots[0] = rooster;

        for (int i = 1; i < spots.length; i++) {
            spots[i] = Location.toLocation(in.readLine());
        }

        long startTime = System.nanoTime();

        long[][] distancesMatrix = calculateAllDistances(spots);
        long bestResult = bestTripRec(spots.length, (1 << (spots.length - 1)) - 1, distancesMatrix);

        long endTime = System.nanoTime();
        System.out.printf("time: %d ms%n", (endTime - startTime) / 1_000_000L);

        System.out.printf("%.6f%n", ((double) bestResult) / Location.SCALE_FACTOR);
    }

    private static long[][] calculateAllDistances(Location[] spots) {

        final int spotsCnt = spots.length;

        long[][] distances = new long[spotsCnt + 1][spotsCnt + 1];

        for (int i = 0; i < spots.length - 1; i++) {
            for (int j = i + 1; j < spots.length; j++) {
                long pairDistance = spots[i].distanceInternal(spots[j]);
                distances[i][j] = pairDistance;
                distances[j][i] = pairDistance;
            }
        }

        return distances;
    }

    private static BigDecimal randomBigDecimal() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        int fullValue = random.nextInt(1001);
        int afterCommaValue = random.nextInt(1000000);

        return new BigDecimal(fullValue + "." + afterCommaValue);
    }

    private static final Map<Integer, Long> CACHE = new HashMap<>();

    private static long bestTripRec(int spotsCnt, int mask, long[][] distancesMatrix) {
        if (mask == 0) {
            return 0L;
        }

        Long cachedResult = CACHE.get(mask);

        if (cachedResult != null) {
            return cachedResult;
        }

        long bestDistance = Long.MAX_VALUE;

        for (int i = 1, firstMask = 1; i < spotsCnt; i++, firstMask <<= 1) {
            if ((mask & firstMask) != 0) {
                mask ^= firstMask;

                long distance1 =
                        calculateTripDistance(0, i, mask != 0, distancesMatrix)
                                + bestTripRec(spotsCnt, mask, distancesMatrix);

                if (distance1 < bestDistance) {
                    bestDistance = distance1;
                }

                if (mask != 0) {
                    for (int j = 1, secondMask = 1; j < spotsCnt; j++, secondMask <<= 1) {
                        if ((mask & secondMask) != 0) {
                            mask ^= secondMask;

                            long distance2 =
                                    calculateTripDistance(0, i, j, mask != 0, distancesMatrix)
                                            + bestTripRec(spotsCnt, mask, distancesMatrix);

                            if (distance2 < bestDistance) {
                                bestDistance = distance2;
                            }

                            mask ^= secondMask;
                        }
                    }

                    mask ^= firstMask;
                }
            }
        }

        CACHE.put(mask, bestDistance);

        return bestDistance;
    }

    private static long calculateTripDistance(
            int roosterIdx, int firstIdx, boolean shouldReturn, long[][] distancesMatrix) {
        long d1 = distancesMatrix[roosterIdx][firstIdx]; // rooster.distance(first);
        return shouldReturn ? d1 + d1 : d1;
    }

    private static long calculateTripDistance(
            int roosterIdx,
            int firstIdx,
            int secondIdx,
            boolean shouldReturn,
            long[][] distancesMatrix) {
        long d1 = distancesMatrix[roosterIdx][firstIdx]; // rooster.distance(first);
        long d2 = distancesMatrix[firstIdx][secondIdx]; // first.distance(second);
        long d3 = distancesMatrix[secondIdx][roosterIdx]; // second.distance(rooster);

        return shouldReturn ? d1 + d2 + d3 : d1 + d2;
    }

    record Location(long x, long y) {

        static final long SCALE_FACTOR = 1_000_000L;

        static Location toLocation(String str) {
            String[] locationStr = str.trim().split(" ");

            long x = (long) (Double.parseDouble(locationStr[0]) * SCALE_FACTOR);
            long y = (long) (Double.parseDouble(locationStr[1]) * SCALE_FACTOR);

            return new Location(x, y);
        }

        public long distanceInternal(Location other) {
            long dx = x - other.x;
            long dy = y - other.y;
            return (long) Math.sqrt((dx * dx) + (dy * dy));
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
