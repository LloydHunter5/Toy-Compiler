package parser.nodes;

import parser.ToyParser;
import token.Token;

public class TypeNode extends Node {
    public Token type;
    public boolean isArrayType;
    public Node arrSize;

    public TypeNode(Token type, boolean isArrayType, Node arrSize) {
        super(Kind.TYPE);
        // int, char, bool, void, ArrayType
        this.type = type;
        this.isArrayType = isArrayType;
        this.arrSize = arrSize;
        this.setPosition(type);
    }

    public TypeNode(Token type, boolean isArrayType) {
        this(type, isArrayType, null);
    }

    public TypeNode(Token type) {
        this(type, false, null);
    }
}
