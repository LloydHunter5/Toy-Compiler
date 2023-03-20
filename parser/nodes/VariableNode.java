package parser.nodes;

import token.Identifier;

import java.util.LinkedList;

public class VariableNode extends ScopedNode {
    //For array vars
    public IndexNode index;

    public VariableNode(LinkedList<Identifier> scope, Identifier name, IndexNode index){
        super(Kind.VARIABLE,name,scope);
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
