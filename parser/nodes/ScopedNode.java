package parser.nodes;

import ast.SymbolTable;
import ast.SymbolTableEntry;
import token.Identifier;

import java.util.LinkedList;

public abstract class ScopedNode extends Node{
    public SymbolTableEntry symbolTableReference;
    public final LinkedList<Identifier> scope;
    public final Identifier name;

    public ScopedNode(Kind kind, Identifier name, LinkedList<Identifier> scope){
        super(kind);
        this.scope = scope;
        this.name = name;
        this.setPosition(name);
    }
}
