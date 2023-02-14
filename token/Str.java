package token;

public class Str extends Token {
    public String value;

    public Str(TokenType type, int line, int col, String value) {
        super(type, line, col);
        this.value = value;
    }

    @Override
    public String toString() {
        return this.type + ": " + this.value + " @ line: " + line + ", column: " + col;
    }
}
