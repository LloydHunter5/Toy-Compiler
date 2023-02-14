package lexer;

import token.*;

import java.util.Scanner;
public class ToyLexer {

    public enum State {
        START(TokenType.ERROR, false) {
            @Override
            public State next(char c) {
                if(c >= 'a' && c <= 'z') return idCHAR;
                else if(c >= 'A' && c <= 'Z') return idCHAR;
                else if (State.isDigit(c) && c != '0') return nlDIGIT;
                switch (c){
                    case ' ':
                    case '\n':
                    case '\t': return START;
                    case '\'': return clSTART;
                    case '\"': return slCHAR;
                    case '0': return nlZERO;
                    case '=': return ASSIGN;
                    case '!': return NOT;
                    case '&': return AND;
                    case '|': return OR;
                    case '<': return LESS;
                    case '>': return GREATER;
                    case '+': return PLUS;
                    case '-': return MINUS;
                    case '*': return MULTIPLY;
                    case '/': return DIVIDE;
                    case '%': return MOD;
                    case '^': return XOR;
                    case '~': return COMPLEMENT;
                    case '(': return OPEN_PAREN;
                    case ')': return CLOSE_PAREN;
                    case '[': return OPEN_BRACKET;
                    case ']': return CLOSE_BRACKET;
                    case '{': return OPEN_CURL_BRACKET;
                    case '}': return CLOSE_CURL_BRACKET;
                    case '.': return DOT;
                    case ',': return COMMA;
                    case ';': return SEMICOLON;
                    case ':': return COLON;
                    case (char)4: return EOF;
                    default: return ERROR;
                }
            }
        },
        TRUE(TokenType.TRUE, true){
            @Override
            public State next(char c){
                return State.ERROR;
            }
        },
        FALSE(TokenType.FALSE, true){
            @Override
            public State next(char c){
                return State.ERROR;
            }
        },
        EOF(TokenType.EOF, true){
            @Override
            public State next(char c) {
                return State.ERROR;
            }
        },
        //Identifiers
        idCHAR(TokenType.IDENTIFIER, true) {
            @Override
            public State next(char c) {
                if(c == '_') return idUNDERSCORE;
                else if(c >= 'a' && c <= 'z') return idCHAR;
                else if(c >= 'A' && c <= 'Z') return idCHAR;
                else if(c >= '0' && c <= '9') return idCHAR;
                else return ERROR;
            }
        },
        idUNDERSCORE(TokenType.ERROR, false){
            @Override
            public State next(char c) {
                if(c >= 'a' && c <= 'z') return idCHAR;
                else if(c >= 'A' && c <= 'Z') return idCHAR;
                else if(c >= '0' && c <= '9') return idCHAR;
                else return ERROR;
            }
        },

        //token.Char Literals
        clSTART(TokenType.ERROR, false) {
            @Override
            public State next(char c) {
                switch (c){
                    case '\\': return clSPECIAL;
                    case '\'': return clEND;
                    default: return clCHAR;
                }
            }
        },
        clSPECIAL(TokenType.ERROR, true){
            @Override
            public State next(char c) {
                switch (c){
                    case '\\':
                    case 'n':
                    case 'r':
                    case 't':
                    case '0':
                    case '\'':
                    case '\"':
                        return clCHAR;
                    default:
                        return ERROR;
                }
            }
        },
        clCHAR(TokenType.ERROR, false){
            @Override
            public State next(char c){
                if(c == '\'') return clEND;
                else return ERROR;
            }
        },
        clEND(TokenType.CHARACTER_LITERAL, true){
            @Override
            public State next(char c){
                return ERROR; //if there is a char after \' it is invalid
            }
        },

        //token.Number Literals
        nlZERO(TokenType.NUMERIC_LITERAL, true) {
            @Override
            public State next(char c) {
                if (State.isDigit(c)) return nlDIGIT;
                switch (c) {
                    case '_': return nlUNDERSCORE;
                    case 'x':
                    case 'X': return nlHEXSTART;
                    default:  return ERROR;
                }
            }
        },
        nlDIGIT(TokenType.NUMERIC_LITERAL, true) {
            @Override
            public State next(char c) {
                if (State.isDigit(c)) return nlDIGIT;
                switch (c) {
                    case '_': return nlUNDERSCORE;
                    default :  return ERROR;
                }
            }
        },
        nlUNDERSCORE(TokenType.ERROR, false) {
            @Override
            public State next(char c) {
                if (State.isDigit(c)) return nlDIGIT;
                return ERROR;
            }
        },
        nlHEXSTART(TokenType.ERROR, false) {
            @Override
            public State next(char c) {
                if (State.isHexChar(c)) return nlHEX;
                return ERROR;
            }
        },
        nlHEX(TokenType.HEXADECIMAL_LITERAL, true) {
            @Override
            public State next (char c) {
                if (State.isHexChar(c)) return nlHEX;
                switch (c) {
                    case '_': return nlHEXUNDERSCORE;
                    default : return ERROR;
                }
            }
        },
        nlHEXUNDERSCORE(TokenType.ERROR, false) {
            @Override
            public State next(char c) {
                if (State.isHexChar(c)) return nlHEX;
                return ERROR;
            }
        },

        //String Literals
        slCHAR(TokenType.ERROR, false) {
            @Override
            public State next(char c) {
                switch (c){
                    case '\\': return slSPECIAL;
                    case '\"': return slEND;
                    case '\n': return slERROR;
                    default: return slCHAR;
                }
            }
        },
        slSPECIAL(TokenType.ERROR, false){
            @Override
            public State next(char c) {
                switch (c) {
                    case '\\':
                    case 'n':
                    case 'r':
                    case 't':
                    case '0':
                    case '\'':
                    case '\"':
                        return slCHAR;
                    default:
                        return ERROR;
                }
            }
        },
        slEND(TokenType.STRING_LITERAL, true){
            @Override
            public State next(char c){
                return ERROR; //if there is a char after \" it is invalid
            }
        },
        slERROR(TokenType.ERROR, true, true){
            @Override
            public State next(char c) {
                return ERROR;
            }
        },

        //Symbols
        // +
        PLUS(TokenType.PLUS, true) {
            @Override
            public State next(char c) {
                switch (c){
                    case '+': return INCREMENT;
                    case '=': return PLUS_ASSIGN;
                    default: return ERROR;
                }
            }
        },
        INCREMENT(TokenType.INCREMENT, true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
        PLUS_ASSIGN(TokenType.PLUS_ASSIGN, true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },

        // -
        MINUS(TokenType.MINUS, true) {
            @Override
            public State next(char c) {
                switch(c){
                    case '-': return DECREMENT;
                    case '=': return MINUS_ASSIGN;
                    default: return ERROR;
                }
            }
        },
        DECREMENT(TokenType.DECREMENT, true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
        MINUS_ASSIGN(TokenType.MINUS_ASSIGN, true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },

        // <
        LESS(TokenType.LESS, true) {
            @Override
            public State next(char c) {
                switch (c){
                    case '<': return LEFT_SHIFT;
                    case '=': return LESS_EQUAL;
                    default: return ERROR;
                }
            }
        },
        LEFT_SHIFT(TokenType.LEFT_SHIFT, true) {
            @Override
            public State next(char c) {
                switch (c){
                    case '=': return LEFT_SHIFT_ASSIGN;
                    default: return ERROR;
                }
            }
        },
        LEFT_SHIFT_ASSIGN(TokenType.LEFT_SHIFT_ASSIGN, true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
        LESS_EQUAL(TokenType.LESS_EQUAL, true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },

        // >
        GREATER(TokenType.GREATER, true) {
            @Override
            public State next(char c) {
                switch(c){
                    case '>': return RIGHT_SHIFT;
                    case '=': return GREATER_EQUAL;
                    default: return ERROR;
                }
            }
        },
        RIGHT_SHIFT(TokenType.RIGHT_SHIFT, true) {
            @Override
            public State next(char c) {
                switch (c){
                    case '=': return RIGHT_SHIFT_ASSIGN;
                    default: return ERROR;
                }
            }
        },
        RIGHT_SHIFT_ASSIGN(TokenType.RIGHT_SHIFT_ASSIGN, true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
        GREATER_EQUAL(TokenType.GREATER_EQUAL, true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },

        // =
        ASSIGN(TokenType.ASSIGN,true) {
            @Override
            public State next(char c) {
                switch (c){
                    case '=': return EQUAL;
                    default: return ERROR;
                }
            }
        },
        EQUAL(TokenType.EQUAL,true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },

        // *
        MULTIPLY(TokenType.MULTIPLY, true) {
            @Override
            public State next(char c) {
                switch (c){
                    case '=': return MULTIPLY_ASSIGN;
                    default: return ERROR;
                }
            }
        },
        MULTIPLY_ASSIGN(TokenType.MULTIPLY_ASSIGN, true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },

        // /
        DIVIDE(TokenType.DIVIDE, true) {
            @Override
            public State next(char c) {
                switch (c){
                    case '=': return DIVIDE_ASSIGN;
                    case '/': return COMMENT;
                    default: return ERROR;
                }
            }
        },
        DIVIDE_ASSIGN(TokenType.DIVIDE_ASSIGN, true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
        COMMENT(TokenType.ERROR, false){
            @Override
            public State next(char c) {
                switch(c) {
                    case '\n':
                    case '\r': return START;
                    default: return COMMENT;
                }
            }
        },

        // %
        MOD(TokenType.MOD, true) {
            @Override
            public State next(char c) {
                switch (c){
                    case '=': return MOD_ASSIGN;
                    default: return ERROR;
                }
            }
        },
        MOD_ASSIGN(TokenType.MOD_ASSIGN, true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
            // &
        AND(TokenType.AND, true) {
            @Override
            public State next(char c) {
                switch (c){
                    case '&': return CONDITIONAL_AND;
                    case '=': return AND_ASSIGN;
                    default: return ERROR;
                }
            }
        },
        CONDITIONAL_AND(TokenType.CONDITIONAL_AND, true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
        AND_ASSIGN(TokenType.AND_ASSIGN, true ) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
            // |
        OR(TokenType.OR, true) {
            @Override
            public State next(char c) {
                switch (c){
                    case '|': return CONDITIONAL_OR;
                    case '=': return OR_ASSIGN;
                    default: return ERROR;
                }
            }
        },
        CONDITIONAL_OR(TokenType.CONDITIONAL_OR, true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
        OR_ASSIGN(TokenType.OR_ASSIGN, true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
            // ~
        COMPLEMENT(TokenType.COMPLEMENT, true) {
            @Override
            public State next(char c) {
                switch (c){
                    case '=': return COMPLEMENT_ASSIGN;
                    default: return ERROR;
                }
            }
        },
        COMPLEMENT_ASSIGN(TokenType.COMPLEMENT_ASSIGN, true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
            // !
        NOT(TokenType.NOT, true) {
            @Override
            public State next(char c) {
                switch (c){
                    case '=': return NOT_EQUAL;
                    default: return ERROR;
                }
            }
        },
        NOT_EQUAL(TokenType.NOT_EQUAL, true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
            // ^
        XOR(TokenType.XOR, true) {
            @Override
            public State next(char c) {
                switch (c){
                    case '=': return XOR_ASSIGN;
                    default: return ERROR;
                }
            }
        },
        XOR_ASSIGN(TokenType.XOR_ASSIGN, true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
            // (
        OPEN_PAREN(TokenType.OPEN_PAREN, true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
            // )
        CLOSE_PAREN(TokenType.CLOSE_PAREN, true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
            // [
        OPEN_BRACKET(TokenType.OPEN_BRACKET, true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
            // ]
        CLOSE_BRACKET(TokenType.CLOSE_BRACKET, true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
            // {
        OPEN_CURL_BRACKET(TokenType.OPEN_CURL_BRACKET, true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
            // }
        CLOSE_CURL_BRACKET(TokenType.CLOSE_CURL_BRACKET, true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
            // .
        DOT(TokenType.DOT,true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
            // ,
        COMMA(TokenType.COMMA,true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
            // ;
        SEMICOLON(TokenType.SEMICOLON,true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
            // :
        COLON(TokenType.COLON,true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },

        PROGRAM(TokenType.PROGRAM,true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
        IF(TokenType.IF,true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
        ELSE(TokenType.ELSE,true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
        WHILE(TokenType.WHILE,true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
        INT(TokenType.INT,true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
        RETURN(TokenType.RETURN,true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
        CHAR(TokenType.CHAR,true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
        BOOLEAN(TokenType.BOOLEAN,true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
        VOID(TokenType.VOID,true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        },
        ERROR(TokenType.ERROR,false, true) {
            @Override
            public State next(char c) {
                return ERROR;
            }
        };

        private static boolean isHexChar(char c) {
            if (State.isDigit(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')) return true;
            return false;
        }

        private static boolean isDigit(char c) {
            if (c >= '0' && c <= '9') return true;
            return false;
        }

        public TokenType type;
        public boolean isFinal;
        public boolean isErrorState;

        private State(TokenType type, boolean isFinal) {
            this.type = type;
            this.isFinal = isFinal;
            this.isErrorState = false;
        }
        private State(TokenType type, boolean isFinal, boolean isErrorState) {
            this.type = type;
            this.isFinal = isFinal;
            this.isErrorState = isErrorState;
        }

        public abstract State next(char c);
    }

    private static int hexToInt(char c){
        if(c >= '0' && c <= '9') return c - '0';
        else if(c >= 'a' && c <= 'f') return c - 'a' + 10;
        else if(c >= 'A' && c <= 'F') return c - 'A' + 10;
        else throw new IllegalArgumentException("not a valid hex digit");
    }
    public boolean hasNextToken(){
        return !isEOF;
    }

    //token.Token Parsing
    private int line;
    private int col;
    private Scanner file;
    private String currentLine;
    private boolean isEOF;

    public ToyLexer(Scanner file){
        this.isEOF = false;
        this.file = file;
        this.line = 1;
        this.col = 0;
        this.currentLine = file.nextLine() + " \n";
    }

    public Token getNextToken(){
        State state = State.START;
        State lastFinalState = State.ERROR;
        int lastFinalLine = 0;
        int lastFinalCol = 0;

        //Possible return values
        int num = 0;
        String value = "";
        char val = 0;

        //Starts where the previous token ended, and iterates until an error state is reached
        //This stores the last final state reached, and also handles newlines and EOF detection
        while(!state.isErrorState){
            boolean sChar = false;
            char c = currentLine.charAt(col++);
            state = state.next(c);

            //Store values if necessary
            switch(state){
                case clCHAR:
                    if(sChar){
                        switch (c) {
                            case 'n':val = '\n'; break;
                            case 'r':val = '\r'; break;
                            case 't':val = '\t'; break;
                            case '0':val = '\0'; break;
                            default: throw new IllegalArgumentException();
                        }
                    }
                    else {
                        val = c;
                    }
                    break;
                case clSPECIAL:
                    sChar = true;
                    break;
                case slCHAR:
                    if(c != '\"') value += c;
                    break;
                case idUNDERSCORE:
                case idCHAR:
                    value += c;
                    break;
                case nlDIGIT: num = (num * 10) + (c - '0'); break;
                case nlHEX: num = (num * 16) + hexToInt(c); break;
            }

            //Store the latest position of a final state
            if(state.isFinal) {
                lastFinalState = state;
                lastFinalCol = col;
                lastFinalLine = line;
            }

            //On a newline, get the next line of the file
            // and adjust col & line vars
            if(c == '\n') {
                if(file.hasNextLine()) {
                    //TODO
                    currentLine = file.nextLine() + " \n"; //DON'T OBLITERATE THIS EARLY
                }else{
                    currentLine = (char)4 + ""; //Sets the next line to EOF, so FSM will read that next
                }
                col = 0;
                line++;
            }

            //Ensures that the last token returned is always the EOF token
            if(state.equals(State.EOF)){
                isEOF = true;
                return generateToken(state,line,col,num,val,value);
            }
        }


        //TODO
        //Rudimentary error checking, will find a better way soon
        if(lastFinalState.isErrorState) {
            int tempLine = line;
            int tempCol = col;
            this.line++;
            this.col = 0;
            currentLine = file.nextLine() + " \n";
            return new ErrorToken(lastFinalState.type,tempLine, tempCol, "not ideal");
        }

        line = lastFinalLine;
        col = lastFinalCol;

        return generateToken(lastFinalState,lastFinalLine,lastFinalCol,num,val,value);
    }

    public int getCurrentLineIndex(){return line;}

    //Helper method for getNextToken()
    private Token generateToken(State state, int l, int c, int num, char val, String value){
        switch (state.type){
            case IDENTIFIER:
                switch(value){
                    case "if": state = State.IF; break;
                    case "else": state = State.ELSE; break;
                    case "program": state = State.PROGRAM; break;
                    case "while": state =  State.WHILE; break;
                    case "int": state =  State.INT; break;
                    case "return": state =  State.RETURN; break;
                    case "char": state =  State.CHAR; break;
                    case "boolean": state =  State.BOOLEAN; break;
                    case "void": state =  State.VOID; break;
                    case "true": state = State.TRUE; break;
                    case "false": state = State.FALSE; break;
                    default: return new Identifier(state.type, l,c, value);
                }
                break;
            case NUMERIC_LITERAL:
            case HEXADECIMAL_LITERAL:
                return new token.Number(state.type,l,c,num);
            case STRING_LITERAL:
                return new Str(state.type,l,c,value);
            case CHARACTER_LITERAL:
                return new Char(state.type,l,c,val);
        }
        return new Token(state.type, l, c);
    }
}
