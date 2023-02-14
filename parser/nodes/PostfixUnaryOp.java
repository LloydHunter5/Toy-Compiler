package parser.nodes;

import parser.ToyParser;
import token.Token;

public class PostfixUnaryOp extends SingleNode {
    public Token postfix;

    public PostfixUnaryOp(Node operand, Token postfix) {
        super(Kind.POSTFIX_UNARY_OP, operand);
        this.postfix = postfix;
    }
}
