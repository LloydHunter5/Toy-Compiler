package parser.nodes;

import token.Identifier;

public abstract class DeclNode extends Node {
    public TypeNode type;
    public Identifier name;

    public DeclNode(Kind kind, TypeNode type, Identifier name) {
        super(kind);
        this.type = type;
        this.name = name;
    }
}
