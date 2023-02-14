package parser.nodes;

import parser.ToyParser;
import token.Token;

public class AssignmentNode extends Node {
    public VariableNode var;
    public Token assignOp;
    public Node expression;

    public AssignmentNode(VariableNode var, Token assignOp, Node expression) {
        super(Kind.ASSIGNMENT);
        this.var = var;
        this.assignOp = assignOp;
        this.expression = expression;
    }
}
