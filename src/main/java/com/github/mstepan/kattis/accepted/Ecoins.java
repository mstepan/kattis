package com.github.mstepan.kattis.accepted;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * e-Coins
 *
 * <p>https://open.kattis.com/problems/ecoins
 */
public class Ecoins {

    public static void run(BufferedReader in) throws IOException {
        int testCasesCnt = Integer.parseInt(in.readLine());

        for (int i = 0; i < testCasesCnt; ++i) {

            if (i != 0) {
                // read empty line between cases
                in.readLine();
            }

            String[] coinsAndEmodulus = in.readLine().split(" ", 2);

            int coinsCnt = Integer.parseInt(coinsAndEmodulus[0]);
            int emodulus = Integer.parseInt(coinsAndEmodulus[1]);

            Coin[] allCoins = new Coin[coinsCnt];

            for (int j = 0; j < coinsCnt; ++j) {
                String[] singleCoinStr = in.readLine().split(" ", 2);

                Coin coin =
                        new Coin(
                                Integer.parseInt(singleCoinStr[0]),
                                Integer.parseInt(singleCoinStr[1]));

                allCoins[j] = coin;
            }

            int minCoinsCnt = findMinCoins(allCoins, emodulus);

            if (minCoinsCnt == -1) {
                System.out.println("not possible");
            } else {
                System.out.println(minCoinsCnt);
            }
        }
    }

    private static int findMinCoins(Coin[] allCoins, int emodulus) {
        Objects.requireNonNull(allCoins);
        if (emodulus < 0 || emodulus > 300) {
            throw new IllegalArgumentException(
                    "emodulus has an incorrect value expected in range [%d; %d]".formatted(0, 300));
        }

        if (emodulus == 0) {
            return 0;
        }

        final int expected = emodulus * emodulus;

        Queue<Coin> level = new ArrayDeque<>();
        level.add(Coin.ZERO);

        Set<Coin> processed = new HashSet<>();
        processed.add(Coin.ZERO);

        int levelIdx = 0;

        while (!level.isEmpty()) {

            ++levelIdx;

            Queue<Coin> next = new ArrayDeque<>();

            while (!level.isEmpty()) {
                Coin cur = level.poll();

                for (Coin newCoin : allCoins) {
                    int conv = cur.conv() + newCoin.conv();
                    int tech = cur.tech() + newCoin.tech();

                    int actual = conv * conv + tech * tech;

                    if (actual == expected) {
                        return levelIdx;
                    }

                    Coin coinToAdd = new Coin(conv, tech);

                    if (actual < expected && !processed.contains(coinToAdd)) {
                        next.add(coinToAdd);
                        processed.add(coinToAdd);
                    }
                }
            }

            level = next;
        }

        return -1;
    }

    record Coin(int conv, int tech) {
        static final Coin ZERO = new Coin(0, 0);
    }

    public static void main(String[] args) throws Exception {

        final boolean debugMode = System.getenv("DEBUG") != null;

        if (debugMode) {
            final String inFileName = "in.txt";
            try (InputStream inStream =
                    Ecoins.class.getClassLoader().getResourceAsStream(inFileName)) {
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
