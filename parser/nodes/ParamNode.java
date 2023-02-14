package parser.nodes;

import parser.ToyParser;
import token.Token;

public class ParamNode extends Node {
    public TypeNode type;
    public Token name;

    public ParamNode(TypeNode type, Token name) {
        super(Kind.PARAMETER);
        this.type = type;
        this.name = name;
    }
}
