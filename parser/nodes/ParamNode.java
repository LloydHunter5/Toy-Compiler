package parser.nodes;

import parser.ToyParser;
import token.Identifier;
import token.Token;

public class ParamNode extends Node {
    public TypeNode type;
    public Identifier name;

    public ParamNode(TypeNode type, Identifier name) {
        super(Kind.PARAMETER);
        this.type = type;
        this.name = name;
    }
}
