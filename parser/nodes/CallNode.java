package parser.nodes;

import parser.ToyParser;
import token.Identifier;
import token.Token;

import java.util.LinkedList;

public class CallNode extends Node {
    public LinkedList<Identifier> name;
    public LinkedList<Node> args;

    public CallNode(LinkedList<Identifier> name, LinkedList<Node> args) {
        super(Kind.CALL);
        this.name = name;
        this.args = args;
    }

    public CallNode(LinkedList<Identifier> name) {
        this(name, null);
    }
}
