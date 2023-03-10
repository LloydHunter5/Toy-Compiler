package ast.types;

import parser.nodes.TypeNode;
import token.Token;

public enum ParameterTypes{
    INT,
    CHAR,
    BOOL,
    aINT,
    aCHAR,
    aBOOL;

    public static ParameterTypes convertType(TypeNode t){
        return switch(t.type.type){
            case INT -> (t.isArrayType) ? aINT : INT;
            case CHAR -> (t.isArrayType) ? aCHAR : CHAR;
            case BOOLEAN -> (t.isArrayType) ? aBOOL : BOOL;
            default -> throw new IllegalArgumentException("Not a valid parameter type");
        };
    }
}