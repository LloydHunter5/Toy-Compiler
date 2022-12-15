import java.util.ArrayList;
import java.util.LinkedList;
/* TODO:
    - for expressions, parse tree might be generating the wrong derivation
*/



public class ToyParser {
    private final ToyLexer lexer;
    public final ArrayList<Token> consumedInputArchive;
    private Token currentToken;
    private EM errorMessageType;
    public ToyParser(ToyLexer lexer){
        this(lexer,EM.STANDARD);
    }

    public ToyParser(ToyLexer lexer, EM errorMessageType){
        this.lexer = lexer;
        this.currentToken = lexer.getNextToken();
        this.consumedInputArchive = new ArrayList<>();
        this.errorMessageType = errorMessageType;
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
        public PrefixUnaryOp(Token prefix, Node operand){
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
        public Token value;

        public LiteralNode(Token value){
            super(Kind.LITERAL);
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

        public boolean isArrayType(){
            return index != null;
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
        public Node indexExpr;

        public IndexNode(Node indexExpr){
            super(Kind.INDEX);
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
        match(Tokens.PROGRAM);
        Token programName = match(Tokens.IDENTIFIER);
        BlockNode block = parseBlock();
        return new ProgramNode(programName,block);
    }

    public BlockNode parseBlock()
    {
        match(Tokens.OPEN_CURL_BRACKET);
        LinkedList<Node> stmts = parseStatements();
        match(Tokens.CLOSE_CURL_BRACKET);
        return new BlockNode(stmts);
    }

    public LinkedList<Node> parseStatements()
    {
        //Statements are technically optional (thanks null program!)
        if(currentTokenIsType(Tokens.CLOSE_CURL_BRACKET)){ return new LinkedList<>(); }
        Node firstStmt = parseStatement();
        LinkedList<Node> stmts = parseStatementsPrime();
        stmts.addFirst(firstStmt);
        return stmts;
    }

    public LinkedList<Node> parseStatementsPrime()
    {
        LinkedList<Node> stmts = new LinkedList<>();
        while(!currentTokenIsType(Tokens.CLOSE_CURL_BRACKET)) {
            stmts.add(parseStatement());
        }
        return stmts;
    }

    public Node parseStatement()
    {
        return switch (currentToken.type) { //variable type
            case INT, CHAR, BOOLEAN, VOID -> parseDeclaration();
            case IDENTIFIER -> parseAssignmentOrCall();  //since both start with identifiers, just parse both in the same function
            case IF -> parseIf();
            case WHILE -> parseWhile();
            case RETURN -> parseReturn();
            case OPEN_CURL_BRACKET -> parseBlock();
            default -> throw expected("statement");
        };
    }

    public DeclNode parseDeclaration()
    {
        TypeNode type;
        Token name;

        type = new TypeNode(match(Tokens.METHOD_TYPES));
        if(currentTokenIsType(Tokens.OPEN_BRACKET)){
            advanceToNextToken();
            type.arrSize = parseExpression();
            match(Tokens.CLOSE_BRACKET);
        }
        
        name = match(Tokens.IDENTIFIER);

        //figure out if it is a method decl or a variable decl
        
        if(currentTokenIsType(Tokens.ASSIGN)){
            // Variable Decl
            advanceToNextToken();
            Node expr = parseExpression();
            if(currentToken.type.equals(Tokens.SEMICOLON)){
                advanceToNextToken();
                return new VarDeclNode(type,name,expr);
            }
        }else if(currentTokenIsType(Tokens.SEMICOLON)) { //ex. "int a;"
            // Variable Decl
            advanceToNextToken();
            return new VarDeclNode(type,name);
        }else{
            //Method decl
            match(Tokens.OPEN_PAREN);
            LinkedList<ParamNode> params = parseParameters();
            match(Tokens.CLOSE_PAREN);
            BlockNode body = parseBlock();
            return new MethodDecl(type, name, params, body);
        }
        //should only be reached if there is an error
        throw expected("(, =, or ;");
    }

    public LinkedList<ParamNode> parseParameters(){
        if(currentTokenIsType(Tokens.CLOSE_PAREN)) { return new LinkedList<>();}
        ParamNode firstParam = parseParameter();
        LinkedList<ParamNode> params = parseParametersPrime();
        params.addFirst(firstParam);
        return params;
    }

    public LinkedList<ParamNode> parseParametersPrime(){
        LinkedList<ParamNode> params = new LinkedList<>();
        while(currentTokenIsType(Tokens.COMMA)){
            advanceToNextToken(); //consume comma
            params.add(parseParameter()); //parse type and name
        }
        return params;
    }

    public ParamNode parseParameter(){
        boolean isArrayParam = false;
        Token typeName = match(Tokens.VARIABLE_TYPES);
        //Optional arrayType
        if(currentTokenIsType(Tokens.OPEN_BRACKET)){
            advanceToNextToken();
            isArrayParam = true;
            match(Tokens.CLOSE_BRACKET);
        }
        Token name = match(Tokens.IDENTIFIER);
        return new ParamNode(new TypeNode(typeName,isArrayParam),name);
    }
    public Node parseAssignmentOrCall()
    {
        VariableNode v = parseVariable();
        //Assignment
        Token assignOp = null;
        Node expr = null;

        //Call
        LinkedList<Node> callArgs = null;

        boolean isCall = false;

        if(currentTokenIsType(Tokens.ASSIGNMENT_OPS)){
            assignOp = advanceToNextToken();
            expr = parseExpression();
        }else if(currentTokenIsType(Tokens.OPEN_PAREN)){
            isCall = true;
            callArgs = parseCallArgs();
        }else{
            Token postfix = parsePrefixOp();
            match(Tokens.SEMICOLON);
            return new PostfixUnaryOp(v, postfix);
        }

        match(Tokens.SEMICOLON);
        if(isCall){
            return new CallNode(v.names,callArgs);
        }else {
            return new AssignmentNode(v, assignOp, expr);
        }
    }
    public IfNode parseIf()
    {
        IfNode node = new IfNode(null,null);
        advanceToNextToken(); //if identifier
        match(Tokens.OPEN_PAREN);
        node.condition = parseExpression(); //should be a boolean expression
        match(Tokens.CLOSE_PAREN);
        node.then = parseStatement();
        //optional else statement
        if(currentTokenIsType(Tokens.ELSE)){
            advanceToNextToken(); //consume else identifier
            node.otherwise = parseStatement();
        }
        return node;
    }


    public WhileNode parseWhile()
    {
        Node expr;
        BlockNode body;

        match(Tokens.WHILE);
        match(Tokens.OPEN_PAREN);
        expr = parseExpression();
        match(Tokens.CLOSE_PAREN);
        body = parseBlock();
        return new WhileNode(expr,body);
    }
    public ReturnNode parseReturn()
    {
        ReturnNode node = new ReturnNode();
        match(Tokens.RETURN);
        if(!currentTokenIsType(Tokens.SEMICOLON)){
            node.expression = parseExpression();
        }
        match(Tokens.SEMICOLON);
        return node;
    }
    public LinkedList<Node> parseCallArgs()
    {

        if(currentToken.type.equals(Tokens.OPEN_PAREN)){ //TODO CHECK THIS
            advanceToNextToken();
        }

        LinkedList<Node> args = parseArguments();
        match(Tokens.CLOSE_PAREN);
        return args;
    }

    public LinkedList<Node> parseArguments(){
        Node firstArg = parseArgument();
        LinkedList<Node> args = parseArgumentsPrime();
        args.addFirst(firstArg);
        return args;
    }

    public LinkedList<Node> parseArgumentsPrime(){
        LinkedList<Node> args = new LinkedList<>();
        while(currentTokenIsType(Tokens.COMMA)){
            advanceToNextToken();
            Node arg = parseArgument();
            args.addLast(arg);
        }
        return args;
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
        if(currentToken.type.equals(Tokens.ASSIGN)){ //TODO CHECK THIS
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
        if(currentTokenIsType(Tokens.OR_OPS)) {
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
        return match(Tokens.OR_OPS);
    }

    public BinaryOp parseConjunction(){
        BinaryOp left = parseRelation();
        BinaryOp node = parseConjunctionPrime();
        node.left = left;
        return node;
    }

    public BinaryOp parseConjunctionPrime(){
        BinaryOp node = new BinaryOp(null,null,null);
        if(currentTokenIsType(Tokens.AND_OPS)){
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
        return match(Tokens.AND_OPS);
    }

    public BinaryOp parseRelation(){
        BinaryOp left = parseSimpleExpression();
        BinaryOp node = parseRelationPrime();
        node.left = left;
        return node;
    }

    public BinaryOp parseRelationPrime(){
        BinaryOp node = new BinaryOp(null,null,null);
        if(currentTokenIsType(Tokens.COMPARE_OPS)){
            node.operator = parseCompareOp();
            node.right = parseSimpleExpression();
        }
        return node;
        //has epsilon
    }

    public Token parseCompareOp(){
        return match(Tokens.COMPARE_OPS);
    }

    public SimpleExpressionNode parseSimpleExpression(){
        Token sign = null; //null assumes positive
        BinaryOp left;
        SimpleExpressionNode node;

        if(currentTokenIsType(Tokens.ADD_OPS)){
            sign = parseSign();
        }
        left = parseTerm();
        node = parseSimpleExpressionPrime();
        node.left = left;
        node.sign = sign;
        return node;
    }

    public Token parseSign(){
        return match(Tokens.ADD_OPS);
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
        return match(Tokens.ADD_OPS);
    }

    //TODO
    public BinaryOp parseTerm(){
        Node right = parseFactor();
        BinaryOp node = parseTermPrime();
        node.right = right;
        return node;
    }

    //TODO
    public BinaryOp parseTermPrime(){
        BinaryOp node = new BinaryOp(null,null,null);
        if(currentTokenIsType(Tokens.MUL_OPS)){
            node.operator = parseMulOp();
            Node leftOfRight = parseFactor();
            BinaryOp right = parseTermPrime();
            right.left = leftOfRight;
            node.right = right;
        }
        //has epsilon
        return node;
    }

    public Token parseMulOp(){
        return match(Tokens.MUL_OPS);
    }
    //TODO
    public Node parseFactor(){
        if(currentTokenIsType(Tokens.IDENTIFIER)){
            return parsePrimary();
        }else if(currentTokenIsType(Tokens.LITERALS)){
            return new LiteralNode(parseLiteral());
        }else if(currentTokenIsType(Tokens.NOT)){
            return new PrefixUnaryOp(parseUnaryOp(),parseFactor());
        }else if(currentTokenIsType(Tokens.INCREMENT_OPS)){
            return new PrefixUnaryOp(parsePrefixOp(),parseVariable());
        }else{
            match(Tokens.OPEN_PAREN);
            Node expr = parseExpression();
            match(Tokens.CLOSE_PAREN);
            return expr;
        }
    }

    public Node parsePrimary(){
        LinkedList<Token> name = parseName();
        LinkedList<Node> args = new LinkedList<>();
        IndexNode expr;
        if(currentTokenIsType(Tokens.INCREMENT_OPS)){
            return new PostfixUnaryOp(new VariableNode(name),parsePrefixOp());
        }
        if (currentTokenIsType(Tokens.OPEN_PAREN)) {
            advanceToNextToken();
            //optional args
            if(!currentTokenIsType(Tokens.CLOSE_PAREN)) {
                args = parseArguments();
            }
            match(Tokens.CLOSE_PAREN);
            return new CallNode(name, args);
        } else if (currentTokenIsType(Tokens.OPEN_BRACKET)) {
            advanceToNextToken();
            expr = new IndexNode(parseExpression());
            match(Tokens.CLOSE_BRACKET);
            return new VariableNode(name, expr);
        } else {
            return new VariableNode(name);
        }
    }

    public VariableNode parseVariable(){
        LinkedList<Token> name = parseName();
        VariableNode variable = new VariableNode(name);

        if(currentTokenIsType(Tokens.OPEN_BRACKET)){
            advanceToNextToken();
            variable.index = new IndexNode(parseExpression());
            match(Tokens.CLOSE_BRACKET);
        }
        return variable;
    }

    public Token parsePrefixOp(){
        return match(Tokens.INCREMENT_OPS);
    }
    public Token parseUnaryOp(){
        return match(Tokens.NOT);
    }

    public Token parseLiteral(){
        return match(Tokens.LITERALS);
    }

    public LinkedList<Token> parseName(){
        Token temp = match(Tokens.IDENTIFIER);
        LinkedList<Token> tokens = parseNamePrime();
        tokens.addFirst(temp);
        return tokens;
    }

    public LinkedList<Token> parseNamePrime(){
        LinkedList<Token> names = new LinkedList<>();
        while(currentTokenIsType(Tokens.DOT)){
            advanceToNextToken();
            names.addLast(match(Tokens.IDENTIFIER));
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

    public boolean currentTokenIsType(Tokens type){
        return currentToken.type.equals(type);
    }

    public boolean currentTokenIsType(Tokens[] types){
        for(Tokens type : types){
            if(currentTokenIsType(type)){
                return true;
            }
        }
        return false;
    }
    public Token match(Tokens type){
        if(currentToken.type.equals(type)){
            return advanceToNextToken();
        }else{
            throw expected(type.name());
        }
    }

    public Token match(Tokens[] types){
        for(Tokens type : types){
            if(currentToken.type.equals(type)){
                return advanceToNextToken();
            }
        }
        StringBuilder error = new StringBuilder();
        for(Tokens type : types){
            error.append(type.name());
            error.append(", ");
        }
        throw expected(error.toString());
    }

    public String locationToString(Token t){
        return "[" + currentToken.line + ", " + currentToken.col + "]";
    }

    public String verboseLocationToString(Token t){
        return "[line: " + currentToken.line + ", column: " + currentToken.col + "]";
    }

    public boolean hasNextToken(){
        return lexer.hasNextToken();
    }
    public void setErrorMessageType(EM errorMessageType){
        this.errorMessageType = errorMessageType;
    }
    public enum EM{
        VERBOSE,
        STANDARD,
        MINIMAL
    }

    public IllegalArgumentException expected(String tokens){
        String message = switch (this.errorMessageType){
            case VERBOSE -> "Expected: " + tokens + " at " + verboseLocationToString(currentToken) + ", but got " + currentToken.type.name();
            case STANDARD -> "Expected: " + tokens + " at " + locationToString(currentToken);
            case MINIMAL -> locationToString(currentToken);
        };
        return new IllegalArgumentException(message);
    }
}
