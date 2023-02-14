package parser.nodes;

import parser.ToyParser;
import token.Token;

public class LiteralNode extends LeafNode {
    public Token value;

    public LiteralNode(Token value) {
        super(Kind.LITERAL);
        this.value = value;
    }
}
