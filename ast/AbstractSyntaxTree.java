package ast;

import ast.types.*;
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
    public static class TypeMismatchException extends IllegalArgumentException {
        public TypeMismatchException(String m){
            super(m);
        }
    }

    public static AbstractSyntaxTree build(ProgramNode parseTreeRoot){
        AbstractSyntaxTree ast = new AbstractSyntaxTree(parseTreeRoot.name);
        // Add "output" to the list of allowed methods
        ParamNode c = new ParamNode(new TypeNode(new Token(TokenType.CHAR,-1,-1),false),new Identifier(TokenType.IDENTIFIER,-1,-1,"c"));
        LinkedList<ParamNode> pms = new LinkedList<>();
        pms.add(c);
        ast.root.add(new MethodDecl(new TypeNode(new Token(TokenType.VOID,-1,-1),false),new Identifier(TokenType.IDENTIFIER,-1,-1,"output"),pms,new BlockNode(new LinkedList<>())));
        // Add "input" to the list of allowed methods
        pms = new LinkedList<>();
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
                MethodDecl n = (MethodDecl) node;
                visit(n.body, scope.getMethodInScope(n.name).getSymbolTable());
                if(scope.getMethodInScope(n.name).getReturnType() != MethodTypes.VOID){
                    // TODO: Check for return statements (waiting to create flow graph?)
                }
            }
            case VARIABLE -> {
                VariableNode n = (VariableNode) node;
                boolean variableExists = scope.annotateSymbolTableEntry(n);
                if(!variableExists){
                    throw new IllegalArgumentException("Variable '" + n.name + "' is not defined!");
                }
                n.typeAnnotation = TypeAnnotation.convertType(((Variable) n.symbolTableReference).getType(), n.isArrayType());
            }
            case CALL -> {
                CallNode n = (CallNode)node;
                boolean methodExists = scope.hasMethod(n);
                if(!methodExists){
                    throw new IllegalArgumentException("Method '" + n.name + "' is not defined");
                }
                Method method = scope.getMethod(n);
                int i = 0;
                for(Node arg : ((CallNode)node).args){
                    visit(arg,scope);
                    checkParameterType(arg, method.parameterTypes[i++]);
                }
                n.typeAnnotation = TypeAnnotation.convertType(method.getReturnType());
            }
            case BINARY_OP,SIMPLE_EXPRESSION -> {
                // ANNOTATE TYPES
                BinaryOp bop = (BinaryOp) node;
                visit(bop.left,scope);
                visit(bop.right,scope);
                if(bop.left == null){
                    bop.typeAnnotation = bop.right.typeAnnotation;
                }else if(bop.right == null){
                    bop.typeAnnotation = bop.left.typeAnnotation;
                }else {
                    bop.typeAnnotation = compareTypes(bop.left, bop.operator.type, bop.right);
                }
            }
            case ASSIGNMENT -> {
                // ANNOTATE TYPES
                AssignmentNode asn = (AssignmentNode) node;
                visit(asn.var,scope);
                visit(asn.expression,scope);
                asn.typeAnnotation = compareTypes(asn.var, TokenType.ASSIGN,asn.expression);
            }
            case WHILE -> {
                // Create a scope (parent doesn't need reference to it, since the parent can't access any enclosed variables
                SymbolTable anonymousScope = new SymbolTable("WhileNode",scope.associatedMethod,scope);
                WhileNode n = (WhileNode) node;
                visit(n.conditions, scope);
                visit(n.then,anonymousScope);
                if(!checkBooleanExpression(n.conditions)){
                    throw new IllegalArgumentException("Boolean expression for while loop is the wrong type: " + n.conditions.typeAnnotation);
                }
            }
            case IF -> {
                SymbolTable anonymousScopeThen = new SymbolTable("IfNodeThen",scope.associatedMethod,scope);
                SymbolTable anonymousScopeElse = new SymbolTable("IfNodeElse",scope.associatedMethod,scope);
                IfNode n = (IfNode) node;
                visit(n.condition, scope);
                visit(n.then, anonymousScopeThen);
                visit(n.otherwise, anonymousScopeElse);
                if(!checkBooleanExpression(n.condition)){
                    throw new IllegalArgumentException("If statement condition does not evaluate to boolean");
                }
            }
            case POSTFIX_UNARY_OP -> {
                PostfixUnaryOp n = (PostfixUnaryOp)node;
                visit(n.child,scope);
                if(n.postfix.type == TokenType.INCREMENT || n.postfix.type == TokenType.DECREMENT){
                    n.typeAnnotation = TypeAnnotation.INT;
                }

            }
            case PREFIX_UNARY_OP -> {
                PrefixUnaryOp n = (PrefixUnaryOp) node;
                visit(n.child,scope);
                if(n.prefix.type == TokenType.NOT && checkBooleanExpression(n.child)){
                    n.typeAnnotation = TypeAnnotation.BOOL;
                }else{
                    n.typeAnnotation = TypeAnnotation.INT;
                }
            }
            case RETURN -> {
                ReturnNode ret = ((ReturnNode)node);
                visit(ret.expression,scope);
                compareTypes(ret.expression, scope.associatedMethod.getReturnType());
            }
            case BLOCK -> {
                for(Node n : ((BlockNode)node).stmts){
                    visit(n,scope);
                }
            }
            case LITERAL -> {
                LiteralNode lin = (LiteralNode) node;
                node.typeAnnotation = switch (lin.value.type){
                    case STRING_LITERAL -> TypeAnnotation.aCHAR;
                    case HEXADECIMAL_LITERAL, NUMERIC_LITERAL -> TypeAnnotation.INT;
                    case CHARACTER_LITERAL -> TypeAnnotation.CHAR;
                    case TRUE,FALSE -> TypeAnnotation.BOOL;
                    default -> null;
                };
            }
        }
    }


    private static TypeAnnotation compareTypes(Node a, TokenType operator,Node b){
        checkComparison(a,b);
        return switch(operator){
            case EQUAL,
                    GREATER_EQUAL,
                    GREATER,
                    LESS,
                    LESS_EQUAL,
                    NOT_EQUAL -> TypeAnnotation.BOOL;
                default -> a.typeAnnotation;
        };
    }

    private static void checkComparison(Node a, Node b){
        // CHAR + INT ambiguity
        if(a.typeAnnotation == TypeAnnotation.CHAR || a.typeAnnotation == TypeAnnotation.INT){
             if(b.typeAnnotation != TypeAnnotation.CHAR && b.typeAnnotation != TypeAnnotation.INT){
                typeMismatch(a,b);
             }
             return;
        }

        if(a.typeAnnotation != b.typeAnnotation){
            typeMismatch(a,b);
        }
    }

    private static boolean checkBooleanExpression(Node a){
        return a.typeAnnotation == TypeAnnotation.BOOL;
    }

    private static TypeAnnotation compareTypes(Node a, MethodTypes b){
        if(a == null){
             if(b.equals(MethodTypes.VOID)) {
                 return TypeAnnotation.VOID;
             }else{
                 throw new IllegalArgumentException("Method should return " + b);
             }
        }
        TypeAnnotation anno = TypeAnnotation.convertType(b);
        if(typesAreCompatible(a,anno)){
            return anno;
        }
        throw new IllegalArgumentException("Type mismatch: return type " + anno + " can't be resolved with returned value " + a.typeAnnotation);


    }

    private static TypeAnnotation checkParameterType(Node a, VariableTypes b){
        TypeAnnotation anno = TypeAnnotation.convertType(b,false);
        if(typesAreCompatible(a,anno)){
            return anno;
        }
        typeMismatch(a, anno);
        return null;
    }

    private static boolean typesAreCompatible(Node a, TypeAnnotation anno){
        // CHAR + INT ambiguity
        if(a.typeAnnotation == TypeAnnotation.CHAR || a.typeAnnotation == TypeAnnotation.INT){
            return anno == TypeAnnotation.CHAR || anno == TypeAnnotation.INT;
        }
        return a.typeAnnotation == anno;
    }

    private static void typeMismatch(Node a, Node b){
        throw new TypeMismatchException(
                "Type mismatch: " +
                a.typeAnnotation + " expression at [" + a.line + ", " + a.col + "]" +
                " conflicts with " +
                b.typeAnnotation + " expression at [" + b.line + ", " + b.col + "]");
    }

    private static void typeMismatch(Node a, TypeAnnotation b){
        throw new TypeMismatchException(
                "Type mismatch: " +
                a.typeAnnotation + " expression at [" + a.line + ", " + a.col + "]" +
                " improperly assigned to "
                + b);
    }

    public void printContents(){
        this.root.printContents();
    }

}
