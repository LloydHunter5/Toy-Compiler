package parser.nodes;

import parser.ToyParser;

public class IndexNode extends Node {
    public Node indexExpr;

    public IndexNode(Node indexExpr) {
        super(Kind.INDEX);
        this.indexExpr = indexExpr;
    }
}
