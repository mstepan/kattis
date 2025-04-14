package com.github.mstepan.kattis;

import com.github.mstepan.kattis.accepted.Ecoins;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Problem title
 *
 * <p>https://open.kattis.com/problems/<name>
 */
public class Template {

    public static void run(BufferedReader in) throws IOException {
        String line = in.readLine();
        System.out.println(line);
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
