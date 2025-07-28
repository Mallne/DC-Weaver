package cloud.mallne.dicentra.weaver.core.execution.command

object CommonCommands {
    val baseCommands = listOf(
        AccessorCommand,
        ArithmeticOperatorCommand,
        ArrayAccessCommand,
        BooleanOperatorCommand,
        CoerceCommand,
        ComputationCommand,
        EqualityOperatorCommand,
        InterpolatedPartCommand,
        ObjectNotationCommand,
        WeaverExpressionCommand,
        FunctionCallCommand
    )

    object ScopeKeys {
        const val COMMONDENOM = "common:cloud.mallne.dicentra.weaver:"
        const val PARAMDENOM = "param:"
        const val INPUT_ELEMENT = COMMONDENOM + "input.JsonElement"
        const val NOTATION = COMMONDENOM + "Notation"
        const val OPERATOR = COMMONDENOM + "Operator"
        const val LEFT_EXPRESSION = COMMONDENOM + "Expression.left"
        const val EXPRESSION = COMMONDENOM + "Expression"
        const val RIGHT_EXPRESSION = COMMONDENOM + "Expression.right"
        const val INPUT_MUTABLE_ELEMENT = COMMONDENOM + "input.MutableJson"
        const val OUTPUT_ELEMENT = COMMONDENOM + "output.JsonElement"
        const val OUTPUT_MUTABLE_ELEMENT = COMMONDENOM + "output.MutableJson"
    }
}