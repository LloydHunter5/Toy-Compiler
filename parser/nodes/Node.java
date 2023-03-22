package parser.nodes;

import ast.SymbolTable;
import ast.types.TypeAnnotation;

public abstract class Node
{
    public Kind kind;
    public TypeAnnotation typeAnnotation;
    public int row;
    public int col;
    public Node(Kind kind){
        this.kind = kind;
    }

    public void setPosition(int row, int col){
        this.row = row;
        this.col = col;
    }
}
//Node subclasses

