package parser.nodes;

public class IfNode extends Node {
    public Node condition; //boolean expression
    public Node then; // if x is true then:
    public Node otherwise; //else: (nullable)

    public IfNode(Node condition, Node then, Node otherwise) {
        super(Kind.IF);
        this.condition = condition;
        this.then = then;
        this.otherwise = otherwise;
    }

    public IfNode(Node condition, Node then) {
        this(condition, then, null);
    }
}
