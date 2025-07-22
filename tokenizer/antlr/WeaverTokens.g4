lexer grammar WeaverTokens;

STRING_LITERAL: '"' (~["\\\r\n] | EscapeSequence)* '"';
SSTRING_LITERAL: '\'' (~['\\\r\n] | EscapeSequence)* '\'';

BOOLEAN_LITERAL : 'true' | 'false' ; // Boolean literals

IDENTIFIER      : [a-zA-Z_][a-zA-Z0-9_]* ;
NUMBER          : [0-9]+ ('.' [0-9]+)? ; // Allow decimals for numbers in computations
DOT             : '.' ;
OPEN_BRACE      : '{' ;
CLOSE_BRACE     : '}' ;
LT              : '<' ;
GT              : '>' ;
HASH            : '#' ;
OPEN_PAREN      : '(' ;
CLOSE_PAREN     : ')' ;
EQ              : '=' ;
PIPE            : '|' ;
OPEN_BRACKET    : '[' ;
CLOSE_BRACKET   : ']' ;
COMMA           : ',' ;
QUESTION_MARK   : '?' ;
COLON           : ':' ;
AMP_AMP         : '&&' ; // For BooleanComputation (bitops)
PIPE_PIPE       : '||' ; // For BooleanComputation (bitops)
EQ_EQ           : '==' ; // For BooleanComputation (equity)
GT_EQ           : '>=' ; // For BooleanComputation (equity)
LT_EQ           : '<=' ; // For BooleanComputation (equity)
BANG_EQ         : '!=' ; // For BooleanComputation (equity)
BANG            : '!' ;  // For BooleanComputation (negation)
PLUS            : '+' ;  // For NumberComputation
MINUS           : '-' ;  // For NumberComputation
STAR            : '*' ;  // For NumberComputation
SLASH           : '/' ;  // For NumberComputation
PERCENT         : '%' ;  // For NumberComputation

// RAW_STRING_FRAGMENT is for string interpolation parts. It must consume anything
// that isn't a brace or other specific token, and should be at the end of the lexer.
//RAW_STRING_FRAGMENT : (~('{'|'}'|'\n'|'\r'))+ ; // Matches any char except curly braces and newlines
// Note: This needs careful ordering in combined lexer. It's best defined in the
// specific grammar that uses it, and ensure it doesn't conflict with more specific tokens.

WS              : [ \t\r\n]+ -> skip;

fragment EscapeSequence:
    '\\' 'u005c'? [btnfr"'\\]
    | '\\' 'u005c'? ([0-3]? [0-7])? [0-7]
    | '\\' 'u'+ HexDigit HexDigit HexDigit HexDigit
;

fragment HexDigits: HexDigit ((HexDigit | '_')* HexDigit)?;

fragment HexDigit: [0-9a-fA-F];