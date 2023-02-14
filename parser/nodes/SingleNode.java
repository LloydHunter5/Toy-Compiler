package parser.nodes;

import parser.ToyParser;

//Single child, for example if Term => Factor
public class SingleNode extends Node {
    public Node child;

    public SingleNode(Kind kind, Node child) {
        super(kind);
        this.child = child;
    }
}
