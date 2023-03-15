package parser.nodes;

import token.Identifier;

import java.util.LinkedList;

public class CallNode extends Node {
    public LinkedList<Identifier> scope;
    public LinkedList<Node> args;
    public Identifier name;

    public CallNode(LinkedList<Identifier> scope,Identifier name, LinkedList<Node> args) {
        super(Kind.CALL);
        this.scope = scope;
        this.name = name;
        this.args = args;
    }

    public CallNode(LinkedList<Identifier> scope) {
        super(Kind.CALL);
        this.name = scope.removeLast();
        this.scope = scope;
        this.args = null;
    }
}
