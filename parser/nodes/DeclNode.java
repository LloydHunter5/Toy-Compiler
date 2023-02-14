package parser.nodes;

import parser.ToyParser;
import token.Token;

public class DeclNode extends Node {
    public TypeNode type;
    public Token name;

    public DeclNode(Kind kind, TypeNode type, Token name) {
        super(kind);
        this.type = type;
        this.name = name;
    }
}
