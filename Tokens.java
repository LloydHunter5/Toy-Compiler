
public enum Tokens {
    IDENTIFIER,
    
    //Literals
    NUMERIC_LITERAL,
    HEXADECIMAL_LITERAL,
    CHARACTER_LITERAL,
    STRING_LITERAL,
    STRING_LITERAL_ERROR,

    //Reserved Words
    PROGRAM,
    IF,
    ELSE,
    WHILE,
    RETURN,
    INT,
    CHAR,
    BOOLEAN,
    VOID,

    //Symbols
    //Unary
    NOT,      //!
    COMPLEMENT,//~
    INCREMENT,//++
    DECREMENT,//--
    //Binary
    PLUS,     //+
    MINUS,    //-
    MULTIPLY, //*
    DIVIDE,   ///
    MOD,      //%
    AND,      //&
    CONDITIONAL_AND,//&&
    OR,       //|
    CONDITIONAL_OR,//||
    XOR,      //^
    LEFT_SHIFT,//<<
    RIGHT_SHIFT,//>>
    //Relational
    LESS,       //<
    LESS_EQUAL, //<=
    GREATER,    //>
    GREATER_EQUAL,//>=
    EQUAL,     //==
    NOT_EQUAL, //!=
    //Assignment
    ASSIGN,
    PLUS_ASSIGN,
    MINUS_ASSIGN,
    MULTIPLY_ASSIGN,
    DIVIDE_ASSIGN,
    MOD_ASSIGN,
    AND_ASSIGN,
    OR_ASSIGN,
    XOR_ASSIGN,
    COMPLEMENT_ASSIGN,
    LEFT_SHIFT_ASSIGN,
    RIGHT_SHIFT_ASSIGN,

    //Bracketing
    OPEN_PAREN,
    CLOSE_PAREN,
    OPEN_BRACKET,
    CLOSE_BRACKET,
    OPEN_CURL_BRACKET,
    CLOSE_CURL_BRACKET,

    //Punctuation
    DOT,
    COMMA,
    SEMICOLON,
    COLON,

    //MISC
    END, // <END>
    ERROR, // <ERROR>
    EOF,

    //Boolean values
    TRUE,
    FALSE;


}

