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
        ASSIGNMENT,
        PROGRAM,
        SIMPLE_EXPRESSION
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
        //For array vars
        public IndexNode index;
        public VariableNode(LinkedList<Token> names){
            this(names,null);
        }

        public VariableNode(LinkedList<Token> names, IndexNode index){
            super(Kind.VARIABLE);
            this.names = names;
            this.index = index;
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
        public boolean isArrayType;
        public Node arrSize;
        public TypeNode(Token type, boolean isArrayType, Node arrSize){
            super(Kind.TYPE);
            // int, char, bool, void, ArrayType
            this.type = type;
            this.isArrayType = isArrayType;
            this.arrSize = arrSize;
        }

        public TypeNode(Token type, boolean isArrayType){
            this(type,isArrayType,null);
        }
        public TypeNode(Token type){
            this(type,false,null);
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

    public class ProgramNode extends Node{
        public Token name;
        public BlockNode body;

        public ProgramNode(Token name,BlockNode body){
            super(Kind.PROGRAM);
            this.name = name;
            this.body = body;
        }
    }

    //Extension of binaryOp to include a sign for the left node
    public class SimpleExpressionNode extends BinaryOp{
        public Token sign;
        public SimpleExpressionNode(Token sign, BinaryOp left, SimpleExpressionNode right, Token operator){
            super(left,right,operator);
            this.kind = Kind.SIMPLE_EXPRESSION;
            this.sign = sign;
        }

    }

    //Group of methods that implement the grammar
    public Node parseProgram()
    {
        if(currentToken.type.equals(Tokens.PROGRAM)){
            advanceToNextToken();
        }else{
            throw new IllegalArgumentException("Expected: reserved word: \"program\" at " + locationToString(currentToken));
        }
        Token prgmName;
        if(currentToken.type.equals(Tokens.IDENTIFIER)) {
            prgmName = advanceToNextToken();
        }else {
            throw new IllegalArgumentException("Expected: identifier at " + locationToString(currentToken));
        }
        BlockNode block = parseBlock();
        return new ProgramNode(prgmName,block);
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
                case VOID: //only for methods, but both are parsed in the same method
                    stmts.add(parseDeclaration());
                    break;
                case IDENTIFIER:
                    stmts.add(parseAssignmentOrCall());
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
                //since both start with identifiers, just parse both in the same function
                return parseAssignmentOrCall();
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
        }else if(currentToken.type.equals(Tokens.SEMICOLON)) { //ex. "int a;"
            advanceToNextToken();
            return new VarDeclNode(type,name);
        }else if(currentToken.type.equals(Tokens.OPEN_PAREN)){
            //Method decl
            advanceToNextToken();
            //Optional method parameters
            LinkedList<ParamNode> params = null;
            if(!currentToken.type.equals(Tokens.CLOSE_PAREN)){
                params = parseParameters();
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
        throw new IllegalArgumentException("Expected: '(', '=', or ';' at" + locationToString(currentToken));

    }

    public LinkedList<ParamNode> parseParameters(){
        ParamNode firstParam = parseParameter();
        LinkedList<ParamNode> params = parseParametersPrime();
        params.addFirst(firstParam);
        return params;
    }

    public LinkedList<ParamNode> parseParametersPrime(){
        LinkedList<ParamNode> params = new LinkedList<>();
        while(currentToken.type.equals(Tokens.COMMA)){
            advanceToNextToken(); //consume comma
            params.add(parseParameter()); //parse type and name
        }
        return params;
    }

    public ParamNode parseParameter(){
        Token typeName;
        Token name;
        boolean isArrayParam = false;
        switch (currentToken.type){
            case INT:
            case CHAR:
            case BOOLEAN:
                typeName = advanceToNextToken();
                break;
            default:
                throw new IllegalArgumentException("Expected: int, char, boolean at " + locationToString(currentToken));
        }

        //Optional arrayType
        if(currentToken.type.equals(Tokens.OPEN_BRACKET)){
            advanceToNextToken();
            if(currentToken.type.equals(Tokens.CLOSE_BRACKET)){
                advanceToNextToken();
            }else{
                throw new IllegalArgumentException("Expected: ']' at " + locationToString(currentToken));
            }
            isArrayParam = true;
        }

        if(currentToken.type.equals(Tokens.IDENTIFIER)){
            name = advanceToNextToken();
        }else{
            throw new IllegalArgumentException("Expected: identifier at " + locationToString(currentToken));
        }

        return new ParamNode(new TypeNode(typeName,isArrayParam),name);
    }
    public Node parseAssignmentOrCall()
    {
        VariableNode v;
        if(currentToken.type.equals(Tokens.IDENTIFIER)){
            v = parseVariable();
        }else{
            throw new IllegalArgumentException("Expected: Identifier at" + locationToString(currentToken));
        }
        //Assignment
        Token assignOp = null;
        Node expr = null;

        //Call
        LinkedList<Node> callArgs = null;

        boolean isCall = false;
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
                expr = parseExpression();
                break;
            case OPEN_PAREN:
                isCall = true;
                callArgs = parseCallArgs();
                if(currentToken.type.equals(Tokens.CLOSE_PAREN)){
                    advanceToNextToken();
                }
                break;
            default:
                throw new IllegalArgumentException("Expected: '=' '+=' '-=' '*=' '<<=' '>>=' '|=' '&=' '%=' FINISH LATER EVAN" + locationToString(currentToken));
        }

        if(currentToken.type.equals(Tokens.SEMICOLON)){
            advanceToNextToken();
            if(isCall){
                return new CallNode(v.names,callArgs);
            }else {
                return new AssignmentNode(v, assignOp, expr);
            }
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
        Node expr;
        Node stmt;

        if(currentToken.type.equals(Tokens.WHILE)){
            advanceToNextToken();
        }else{
            throw new IllegalArgumentException("Expected: while at " + locationToString(currentToken));
        }

        if(currentToken.type.equals(Tokens.OPEN_PAREN)){
            advanceToNextToken();
        }else{
            throw new IllegalArgumentException("Expected: '(' at " + locationToString(currentToken));
        }

        expr = parseExpression();

        if(currentToken.type.equals(Tokens.CLOSE_PAREN)){
            advanceToNextToken();
        }else{
            throw new IllegalArgumentException("Expected: ')' at " + locationToString(currentToken));
        }

        stmt = parseStatement();
        return new WhileNode(expr,stmt);
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
            node.expression = parseExpression();
        }
        //semicolon
        if(currentToken.type.equals(Tokens.SEMICOLON)){
            advanceToNextToken();
        }else{
            throw new IllegalArgumentException("Expected ';' at " + locationToString(currentToken));
        }
        return node;
    }
    public LinkedList<Node> parseCallArgs()
    {
        LinkedList<Node> args;
        if(currentToken.type.equals(Tokens.OPEN_PAREN)){
            advanceToNextToken();
        }
        // Optional args
        if(currentToken.type.equals(Tokens.CLOSE_PAREN)){
            advanceToNextToken();
            args = new LinkedList<>(); //return empty list if no args
        }else{
            args = parseArguments();
        }
        if(currentToken.type.equals(Tokens.CLOSE_PAREN)){
            advanceToNextToken();
            return args;
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

    public Node parseArgument(){
       return parseExpression();
    }
    // Expressions
    public BinaryOp parseExpression(){
        Node left = parseDisjunction();
        BinaryOp node = parseExpressionPrime();
        node.left = left;
        return node;
    }

    public BinaryOp parseExpressionPrime(){
        BinaryOp node = new BinaryOp(null,null,null);
        if(currentToken.type.equals(Tokens.ASSIGN)){
            //TERMINAL consume input
            node.operator = advanceToNextToken();
            Node leftOfRight = parseDisjunction();
            BinaryOp right = parseExpressionPrime();
            right.left = leftOfRight;
            node.right = right;
        }
        return node;
        //Has epsilon; no error
    }

    public BinaryOp parseDisjunction(){
        BinaryOp left = parseConjunction();
        BinaryOp node = parseDisjunctionPrime();
        node.left = left;
        return node;
    }

    public BinaryOp parseDisjunctionPrime(){
        BinaryOp node = new BinaryOp(null,null,null);
        if(currentToken.type.equals(Tokens.OR) || currentToken.type.equals(Tokens.CONDITIONAL_OR) || currentToken.type.equals(Tokens.COMPLEMENT)) {
            node.operator = parseOrOp();
            BinaryOp leftOfRight = parseConjunction();
            BinaryOp right = parseDisjunctionPrime();
            right.left = leftOfRight;
            node.right = right;
        }
        return node;
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

    public BinaryOp parseConjunction(){
        BinaryOp left = parseRelation();
        BinaryOp node = parseConjunctionPrime();
        node.left = left;
        return node;
    }

    public BinaryOp parseConjunctionPrime(){
        BinaryOp node = new BinaryOp(null,null,null);
        if(currentToken.type.equals(Tokens.AND) || currentToken.type.equals(Tokens.CONDITIONAL_AND)){
            node.operator = parseAndOp();
            BinaryOp leftOfRight = parseRelation();
            BinaryOp right = parseConjunctionPrime();
            right.left = leftOfRight;
            node.right = right;
        }
        return node;
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

    public BinaryOp parseRelation(){
        BinaryOp left = parseSimpleExpression();
        BinaryOp node = parseRelationPrime();
        node.left = left;
        return node;
    }

    public BinaryOp parseRelationPrime(){
        BinaryOp node = new BinaryOp(null,null,null);
        switch (currentToken.type){
            case LESS:
            case LESS_EQUAL:
            case GREATER:
            case GREATER_EQUAL:
            case EQUAL:
            case NOT_EQUAL:
                node.operator = parseCompareOp();
                node.right = parseSimpleExpression();
                break;
        }
        return node;
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

    public SimpleExpressionNode parseSimpleExpression(){
        Token sign = null; //null assumes positive
        BinaryOp left;
        SimpleExpressionNode node;

        switch (currentToken.type){
            case PLUS:
            case MINUS:
                sign = parseSign();
                left = parseTerm();
                node = parseSimpleExpressionPrime();
                break;
            default:
                left = parseTerm();
                node = parseSimpleExpressionPrime();
        }
        node.left = left;
        node.sign = sign;
        return node;
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

    public SimpleExpressionNode parseSimpleExpressionPrime(){
        //Leave sign and left null
        SimpleExpressionNode node = new SimpleExpressionNode(null,null,null,null);
        switch (currentToken.type){
            case PLUS:
            case MINUS:
                node.operator = parseAddOp();
                BinaryOp leftOfRight = parseTerm();
                SimpleExpressionNode right = parseSimpleExpressionPrime();
                right.left = leftOfRight;
                node.right = right;
                break;
        }
        return node;
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

    public BinaryOp parseTerm(){
        parseFactor();
        parseTermPrime();
        return null;
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
