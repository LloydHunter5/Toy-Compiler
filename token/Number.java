package token;

public class Number extends Token {
    public int value;

    public Number(TokenType type, int line, int col, int value) {
        super(type, line, col);
        this.value = value;
    }

    @Override
    public String toString() {
        return this.type + ": " + this.value + " @ line: " + line + ", column: " + col;
    }
}
