package ast.types;

import parser.nodes.TypeNode;

public enum MethodTypes{
    INT,
    CHAR,
    BOOL,
    VOID,
    aINT,
    aCHAR,
    aBOOL;

    public static MethodTypes convertType(TypeNode t){
        MethodTypes type = switch(t.type.type){
            case INT -> (t.isArrayType) ? aINT : INT;
            case CHAR -> (t.isArrayType) ? aCHAR : CHAR;
            case BOOLEAN -> (t.isArrayType) ? aBOOL : BOOL;
            case VOID -> VOID;
            default -> throw new IllegalArgumentException("Not a valid parameter type");
        };
        if(t.isArrayType && type.equals(VOID)){
            throw new IllegalArgumentException("Array cannot be void!");
        }
        return type;
    }
}
