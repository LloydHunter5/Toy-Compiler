package ast;


import token.Identifier;

public interface SymbolTableEntry {
    public Types getType();
    public Identifier getName();

}