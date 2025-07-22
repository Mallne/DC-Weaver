grammar WeaverObjectNotation;

import WeaverTokens;

// This rule defines the content that appears *inside* the curly braces {}
// It contains the "bare" Weaver-specific syntax components.
weaverContent
    : schemaPath                 #SchemaCallContent           // e.g., otherschema, otherschema.nested
    | HASH IDENTIFIER HASH           #LimboObjectCallContent      // e.g., <menuKey>
    | schemaPath DOT HASH IDENTIFIER HASH #LimboObjectSchemaCallContent // e.g., otherschema.<menuKey>
    | HASH HASH IDENTIFIER HASH HASH     #ParameterAccessContent      // e.g., <<param>>
    | HASH IDENTIFIER HASH OPEN_PAREN parameterList CLOSE_PAREN #LimboObjectCallWithParametersContent // e.g., <menuKey>(param=value)
    | schemaPath DOT HASH IDENTIFIER HASH OPEN_PAREN parameterList CLOSE_PAREN #LimboObjectSchemaCallWithParametersContent // e.g., otherschema.<menuKey>(param=value)
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