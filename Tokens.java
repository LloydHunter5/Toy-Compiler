
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


    public static final Tokens[] LITERALS =
            {STRING_LITERAL,
            CHARACTER_LITERAL,
            NUMERIC_LITERAL,
            HEXADECIMAL_LITERAL,
            TRUE,
            FALSE};

    public static final Tokens[] METHOD_TYPES =
            {INT,
            CHAR,
            BOOLEAN,
            VOID};
    public static final Tokens[] VARIABLE_TYPES =
            {INT,
            CHAR,
            BOOLEAN};
    public static final Tokens[] ASSIGNMENT_OPS =
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
    public static final Tokens[] MUL_OPS =
            {MULTIPLY,
            DIVIDE,
            MOD};
    public static final Tokens[] ADD_OPS =
            {PLUS,
            MINUS};
    public static final Tokens[] INCREMENT_OPS =
            {INCREMENT,
            DECREMENT};
    public static final Tokens[] OR_OPS =
            {OR,
            CONDITIONAL_OR,
            COMPLEMENT};

    public static final Tokens[] AND_OPS =
            {AND,
            CONDITIONAL_AND};
    public static final Tokens[] COMPARE_OPS =
            {LESS,
             LESS_EQUAL,
             GREATER,
             GREATER_EQUAL,
             EQUAL,
             NOT_EQUAL};
}

