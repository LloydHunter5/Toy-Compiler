package token;

public class Token {
    public TokenType type;
    public int line;
    public int col;
    public Token(TokenType type, int line, int col){
        this.type = type;
        this.line = line;
        this.col = col;
    }
    @Override
    public String toString(){
        return this.type.toString() + " @ line: " + line + ", column: " + col;
    }
}

