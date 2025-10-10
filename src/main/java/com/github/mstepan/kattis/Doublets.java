package com.github.mstepan.kattis;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Doublets
 *
 * <p>https://open.kattis.com/problems/doublets
 */
public class Doublets {

    // TODO: 'Time Limit Exceeded' for last case
    // Use Ternary-Search Tree for find words that has 1 character difference

    public static void run(BufferedReader in) throws IOException {

        List<String> dicWords = new ArrayList<>();

        for (String line = in.readLine().trim(); !line.isEmpty(); line = in.readLine().trim()) {
            dicWords.add(line);
        }

        ClosestPathFinder finder = new ClosestPathFinder(dicWords);

        boolean secondLineOrMore = false;

        for (String line = in.readLine(); line != null; line = in.readLine()) {
            String[] pairOfWords = line.trim().split("\\s+", 2);
            List<String> path = finder.findClosetsPath(pairOfWords[0], pairOfWords[1]);

            if (secondLineOrMore) {
                System.out.println();
            }

            if (path.isEmpty()) {
                System.out.println("No solution.");
            } else {
                for (String part : path) {
                    System.out.println(part);
                }
            }

            secondLineOrMore = true;
        }
    }

    static class ClosestPathFinder {
        final List<String> dictionaryWords;

        final Map<String, Set<String>> graph;

        ClosestPathFinder(List<String> dictionaryWords) {
            this.dictionaryWords = Objects.requireNonNull(dictionaryWords);
            this.graph = buildSimilarityGraph(dictionaryWords);
        }

        private static Map<String, Set<String>> buildSimilarityGraph(List<String> dictionaryWords) {

            Map<String, Set<String>> simGraph = new HashMap<>();

            Map<String, Set<String>> edges = new HashMap<>();

            for (String word : dictionaryWords) {

                addVertex(simGraph, word);

                Set<String> variants = generateVariants(word);

                for (String singleVariant : variants) {
                    Set<String> similarVertexes =
                            edges.compute(
                                    singleVariant,
                                    (key, other) -> other == null ? new HashSet<>() : other);

                    for (String simVertex : similarVertexes) {
                        addEdge(simGraph, word, simVertex);
                    }

                    similarVertexes.add(word);
                }
            }

            return simGraph;
        }

        private static void addVertex(Map<String, Set<String>> simGraph, String word) {
            simGraph.put(word, new HashSet<>());
        }

        private static void addEdge(Map<String, Set<String>> simGraph, String from, String to) {
            simGraph.compute(from, (key, adjList) -> adjList == null ? new HashSet<>() : adjList)
                    .add(to);

            simGraph.compute(to, (key, adjList) -> adjList == null ? new HashSet<>() : adjList)
                    .add(from);
        }

        List<String> findClosetsPath(String from, String to) {

            Queue<String> bfsQueue = new LinkedList<>();

            Map<String, String> vertexParents = new HashMap<>();
            vertexParents.put(from, null);

            if (!(graph.containsKey(from) && graph.containsKey(to))) {
                return List.of();
            }

            if (from.equals(to)) {
                return List.of(from);
            }

            // do BFS to find path 'from' ->  .... -> 'to'

            Set<String> visited = new HashSet<>();

            visited.add(from);
            bfsQueue.add(from);

            while (!bfsQueue.isEmpty()) {
                String curVertex = bfsQueue.poll();

                for (String adjVertex : graph.get(curVertex)) {
                    if (!visited.contains(adjVertex)) {

                        vertexParents.put(adjVertex, curVertex);
                        visited.add(adjVertex);

                        bfsQueue.add(adjVertex);

                        if (adjVertex.equals(to)) {
                            return reconstructPath(vertexParents, to);
                        }
                    }
                }
            }

            return List.of();
        }

        private List<String> reconstructPath(Map<String, String> vertexParents, String end) {

            List<String> path = new ArrayList<>();

            for (String cur = end; cur != null; cur = vertexParents.get(cur)) {
                path.add(cur);
            }

            Collections.reverse(path);

            return path;
        }

        private static Set<String> generateVariants(String value) {
            Set<String> allVariants = new HashSet<>();

            char[] arr = value.toCharArray();

            for (int i = 0; i < arr.length; i++) {
                char temp = arr[i];
                arr[i] = '.';

                allVariants.add(new String(arr));

                arr[i] = temp;
            }

            return allVariants;
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
