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

    static class TSNode {
        char ch;
        TSNode left;
        TSNode mid;
        TSNode right;
        boolean endOfWord;

        TSNode(char ch) {
            this.ch = ch;
        }

        @Override
        public String toString() {
            return String.valueOf(ch);
        }
    }

    static class TSTree {

        TSNode root;

        void add(String value) {
            if (root == null) {
                root = insertSuffix(value, 0);
            } else {

                int idx = 0;
                TSNode cur = root;
                TSNode lastNotNull = root;

                while (idx < value.length() && cur != null) {
                    char ch = value.charAt(idx);

                    if (cur.ch == ch) {
                        cur = cur.mid;
                        ++idx;
                    } else if (ch > cur.ch) {
                        cur = cur.right;
                    } else {
                        cur = cur.left;
                    }

                    if (cur != null) {
                        lastNotNull = cur;
                    }
                }

                if (idx == value.length()) {
                    lastNotNull.endOfWord = true;
                } else {
                    char ch = value.charAt(idx);

                    if (ch == lastNotNull.ch) {
                        lastNotNull.mid = insertSuffix(value, idx);
                    } else if (ch > lastNotNull.ch) {
                        lastNotNull.right = insertSuffix(value, idx);
                    } else {
                        lastNotNull.left = insertSuffix(value, idx);
                    }
                }
            }
        }

        private TSNode insertSuffix(String value, int from) {
            assert value != null;
            assert from > 0 && from < value.length();

            TSNode cur = new TSNode(value.charAt(from));
            TSNode last = cur;

            for (int i = from + 1; i < value.length(); i++) {
                TSNode next = new TSNode(value.charAt(i));
                last.mid = next;

                last = next;
            }

            last.endOfWord = true;

            return cur;
        }

        public void printAllWords() {
            Queue<TSTreeTraversalResult> queue = new ArrayDeque<>();
            queue.add(new TSTreeTraversalResult(root, ""));

            List<String> words = new ArrayList<>();

            while (!queue.isEmpty()) {
                TSTreeTraversalResult result = queue.poll();

                if (result.node.endOfWord) {
                    words.add(result.prefix + result.node.ch);
                }

                if (result.node.left != null) {
                    queue.add(new TSTreeTraversalResult(result.node.left, result.prefix));
                }

                if (result.node.right != null) {
                    queue.add(new TSTreeTraversalResult(result.node.right, result.prefix));
                }

                if (result.node.mid != null) {
                    queue.add(
                            new TSTreeTraversalResult(
                                    result.node.mid, result.prefix + result.node.ch));
                }
            }

            for (String singleWord : words) {
                System.out.println(singleWord);
            }
        }

        public List<String> findSimilar(String word) {

            List<String> result = new ArrayList<>();

            Queue<SearchPart> queue = new ArrayDeque<>();
            queue.add(new SearchPart("", root, word, 0, 1));

            while (!queue.isEmpty()) {

                SearchPart part = queue.poll();

                if (part.editsCount < 0) {
                    continue;
                }

                if (part.curNode == null) {
                    continue;
                }

                if (part.curNode.endOfWord) {
                    if (part.wordIdx + 1 == word.length()
                            && (part.curNode.ch == word.charAt(part.wordIdx)
                                    || part.editsCount == 1)) {
                        result.add(part.prefix + part.curNode.ch);
                        continue;
                    } else if (part.wordIdx + 2 == word.length()
                            && part.curNode.ch == word.charAt(part.wordIdx)
                            && part.editsCount == 1) {
                        result.add(part.prefix + part.curNode.ch);
                        continue;
                    } else {
                        continue;
                    }
                }

                // go left
                queue.add(
                        new SearchPart(
                                part.prefix,
                                part.curNode.left,
                                part.word,
                                part.wordIdx,
                                part.editsCount));

                // go right
                queue.add(
                        new SearchPart(
                                part.prefix,
                                part.curNode.right,
                                part.word,
                                part.wordIdx,
                                part.editsCount));

                // go middle
                char wordCh = part.wordIdx >= word.length() ? '?' : word.charAt(part.wordIdx);
                char nodeCh = part.curNode.ch;

                if (wordCh == nodeCh) {
                    queue.add(
                            new SearchPart(
                                    part.prefix + nodeCh,
                                    part.curNode.mid,
                                    part.word,
                                    part.wordIdx + 1,
                                    part.editsCount));
                } else {
                    queue.add(
                            new SearchPart(
                                    part.prefix + nodeCh,
                                    part.curNode.mid,
                                    part.word,
                                    part.wordIdx + 1,
                                    part.editsCount - 1));
                }
            }

            return result;
        }
    }

    record SearchPart(String prefix, TSNode curNode, String word, int wordIdx, int editsCount) {}

    record TSTreeTraversalResult(TSNode node, String prefix) {}

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

            TSTree tree = new TSTree();

            for (String word : dictionaryWords) {
                tree.add(word);
            }

            //            tree.printAllWords();

            Map<String, Set<String>> simGraph = new HashMap<>();

            for (String word : dictionaryWords) {
                addVertex(simGraph, word);

                List<String> similarWords = tree.findSimilar(word);

                //                System.out.printf("Similar: %s -> %s\n", word, similarWords);

                for (String similarWord : similarWords) {
                    addEdge(simGraph, word, similarWord);
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
