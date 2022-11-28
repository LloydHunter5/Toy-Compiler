import java.util.ArrayList;
import java.util.LinkedList;

public class ToyParser {
    private final ToyLexer lexer;
    public final ArrayList<Token> consumedInputArchive;
    private Token currentToken;
    public ToyParser(ToyLexer lexer){
        this.lexer = lexer;
        currentToken = lexer.getNextToken();
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
        PARAMETER,
        TYPE,
        ASSIGNMENT
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

    public class PrefixUnaryOp extends SingleNode{
        public Token prefix;
        public PrefixUnaryOp(Node operand, Token prefix){
            super(Kind.PREFIX_UNARY_OP,operand);
            this.prefix = prefix;
        }
    }
    public class PostfixUnaryOp extends SingleNode{
        public Token postfix;
        public PostfixUnaryOp(Node operand, Token postfix){
            super(Kind.POSTFIX_UNARY_OP,operand);
            this.postfix = postfix;
        }
    }

    // contains a single terminal token, for example AndOp => '&&' or Name => IDENTIFIER
    public class LeafNode extends Node{
        public LeafNode(Kind kind){
            //Kinds: variable, integer, char, string, bool
            super(kind);
        }
    }

    public class LiteralNode extends LeafNode{
        public TypeNode type;
        public Token value;

        public LiteralNode(TypeNode type, Token value){
            super(Kind.LITERAL);
            this.type = type;
            this.value = value;
        }
    }

    public class VariableNode extends LeafNode{
        public LinkedList<Token> names;
        public VariableNode(LinkedList<Token> names){
            super(Kind.VARIABLE);
            this.names = names;
        }
    }

    // expression node, addition, subtraction, equality, etc
    public class BinaryOp extends BinaryNode
    {
        public Token operator;
        public BinaryOp(Node left, Node right, Token operator){
            super(Kind.BINARY_OP,left,right);
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

    public class CallNode extends Node{
        public LinkedList<Token> name;
        public LinkedList<Node> args;
        public CallNode(LinkedList<Token> name, LinkedList<Node> args){
            super(Kind.CALL);
            this.name = name;
            this.args = args;
        }

        public CallNode(LinkedList<Token> name){
            this(name,null);
        }
    }

    public class IndexNode extends Node{
        public Node arrayVar;
        public Node indexExpr;

        public IndexNode(Node arrayVar, Node indexExpr){
            super(Kind.INDEX);
            this.arrayVar = arrayVar;
            this.indexExpr = indexExpr;
        }
    }

    public class IfNode extends Node{
        public Node condition; //boolean expression
        public Node then; // if x is true then:
        public Node otherwise; //else: (nullable)

        public IfNode(Node condition, Node then, Node otherwise){
            super(Kind.IF);
            this.condition = condition;
            this.then = then;
            this.otherwise = otherwise;
        }
        public IfNode(Node condition, Node then){
            this(condition,then,null);
        }
    }

    public class WhileNode extends Node{
        public Node conditions; //boolean expression
        public Node then;

        public WhileNode(Node conditions, Node then){
            super(Kind.WHILE);
            this.conditions = conditions;
            this.then = then;
        }
    }

    public class ReturnNode extends Node{
        public Node expression;

        public ReturnNode(Node expression){
            super(Kind.RETURN);
            this.expression = expression;
        }
        public ReturnNode(){
            this(null);
        }
    }
    public class DeclNode extends Node{
        public TypeNode type;
        public Token name;

        public DeclNode(Kind kind, TypeNode type, Token name){
            super(kind);
            this.type = type;
            this.name = name;
        }
    }
    public class VarDeclNode extends DeclNode{
        public Node expression;

        public VarDeclNode(TypeNode type, Token name, Node expression){
            super(Kind.DECL,type,name);
            this.expression = expression;
        }
        public VarDeclNode(TypeNode type, Token name){
            this(type,name,null);
        }
    }

    public class TypeNode extends Node{
        public Token type;
        public TypeNode(Token type){
            super(Kind.TYPE);
            // int, char, bool, void, ArrayType
            this.type = type;
        }
    }
    public class BlockNode extends Node{
        public LinkedList<Node> stmts;

        public BlockNode(LinkedList<Node> stmts){
            super(Kind.BLOCK);
            this.stmts = stmts;
        }
    }
    public class MethodDecl extends DeclNode{
        public LinkedList<ParamNode> params;
        public BlockNode body;

        public MethodDecl(TypeNode resultType, Token name, LinkedList<ParamNode> params, BlockNode body){
            super(Kind.METHOD_DECL,resultType,name);
            this.params = params;
            this.body = body;
        }

        public MethodDecl(TypeNode resultType, Token name,BlockNode body){
            this(resultType,name,null,body);
        }
    }

    public class ParamNode extends Node{
        public TypeNode type;
        public Token name;

        public ParamNode(TypeNode type, Token name){
            super(Kind.PARAMETER);
            this.type = type;
            this.name = name;
        }
    }

    public class AssignmentNode extends Node{
        public VariableNode var;
        public Token assignOp;
        public Node expression;

        public AssignmentNode(VariableNode var, Token assignOp, Node expression){
            super(Kind.ASSIGNMENT);
            this.var = var;
            this.assignOp = assignOp;
            this.expression = expression;
        }
    }

    //Group of methods that implement the grammar
    public void parseProgram()
    {
        if(currentToken.type.equals(Tokens.PROGRAM)){
            advanceToNextToken();
        }else{
            throw new IllegalArgumentException("Expected: reserved word: \"program\" at " + locationToString(currentToken));
        }

        if(currentToken.type.equals(Tokens.IDENTIFIER)) {
            advanceToNextToken();
        }else {
            throw new IllegalArgumentException("Expected: identifier at " + locationToString(currentToken));
        }
        parseBlock();
    }

    public BlockNode parseBlock()
    {
        if(currentToken.type.equals(Tokens.OPEN_CURL_BRACKET)){
            advanceToNextToken();
        }else{
            throw new IllegalArgumentException("Expected: '{' at" + locationToString(currentToken));
        }
        LinkedList<Node> stmts = parseStatements();
        if(currentToken.type.equals(Tokens.CLOSE_CURL_BRACKET)){
            advanceToNextToken();
        }else{
            throw new IllegalArgumentException("Expected: '}' at " + locationToString(currentToken));
        }
        return new BlockNode(stmts);
    }

    public LinkedList<Node> parseStatements()
    {
        Node firstStmt = parseStatement();
        LinkedList<Node> stmts = parseStatementsPrime();
        stmts.addFirst(firstStmt);
        return stmts;
    }

    public LinkedList<Node> parseStatementsPrime()
    {
        LinkedList<Node> stmts = new LinkedList<>();
        boolean hasNextStatement = true;
        while(hasNextStatement) {
            switch (currentToken.type) {
                case INT: //variable type
                case CHAR:
                case BOOLEAN:
                    stmts.add(parseDeclaration());
                    break;
                case IDENTIFIER:
                    stmts.add(parseAssignment());
                    break;
                case IF:
                    stmts.add(parseIf());
                    break;
                case WHILE:
                    stmts.add(parseWhile());
                    break;
                case RETURN:
                    stmts.add(parseReturn());
                    break;
                case OPEN_CURL_BRACKET:
                    stmts.add(parseBlock());
                    break;
                default:
                    hasNextStatement = false;
            }
        }
        return stmts;
    }

    public Node parseStatement()
    {
        switch (currentToken.type) {
            case INT: //variable type
            case CHAR:
            case BOOLEAN:
                return parseDeclaration();
            case IDENTIFIER:
                return parseAssignment();
            case IF:
                return parseIf();
            case WHILE:
                return parseWhile();
            case RETURN:
                return parseReturn();
            case OPEN_CURL_BRACKET:
                return parseBlock();
            default:
                throw new IllegalArgumentException("Expected: stuff at" + locationToString(currentToken));
        }
    }

    public DeclNode parseDeclaration()
    {
        TypeNode type;
        Token name;

        switch (currentToken.type){
            case INT:
            case CHAR:
            case BOOLEAN:
            case VOID:
                type = new TypeNode(advanceToNextToken());
                break;
            default:
                throw new IllegalArgumentException("Expected: int, char, boolean, or void at" + locationToString(currentToken));
        }

        if(currentToken.type.equals(Tokens.IDENTIFIER)){
            name = advanceToNextToken();
        }else{
            throw new IllegalArgumentException("Expected: identifier at " + locationToString(currentToken));
        }

        //figure out if it is a method decl or a variable decl
        if(currentToken.type.equals(Tokens.ASSIGN)){
            //variable decl
            advanceToNextToken();
            //TODO
            Node expr = null;
            parseExpression();
            if(currentToken.type.equals(Tokens.SEMICOLON)){
                advanceToNextToken();
                return new VarDeclNode(type,name,expr);
            }
        }else if(currentToken.type.equals(Tokens.OPEN_PAREN)){
            //Method decl
            advanceToNextToken();
            LinkedList<ParamNode> params = null;
            if(!currentToken.type.equals(Tokens.CLOSE_PAREN)){
                //parseParameters -> LinkedList<ParamNode>

            }
            if(currentToken.type.equals(Tokens.CLOSE_PAREN)){
                advanceToNextToken();
            }else{
                throw new IllegalArgumentException("Expected: ')' at " + locationToString(currentToken));
            }
            BlockNode block = parseBlock();
            return new MethodDecl(type, name, params, block);
        }
        //should only be reached if there is an error
        throw new IllegalArgumentException("Expected: ( or = at" + locationToString(currentToken));

    }
    public Node parseAssignment()
    {
        VariableNode v;
        if(currentToken.type.equals(Tokens.IDENTIFIER)){
            v = parseVariable();
        }else{
            throw new IllegalArgumentException("Expected: Identifier at" + locationToString(currentToken));
        }
        Token assignOp;
        switch (currentToken.type){
            case ASSIGN:
            case PLUS_ASSIGN:
            case MINUS_ASSIGN:
            case OR_ASSIGN:
            case AND_ASSIGN:
            case COMPLEMENT_ASSIGN:
            case DIVIDE_ASSIGN:
            case MOD_ASSIGN:
            case MULTIPLY_ASSIGN:
            case XOR_ASSIGN:
            case LEFT_SHIFT_ASSIGN:
            case RIGHT_SHIFT_ASSIGN:
                assignOp = advanceToNextToken();
                break;
            default:
                throw new IllegalArgumentException("Expected: '=' '+=' '-=' '*=' '<<=' '>>=' '|=' '&=' '%=' FINISH LATER EVAN" + locationToString(currentToken));
        }
        //TODO
        parseExpression();
        Node expr = null;

        if(currentToken.type.equals(Tokens.SEMICOLON)){
            advanceToNextToken();
            return new AssignmentNode(v,assignOp,expr);
        }else{
            throw new IllegalArgumentException("Expected: ';' at" + locationToString(currentToken));
        }
    }
    public IfNode parseIf()
    {
        advanceToNextToken(); //if identifier
        if(currentToken.type.equals(Tokens.OPEN_PAREN)){
            advanceToNextToken();
        }else{
            throw new IllegalArgumentException("Expected: '(' at" + locationToString(currentToken));
        }
        parseExpression(); //should be a boolean expression

        return null;
    }
    public void parseMatchedStatement()
    {

    }


    public WhileNode parseWhile()
    {
        // WHILE
        // '('
        // parseBooleanExpr
        // ')'
        // parseStatement
        return null;
    }
    public ReturnNode parseReturn()
    {
        ReturnNode node = new ReturnNode();
        // reserved word return
        if(currentToken.type.equals(Tokens.RETURN)){
            advanceToNextToken();
        }else{
            throw new IllegalArgumentException("Expected return statement at " + locationToString(currentToken));
        }
        //Optional return value
        if(!currentToken.type.equals(Tokens.SEMICOLON)){
            //TODO
            node.expression = null;
            parseExpression();
        }
        //semicolon
        if(currentToken.type.equals(Tokens.SEMICOLON)){
            advanceToNextToken();
        }else{
            throw new IllegalArgumentException("Expected ';' at " + locationToString(currentToken));
        }
        return node;
    }

    public CallNode parseCallStatement(){
        CallNode node = parseCall();
        if(currentToken.type.equals(Tokens.SEMICOLON)){
            advanceToNextToken();
            return node;
        }else{
            throw new IllegalArgumentException("Expected ';' at " + locationToString(currentToken));
        }
    }
    public CallNode parseCall()
    {
        LinkedList<Token> names = parseName();
        LinkedList<Node> args;
        if(currentToken.type.equals(Tokens.OPEN_PAREN)){
            advanceToNextToken();
        }

        // Optional args
        if(currentToken.type.equals(Tokens.CLOSE_PAREN)){
            advanceToNextToken();
            return new CallNode(names);
        }else{
            args = parseArguments();
        }
        if(currentToken.type.equals(Tokens.CLOSE_PAREN)){
            advanceToNextToken();
            return new CallNode(names,args);
        }else{
            throw new IllegalArgumentException("Expected: ')' at " + locationToString(currentToken));
        }
    }

    public LinkedList<Node> parseArguments(){
        parseArgument();
        parseArgumentsPrime();
        return new LinkedList<>();
    }

    public void parseArgumentsPrime(){

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
                throw new IllegalArgumentException("Expected: Identifier, Literal, or ( at " + locationToString(currentToken));
        }
    }

    public VariableNode parseVariable(){
        LinkedList<Token> name = parseName();
        VariableNode variable = new VariableNode(name);
        //TODO: handle arrayType variables
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
        return variable;
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

    public LinkedList<Token> parseName(){
        if(currentToken.type.equals(Tokens.IDENTIFIER)){
            Token temp = advanceToNextToken(); //got identifier
            LinkedList<Token> tokens = parseNamePrime();
            tokens.add(0,temp);
            return tokens;
        }else{
            throw new IllegalArgumentException("Expected: Identifier  at " + locationToString(currentToken));
        }
    }

    public LinkedList<Token> parseNamePrime(){
        LinkedList<Token> names = new LinkedList<>();

        while(currentToken.type.equals(Tokens.DOT)){
            advanceToNextToken();
            if(currentToken.type.equals(Tokens.IDENTIFIER)){
                names.addLast(advanceToNextToken());
            }else{
                throw new IllegalArgumentException("Expected: Identifier at " + locationToString(currentToken));
            }
        }
        return names;
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
