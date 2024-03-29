package parser.nodes;

import token.Identifier;

import java.util.LinkedList;

public class CallNode extends ScopedNode {
    public LinkedList<Node> args;

    public CallNode(LinkedList<Identifier> scope,Identifier name, LinkedList<Node> args) {
        super(Kind.CALL,name,scope);
        this.args = args;
        this.setPosition(name);
    }
}
