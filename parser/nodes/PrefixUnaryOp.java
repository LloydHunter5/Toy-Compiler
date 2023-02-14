package parser.nodes;

import parser.ToyParser;
import token.Token;

public class PrefixUnaryOp extends SingleNode {
    public Token prefix;

    public PrefixUnaryOp(Token prefix, Node operand) {
        super(Kind.PREFIX_UNARY_OP, operand);
        this.prefix = prefix;
    }
}
