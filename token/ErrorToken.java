package token;

public class ErrorToken extends Token {
    public String errorReport;

    public ErrorToken(TokenType type, int line, int col, String errorReport) {
        super(type, line, col);
        this.errorReport = errorReport;
    }

    public String getErrorReport() {
        return errorReport;
    }

    @Override
    public String toString() {
        return this.type + ": " +
                this.errorReport +
                "@ line: " + line +
                ", column: " +
                col;
    }
}
