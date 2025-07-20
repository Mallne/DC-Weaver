grammar AccessorWeaverObjectLanguage;

import WeaverObjectNotation;
// Parser Rules
// -----------------------------------------------------------------------------

// The top-level rule for an Accessor WOL expression.
// It can be direct object access (pathAccess), array access, or a Weaver expression wrapped in braces.
accessorExpression
    : pathAccess                 #PathAccessExpr          // e.g., menu.id, menu.{schema}.field[1]
    | arrayAccess                #DirectArrayAccessExpr   // e.g., [{schema}] (array access as root)
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