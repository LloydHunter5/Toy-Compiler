package ast;

import parser.nodes.*;
import token.Identifier;
import token.Token;
import token.TokenType;

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
        LinkedList<Identifier> names = variable.names;
        SymbolTable tempScope = this;
        if(names.size() <= 1){
            while(tempScope.parent != null){
                if(tempScope.isVariableInScope(names.getFirst())){
                    return true;
                }
                tempScope = tempScope.parent;
            }
        }else{
            while(tempScope.parent != null){
                // We are in the right scope, stop
                if(tempScope.name.equals(names.getFirst().value)){
                    tempScope = tempScope.parent;
                    break;
                }
                tempScope = tempScope.parent;
            }
        }
        // Check through the scopes, confirm they all exist within each other. Stops before the last ID
        Iterator<Identifier> i = names.iterator();
        Identifier currentID = i.next();
        //If we're in the program scope, check if that's correct. If it's not its ok
        if(tempScope.parent == null){
            if(tempScope.name.equals(currentID.value)){
                currentID = i.next();
            };
        }
        while(i.hasNext()){
            if(!tempScope.isMethodInScope(currentID)){
                return false;
            }
            tempScope = tempScope.methods.get(currentID.value).getSymbolTable();
            currentID = i.next();
        }
        //Return if the variable exists in the given scope
        return tempScope.isVariableInScope(currentID);
    }

    public boolean hasMethod(CallNode call) {
        LinkedList<Identifier> names = call.name;
        SymbolTable tempScope = this;
        if(names.size() <= 1){
            while(tempScope.parent != null){
                if(tempScope.isMethodInScope(names.getFirst())){
                    return true;
                }
                tempScope = tempScope.parent;
            }
        }else{
            while(tempScope.parent != null){
                // We are in the right scope, stop
                if(tempScope.name.equals(names.getFirst().value)){
                    tempScope = tempScope.parent;
                    break;
                }
                tempScope = tempScope.parent;
            }
        }

        // Check through the scopes, confirm they all exist within each other. Stops before the last ID
        Iterator<Identifier> i = names.iterator();
        Identifier currentID = i.next();
        while(i.hasNext()){
            if(!tempScope.isMethodInScope(currentID)){
                return false;
            }
            tempScope = tempScope.methods.get(currentID.value).getSymbolTable();
            currentID = i.next();
        }
        //Return if the method exists in the given scope
        return tempScope.isMethodInScope(currentID);
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
        for(SymbolTableEntry item : parameters.values()){
            s.append("\t");
            s.append(item.toString());
            s.append("\n");
        }
        s.append("\tVariables: \n");
        for(SymbolTableEntry item : variables.values()){
            s.append("\t");
            s.append(item.toString());
            s.append("\n");
        }
        s.append("\tMethods: \n");
        for(SymbolTableEntry item : methods.values()){
            s.append("\t");
            s.append(item.toString());
            s.append("\n");
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
