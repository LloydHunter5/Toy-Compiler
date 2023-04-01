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

    public static TypeAnnotation convertType(VariableTypes vType, boolean isIndexed){
        if(isIndexed){
            return switch (vType){
                case INT,aINT -> INT;
                case CHAR,aCHAR -> CHAR;
                case BOOL,aBOOL -> BOOL;
            };
        }else{
            return switch (vType){
                case INT -> INT;
                case CHAR -> CHAR;
                case BOOL -> BOOL;
                case aINT -> aINT;
                case aBOOL -> aBOOL;
                case aCHAR -> aCHAR;
            };
        }

    }


}
