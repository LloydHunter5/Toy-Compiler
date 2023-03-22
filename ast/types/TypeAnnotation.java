package ast.types;

public enum TypeAnnotation {
    INT,
    CHAR,
    BOOL,
    VOID,
    aINT,
    aCHAR,
    aBOOL;

    public static TypeAnnotation convertType(MethodTypes mType){
        return switch (mType){
            case INT -> INT;
            case CHAR -> CHAR;
            case BOOL -> BOOL;
            case VOID -> VOID;
            case aINT -> aINT;
            case aBOOL -> aBOOL;
            case aCHAR -> aCHAR;
        };
    }

    public static TypeAnnotation convertType(ParameterTypes pType){
        return switch (pType){
            case INT -> INT;
            case CHAR -> CHAR;
            case BOOL -> BOOL;
            case aINT -> aINT;
            case aBOOL -> aBOOL;
            case aCHAR -> aCHAR;
        };
    }

    public static TypeAnnotation convertType(VariableTypes vType){
        return switch (vType){
            case INT,aINT -> INT;
            case CHAR,aCHAR -> CHAR;
            case BOOL,aBOOL -> BOOL;
        };
    }


}
