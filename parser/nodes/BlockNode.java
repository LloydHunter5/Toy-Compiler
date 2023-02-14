package parser.nodes;

import parser.ToyParser;

import java.util.LinkedList;

public class BlockNode extends Node {
    public LinkedList<Node> stmts;

    public BlockNode(LinkedList<Node> stmts) {
        super(Kind.BLOCK);
        this.stmts = stmts;
    }
}
