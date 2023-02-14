package parser.nodes;

import parser.ToyParser;

public class ReturnNode extends Node {
    public Node expression;

    public ReturnNode(Node expression) {
        super(Kind.RETURN);
        this.expression = expression;
    }

    public ReturnNode() {
        this(null);
    }
}
