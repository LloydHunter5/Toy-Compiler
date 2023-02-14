package parser.nodes;

import parser.ToyParser;
import token.Token;

import java.util.LinkedList;

public abstract class Node
{
    public Kind kind;
    public Node(Kind kind){
        this.kind = kind;
    }
}
//Node subclasses

