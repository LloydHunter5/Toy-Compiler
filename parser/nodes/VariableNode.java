package parser.nodes;

import parser.ToyParser;
import token.Identifier;
import token.Token;

import java.util.LinkedList;

public class VariableNode extends LeafNode {
    public LinkedList<Identifier> names;
    //For array vars
    public IndexNode index;

    public VariableNode(LinkedList<Identifier> names) {
        this(names, null);
    }

    public VariableNode(LinkedList<Identifier> names, IndexNode index) {
        super(Kind.VARIABLE);
        this.names = names;
        this.index = index;
    }

    public boolean isArrayType() {
        return index != null;
    }
}
