import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ToyScanner {
    public static void main(String[] args) throws FileNotFoundException {
        testParser("src/parseTest.txt");
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

    public static void testParser(String filepath) throws FileNotFoundException{
        Scanner s = new Scanner(new File(filepath));
        ToyLexer lexer = new ToyLexer(s);
        ToyParser parser = new ToyParser(lexer);

        if(parser.parseLine()){
            System.out.println("Input is a valid expression");
        }else{
            System.err.println("Input is not a valid expression");
        }


        for(Token t : parser.consumedInputArchive){
            System.out.println(t);
        }
    }

    public static void testParserUsingStdIn(){
        // Tests parser using standard input
        Scanner s = new Scanner(System.in);
        ToyParser parser = new ToyParser(new ToyLexer(s));

        while(parser.hasNextToken()){
            System.out.println(parser.parseLine()); // Returns boolean value (is line valid?)
        }

        for(Token t : parser.consumedInputArchive){
            System.out.println(t);
        }
    }
}
