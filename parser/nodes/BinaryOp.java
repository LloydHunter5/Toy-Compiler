package parser.nodes;

import parser.ToyParser;
import token.Token;

// expression node, addition, subtraction, equality, etc
public class BinaryOp extends BinaryNode {
    public Token operator;

    public BinaryOp(Node left, Node right, Token operator) {
        super(Kind.BINARY_OP, left, right);
        this.operator = operator;
    }
}
