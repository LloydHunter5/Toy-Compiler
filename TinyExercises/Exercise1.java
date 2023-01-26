package TinyExercises;

public class Exercise1 {

    // Exercise 1A:
    //
    // Write TINY assembler method(s) to print a null terminated
    // string to standard output as shown in the methods print
    // and println below.  Your main program (at loation zero).
    // should just be the first line of the Java main program
    // shown below.
    //
    // Exercise 1B:
    //
    // Write TINY assembler methods(s) to flip the case of a
    // character and to print a string with its case flipped
    // as shown in the Java methods below.  Update your main
    // program to include the second line of the Java main
    // program shown below.
    //
    // Exercise 1C:
    //
    // Write a TINY assemberl method to copy a source string to
    // a destination buffer while flipping the case as shown in
    // the Java flipCase method below.  Then update your main
    // program to include the last two lines of the Java main
    // program shown below.
    //
    // Hints:
    //
    // 1: Pass parameters in registers.  The first param
    // will be placed in R0, the second in R1, etc.
    //
    // 2: If you have any values in registers which you
    // need use again after a call, they will need to be
    // saved.  The easiest way to do so is to push then
    // onto the stack before the call and pop them from
    // the stack after the call.
    //
    // 3: The null character in TINY is represented as '/0'
    // and not '\000' as in Java.

    public static void out(char c) {
        System.out.print(c);
    }

    public static void print(char[] s) {
        int i = 0;
        while (s[i] != '\000') {
            out(s[i]);
			i++;
        }
    }

    public static void println(char[] s) {
        print(s);
        out('\n');
    }

    public static char flipCase(char c) {
        if (c >= 'a' && c <= 'z') {
            return (char) (c - 'a' + 'A');
        } else if (c >= 'A' && c <= 'Z') {
            return (char) (c - 'A' + 'a');
        } else {
            return c;
        }
    }

    public static void printFlipCase(char[] s) {
        int i = 0;
        while (s[i] != '\000') {
            char c = flipCase(s[i]);
            out(c);
			i++;
        }
        out('\n');
    }

    public static void flipCase(char[] source, char[] target) {
        int i = 0;
        do {
            target[i] = flipCase(source[i]);
			i++;
        } while (source[i] != '\000');
    }


    public static final char[] TINY = {'T', 'I', 'N', 'Y', ' ', 'i', 's', ' ', 'f', 'u', 'n', '\000'};
    public static final char[] flip = new char[TINY.length];

    // Something that is easier in TINY asmembler:
    //
    // TINY: .ascii "TINY is FUN"
    // flip: .space 12
    //

    public static void main(String[] args) {
        println(TINY);
        printFlipCase(TINY);
        flipCase(TINY, flip);
        println(flip);
    }
}

