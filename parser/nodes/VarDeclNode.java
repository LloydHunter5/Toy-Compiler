package parser.nodes;

import parser.ToyParser;
import token.Token;

public class VarDeclNode extends DeclNode {
    public Node expression;

    public VarDeclNode(TypeNode type, Token name, Node expression) {
        super(Kind.DECL, type, name);
        this.expression = expression;
    }

    public VarDeclNode(TypeNode type, Token name) {
        this(type, name, null);
    }
}
