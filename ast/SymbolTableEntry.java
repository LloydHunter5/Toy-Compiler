package ast;


import ast.types.Kind;
import token.Identifier;

public abstract class SymbolTableEntry {
    Identifier name;
    Kind kind;
    @Override
    public String toString(){
        return "\t" + name.value;
    }
}