package ast;

import parser.nodes.*;
import token.Identifier;
import token.Token;
import token.TokenType;

import java.util.LinkedList;

public class AbstractSyntaxTree {
    private final SymbolTable root;

    private AbstractSyntaxTree(Identifier name){
        this.root = new SymbolTable(name);
    }


    public static AbstractSyntaxTree build(ProgramNode parseTreeRoot){
        AbstractSyntaxTree ast = new AbstractSyntaxTree(parseTreeRoot.name);
        // Add "output" to the list of allowed methods
        ParamNode c = new ParamNode(new TypeNode(new Token(TokenType.CHAR,-1,-1),false),new Identifier(TokenType.IDENTIFIER,-1,-1,"c"));
        LinkedList<ParamNode> pms = new LinkedList<>();
        pms.add(c);
        ast.root.add(new MethodDecl(new TypeNode(new Token(TokenType.VOID,-1,-1),false),new Identifier(TokenType.IDENTIFIER,-1,-1,"output"),pms,new BlockNode(new LinkedList<>())));
        // Add "input" to the list of allowed methods
        ast.root.add(new MethodDecl(new TypeNode(new Token(TokenType.CHAR,-1,-1),false),new Identifier(TokenType.IDENTIFIER,-1,-1,"input"),pms,new BlockNode(new LinkedList<>())));


        visit(parseTreeRoot,ast.root);
        return ast;
    }

    private static void visit(Node node, SymbolTable scope){
        if(node == null) return;
        switch (node.kind){
            case PROGRAM -> visit(((ProgramNode)node).body,scope);
            case DECL,PARAMETER -> scope.add(node);
            case METHOD_DECL -> {
                scope.add(node);
                visit(((MethodDecl)node).body, scope.getMethod(((MethodDecl) node).name).getSymbolTable());
            }

            case VARIABLE -> {
                if(!scope.hasVariable((VariableNode)node)){
                    throw new IllegalArgumentException("Variable '" + ((VariableNode) node).name + "' is not defined!");
                }
            }
            case CALL -> {
                if(!scope.hasMethod((CallNode)node)){
                    throw new IllegalArgumentException("Method '" + ((CallNode) node).name + "' is not defined");
                }
                for(Node arg : ((CallNode)node).args){
                    visit(arg,scope);
                }
            }
            case BINARY_OP -> {
                visit(((BinaryOp)node).left,scope);
                visit(((BinaryOp)node).right,scope);
            }
            case ASSIGNMENT -> {
                visit(((AssignmentNode)node).var,scope);
                visit(((AssignmentNode)node).expression,scope);
            }
            case WHILE -> {
                // Create a scope (parent doesn't need reference to it, since the parent can't access any enclosed variables
                SymbolTable anonymousScope = new SymbolTable("WhileNode",scope);
                visit(((WhileNode)node).then,anonymousScope);
            }
            case IF -> {
                SymbolTable anonymousScopeThen = new SymbolTable("IfNodeThen",scope);
                SymbolTable anonymousScopeElse = new SymbolTable("IfNodeElse",scope);
                visit(((IfNode)node).then,anonymousScopeThen);
                visit(((IfNode)node).otherwise,anonymousScopeElse);
            }
            case POSTFIX_UNARY_OP -> visit(((PostfixUnaryOp)node).child,scope);
            case PREFIX_UNARY_OP -> visit(((PrefixUnaryOp)node).child,scope);
            case SIMPLE_EXPRESSION -> {
                visit(((SimpleExpressionNode)node).left,scope);
                visit(((SimpleExpressionNode)node).right,scope);
            }
            case RETURN -> visit(((ReturnNode)node).expression,scope);
            case BLOCK -> {
                for(Node n : ((BlockNode)node).stmts){
                    visit(n,scope);
                }
            }

        }
    }

    public void printContents(){
        this.root.printContents();
    }

}
