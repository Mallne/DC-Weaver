grammar WeaverObjectNotation;

// This rule defines the content that appears *inside* the curly braces {}
// It contains the "bare" Weaver-specific syntax components.
weaverContent
    : schemaPath                 #SchemaCallContent           // e.g., otherschema, otherschema.nested
    | LT IDENTIFIER GT           #LimboObjectCallContent      // e.g., <menuKey>
    | schemaPath DOT LT IDENTIFIER GT #LimboObjectSchemaCallContent // e.g., otherschema.<menuKey>
    | LT LT IDENTIFIER GT GT     #ParameterAccessContent      // e.g., <<param>>
    | LT IDENTIFIER GT OPEN_PAREN parameterList CLOSE_PAREN #LimboObjectCallWithParametersContent // e.g., <menuKey>(param=value)
    | schemaPath DOT LT IDENTIFIER GT OPEN_PAREN parameterList CLOSE_PAREN #LimboObjectSchemaCallWithParametersContent // e.g., otherschema.<menuKey>(param=value)
    | coercionPart #TypeCoercionWrapper
    ;

// Defines a dot-separated path of identifiers for schemas (used by schema calls and LimboObjects in schemas)
schemaPath
    : IDENTIFIER (DOT IDENTIFIER)*
    ;

// List of parameters for LimboObjectCallWithParameters and LimboObjectSchemaCallWithParameters
parameterList
    : parameter (COMMA parameter)*
    ;

// A single parameter definition
parameter
    : IDENTIFIER EQ weaverContent // Parameter name = accessor expression value
    ;

coercionPart
    : PIPE weaverContent PIPE IDENTIFIER PIPE
    ;

// Lexer Rules
// -----------------------------------------------------------------------------
IDENTIFIER      : [a-zA-Z_][a-zA-Z0-9_]* ;
NUMBER          : [0-9]+ ;
DOT             : '.' ;
OPEN_BRACE      : '{' ;
CLOSE_BRACE     : '}' ;
LT              : '<' ;
GT              : '>' ;
OPEN_PAREN      : '(' ;
CLOSE_PAREN     : ')' ;
EQ              : '=' ;
PIPE            : '|' ;
OPEN_BRACKET    : '[' ;
CLOSE_BRACKET   : ']' ;
COMMA           : ',' ;

WS              : [ \t\r\n]+ -> skip;