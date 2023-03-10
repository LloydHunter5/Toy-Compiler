package ast;
import ast.types.ParameterTypes;
import parser.nodes.ParamNode;
import token.Identifier;

public class Parameter extends SymbolTableEntry{
    private ParameterTypes type;

    public Parameter(ParamNode node){
        this.type = ParameterTypes.convertType(node.type);
        this.name = node.name;
    }


}
