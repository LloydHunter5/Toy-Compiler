package ast;

import ast.types.MethodTypes;
import parser.nodes.DeclNode;
import parser.nodes.MethodDecl;
import parser.nodes.ParamNode;
import token.Identifier;

public class Method extends SymbolTableEntry {
    private MethodTypes type;
    private SymbolTable table;
    private final SymbolTable parent;
    public Method(DeclNode node, SymbolTable parentScope) {
        this.type = MethodTypes.convertType(node.type);
        this.name = node.name;
        this.parent = parentScope;
        this.table = new SymbolTable(this.name.value,parentScope);
        for(ParamNode param : ((MethodDecl)node).params){
            table.add(param);
        }
    }

    public SymbolTable getParent(){
        return parent;
    }

    public SymbolTable getSymbolTable(){
        return table;
    }

    public MethodTypes getReturnType(){
        return type;
    }

}
