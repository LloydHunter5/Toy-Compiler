package token;

public class Identifier extends Token {
    public String value;

    public Identifier(TokenType type, int line, int col, String value) {
        super(type, line, col);
        this.value = value;
    }

    @Override
    public String toString() {
        return this.type + ": " + this.value + " @ line: " + line + ", column: " + col;
    }
}
