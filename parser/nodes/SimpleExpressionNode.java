package parser.nodes;

import parser.ToyParser;
import token.Token;

//Extension of binaryOp to include a sign for the left node
public class SimpleExpressionNode extends BinaryOp {
    public Token sign;

    public SimpleExpressionNode(Token sign, BinaryOp left, SimpleExpressionNode right, Token operator) {
        super(left, right, operator);
        this.kind = Kind.SIMPLE_EXPRESSION;
        this.sign = sign;
    }

}
