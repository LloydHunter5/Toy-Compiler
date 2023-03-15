package parser.nodes;

import token.Identifier;

import java.util.LinkedList;

public class VariableNode extends LeafNode {
    public LinkedList<Identifier> scope;
    public Identifier name;
    //For array vars
    public IndexNode index;


    public VariableNode(LinkedList<Identifier> scope, Identifier name, IndexNode index){
        super(Kind.VARIABLE);
        this.scope = scope;
        this.name  = name;
        this.index = index;
    }

    public VariableNode(LinkedList<Identifier> scope, Identifier name) {
        this(scope,name,null);
    }

    public VariableNode(Identifier name){
        this(null,name,null);
    }

    public VariableNode(Identifier name, IndexNode index){
        this(null,name,index);
    }

    public boolean isArrayType() {
        return index != null;
    }
}
