package TinyExercises;

public class Exercise2 {

    // Exercise #2A:
    //
    // Write method(s) in the TINY assembly language
    // to print a number in decimal using the recursive
    // algorithm shown below.  Your 'main' program at
    // location 0 should implement the main1 method
    // shown below.

    // Exercise #2B:
    //
    // Write methods(s) in the TINY assembly language
    // to print a number in a given base (radix) using
    // the recursive algorithm show below.  Your 'main'
    // program at location 0 should implement the main2
    // method shown below.

    // Hints:
    //
    // 1: Pass parameters in registers.  The first param
    // will be placed in R0, the second in R1, etc.
    //
    // 2: If you have any values in registers which you
    // need use again after a call the will need to be
    // saved.  The easiest way to do so is to push them
    // onto the stack before the call and then pop them
    // from the stack after the call.


    private static void out(char c) {
        System.out.print(c);
    }

    private static void println() {
        out('\n');
    }

    // Methods to print an integer in decimal
    // Note that the helper method is recursive

    private static void print(int n) {
        if (n > 0) {
            print (n / 10);
            char c = (char) ('0' + n % 10);
            out(c);
        }
    }

    public static void printInt(int n) {
        if (n < 0) {
            out('-');
            print(-n);
        } else if (n > 0) {
            print(n);
        } else {
            out('0');
        }
    }

    // Methods to print an integer in a given radix

    private static void print(int n, int radix) {
        if (n > 0) {
            print (n / radix, radix);
            char c = "0123456789ABCDEF".charAt(n % radix);
            out(c);
        }
    }

    public static void printInt(int n, int radix) {
        if (n < 0) {
            out('-');
            print(-n, radix);
        } else if (n > 0) {
            print(n, radix);
        } else {
            out('0');
        }
    }

    public static void main1() {
        printInt(1); println();
        printInt(-1); println();
        printInt(11); println();
        printInt(-11); println();
        printInt(751); println();
        printInt(-751); println();
        printInt(1<<14); println();
        printInt(1<<15); println();
    }

    public static void main2() {
        printInt(1, 16); println();
        printInt(-1, 16); println();
        printInt(11, 16); println();
        printInt(-11, 16); println();
        printInt(751, 16); println();
        printInt(-751, 16); println();
        printInt(1<<14, 16); println();
        printInt(1<<15, 16); println();
    }

    public static void main(String[] args) {
        main1();
    }
}
