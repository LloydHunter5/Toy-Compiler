public class Token {
    Tokens type;
    int line;
    int col;
    public Token(Tokens type, int line, int col){
        this.type = type;
        this.line = line;
        this.col = col;
    }
    @Override
    public String toString(){
        return this.type.toString() + " @ line: " + line + ", column: " + col;
    }
}

class Identifier extends Token {
    String value;
    public Identifier(Tokens type, int line, int col, String value){
        super(type, line, col);
        this.value = value;
    }
    @Override
    public String toString(){
        return this.type + ": " + this.value + " @ line: " + line + ", column: " + col;
    }
}

class Number extends Token{
    int value;
    public Number(Tokens type, int line, int col, int value){
        super(type, line, col);
        this.value = value;
    }
    @Override
    public String toString(){
        return this.type + ": " + this.value + " @ line: " + line + ", column: " + col;
    }
}
class Str extends Token{
    String value;
    public Str(Tokens type, int line, int col, String value){
        super(type,line,col);
        this.value = value;
    }
    @Override
    public String toString(){
        return this.type + ": " + this.value + " @ line: " + line + ", column: " + col;
    }
}
class Char extends Token{
    char value;
    public Char(Tokens type, int line, int col, char value){
        super(type,line,col);
        this.value = value;
    }
    @Override
    public String toString(){
        return this.type + ": " + this.value + " @ line: " + line + ", column: " + col;
    }
}
class ErrorToken extends Token{
    private String errorReport;

    public ErrorToken(Tokens type, int line, int col, String errorReport){
        super(type,line,col);
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
