package parser.nodes;

import parser.ToyParser;

public class WhileNode extends Node {
    public Node conditions; //boolean expression
    public Node then;

    public WhileNode(Node conditions, Node then) {
        super(Kind.WHILE);
        this.conditions = conditions;
        this.then = then;
    }
}
