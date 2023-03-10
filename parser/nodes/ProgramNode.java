package parser.nodes;

import parser.ToyParser;
import token.Identifier;
import token.Token;

public class ProgramNode extends Node {
    public Identifier name;
    public BlockNode body;

    public ProgramNode(Token name, BlockNode body) {
        super(Kind.PROGRAM);
        this.name = (Identifier) name;
        this.body = body;
    }
}
