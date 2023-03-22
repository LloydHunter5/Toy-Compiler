package ast;


import parser.nodes.*;
import token.Identifier;

import java.util.HashMap;
import java.util.LinkedList;

public class SymbolTable {
    public final SymbolTable parent;
    public final Method associatedMethod;
    public final String name;
    private final HashMap<String, Method> methods;
    private final HashMap<String, Variable> variables;

    public SymbolTable(Identifier name){
        this(name.value,null,null);
        // may want a check here for main programs
        // throw new IllegalArgumentException("Not a valid main program: " + name);
    }

    public SymbolTable(String name, Method associatedMethod){
        this(name,associatedMethod, associatedMethod.getParentSymbolTable());
    }
    public SymbolTable(String name, SymbolTable parent){
        this(name,null,parent);
    }

    public SymbolTable(String name, Method associatedMethod, SymbolTable parent){
        this.associatedMethod = associatedMethod;
        this.parent = parent;
        this.name = name;
        this.methods = new HashMap<>();
        this.variables = new HashMap<>();
    }


    public void add(MethodDecl decl) {
        if(methods.containsKey(decl.name.value)){
            throw new IllegalArgumentException("Method '" + decl.name.value +  "' is already defined in this scope");
        }
        // this.associatedMethod may be null, that's ok!
        methods.put(decl.name.value,new Method(decl,this.associatedMethod,this));
    }
    public void add(VarDeclNode decl){
        if(variables.containsKey(decl.name.value)){
            throw new IllegalArgumentException("Variable '" + decl.name.value +  "' is already defined in this scope");
        }
        variables.put(decl.name.value,new Variable(decl));

    }

    public void add(ParamNode param){
        variables.put(param.name.value,new Variable(param));
    }

    public void add(Node node){
        switch (node.kind){
            case METHOD_DECL -> add((MethodDecl)node);
            case DECL -> add((VarDeclNode) node);
            case PARAMETER -> add((ParamNode)node);
        }
    }
    public boolean annotateSymbolTableEntry(VariableNode variable) {
        LinkedList<Identifier> variableScope = variable.scope;
        SymbolTable tempScope = this;
        if(variableScope.isEmpty()){
            while(tempScope.parent != null){
                if(tempScope.isVariableInScope(variable.name)){
                    tempScope.annotateVariableOrParamEntry(variable);
                    return true;
                }
                tempScope = tempScope.parent;
            }
        }else {
            tempScope = hasCorrectScope(variableScope, tempScope);
        }
        //Return true if the variable exists in the given scope
        if(tempScope != null) {
            tempScope.annotateVariableOrParamEntry(variable);
            return tempScope.isVariableInScope(variable.name);
        }
        return false;
    }

    private void annotateVariableOrParamEntry(VariableNode v){
        v.symbolTableReference = this.variables.get(v.name.value);
    }

    public Method getMethod(CallNode call){
        LinkedList<Identifier> callScope = call.scope;
        SymbolTable tempScope = this;
        // No defined scope, go out until variable exists
        if(callScope.isEmpty()) {
            while (tempScope.parent != null) {
                if (tempScope.isMethodInScope(call.name)) {
                    return tempScope.getMethodInScope(call.name);
                }
                tempScope = tempScope.parent;
            }
        }else{
            // Scope is defined, go out until we find the first defined symbol table
            tempScope = hasCorrectScope(callScope,tempScope);
        }
        if(tempScope == null){
            throw new IllegalArgumentException("Method " + call.name + "is not defined in the given scope");
        }
        return tempScope.getMethodInScope(call.name);
    }

    public boolean hasMethod(CallNode call) {
        LinkedList<Identifier> callScope = call.scope;
        SymbolTable tempScope = this;
        // No defined scope, go out until variable exists
        if(callScope.isEmpty()) {
            while (tempScope.parent != null) {
                if (tempScope.isMethodInScope(call.name)) {
                    tempScope.annotateMethodEntry(call);
                    return true;
                }
                tempScope = tempScope.parent;
            }
            return tempScope.isMethodInScope(call.name);
        }else{
            // Scope is defined, go out until we find the first defined symbol table
            tempScope = hasCorrectScope(callScope,tempScope);
        }

        if(tempScope != null){
            //Return if the method exists in the given scope
            tempScope.annotateMethodEntry(call);
            return tempScope.isMethodInScope(call.name);
        }
        return false;
    }

    private void annotateMethodEntry(CallNode c){
        c.symbolTableReference = methods.get(c.name.value);
    }

    private SymbolTable hasCorrectScope(LinkedList<Identifier> scope, SymbolTable tempScope){
        while(tempScope.parent != null){
            tempScope = tempScope.parent;
            // We are in the right scope, stop
            if(tempScope.name.equals(scope.getFirst().value)){
                break;
            }
        }

        // We unwound to the parent scope and couldn't find the correct method
        if(!tempScope.name.equals(scope.getFirst().value)){
            return null;
        }

        // Check through the scopes, confirm they all exist within each other
        boolean firstIter = true;
        for (Identifier currentID : scope) {
            if(!firstIter) {
                if (!tempScope.isMethodInScope(currentID)) {
                    return null;
                }
                tempScope = tempScope.methods.get(currentID.value).getSymbolTable();
            }else{
                firstIter = false;
            }
        }
        return tempScope;
    }

    public boolean isMethodInScope(Identifier name){
        return methods.containsKey(name.value);
    }

    private boolean isVariableInScope(Identifier name){
        return variables.containsKey(name.value);
    }


    public Method getMethodInScope(Identifier name){
        Method m = methods.get(name.value);
        if(m == null) {
            throw new IllegalArgumentException("Method: " + name + "must be declared before it is called");
        }
        return m;
    }

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder("Scope: " + name + "\n");
        s.append("\tVariables: \n");
        for(Variable item : variables.values()){
            s.append("\t")
            .append(item.toString())
                .append(" : ")
                .append(item.getType())
            .append("\n");
        }
        s.append("\tMethods: \n");
        for(Method item : methods.values()){
            s.append("\t")
            .append(item.toString())
                .append(" : ")
                .append(item.getReturnType())
            .append("\n");
        }
        return s.toString();
    }

    public void printContents(){
        System.out.println(this);
        for(Method m : methods.values()){
            m.getSymbolTable().printContents();
        }
    }

}
