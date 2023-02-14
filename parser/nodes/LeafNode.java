package parser.nodes;

import parser.ToyParser;

// contains a single terminal token, for example AndOp => '&&' or Name => IDENTIFIER
public class LeafNode extends Node {
    public LeafNode(Kind kind) {
        //Kinds: variable, integer, char, string, bool
        super(kind);
    }
}
