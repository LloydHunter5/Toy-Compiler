package ast;
import ast.types.ParameterTypes;
import parser.nodes.ParamNode;

public class Parameter extends SymbolTableEntry{
    private final ParameterTypes type;

    public Parameter(ParamNode node){
        this.type = ParameterTypes.convertType(node.type);
        this.name = node.name;
    }

    public ParameterTypes getType(){
        return this.type;
    }


}
