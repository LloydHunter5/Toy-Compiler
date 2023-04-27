package parser;

import token.Identifier;
import token.Token;
import token.TokenType;
import lexer.ToyLexer;

import java.util.ArrayList;
import java.util.LinkedList;
import parser.nodes.*;
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
    /*
    *
    *
    *
    * Actual methods that generate the parse tree
    * this is a recursive descent parser,
    * and it is LL(1) meaning it can read linearly,
    * one token at a time from the lexer
    *
    *
    *
    */

    //Group of methods that implement the grammar
    public ProgramNode parseProgram()
    {
        match(TokenType.PROGRAM);
        Token programName = match(TokenType.IDENTIFIER);
        BlockNode block = parseBlock();
        return new ProgramNode(programName,block);
    }

    public BlockNode parseBlock()
    {
        Token t = match(TokenType.OPEN_CURL_BRACKET);
        LinkedList<Node> stmts = parseStatements();
        match(TokenType.CLOSE_CURL_BRACKET);
        BlockNode block = new BlockNode(stmts);
        block.setPosition(t);
        return block;
    }

    public LinkedList<Node> parseStatements()
    {
        //Statements are technically optional (thanks null program!)
        if(currentTokenIsType(TokenType.CLOSE_CURL_BRACKET)){ return new LinkedList<>(); }
        Node firstStmt = parseStatement();
        LinkedList<Node> stmts = parseStatementsPrime();
        stmts.addFirst(firstStmt);
        return stmts;
    }

    public LinkedList<Node> parseStatementsPrime()
    {
        LinkedList<Node> stmts = new LinkedList<>();
        while(!currentTokenIsType(TokenType.CLOSE_CURL_BRACKET)) {
            stmts.add(parseStatement());
        }
        return stmts;
    }

    public Node parseStatement()
    {
        return switch (currentToken.type) { //variable type
            case INT, CHAR, BOOLEAN, VOID -> parseDeclaration();
            //since both start with identifiers, just parse both in the same function
            case IDENTIFIER -> parseAssignmentOrCall();
            case INCREMENT,DECREMENT -> parsePrefixUnaryOp();
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
        Identifier name;

        type = new TypeNode(match(TokenType.METHOD_TYPES));
        if(currentTokenIsType(TokenType.OPEN_BRACKET)){
            advanceToNextToken();
            type.isArrayType = true;
            // Can define array size, or it can be implied/assigned later
            if(!currentTokenIsType(TokenType.CLOSE_BRACKET)){
                type.arrSize = parseExpression();
            }
            match(TokenType.CLOSE_BRACKET);
        }
        
        name = (Identifier) match(TokenType.IDENTIFIER);

        //figure out if it is a method decl or a variable decl
        if(currentTokenIsType(TokenType.ASSIGN)){
            // Variable Decl
            advanceToNextToken();
            Node expr = parseExpression();
            if(currentToken.type.equals(TokenType.SEMICOLON)){
                advanceToNextToken();
                return new VarDeclNode(type,name,expr);
            }
        }else if(currentTokenIsType(TokenType.SEMICOLON)) { //ex. "int a;"
            // Variable Decl
            advanceToNextToken();
            return new VarDeclNode(type,name);
        }else{
            //Method decl
            match(TokenType.OPEN_PAREN);
            LinkedList<ParamNode> params = parseParameters();
            match(TokenType.CLOSE_PAREN);
            BlockNode body = parseBlock();
            return new MethodDecl(type, name, params, body);
        }
        //should only be reached if there is an error
        throw expected("(, =, or ;");
    }

    public LinkedList<ParamNode> parseParameters(){
        if(currentTokenIsType(TokenType.CLOSE_PAREN)) { return new LinkedList<>();}
        ParamNode firstParam = parseParameter();
        LinkedList<ParamNode> params = parseParametersPrime();
        params.addFirst(firstParam);
        return params;
    }

    public LinkedList<ParamNode> parseParametersPrime(){
        LinkedList<ParamNode> params = new LinkedList<>();
        while(currentTokenIsType(TokenType.COMMA)){
            advanceToNextToken(); //consume comma
            params.add(parseParameter()); //parse type and name
        }
        return params;
    }

    public ParamNode parseParameter(){
        boolean isArrayParam = false;
        Token typeName = match(TokenType.VARIABLE_TYPES);
        //Optional arrayType
        if(currentTokenIsType(TokenType.OPEN_BRACKET)){
            advanceToNextToken();
            isArrayParam = true;
            match(TokenType.CLOSE_BRACKET);
        }
        Identifier name = (Identifier) match(TokenType.IDENTIFIER);
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

        if(currentTokenIsType(TokenType.ASSIGNMENT_OPS)){
            assignOp = advanceToNextToken();
            expr = parseExpression();
        }else if(currentTokenIsType(TokenType.OPEN_PAREN)){
            isCall = true;
            //skip over parenthesis
            advanceToNextToken();
            callArgs = parseCallArgs();
        }else{
            Token postfix = parsePrefixOp();
            match(TokenType.SEMICOLON);
            return new PostfixUnaryOp(v, postfix);
        }

        match(TokenType.SEMICOLON);
        if(isCall){
            return new CallNode(v.scope,v.name,callArgs);
        }else {
            return new AssignmentNode(v, assignOp, expr);
        }
    }
    public IfNode parseIf()
    {
        IfNode node = new IfNode(null,null);
        Token t = advanceToNextToken(); //if identifier
        match(TokenType.OPEN_PAREN);
        node.condition = parseExpression(); //should be a boolean expression
        match(TokenType.CLOSE_PAREN);
        node.then = parseStatement();
        //optional else statement
        if(currentTokenIsType(TokenType.ELSE)){
            advanceToNextToken(); //consume else identifier
            node.otherwise = parseStatement();
        }
        node.setPosition(t);
        return node;
    }


    public WhileNode parseWhile()
    {
        Node expr;
        BlockNode body;

        Token t = match(TokenType.WHILE);
        match(TokenType.OPEN_PAREN);
        expr = parseExpression();
        match(TokenType.CLOSE_PAREN);
        body = parseBlock();
        WhileNode whileNode = new WhileNode(expr,body);
        whileNode.setPosition(t);
        return whileNode;
    }
    public ReturnNode parseReturn()
    {
        ReturnNode node = new ReturnNode();
        Token t = match(TokenType.RETURN);
        if(!currentTokenIsType(TokenType.SEMICOLON)){
            node.expression = parseExpression();
        }
        match(TokenType.SEMICOLON);
        node.setPosition(t);
        return node;
    }
    public LinkedList<Node> parseCallArgs()
    {
        LinkedList<Node> args = parseArguments();
        match(TokenType.CLOSE_PAREN);
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
        while(currentTokenIsType(TokenType.COMMA)){
            advanceToNextToken();
            args.addLast(parseArgument());
        }
        return args;
    }

    public Node parseArgument(){
       return parseExpression();
    }
    // Expressions
    public Node parseExpression(){
        // TODO: Sometimes this generates assignments, however it returns them as BinaryOps. THIS IS BAD!!!

        BinaryOp disjunction = parseDisjunction();
        VariableNode var = null;
        // Converts any assignment statement to an assignment node
        if(disjunction.kind == Kind.SIMPLE_EXPRESSION){
            SimpleExpressionNode t1 = ((SimpleExpressionNode) disjunction);
            // For example (return -c = '1') shouldn't be valid
            if(t1.sign == null){
                // Term
                if(t1.left.kind == Kind.BINARY_OP){
                    BinaryOp t2 = ((BinaryOp) t1.left);
                    // Factor
                    if(t2.left.kind == Kind.VARIABLE){
                        var = ((VariableNode) t2.left);
                    }
                }
            }
        }
        if(currentTokenIsType(TokenType.ASSIGNMENT_OPS)){
            Token assignOp = advanceToNextToken();
            if(var == null) throw expected("Variable");
            return new AssignmentNode(var,assignOp,parseExpression());
        }
        // Otherwise, return the disjunction
        return disjunction;
    }

    public BinaryOp parseExpressionPrime(){
        BinaryOp node = new BinaryOp(null,null,null);
        if(currentTokenIsType(TokenType.ASSIGNMENT_OPS)){ //TODO CHECK THIS
            //TERMINAL consume input
            node.operator = advanceToNextToken();
            BinaryOp leftOfRight = parseDisjunction();
            BinaryOp right = parseExpressionPrime();
            right.left = leftOfRight;
            node.right = right;
            node.setPosition(node.operator);
        }
        return node;
        //Has epsilon; no error
    }

    public BinaryOp parseDisjunction(){
        BinaryOp left = parseConjunction();
        BinaryOp node = parseDisjunctionPrime();
        node.left = left;
        if(node.right == null){
            return left;
        }
        return node;
    }

    public BinaryOp parseDisjunctionPrime(){
        BinaryOp node = new BinaryOp(null,null,null);
        if(currentTokenIsType(TokenType.OR_OPS)) {
            node.operator = parseOrOp();
            BinaryOp leftOfRight = parseConjunction();
            BinaryOp right = parseDisjunctionPrime();
            right.left = leftOfRight;
            node.right = right;
            node.setPosition(node.operator);
        }
        return node;
        //has epsilon
    }

    public Token parseOrOp(){
        return match(TokenType.OR_OPS);
    }

    public BinaryOp parseConjunction(){
        BinaryOp left = parseRelation();
        BinaryOp node = parseConjunctionPrime();
        node.left = left;
        if(node.right == null){
            return left;
        }
        return node;
    }

    public BinaryOp parseConjunctionPrime(){
        BinaryOp node = new BinaryOp(null,null,null);
        if(currentTokenIsType(TokenType.AND_OPS)){
            node.operator = parseAndOp();
            BinaryOp leftOfRight = parseRelation();
            BinaryOp right = parseConjunctionPrime();
            right.left = leftOfRight;
            node.right = right;
            node.setPosition(node.operator);
        }
        return node;
        //has epsilon
    }

    public Token parseAndOp(){
        return match(TokenType.AND_OPS);
    }

    public BinaryOp parseRelation(){
        BinaryOp left = parseSimpleExpression();
        BinaryOp node = parseRelationPrime();
        node.left = left;
        if(node.right == null){
            return left;
        }
        return node;
    }

    public BinaryOp parseRelationPrime(){
        BinaryOp node = new BinaryOp(null,null,null);
        if(currentTokenIsType(TokenType.COMPARE_OPS)){
            node.operator = parseCompareOp();
            node.right = parseSimpleExpression();
            node.setPosition(node.operator);
        }
        return node;
        //has epsilon
    }

    public Token parseCompareOp(){
        return match(TokenType.COMPARE_OPS);
    }

    public SimpleExpressionNode parseSimpleExpression(){
        Token sign = null; //null assumes positive
        BinaryOp left;
        SimpleExpressionNode node;

        if(currentTokenIsType(TokenType.ADD_OPS)){
            sign = parseSign();
        }
        left = parseTerm();
        node = parseSimpleExpressionPrime();
        node.left = left;
        node.sign = sign;
        return node;
    }

    public Token parseSign(){
        return match(TokenType.ADD_OPS);
    }

    public SimpleExpressionNode parseSimpleExpressionPrime(){
        //Leave sign and left null
        SimpleExpressionNode node = new SimpleExpressionNode(null,null,null,null);
        switch (currentToken.type) {
            case PLUS, MINUS -> {
                node.operator = parseAddOp();
                BinaryOp leftOfRight = parseTerm();
                SimpleExpressionNode right = parseSimpleExpressionPrime();
                right.left = leftOfRight;
                node.right = right;
                node.setPosition(node.operator);
            }
        }
        return node;
    }

    public Token parseAddOp(){
        return match(TokenType.ADD_OPS);
    }

    public BinaryOp parseTerm(){
        Node left = parseFactor();
        BinaryOp node = parseTermPrime();
        node.left = left;
        return node;
    }
    public BinaryOp parseTermPrime(){
        BinaryOp node = new BinaryOp(null,null,null);
        if(currentTokenIsType(TokenType.MUL_OPS)){
            node.operator = parseMulOp();
            Node leftOfRight = parseFactor();
            BinaryOp right = parseTermPrime();
            right.left = leftOfRight;
            node.right = right;
            node.setPosition(node.operator);
        }
        //has epsilon
        return node;
    }

    public Token parseMulOp(){
        return match(TokenType.MUL_OPS);
    }
    public Node parseFactor(){
        if(currentTokenIsType(TokenType.IDENTIFIER)){
            return parsePrimary();
        }else if(currentTokenIsType(TokenType.LITERALS)){
            return new LiteralNode(parseLiteral());
        }else if(currentTokenIsType(TokenType.NOT)){
            return new PrefixUnaryOp(parseUnaryOp(),parseFactor());
        }else if(currentTokenIsType(TokenType.INCREMENT_OPS)){
            return new PrefixUnaryOp(parsePrefixOp(),parseVariable());
        }else{
            match(TokenType.OPEN_PAREN);
            Node expr = parseExpression();
            match(TokenType.CLOSE_PAREN);
            return expr;
        }
    }

    public Node parsePrimary(){
        LinkedList<Identifier> scope = parseName();
        Identifier name = scope.removeLast();
        // Is a method call
        if (currentTokenIsType(TokenType.OPEN_PAREN)) {
            advanceToNextToken();
            //optional args
            LinkedList<Node> args;
            if(!currentTokenIsType(TokenType.CLOSE_PAREN)) {
                args = parseArguments();
            }else{
                args = new LinkedList<>();
            }
            match(TokenType.CLOSE_PAREN);
            return new CallNode(scope,name,args);
        }

        // Is a variable reference of some kind
        VariableNode variable = new VariableNode(scope,name);
        if(currentTokenIsType(TokenType.INCREMENT_OPS)){
            return new PostfixUnaryOp(variable,parsePrefixOp());
        }
        // optional array index
        if (currentTokenIsType(TokenType.OPEN_BRACKET)) {
            Token t = advanceToNextToken();
            variable.index = new IndexNode(parseExpression());
            variable.index.setPosition(t);
            match(TokenType.CLOSE_BRACKET);
        }
        return variable;

    }

    public VariableNode parseVariable(){
        LinkedList<Identifier> scope = parseName();
        Identifier name = scope.removeLast();

        VariableNode variable = new VariableNode(scope,name);
        if(currentTokenIsType(TokenType.OPEN_BRACKET)){
            advanceToNextToken();
            variable.index = new IndexNode(parseExpression());
            match(TokenType.CLOSE_BRACKET);
        }
        return variable;
    }

    public Node parsePrefixUnaryOp(){
        Token op = parsePrefixOp();
        VariableNode v = parseVariable();
        match(TokenType.SEMICOLON);
        return new PrefixUnaryOp(op,v);
    }

    public Token parsePrefixOp(){
        return match(TokenType.INCREMENT_OPS);
    }
    public Token parseUnaryOp(){
        return match(TokenType.NOT);
    }

    public Token parseLiteral(){
        return match(TokenType.LITERALS);
    }

    public LinkedList<Identifier> parseName(){
        Identifier temp = (Identifier) match(TokenType.IDENTIFIER);
        LinkedList<Identifier> tokens = parseNamePrime();
        tokens.addFirst(temp);
        return tokens;
    }

    public LinkedList<Identifier> parseNamePrime(){
        LinkedList<Identifier> names = new LinkedList<>();
        while(currentTokenIsType(TokenType.DOT)){
            advanceToNextToken();
            names.addLast((Identifier) match(TokenType.IDENTIFIER));
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

    public boolean currentTokenIsType(TokenType type){
        return currentToken.type.equals(type);
    }

    public boolean currentTokenIsType(TokenType[] types){
        for(TokenType type : types){
            if(currentTokenIsType(type)){
                return true;
            }
        }
        return false;
    }
    public Token match(TokenType type){
        if(currentToken.type.equals(type)){
            return advanceToNextToken();
        }else{
            throw expected(type.name());
        }
    }

    public Token match(TokenType[] types){
        for(TokenType type : types){
            if(currentToken.type.equals(type)){
                return advanceToNextToken();
            }
        }
        StringBuilder error = new StringBuilder();
        for(TokenType type : types){
            error.append(type.name());
            error.append(", ");
        }
        throw expected(error.toString());
    }

    public String locationToString(Token t){
        return "[" + t.line + ", " + t.col + "]";
    }

    public String verboseLocationToString(Token t){
        return "[line: " + t.line + ", column: " + t.col + "]";
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
