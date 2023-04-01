package parser.nodes;

import parser.ToyParser;
import token.Identifier;
import token.Token;

public class VarDeclNode extends DeclNode {
    public Node expression;

    public VarDeclNode(TypeNode type, Identifier name, Node expression) {
        super(Kind.DECL, type, name);
        this.expression = expression;
        this.setPosition(name);
    }

    public VarDeclNode(TypeNode type, Identifier name) {
        this(type, name, null);
    }
}
