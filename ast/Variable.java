package ast;

import parser.nodes.DeclNode;
import token.Identifier;
import ast.types.VariableTypes;


public class Variable extends SymbolTableEntry{
    private final VariableTypes type;


    public Variable(DeclNode node){
        this.type = VariableTypes.convertType(node.type);
        this.name = node.name;
    }
    public Identifier getName() {
        return this.name;
    }
    public VariableTypes getType(){return this.type;}


}