package parser.nodes;

import ast.SymbolTable;
import ast.types.TypeAnnotation;
import token.Token;

public abstract class Node
{
    public Kind kind;
    public TypeAnnotation typeAnnotation;
    public int line;
    public int col;
    public Node(Kind kind){
        this.kind = kind;
    }

    public void setPosition(int line, int col){
        this.line = line;
        this.col = col;
    }

    public void setPosition(Token t){
        setPosition(t.line,t.col);
    }
}

