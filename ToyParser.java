import java.util.ArrayList;

public class ToyParser {
    private final ToyLexer lexer;
    public final ArrayList<Token> consumedInputArchive;
    private Token currentToken;
    public ToyParser(ToyLexer lexer){
        this.lexer = lexer;
        this.consumedInputArchive = new ArrayList<>();
    }

    public enum Kind{
        VARIABLE,
        LITERAL,
        BINARY_OP,
        PREFIX_UNARY_OP,
        POSTFIX_UNARY_OP,
        INDEX,
        CALL,
        IF,
        WHILE,
        RETURN,
        BLOCK,
        ARGUMENTS,
        DECL,
        METHOD_DECL,
        PARAMETER
    }
    //Root class for all objects of type node, every node has reference to its parent
    public class Node
    {
        public Kind kind;
        public Node(Kind kind){
            this.kind = kind;
        }
    }
    //Node subclasses

    //Single child, for example if Term => Factor
    public class SingleNode extends Node
    {
        public Node child;

        public SingleNode(Kind kind, Node child){
            super(kind);
            this.child = child;
        }
    }

    public class PrefixSingleNode extends SingleNode{
        public Token prefix;
        public PrefixSingleNode(Kind kind, Node child, Token prefix){
            super(kind,child);
            this.prefix = prefix;
        }
    }
    public class PostfixSingleNode extends SingleNode{
        public Token postfix;
        public PostfixSingleNode(Kind kind, Node child, Token postfix){
            super(kind,child);
            this.postfix = postfix;
        }
    }

    // contains a single terminal token, for example AndOp => '&&' or Name => IDENTIFIER
    public class TerminalNode extends Node{
        public Token token;

        public TerminalNode(Kind kind, Token token){
            super(kind);
            this.token = token;
        }
    }

    // expression node, addition, subtraction, equality, etc
    public class OperationNode extends BinaryNode
    {
        public Token operator;
        public OperationNode(Kind kind, Node left, Node right, Token operator){
            super(kind,left,right);
            this.operator = operator;
        }
    }

    //Two child nodes, left op comes first
    public class BinaryNode extends Node{
        public Node left;
        public Node right;

        public BinaryNode(Kind kind, Node left, Node right){
            super(kind);
            this.left = left;
            this.right = right;
        }
    }

    public class TrinaryNode extends BinaryNode {
        public Node middle;

        public TrinaryNode(Kind kind, Node left, Node right, Node middle){
            super(kind,left,right);
            this.middle = middle;
        }
    }

    // single node surrounded by two parens/brackets
    public class ContainedNode extends SingleNode{
        public Token left;
        public Token right;

        public ContainedNode(Kind kind,Token left, Node child, Token right){
            super(kind,child);
            this.left = left;
            this.right = right;
        }
    }

    //contained node with a prefix
    /* public class PrefixContainedNode extends ContainedNode{
        public Node prefix;
        public PrefixContainedNode(Node parent, Token left, Node child, Token right, Node prefix) {
            super(parent,left,child,right);
            this.prefix = prefix;
        }
    }
    //
    public class PostfixContainedNode extends ContainedNode{
        public Node postfix;
        public PostfixContainedNode(Node parent, Token left, Node child, Token right, Node postfix) {
            super(parent,left,child,right);
            this.postfix = postfix;
        }
    } */


    /* should work for nodes that require an arbitrary number of terminals and non-terminals
    user would pre-allocate an array, pass it in, and then check + cast it to the appropriate type */
    // Helper class for getting the correct node, for example: return Wizard.createNode(parseConjunction(), parseDisjunctionPrime())
    // should make code a little easier to write, will always be able to create the correct type of node

    //Group of methods that implement the grammar
    public void parseProgram()
    {
        //PROGRAM
        //IDENTIFIER
        //parseBlock
    }

    public void parseBlock()
    {
        // '{'
        // parseStatements
        // '}'
    }

    public void parseStatements()
    {
        // parseStatement
        // parseStatementsPrime
    }

    public void parseStatementsPrime()
    {
        // while (there is another statement)
        //      parseStatement
        // endWhile
    }

    public void parseStatement()
    {
        // switch (first token)
        //      case (variableType): parseDeclaration
        //      case (identifier): parseAssignment
        //      case (if): parseIf
        //      case (while): parseWhile
        //      case (return): parseReturn
        //      case (methodName): parseCall
        //      case ('{'): parseBlock
    }

    public void parseDeclaration()
    {

    }
    public void parseAssignment()
    {

    }
    public void parseIf()
    {

    }
    public void parseMatchedStatement()
    {

    }


    public void parseWhile()
    {
        // WHILE
        // '('
        // parseBooleanExpr
        // ')'
        // parseStatement
    }
    public void parseReturn()
    {
        // RETURN
        // if(next token is ';')
        //      ';'
        // else
        //      parseExpression
        //      ';'
        // endIf
    }
    public void parseCall()
    {

    }

    public void parseArguments(){
        parseArgument();
        parseArgumentsPrime();
    }

    public void parseArgumentsPrime(){
        switch (currentToken.type){
        }
    }

    public void parseArgument(){
        parseExpression();
    }
    // Expressions
    public void parseExpression(){
        parseDisjunction();
        parseExpressionPrime();
    }

    public void parseExpressionPrime(){
        if(currentToken.type.equals(Tokens.ASSIGN)){
            //TERMINAL consume input
            advanceToNextToken();
            parseDisjunction();
            parseExpressionPrime();
        }
        //Has epsilon; no error
    }

    public void parseDisjunction(){
        parseConjunction();
        parseDisjunctionPrime();
    }

    public void parseDisjunctionPrime(){
        if(currentToken.type.equals(Tokens.OR) || currentToken.type.equals(Tokens.CONDITIONAL_OR) || currentToken.type.equals(Tokens.COMPLEMENT)) {
            parseOrOp();
            parseConjunction();
            parseDisjunctionPrime();
        }
        //has epsilon
    }

    public Token parseOrOp(){
        switch(currentToken.type){
            case OR:
            case CONDITIONAL_OR:
            case COMPLEMENT:
                return advanceToNextToken();
            default:
                throw new IllegalArgumentException("expected: |, ||, or ^ at " + locationToString(currentToken));
        }
    }

    public void parseConjunction(){
        parseRelation();
        parseConjunctionPrime();
    }

    public void parseConjunctionPrime(){
        if(currentToken.type.equals(Tokens.AND) || currentToken.type.equals(Tokens.CONDITIONAL_AND)){
            parseAndOp();
            parseRelation();
            parseConjunctionPrime();
        }
        //has epsilon
    }

    public Token parseAndOp(){
        switch(currentToken.type){
            case AND:
            case CONDITIONAL_AND:
                return advanceToNextToken();
            default:
                throw new IllegalArgumentException("Expected: & or && at " + locationToString(currentToken));
        }
    }

    public void parseRelation(){
        parseSimpleExpression();
        parseRelationPrime();
    }

    public void parseRelationPrime(){
        switch (currentToken.type){
            case LESS:
            case LESS_EQUAL:
            case GREATER:
            case GREATER_EQUAL:
            case EQUAL:
            case NOT_EQUAL:
                parseCompareOp();
                parseTerm();
                parseRelationPrime();
                break;
        }
        //has epsilon
    }

    public Token parseCompareOp(){
        switch (currentToken.type) {
            case LESS:
            case LESS_EQUAL:
            case GREATER:
            case GREATER_EQUAL:
            case EQUAL:
            case NOT_EQUAL:
                return advanceToNextToken();
            default:
                throw new IllegalArgumentException("Expected: >, <, >=, <=, ==, or != at " + locationToString(currentToken));
        }
    }

    public void parseSimpleExpression(){
        switch (currentToken.type){
            case PLUS:
            case MINUS:
                parseSign();
                parseTerm();
                parseSimpleExpressionPrime();
                break;
            default:
                parseTerm();
                parseSimpleExpressionPrime();
        }
    }

    public Token parseSign(){
        switch (currentToken.type){
            case PLUS:
            case MINUS:
                return advanceToNextToken();
            default:
                throw new IllegalArgumentException("Expected: + or - at " + locationToString(currentToken));
        }
    }

    public void parseSimpleExpressionPrime(){
        switch (currentToken.type){
            case PLUS:
            case MINUS:
                parseAddOp();
                parseTerm();
                parseSimpleExpressionPrime();
                break;
        }
    }

    public Token parseAddOp(){
        switch (currentToken.type){
            case PLUS:
            case MINUS:
                return advanceToNextToken();
            default:
                throw new IllegalArgumentException("Expected: + or - at" + locationToString(currentToken));
        }
    }

    public void parseTerm(){
        parseFactor();
        parseTermPrime();
    }

    public void parseTermPrime(){
        switch (currentToken.type){
            case MULTIPLY:
            case DIVIDE:
            case MOD:
                parseMulOp();
                parseFactor();
                parseTermPrime();
                break;
        }
        //has epsilon
    }

    public Token parseMulOp(){
        switch (currentToken.type){
            case MULTIPLY:
            case DIVIDE:
            case MOD:
                return advanceToNextToken();
            default:
                throw new IllegalArgumentException("Expected: *, /, or % at " + locationToString(currentToken));
        }
    }

    public void parseFactor(){
        switch (currentToken.type){
            case IDENTIFIER:
                parseName();
                parseFactorSuffix();
                break;
            case HEXADECIMAL_LITERAL:
            case NUMERIC_LITERAL:
            case CHARACTER_LITERAL:
            case STRING_LITERAL:
            case TRUE:
            case FALSE:
                parseLiteral();
                break;
            case NOT:
                parseUnaryOp();
                parseFactor();
                break;
            case INCREMENT:
            case DECREMENT:
                parsePrefixOp();
                parseVariable();
                break;
            case OPEN_PAREN:
                advanceToNextToken();
                parseExpression();
                if(currentToken.type.equals(Tokens.CLOSE_PAREN)){
                    advanceToNextToken();
                }else{
                    throw new IllegalArgumentException("Expected: ) at " + locationToString(currentToken));
                }
                break;
            default:
                throw new IllegalArgumentException("Expecting: Identifier, Literal, or ( at " + locationToString(currentToken));
        }
    }

    public void parseVariable(){
        parseName();

        //can be epsilon
        if(currentToken.type.equals(Tokens.OPEN_BRACKET)){
             advanceToNextToken();

            parseExpression();
            if(currentToken.type.equals(Tokens.CLOSE_BRACKET)){
                 advanceToNextToken();
            }else{
                throw new IllegalArgumentException("Expected: ] at " + locationToString(currentToken));
            }
        }
    }

    public Token parsePrefixOp(){
        switch (currentToken.type){
            case INCREMENT:
            case DECREMENT:
                return advanceToNextToken();
            default:
                throw new IllegalArgumentException("Expected: ++ or -- at " + locationToString(currentToken));
        }
    }
    public Token parseUnaryOp(){
        switch (currentToken.type){
            case NOT:
                return advanceToNextToken();
            default:
                throw new IllegalArgumentException("Expected: ! at " + locationToString(currentToken) );
        }
    }

    public Token parseLiteral(){
        switch (currentToken.type){
            case HEXADECIMAL_LITERAL:
            case NUMERIC_LITERAL:
            case CHARACTER_LITERAL:
            case STRING_LITERAL:
            case TRUE:
            case FALSE:
                return advanceToNextToken();
            default:
                throw new IllegalArgumentException("Expected: Literal at " + locationToString(currentToken));
        }
    }

    public void parseFactorSuffix(){
        switch (currentToken.type){
            case OPEN_PAREN:
                advanceToNextToken();
                //Optional Arguments inside of parenthesis
                if(currentToken.type.equals(Tokens.CLOSE_PAREN)){
                    advanceToNextToken();
                }else{
                    parseArguments();
                    advanceToNextToken();
                }
                break;
            case OPEN_BRACKET:
                advanceToNextToken();
                parseExpression();
                if(currentToken.type.equals(Tokens.CLOSE_BRACKET)){
                    advanceToNextToken();
                }else{
                    throw new IllegalArgumentException("Expected: ] at " + locationToString(currentToken));
                }
                //PostfixOp
                if(currentToken.type.equals(Tokens.INCREMENT) || currentToken.type.equals(Tokens.DECREMENT)){
                    advanceToNextToken();
                }
                break;
        }
        //has epsilon
    }

    public void parseName(){
        if(currentToken.type.equals(Tokens.IDENTIFIER)){
            advanceToNextToken(); //got identifier
            parseNamePrime();
        }else{
            throw new IllegalArgumentException("Expected: Identifier  at " + locationToString(currentToken));
        }
    }

    public void parseNamePrime(){
        switch (currentToken.type){
            case DOT:
                advanceToNextToken();
                if(currentToken.type.equals(Tokens.IDENTIFIER)){
                    advanceToNextToken();
                }else{
                    throw new IllegalArgumentException("Expected: Identifier at " + locationToString(currentToken));
                }
                parseNamePrime();
                break;
        }
        //has epsilon
    }



    //Helper Methods
    public Token advanceToNextToken(){
        //Go to the next token
        Token oldToken = currentToken;
        currentToken = lexer.getNextToken();

        //Store and return the previous
        consumedInputArchive.add(oldToken);
        return oldToken;
    }

    public String locationToString(Token t){
        return "[line: " + currentToken.line + ", column: " + currentToken.col + "]";
    }

    //Methods that use the grammar
    public boolean parseLine(){
        currentToken = lexer.getNextToken();
        try {
            parseExpression();
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return false;
        }

        //if there are tokens left after parsing an expression, then the expression is invalid
        return !lexer.hasNextToken();
    }

    public boolean hasNextToken(){
        return lexer.hasNextToken();
    }
}
