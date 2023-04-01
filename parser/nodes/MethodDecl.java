package parser.nodes;

import token.Identifier;

import java.util.LinkedList;

public class MethodDecl extends DeclNode {
    public LinkedList<ParamNode> params;
    public BlockNode body;

    public MethodDecl(TypeNode resultType, Identifier name, LinkedList<ParamNode> params, BlockNode body) {
        super(Kind.METHOD_DECL, resultType, name);
        this.params = params;
        this.body = body;
        this.setPosition(name);
    }
}
