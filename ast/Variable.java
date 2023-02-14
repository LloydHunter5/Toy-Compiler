package ast;

import token.Identifier;
import parser.ToyParser.*;


public class Variable{
    private Types.VariableTypes type;
    private Identifier name;


    public Identifier getName() {
        return this.name;
    }
}