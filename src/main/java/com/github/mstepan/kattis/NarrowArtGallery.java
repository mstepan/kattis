package com.github.mstepan.kattis;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;

/**
 * Narrow Art Gallery
 *
 * <p>https://open.kattis.com/problems/narrowartgallery
 */
public class NarrowArtGallery {

    public static void run(BufferedReader in) throws IOException {

        String[] rowsAndRooms = in.readLine().trim().split(" ");

        int n = Integer.parseInt(rowsAndRooms[0]);
        int k = Integer.parseInt(rowsAndRooms[1]);

        int[][] rooms = new int[n][2];

        for (int row = 0; row < rooms.length; row++) {
            String[] roomsRow = in.readLine().trim().split(" ");
            rooms[row][0] = Integer.parseInt(roomsRow[0]);
            rooms[row][1] = Integer.parseInt(roomsRow[1]);
        }

        int result = optimalSolution(rooms, k);

        System.out.printf("%d%n", result);
    }

    private static int optimalSolution(int[][] rooms, int roomsToRemove) {
        assert rooms != null;
        assert roomsToRemove >= 0;

        int[][] prevOpt = sumByRow(rooms);
        final int lastRow = rooms.length - 1;

        for (int k = 1; k <= roomsToRemove; k++) {

            int[][] curOpt = new int[rooms.length][3];

            if (k == 1) {
                curOpt[0][0] = rooms[0][1];
                curOpt[0][1] = rooms[0][0];
                curOpt[0][2] = rooms[0][0] + rooms[0][1];
            } else {
                for (int i = 0; i < k - 1; ++i) {
                    curOpt[i][0] = Integer.MIN_VALUE;
                    curOpt[i][1] = Integer.MIN_VALUE;
                    curOpt[i][2] = Integer.MIN_VALUE;
                }
            }

            for (int row = k - 1; row < rooms.length; ++row) {
                // close 'rooms[row][0]' case

                curOpt[row][0] =
                        rooms[row][1]
                                + max(
                                        row >= 1 ? prevOpt[row - 1][2] : 0,
                                        row >= 1 ? prevOpt[row - 1][0] : 0);

                // close 'rooms[row][1]' case
                curOpt[row][1] =
                        rooms[row][0]
                                + max(
                                        row >= 1 ? prevOpt[row - 1][2] : 0,
                                        row >= 1 ? prevOpt[row - 1][1] : 0);

                // don't close any doors case
                curOpt[row][2] =
                        rooms[row][0]
                                + rooms[row][1]
                                + max(
                                        row >= 1 ? curOpt[row - 1][0] : 0,
                                        row >= 1 ? curOpt[row - 1][1] : 0,
                                        row >= 1 ? curOpt[row - 1][2] : 0);
            }

            prevOpt = curOpt;
        }

        return max(prevOpt[lastRow][0], prevOpt[lastRow][1], prevOpt[lastRow][2]);
    }

    private static int[][] sumByRow(int[][] rooms) {
        assert rooms != null;

        int[][] sol = new int[rooms.length][3];
        int totalSum = 0;

        for (int row = 0; row < rooms.length; row++) {
            totalSum += rooms[row][0];
            totalSum += rooms[row][1];

            sol[row][0] = totalSum;
            sol[row][1] = totalSum;
            sol[row][2] = totalSum;
        }

        return sol;
    }

    private static int max(int first, int... otherValues) {
        int maxValue = first;

        for (int val : otherValues) {
            maxValue = Math.max(maxValue, val);
        }

        return maxValue;
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
