package com.github.mstepan.kattis;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Send More Money
 *
 * <p>https://open.kattis.com/problems/sendmoremoney
 */
public class TemplateMain {

    public static void run(BufferedReader in, PrintStream out) throws IOException {
        String line = in.readLine();

        String trimmedLine = line.trim();

        String[] firstHalf = trimmedLine.split("\\+");
        assert firstHalf.length == 2;

        String value1 = firstHalf[0];

        String[] secondHalf = firstHalf[1].split("=");
        assert secondHalf.length == 2;

        String value2 = secondHalf[0];
        String sum = secondHalf[1];

        String solution = findSolution(value1, value2, sum);

        out.println(solution);
    }

    private static String findSolution(String value1, String value2, String sum) {
        List<Slot> slotsSortedByChar = combineSlots(value1, value2, sum);

        final String[] solution = {null};
        final int digitsUsedMask = 0;

        findSolutionRec(0, value1, value2, sum, slotsSortedByChar, digitsUsedMask, solution);

        if (solution[0] == null) {
            return "impossible";
        }

        return solution[0];
    }

    private static void findSolutionRec(
            int slotIdx,
            String val1,
            String val2,
            String sum,
            List<Slot> slotsSortedByChar,
            int digitsUsedMask,
            String[] solution) {

        if (solution[0] != null) {
            return;
        }

        if (slotIdx >= slotsSortedByChar.size()) {
            // all slots assigned some decimal digit, so evaluate if proper solution or not
            BigInteger v1 = toDecimal(val1, slotsSortedByChar);
            BigInteger v2 = toDecimal(val2, slotsSortedByChar);
            BigInteger sumVal = toDecimal(sum, slotsSortedByChar);

            if (v1.add(v2).equals(sumVal)) {
                solution[0] = "%d+%d=%d".formatted(v1, v2, sumVal);
            }
        } else {

            Slot notAssignedSlot = slotsSortedByChar.get(slotIdx);

            int from = notAssignedSlot.startOfWord ? 1 : 0;

            for (int idx = from; idx < 10; idx++) {
                if ((digitsUsedMask & (1 << idx)) == 0) {
                    notAssignedSlot.digit = idx;
                    findSolutionRec(
                            slotIdx + 1,
                            val1,
                            val2,
                            sum,
                            slotsSortedByChar,
                            digitsUsedMask | (1 << idx),
                            solution);
                }
            }
        }
    }

    private static List<Slot> combineSlots(String value1, String value2, String sum) {
        Set<Slot> slotsSorted = new TreeSet<>(Slot.CH_ASC);

        slotsSorted.add(new Slot(value1.charAt(0), true));
        slotsSorted.add(new Slot(value2.charAt(0), true));
        slotsSorted.add(new Slot(sum.charAt(0), true));

        for (int i = 1; i < value1.length(); i++) {
            slotsSorted.add(new Slot(value1.charAt(i), false));
        }

        for (int i = 1; i < value2.length(); i++) {
            slotsSorted.add(new Slot(value2.charAt(i), false));
        }

        for (int i = 1; i < sum.length(); i++) {
            slotsSorted.add(new Slot(sum.charAt(i), false));
        }

        return new ArrayList<>(slotsSorted);
    }

    private static final class Slot {

        private final char ch;
        int digit;
        private final boolean startOfWord;

        public Slot(char ch, boolean startOfWord) {
            this.ch = ch;
            this.digit = -1;
            this.startOfWord = startOfWord;
        }

        private char getCh() {
            return ch;
        }

        public static final Comparator<Slot> CH_ASC = Comparator.comparing(Slot::getCh);

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Slot slot = (Slot) o;
            return ch == slot.ch;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(ch);
        }

        @Override
        public String toString() {
            return "%c => %d".formatted(ch, digit);
        }
    }

    private static BigInteger toDecimal(String value, List<Slot> slotsSortedByChar) {
        BigInteger res = BigInteger.ZERO;

        for (int i = 0; i < value.length(); i++) {
            Slot slot = findByChar(value.charAt(i), slotsSortedByChar);
            res = res.multiply(BigInteger.TEN).add( BigInteger.valueOf(slot.digit));
        }

        return res;
    }

    private static Slot findByChar(char ch, List<Slot> slotsSortedByChar) {
        for (Slot slot : slotsSortedByChar) {
            if (slot.ch == ch) {
                return slot;
            }
        }

        throw new IllegalStateException("Can't find any slot for char '%c'".formatted(ch));
    }

    public static void main(String[] args) throws Exception {

        boolean debugMode = System.getenv("DEBUG") != null;

        BufferedReader in;
        PrintStream out = System.out;

        if (debugMode) {
            final Path inFilePath =
                    Path.of(
                            Objects.requireNonNull(
                                            TemplateMain.class
                                                    .getClassLoader()
                                                    .getResource("in.txt"))
                                    .toURI());

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
