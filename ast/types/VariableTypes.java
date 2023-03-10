package ast.types;

import parser.nodes.Node;
import parser.nodes.TypeNode;

public enum VariableTypes{
    INT,
    CHAR,
    BOOL,
    aINT,
    aCHAR,
    aBOOL;
    public Node size;

    public static VariableTypes convertType(TypeNode t){
        VariableTypes type = switch(t.type.type){
            case INT -> (t.isArrayType) ? aINT : INT;
            case CHAR -> (t.isArrayType) ? aCHAR : CHAR;
            case BOOLEAN -> (t.isArrayType) ? aBOOL : BOOL;
            default -> throw new IllegalArgumentException("Not a valid parameter type");
        };
        type.setSize(t.arrSize);
        return type;
    }
    public void setSize(Node size){
        this.size = size;
    }
}
