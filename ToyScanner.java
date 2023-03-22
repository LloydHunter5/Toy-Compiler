import ast.AbstractSyntaxTree;
import lexer.ToyLexer;
import parser.ToyParser;
import token.Token;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ToyScanner {
    // For cool terminal font colors
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    /*
    * ==================
    * ==================
    *  Program Settings
    * ==================
    * ==================
    */

    public static class Settings {
        public static final boolean printParserStackTrace   = false;
        public static final boolean printSemanticStackTrace = false;
        public static final ToyParser.EM errorMessageType   = ToyParser.EM.STANDARD;

    }
    public static void main(String[] args) {
        File testProgramDirectory = new File("src/test");
        File[] testPrograms = testProgramDirectory.listFiles();

        if(testPrograms != null) {
            System.out.println("Program\t\t  Parse\t\tSemantics");
            for (File prgm : testPrograms) {
                if(!prgm.getName().equals("test.txt")){
                    boolean completedParse = false;
                    boolean completedSemantics = false;
                    String parseErrorMessage = "Program has code after the program block";
                    String semanticErrorMessage = "Something broke while checking semantics";
                    try {
                        completedParse = testParser(prgm,Settings.printParserStackTrace);
                        try {
                            completedSemantics = testSymbolTable(prgm,Settings.printSemanticStackTrace);
                        }catch(IllegalArgumentException e){
                            semanticErrorMessage = e.getMessage();
                        }
                    }catch(IllegalArgumentException e){
                        parseErrorMessage = e.getMessage();
                    }

                    System.out.print(prgm.getName().substring(0,prgm.getName().lastIndexOf(".")) + ": ");
                    int len = prgm.getName().length();
                    // Text align
                    while (len < 16){
                        System.out.print(" ");
                        len++;
                    }
                    if(completedParse){
                        System.out.print(ANSI_GREEN + "Valid" + ANSI_RESET);
                        System.out.print("\t\t");
                        if(completedSemantics){
                            System.out.print(ANSI_GREEN + "Valid" + ANSI_RESET);
                        }else{
                            System.out.print(ANSI_RED + semanticErrorMessage + ANSI_RESET);
                        }
                    }else{
                        System.out.println(ANSI_RED + parseErrorMessage + ANSI_RESET);
                    }
                    System.out.println();
                }
            }
        }
    }

    public static void testLexer(String filepath) throws FileNotFoundException{
        // Tests toy lexer using file at given filepath
        Scanner s = new Scanner(new File(filepath));
        ArrayList<Token> tokens = new ArrayList<>();
        ToyLexer lexer = new ToyLexer(s);

        while (lexer.hasNextToken()) {
            tokens.add(lexer.getNextToken());
        }

        for(Token t : tokens){
            System.out.println(t);
        }
    }



    public static void testLexerUsingStdIn(){
        // Tests lexer using standard input
        Scanner s = new Scanner(System.in);
        ToyLexer lexer = new ToyLexer(s);

        while(lexer.hasNextToken()){
            System.out.println(lexer.getNextToken());
        }
    }

    public static boolean testParser(File f, boolean trace) {
        try {
            Scanner s = new Scanner(f);
            ToyLexer lexer = new ToyLexer(s);
            ToyParser parser = new ToyParser(lexer,Settings.errorMessageType);

            parser.parseProgram();

            if(trace){
                for (token.Token t : parser.consumedInputArchive) {
                    System.out.println(t);
                }
            }

            return !lexer.hasNextToken();
        }catch(FileNotFoundException e){
            System.err.println("file not found");
        }
        return false;
    }
    public static boolean testParser(String filepath,boolean trace) {
        return testParser(new File(filepath), trace);
    }

    public static boolean testSymbolTable(File f,boolean trace){
        try {
            Scanner s = new Scanner(f);
            ToyLexer lexer = new ToyLexer(s);
            ToyParser parser = new ToyParser(lexer,Settings.errorMessageType);
            AbstractSyntaxTree ast = AbstractSyntaxTree.build(parser.parseProgram());
            if(trace) {
                ast.printContents();
            }
            return true;
        }catch(FileNotFoundException e){
            System.err.println("file not found");
        }
        return false;
    }
}
