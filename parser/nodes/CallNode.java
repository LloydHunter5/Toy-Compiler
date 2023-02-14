package parser.nodes;

import parser.ToyParser;
import token.Token;

import java.util.LinkedList;

public class CallNode extends Node {
    public LinkedList<Token> name;
    public LinkedList<Node> args;

    public CallNode(LinkedList<Token> name, LinkedList<Node> args) {
        super(Kind.CALL);
        this.name = name;
        this.args = args;
    }

    public CallNode(LinkedList<Token> name) {
        this(name, null);
    }
}
