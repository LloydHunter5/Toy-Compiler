package ast;

import token.Identifier;
import token.TokenType;

import java.util.LinkedList;

public class SymbolTable {
    public final SymbolTable parent;
    public final Identifier name;
    public LinkedList<SymbolTableEntry> parameters;
    public LinkedList<SymbolTableEntry> contents;


    public SymbolTable(Identifier name){
        this(null,name);
        if(name.type != TokenType.PROGRAM) {
            throw new IllegalArgumentException("Not a valid main program: " + name);
        }
    }

    public SymbolTable(SymbolTable parent, Identifier name){
        this.parent = parent;
        this.name = name;
    }

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder("Scope: " + name.value + "\n");
        for(SymbolTableEntry item : parameters){
            s.append(item.toString());
            s.append("\n");
        }
        for(SymbolTableEntry item : contents){
            s.append(item.toString());
            s.append("\n");
        }
        return s.toString();
    }

}
