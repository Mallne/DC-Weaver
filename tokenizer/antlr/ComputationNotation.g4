grammar ComputationNotation;

import WeaverObjectNotation;

baseComputationExpression
    : NUMBER #NumberLiteral
    | STRING_LITERAL #StringLiteral
    | SSTRING_LITERAL #SStringLiteral
    | BOOLEAN_LITERAL #BooleanLiteral
    | accessorAccess #AccessorAccessExpr
    | PIPE baseComputationExpression PIPE IDENTIFIER PIPE #TopLevelCoercion // New: |any_comp_expr|type|
    | IDENTIFIER OPEN_PAREN functionParameterList CLOSE_PAREN #FunctionInvocationExpr
    | OPEN_PAREN baseComputationExpression CLOSE_PAREN #ParenBaseComputationExpr
    //Boolean Expressions
    | BANG baseComputationExpression #NegatedCompute
    | first=baseComputationExpression BOOLEAN_OPERATIONS second=baseComputationExpression #BooleanOperatorExpr
    | first=baseComputationExpression EQUALITY_OPERATIONS second=baseComputationExpression #EqualityOperatorExpr
    //Operator Expressions
    | first=baseComputationExpression ARITHMETIC_OPERATIONS second=baseComputationExpression #ArithmeticOperatorExpr
    //Ternary
    | condition=baseComputationExpression QUESTION_MARK trueBranch=baseComputationExpression COLON falseBranch=baseComputationExpression #FullTernary
    // Kotlin-like shorthand: value ?: other
    // Semantically resolves to |value|ab| ? value : other
    | value=baseComputationExpression QUESTION_MARK COLON other=baseComputationExpression #AvailabilityTernaryShorthand
    ;

BOOLEAN_OPERATIONS: AMP_AMP | PIPE_PIPE;
EQUALITY_OPERATIONS: EQ_EQ | BANG_EQ | LT_EQ | LT | GT_EQ | GT;
ARITHMETIC_OPERATIONS: PLUS | MINUS | STAR | SLASH | PERCENT;

functionParameterList
    : baseComputationExpression (COMMA baseComputationExpression)*
    ;

accessorAccess
    : OPEN_BRACE weaverContent CLOSE_BRACE #AccessorExpr
    | OPEN_BRACE CLOSE_BRACE #CurrentAccessorExpr
    ;

