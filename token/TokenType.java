package token;

public enum TokenType {
    IDENTIFIER,
    
    //Literals
    NUMERIC_LITERAL,
    HEXADECIMAL_LITERAL,
    CHARACTER_LITERAL,
    STRING_LITERAL,

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

    //Errors
    ERROR,
    STRING_LITERAL_ERROR,
    CHAR_LITERAL_ERROR,


    //MISC
    END, // <END>
    EOF,

    //Boolean values
    TRUE,
    FALSE;


    public static final TokenType[] LITERALS =
            {STRING_LITERAL,
            CHARACTER_LITERAL,
            NUMERIC_LITERAL,
            HEXADECIMAL_LITERAL,
            TRUE,
            FALSE};

    public static final TokenType[] METHOD_TYPES =
            {INT,
            CHAR,
            BOOLEAN,
            VOID};
    public static final TokenType[] VARIABLE_TYPES =
            {INT,
            CHAR,
            BOOLEAN};
    public static final TokenType[] ASSIGNMENT_OPS =
            {ASSIGN,
            OR_ASSIGN,
            XOR_ASSIGN,
            COMPLEMENT_ASSIGN,
            LEFT_SHIFT_ASSIGN,
            RIGHT_SHIFT_ASSIGN,
            AND_ASSIGN,
            MULTIPLY_ASSIGN,
            DIVIDE_ASSIGN,
            MINUS_ASSIGN,
            PLUS_ASSIGN,
            MOD_ASSIGN};
    public static final TokenType[] MUL_OPS =
            {MULTIPLY,
            DIVIDE,
            MOD};
    public static final TokenType[] ADD_OPS =
            {PLUS,
            MINUS};
    public static final TokenType[] INCREMENT_OPS =
            {INCREMENT,
            DECREMENT};
    public static final TokenType[] OR_OPS =
            {OR,
            CONDITIONAL_OR,
            COMPLEMENT};

    public static final TokenType[] AND_OPS =
            {AND,
            CONDITIONAL_AND};
    public static final TokenType[] COMPARE_OPS =
            {LESS,
             LESS_EQUAL,
             GREATER,
             GREATER_EQUAL,
             EQUAL,
             NOT_EQUAL};
}

