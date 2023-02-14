package parser.nodes;

import parser.ToyParser;
import token.Token;

public class ProgramNode extends Node {
    public Token name;
    public BlockNode body;

    public ProgramNode(Token name, BlockNode body) {
        super(Kind.PROGRAM);
        this.name = name;
        this.body = body;
    }
}
