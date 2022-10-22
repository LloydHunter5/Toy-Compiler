import java.util.ArrayList;

public class ToyParser {
    private final ToyLexer lexer;
    public final ArrayList<Token> consumedInputArchive;
    private Token currentToken;
    public ToyParser(ToyLexer lexer){
        this.lexer = lexer;
        this.consumedInputArchive = new ArrayList<>();
    }

    //Root class for all objects of type node, every node has reference to its parent
    public class Node
    {
        public Node parent;

        public Node(Node parent){
            this.parent = parent;
        }
    }
    //Node subclasses

    //Single child, for example if Term => Factor
    public class SingleNode extends Node
    {
        public Node child;

        public SingleNode(Node parent, Node child){
            super(parent);
            this.child = child;
        }
    }

    // contains a single terminal token, for example AndOp => '&&' or Name => IDENTIFIER
    public class TerminalNode extends Node{
        public Token token;

        public TerminalNode(Node parent, Token token){
            super(parent);
            this.token = token;
        }
    }

    // expression node, addition, subtraction, equality, etc
    public class OperationNode extends BinaryNode
    {
        public Token operator;
        public OperationNode(Node parent, Node left, Node right, Token operator){
            super(parent,left,right);
            this.operator = operator;
        }
    }

    //Two child nodes, left op comes first
    public class BinaryNode extends Node{
        public Node left;
        public Node right;

        public BinaryNode(Node parent, Node left, Node right){
            super(parent);
            this.left = left;
            this.right = right;
        }
    }

    public class TrinaryNode extends BinaryNode {
        public Node middle;

        public TrinaryNode(Node parent, Node left, Node right, Node middle){
            super(parent,left,right);
            this.middle = middle;
        }
    }

    // single node surrounded by two parens/brackets
    public class ContainedNode extends SingleNode{
        public Token left;
        public Token right;

        public ContainedNode(Node parent, Token left, Node child, Token right){
            super(parent,child);
            this.left = left;
            this.right = right;
        }
    }

    //contained node with a prefix
    public class PrefixContainedNode extends ContainedNode{
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
    }


    /* should work for nodes that require an arbitrary number of terminals and non-terminals
    user would pre-allocate an array, pass it in, and then check + cast it to the appropriate type */
    public class MiscNode extends Node{
        private Object[] children;
        private int index;

        public MiscNode(Node parent, Object[] itemsLeftToRight){
            super(parent);
            children = itemsLeftToRight;
            index = 0;
        }

        public void startScanning(){
            this.index = 0;
        }

        public boolean hasNextChild(){
            return this.index < children.length;
        }

        public Object nextChild(){
            return children[this.index++];
        }
    }

    // Helper class for getting the correct node, for example: return Wizard.createNode(parseConjunction(), parseDisjunctionPrime())
    // should make code a little easier to write, will always be able to create the correct type of node
    public class Wizard{
        public Node createNode(Node parent){
            return new Node(parent);
        }
        public Node createNode(Node parent, Node child){
            return new SingleNode(parent,child);
        }
        public Node createNode(Node parent, Token left, Node child, Token right){
            return new ContainedNode(parent,left,child,right);
        }
        public Node createNode(Node parent, Token left, Node child, Token right, Node fix, boolean isPrefix){
            if(isPrefix){
                return new PrefixContainedNode(parent,left,child,right,fix);
            }else{
                return new PostfixContainedNode(parent,left,child,right,fix);
            }
        }
        public Node createNode(Node parent, Node left, Node right){
            return new BinaryNode(parent,left,right);
        }
        public Node createNode(Node parent, Node left, Node right, Node middle){
            return new TrinaryNode(parent, left,right,middle);
        }
        public Node createNode(Node parent, Token token){
            return new TerminalNode(parent, token);
        }
        public Node createNode(Node parent, Node left,Token operation, Node right){
            return new OperationNode(parent,left,right,operation);
        }
        public Node createNode(Node parent, Object[] children){
            return new MiscNode(parent, children);
        }
    }



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

    public void parseOrOp(){
        switch(currentToken.type){
            case OR:
            case CONDITIONAL_OR:
            case COMPLEMENT:
                advanceToNextToken();
                break;
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

    public void parseAndOp(){
        switch(currentToken.type){
            case AND:
            case CONDITIONAL_AND:
                advanceToNextToken();
                break;
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

    public void parseCompareOp(){
        switch (currentToken.type) {
            case LESS:
            case LESS_EQUAL:
            case GREATER:
            case GREATER_EQUAL:
            case EQUAL:
            case NOT_EQUAL:
                advanceToNextToken();
                break;
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

    public void parseSign(){
        switch (currentToken.type){
            case PLUS:
            case MINUS:
                advanceToNextToken();
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

    public void parseAddOp(){
        switch (currentToken.type){
            case PLUS:
            case MINUS:
                advanceToNextToken();
                break;
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

    public void parseMulOp(){
        switch (currentToken.type){
            case MULTIPLY:
            case DIVIDE:
            case MOD:
                advanceToNextToken();
                break;
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

    public void parsePrefixOp(){
        switch (currentToken.type){
            case INCREMENT:
            case DECREMENT:
                advanceToNextToken();
                break;
            default:
                throw new IllegalArgumentException("Expected: ++ or -- at " + locationToString(currentToken));
        }
    }
    public void parseUnaryOp(){
        switch (currentToken.type){
            case NOT:
                advanceToNextToken();
            default:
                throw new IllegalArgumentException("Expected: ! at " + locationToString(currentToken) );
        }
    }

    public void parseLiteral(){
        switch (currentToken.type){
            case HEXADECIMAL_LITERAL:
            case NUMERIC_LITERAL:
            case CHARACTER_LITERAL:
            case STRING_LITERAL:
            case TRUE:
            case FALSE:
                advanceToNextToken();
                break;
            default:
                throw new IllegalArgumentException("Expected: Literal at " + locationToString(currentToken));
        }
    }

    public void parseFactorSuffix(){

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
    public void advanceToNextToken(){
        consumedInputArchive.add(currentToken);
        currentToken = lexer.getNextToken();
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
        return true;
    }

    public boolean hasNextToken(){
        return lexer.hasNextToken();
    }
}
