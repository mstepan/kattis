package com.github.mstepan.kattis;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class TemplateMain {

    public static void run(BufferedReader in, PrintStream out) throws IOException {
        String line = in.readLine();
        out.println(line);
    }

    public static void main(String[] args) throws Exception {

        boolean debugMode = System.getenv("DEBUG") != null;

        BufferedReader in;
        PrintStream out = System.out;

        if (debugMode) {
            final Path inFilePath =
                    Path.of(TemplateMain.class.getClassLoader().getResource("in.txt").toURI());

            in = Files.newBufferedReader(inFilePath);
            try {
                System.out.println("==== Debug mode ====");
                run(in, out);

            } finally {
                in.close();
            }

        } else {
            in = new BufferedReader(new InputStreamReader(System.in, Charset.defaultCharset()));
            run(in, out);
        }
    }
}
