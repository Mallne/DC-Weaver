grammar AccessorWeaverObjectLanguage;
// Parser Rules
// -----------------------------------------------------------------------------

// The top-level rule for an Accessor WOL expression.
// It can be direct object access (pathAccess), array access, or a Weaver expression wrapped in braces.
accessorExpression
    : pathAccess                 #PathAccessExpr          // e.g., menu.id, menu.{schema}.field[1]
    | arrayAccess                #DirectArrayAccessExpr   // e.g., [{schema}] (array access as root)
    | OPEN_BRACE weaverContent CLOSE_BRACE #WeaverExpressionWrapper // Catches all Weaver-specific calls like {<key>} or {schema.name}
    ;

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

// A path expression, consisting of segments separated by dots, optionally ending with an array access
pathAccess
    : pathElement (DOT pathElement)*
    ;

// A path element is a segment potentially followed by an array access (e.g., 'nested[1]')
pathElement
    : pathSegment (arrayAccess)?
    ;

// A path segment can be a sequence of interpolated parts (e.g., 'menu{item}s')
pathSegment
    : interpolatedPathPart+
    ;

// An individual part within a path segment that can be interpolated
interpolatedPathPart
    : IDENTIFIER                  #IdentifierInterpolation   // e.g., 'menu', 's', 'nested'
    // This expects an accessorExpression (recursive call) because it's an embedded expression
    | OPEN_BRACE weaverContent CLOSE_BRACE #ExpressionInterpolation // e.g., '{item}'
    ;

// An array access, consisting of brackets enclosing an index expression
arrayAccess
    : OPEN_BRACKET NUMBER CLOSE_BRACKET                                    #LiteralArrayIndex
    // This expects an accessorExpression (recursive call) for the computed index
    | OPEN_BRACKET OPEN_BRACE weaverContent CLOSE_BRACE CLOSE_BRACKET #ComputedArrayIndexExpr
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

// Lexer Rules (These remain unchanged)
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