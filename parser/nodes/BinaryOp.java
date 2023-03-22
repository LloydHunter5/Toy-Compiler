package parser.nodes;

import token.Token;
import token.TokenType;

// expression node, addition, subtraction, equality, etc
public class BinaryOp extends BinaryNode {
    public Token operator;
    public BinaryOp(Node left, Node right, Token operator) {
        super(Kind.BINARY_OP, left, right);
        this.operator = operator;
    }
}
