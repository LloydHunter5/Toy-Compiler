package token;

public class Char extends Token {
    public char value;

    public Char(TokenType type, int line, int col, char value) {
        super(type, line, col);
        this.value = value;
    }

    @Override
    public String toString() {
        return this.type + ": " + this.value + " @ line: " + line + ", column: " + col;
    }
}
