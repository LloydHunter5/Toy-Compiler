package ast;

import parser.nodes.*;
import token.Identifier;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class SymbolTable {
    public final SymbolTable parent;
    public final String name;
    private HashMap<String, Parameter> parameters;
    private HashMap<String, Method> methods;
    private HashMap<String, Variable> variables;

    public SymbolTable(Identifier name){
        this(name.value,null);
        // may want a check here for main programs
        // throw new IllegalArgumentException("Not a valid main program: " + name);
    }

    public SymbolTable(String name, SymbolTable parent){
        this.parent = parent;
        this.name = name;
        this.parameters = new HashMap<>();
        this.methods = new HashMap<>();
        this.variables = new HashMap<>();
    }


    public void add(MethodDecl decl) {
        if(methods.containsKey(decl.name.value)){
            throw new IllegalArgumentException("Method '" + decl.name.value +  "' is already defined in this scope");
        }
        methods.put(decl.name.value,new Method(decl,this));
    }
    public void add(VarDeclNode decl){
        if(variables.containsKey(decl.name.value) || parameters.containsKey(decl.name.value)){
            throw new IllegalArgumentException("Variable '" + decl.name.value +  "' is already defined in this scope");
        }
        variables.put(decl.name.value,new Variable(decl));

    }

    public void add(ParamNode param){
        parameters.put(param.name.value,new Parameter(param));
    }

    public void add(Node node){
        switch (node.kind){
            case METHOD_DECL -> add((MethodDecl)node);
            case DECL -> add((VarDeclNode) node);
            case PARAMETER -> add((ParamNode)node);
        }
    }

    public boolean hasVariable(VariableNode variable) {
        LinkedList<Identifier> variableScope = variable.scope;
        SymbolTable tempScope = this;
        if(variableScope == null || variableScope.size() == 0){
            while(tempScope.parent != null){
                if(tempScope.isVariableInScope(variable.name)){
                    return true;
                }
                tempScope = tempScope.parent;
            }
            return tempScope.isVariableInScope(variable.name);
        }

        while(tempScope.parent != null){
            // We are in the right scope, stop
            if(tempScope.name.equals(variableScope.getFirst().value)){
                break;
            }
            tempScope = tempScope.parent;
        }

        // Check through the scopes, confirm they all exist within each other. Stops before the last ID
        Iterator<Identifier> i = variableScope.iterator();
        Identifier currentID = i.next();
        //If we're in the program scope, check if that's correct. If it's not its ok
        if(tempScope.parent == null){
            if(tempScope.name.equals(currentID.value)){
                if(i.hasNext()) currentID = i.next();
            }
        }
        while(i.hasNext()){
            if(!tempScope.isMethodInScope(currentID)){
                return false;
            }
            tempScope = tempScope.methods.get(currentID.value).getSymbolTable();
            currentID = i.next();
        }
        //Return if the variable exists in the given scope
        return tempScope.isVariableInScope(variable.name);
    }

    public boolean hasMethod(CallNode call) {
        LinkedList<Identifier> callScope = call.scope;
        SymbolTable tempScope = this;
        if(callScope == null || callScope.size() == 0){
            while(tempScope.parent != null){
                if(tempScope.isMethodInScope(call.name)){
                    return true;
                }
                tempScope = tempScope.parent;
            }
            return tempScope.isMethodInScope(call.name);
        }else{
            while(tempScope.parent != null){
                // We are in the right scope, stop
                if(tempScope.name.equals(callScope.getFirst().value)){
                    tempScope = tempScope.parent;
                    break;
                }
                tempScope = tempScope.parent;
            }
        }

        // Check through the scopes, confirm they all exist within each other. Stops before the last ID
        Iterator<Identifier> i = callScope.iterator();
        Identifier currentID = i.next();
        while(i.hasNext()){
            if(!tempScope.isMethodInScope(currentID)){
                return false;
            }
            tempScope = tempScope.methods.get(currentID.value).getSymbolTable();
            currentID = i.next();
        }
        //Return if the method exists in the given scope
        return tempScope.isMethodInScope(call.name);
    }

    public boolean isMethodInScope(Identifier name){
        return methods.containsKey(name.value);
    }

    private boolean isVariableInScope(Identifier name){
        return variables.containsKey(name.value) || parameters.containsKey(name.value);
    }

    public Method getMethod(Identifier name){
        return methods.get(name.value);
    }

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder("Scope: " + name + "\n");
        s.append("\tParameters: \n");
        for(Parameter item : parameters.values()){
            s.append("\t")
            .append(item.toString())
                .append(" : ")
                .append(item.getType())
            .append("\n");
        }
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
