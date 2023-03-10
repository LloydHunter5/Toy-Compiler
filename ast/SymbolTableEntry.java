package ast;


import token.Identifier;

public abstract class SymbolTableEntry {
    Identifier name;

    @Override
    public String toString(){
        return "\t" + name.value;
    }
}