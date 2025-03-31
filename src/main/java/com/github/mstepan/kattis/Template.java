package com.github.mstepan.kattis;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Problem title
 *
 * <p>https://open.kattis.com/problems/<name>
 */
public class Template {

    public static void run(BufferedReader in, PrintStream out) throws IOException {
        String line = in.readLine();
        out.println(line);
    }

    public static void main(String[] args) throws Exception {

        final boolean debugMode = System.getenv("DEBUG") != null;

        if (debugMode) {

            final String inFileName = "in.txt";
            try (InputStream inStream =
                    Template.class.getClassLoader().getResourceAsStream(inFileName)) {
                if (inStream == null) {
                    throw new IllegalStateException(
                            "Can't read input from file '%s'".formatted(inFileName));
                }
                try (BufferedReader in = new BufferedReader(new InputStreamReader(inStream))) {
                    System.out.println("==== Debug mode ====");
                    run(in, System.out);
                }
            }

        } else {
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(System.in, Charset.defaultCharset()));
            run(in, System.out);
        }
    }
}
