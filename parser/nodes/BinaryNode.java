package parser.nodes;

import parser.ToyParser;

//Two child nodes, left op comes first
public class BinaryNode extends Node {
    public Node left;
    public Node right;

    public BinaryNode(Kind kind, Node left, Node right) {
        super(kind);
        this.left = left;
        this.right = right;
    }
}
