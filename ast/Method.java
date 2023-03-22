package ast;

import ast.types.MethodTypes;
import ast.types.VariableTypes;
import parser.nodes.DeclNode;
import parser.nodes.MethodDecl;
import parser.nodes.ParamNode;

import java.util.LinkedList;

public class Method extends SymbolTableEntry {
    private final MethodTypes returnType;
    // Ordered list of param types, for type analysis of method calls
    public VariableTypes[] parameterTypes;
    private final SymbolTable table;
    private final Method parentMethod;
    private final SymbolTable parentScope;
    public Method(DeclNode node, Method parentMethod, SymbolTable parentScope) {
        this.returnType = MethodTypes.convertType(node.type);
        this.name = node.name;
        this.parentMethod = parentMethod;
        this.parentScope = parentScope;
        this.table = new SymbolTable(this.name.value,this,parentScope);
        LinkedList<ParamNode> params = ((MethodDecl)node).params;
        parameterTypes = new VariableTypes[params.size()];
        int i = 0;
        for(ParamNode param : params){
            parameterTypes[i++] = VariableTypes.convertType(param.type);
            table.add(param);
        }
    }

    public Method (DeclNode node, Method parentMethod){
        this(node, parentMethod, parentMethod.getSymbolTable());
    }

    public Method (DeclNode node, SymbolTable parentScope){
        this(node, null, parentScope);
    }


    public SymbolTable getParentSymbolTable(){
        return parentScope;
    }

    public Method getParentMethod() {
        return parentMethod;
    }

    public SymbolTable getSymbolTable(){
        return table;
    }

    public MethodTypes getReturnType(){
        return returnType;
    }

}
