package com.github.mstepan.kattis.accepted;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Send More Money
 *
 * <p>https://open.kattis.com/problems/sendmoremoney
 */
public class SendMoreMoney {

    public static void run(BufferedReader in) throws IOException {
        String line = in.readLine();

        String trimmedLine = line.trim();

        String[] firstHalf = trimmedLine.split("\\+", 2);
        assert firstHalf.length == 2;

        String value1 = firstHalf[0];

        String[] secondHalf = firstHalf[1].split("=", 1);
        assert secondHalf.length == 2;

        String value2 = secondHalf[0];
        String sum = secondHalf[1];

        SolutionTracker tracker =
                new SolutionTracker(value1.toCharArray(), value2.toCharArray(), sum.toCharArray());

        String solution = tracker.hasViableSolution() ? findSolution(tracker) : "impossible";

        System.out.println(solution);
    }

    private static String findSolution(SolutionTracker tracker) {

        final int digitsUsedMask = 0;

        findSolutionRec(tracker, digitsUsedMask);

        if (tracker.hasSolution()) {
            return tracker.solution;
        }
        return "impossible";
    }

    private static final char A = 'A';
    private static final char Z = 'Z';

    private static void findSolutionRec(SolutionTracker tracker, int digitsUsedMask) {

        if (tracker.hasSolution()) {
            return;
        }

        if (tracker.noMoreLeft()) {
            if (tracker.isValidSolution()) {
                tracker.recordSolution();
            }
        } else {

            char ch = tracker.curChar();

            for (int idx = tracker.startIndex(ch); idx < 10; idx++) {
                if ((digitsUsedMask & (1 << idx)) == 0) {

                    tracker.assignDigit(idx);

                    tracker.next();
                    findSolutionRec(tracker, digitsUsedMask | (1 << idx));
                    tracker.prev();
                }
            }
        }
    }

    private static final class SolutionTracker {
        private String solution;

        private final char[] value1;
        private final char[] value2;
        private final char[] sum;

        private char[] uniqueChars;
        private int curIdx;

        private char[] startOfWords;

        private final int[] charToDigit = new int[Z - A + 1];

        public SolutionTracker(char[] value1, char[] value2, char[] sum) {
            this.solution = null;
            this.value1 = value1;
            this.value2 = value2;
            this.sum = sum;
            calculateUniqueChars(value1, value2, sum);
            this.curIdx = 0;
        }

        private void calculateUniqueChars(char[] value1, char[] value2, char[] sum) {
            startOfWords = uniqueCharsOnly(value1[0], value2[0], sum[0]);

            Set<Character> uniqueCharsSet = new HashSet<>();
            addToSet(uniqueCharsSet, value1);
            addToSet(uniqueCharsSet, value2);
            addToSet(uniqueCharsSet, sum);

            char[] temp = toCharArray(uniqueCharsSet);
            Arrays.sort(temp);

            uniqueChars = temp;
        }

        private char[] uniqueCharsOnly(char ch1, char ch2, char ch3) {
            Set<Character> set = new HashSet<>();
            set.add(ch1);
            set.add(ch2);
            set.add(ch3);

            return toCharArray(set);
        }

        private void addToSet(Set<Character> set, char[] arr) {
            for (char ch : arr) {
                set.add(ch);
            }
        }

        private char[] toCharArray(Set<Character> set) {
            char[] arr = new char[set.size()];

            Iterator<Character> it = set.iterator();
            for (int i = 0; i < arr.length && it.hasNext(); i++) {
                arr[i] = it.next();
            }

            return arr;
        }

        public boolean noMoreLeft() {
            return curIdx >= uniqueChars.length;
        }

        public char curChar() {
            return uniqueChars[curIdx];
        }

        public void next() {
            ++curIdx;
        }

        public void prev() {
            --curIdx;
        }

        public int startIndex(char ch) {
            for (char startCh : startOfWords) {
                if (startCh == ch) {
                    return 1;
                }
            }

            return 0;
        }

        public void assignDigit(int decimalDigit) {
            assert decimalDigit >= 0 && decimalDigit <= 9;
            char curCh = uniqueChars[curIdx];

            charToDigit[curCh - A] = decimalDigit;
        }

        public boolean isValidSolution() {

            int carry = 0;

            for (int k = sum.length - 1, i = value1.length - 1, j = value2.length - 1;
                    k >= 0;
                    --k) {

                int d1 = getDigit(value1, i);
                int d2 = getDigit(value2, j);
                int d3 = getDigit(sum, k);

                int curRes = (d1 + d2 + carry);
                int curDigit = curRes % 10;
                carry = curRes / 10;

                if (curDigit != d3) {
                    return false;
                }

                --i;
                --j;
            }

            return carry == 0;
        }

        private int getDigit(char[] value, int idx) {
            if (idx < 0) {
                return 0;
            }

            return charToDigit[value[idx] - A];
        }

        private String toDecimalStr(char[] value) {
            StringBuilder buf = new StringBuilder(value.length);

            for (char ch : value) {
                buf.append(charToDigit[ch - A]);
            }

            return buf.toString();
        }

        public void recordSolution() {
            solution =
                    "%s+%s=%s"
                            .formatted(
                                    toDecimalStr(value1), toDecimalStr(value2), toDecimalStr(sum));
        }

        public boolean hasSolution() {
            return solution != null;
        }

        public boolean hasViableSolution() {
            return uniqueChars.length <= 10
                    && value1.length <= sum.length
                    && value2.length <= sum.length;
        }
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
