package cloud.mallne.dicentra.weaver.core.execution.command

import cloud.mallne.dicentra.weaver.core.execution.command.CommonCommands.ScopeKeys.COMMONDENOM
import cloud.mallne.dicentra.weaver.core.model.*
import cloud.mallne.dicentra.weaver.core.model.json.*
import cloud.mallne.dicentra.weaver.core.specification.ObjectType
import cloud.mallne.dicentra.weaver.core.specification.compute.*
import cloud.mallne.dicentra.weaver.core.specification.compute.expression.resolve
import cloud.mallne.dicentra.weaver.language.ast.expressions.TypeCoercion
import kotlinx.serialization.json.JsonElement

object ComputationCommand : ConvenientWeaverCommand {
    override val name = "ComputationDeclaration"

    object Keys {
        val computation = WeaverCommandKey(ComputationDeclaration::class, COMMONDENOM + "ComputationDeclaration")
        val notation = WeaverCommandKey(WeaverObjectNotationInvocation::class, CommonCommands.ScopeKeys.NOTATION)
        val input = WeaverCommandKey(JsonElement::class, CommonCommands.ScopeKeys.INPUT_ELEMENT)
        val yield = WeaverCommandKey(MutableJson::class, CommonCommands.ScopeKeys.OUTPUT_MUTABLE_ELEMENT)
    }

    private const val ACCESSOR_DESTRUCTURING_VALUE = "value"
    private const val ACCESSOR_DESTRUCTURING_KEY = "key"
    private const val ACCESSOR_DESTRUCTURING_INDEX = "index"

    override fun execute(context: WeaverContext, dispatcher: CommandDispatcher) {
        val computation = context.get(Keys.computation)
        val notation = context.get(Keys.notation)
        val input = context.get(Keys.input)

        val yield = when (computation) {
            is ComputeDeclaration -> performCommonComputation(context, computation, dispatcher, notation, input)
            is ArrayComputationDeclaration -> performArrayComputation(dispatcher, context, computation, notation, input)
            is MapComputationDeclaration -> performMapComputation(dispatcher, context, computation, notation, input)
            is ObjectComputationDeclaration -> performObjectComputation(
                context,
                dispatcher,
                computation,
                notation,
                input
            )
        }

        context.log("$name:Resolved") {
            debug("Resolved Computation $computation to $yield")
        }

        context.put(Keys.yield, yield)
    }

    private fun performObjectComputation(
        context: WeaverContext,
        dispatcher: CommandDispatcher,
        computation: ObjectComputationDeclaration,
        notation: WeaverObjectNotationInvocation,
        input: JsonElement
    ): MutableJson {
        return if (computation.nested != null) {
            val kvMap = computation.nested.map {
                val won = it.content.resolve(notation.schemaRoot, notation.limboObject)
                val obj = ObjectNotationCommand.thisCommand(won, input, context, dispatcher)
                it.name to obj
            }
            MutableJsonObject(kvMap.toMap().toMutableMap())
        } else {
            AccessorCommand.thisCommand(input, notation.limboObject.accessorAst, context, dispatcher)
        }
    }

    private fun performArrayComputation(
        dispatcher: CommandDispatcher,
        context: WeaverContext,
        computation: ArrayComputationDeclaration,
        notation: WeaverObjectNotationInvocation,
        input: JsonElement
    ): MutableJsonArray {
        val countObj = WeaverExpressionCommand.thisCommand(dispatcher, context, computation.loopAst, notation, input)
        val count = countObj.asIntOrNull() ?: CoerceCommand.thisCommand(
            dispatcher,
            context,
            countObj,
            TypeCoercion.Type.Integer
        ).asIntOrNull() ?: 0

        context.log("$name:ArrayConstruction:LoopSize") {
            trace("The WON ${computation.loop} will create a loop with $count iterations.")
        }

        val repeatsUsesAccessor = repeatsUsesAccessor(computation.repeat)
        val list: List<Pair<String?, MutableJson>> = (if (repeatsUsesAccessor) {
            val acc = notation.limboObject.accessorAst
            val accessorObject = AccessorCommand.thisCommand(input, acc, context, dispatcher)
            when (accessorObject.type()) {
                ObjectType.List -> accessorObject.asListOrNull()?.mapIndexed { ind, obj -> ind.toString() to obj }
                    ?: emptyList()

                ObjectType.Map -> accessorObject.asMapOrNull()?.map { it.key to it.value }
                    ?: emptyList()

                ObjectType.Object -> accessorObject.asMapOrNull()?.map { it.key to it.value } ?: emptyList()

                ObjectType.String -> accessorObject.asStringOrNull()
                    ?.mapIndexed { ind, obj -> ind.toString() to MutableJsonChar(obj) }
                    ?: emptyList()

                else -> listOf(null to accessorObject)
            }
        } else emptyList())

        if (repeatsUsesAccessor) {
            context.log("$name:ArrayConstruction:UsesAccessor") {
                trace("The WON ${computation.repeat} makes use of the Accessor, prechopping into $list.")
            }
        }

        val y2 = MutableJsonArray()

        for (i in 0..count) {
            val value = if (list.size == 1 && list.first().first == null) {
                list.first()
            } else {
                list.getOrNull(i)
            }
            if (value != null) {
                context.put(
                    WeaverCommandKey(
                        MutableJson::class,
                        CommonCommands.ScopeKeys.PARAMDENOM + ACCESSOR_DESTRUCTURING_VALUE
                    ), value.second
                )
                value.first?.let {
                    context.put(
                        WeaverCommandKey(
                            MutableJson::class,
                            CommonCommands.ScopeKeys.PARAMDENOM + ACCESSOR_DESTRUCTURING_KEY
                        ), MutableJsonString(it)
                    )
                }
            }
            context.put(
                WeaverCommandKey(
                    MutableJson::class,
                    CommonCommands.ScopeKeys.PARAMDENOM + ACCESSOR_DESTRUCTURING_INDEX
                ), MutableJsonNumber(i)
            )
            y2.add(WeaverExpressionCommand.thisCommand(dispatcher, context, computation.repeatAst, notation, input))
        }
        return y2
    }

    private fun repeatsUsesAccessor(wol: String): Boolean =
        wol.contains("##$ACCESSOR_DESTRUCTURING_VALUE##") || wol.contains("##$ACCESSOR_DESTRUCTURING_KEY##")

    private fun performMapComputation(
        dispatcher: CommandDispatcher,
        context: WeaverContext,
        computation: MapComputationDeclaration,
        notation: WeaverObjectNotationInvocation,
        input: JsonElement
    ): MutableJsonObject {
        val countObj = WeaverExpressionCommand.thisCommand(dispatcher, context, computation.loopAst, notation, input)
        val count = countObj.asIntOrNull() ?: CoerceCommand.thisCommand(
            dispatcher,
            context,
            countObj,
            TypeCoercion.Type.Integer
        ).asIntOrNull() ?: 0

        context.log("$name:MapConstruction:LoopSize") {
            trace("The WON ${computation.loop} will create a loop with $count iterations.")
        }

        val repeatsUsesAccessor =
            repeatsUsesAccessor(computation.keyRepeat) || repeatsUsesAccessor(computation.valueRepeat)
        val list: List<Pair<String?, MutableJson>> = (if (repeatsUsesAccessor) {
            val acc = notation.limboObject.accessorAst
            val accessorObject = AccessorCommand.thisCommand(input, acc, context, dispatcher)
            when (accessorObject.type()) {
                ObjectType.List -> accessorObject.asListOrNull()?.mapIndexed { ind, obj -> ind.toString() to obj }
                    ?: emptyList()

                ObjectType.Map -> accessorObject.asMapOrNull()?.map { it.key to it.value }
                    ?: emptyList()

                ObjectType.Object -> accessorObject.asMapOrNull()?.map { it.key to it.value } ?: emptyList()

                ObjectType.String -> accessorObject.asStringOrNull()
                    ?.mapIndexed { ind, obj -> ind.toString() to MutableJsonChar(obj) }
                    ?: emptyList()

                else -> listOf(null to accessorObject)
            }
        } else emptyList())

        if (repeatsUsesAccessor) {
            context.log("$name:MapConstruction:UsesAccessor") {
                trace("The WON makes use of the Accessor, prechopping into $list.")
            }
        }

        val y2 = MutableJsonObject()

        for (i in 0..count) {
            val value = if (list.size == 1 && list.first().first == null) {
                list.first()
            } else {
                list.getOrNull(i)
            }
            if (value != null) {
                context.put(
                    WeaverCommandKey(
                        MutableJson::class,
                        CommonCommands.ScopeKeys.PARAMDENOM + ACCESSOR_DESTRUCTURING_VALUE
                    ), value.second
                )
                value.first?.let {
                    context.put(
                        WeaverCommandKey(
                            MutableJson::class,
                            CommonCommands.ScopeKeys.PARAMDENOM + ACCESSOR_DESTRUCTURING_KEY
                        ), MutableJsonString(it)
                    )
                }
            }
            context.put(
                WeaverCommandKey(
                    MutableJson::class,
                    CommonCommands.ScopeKeys.PARAMDENOM + ACCESSOR_DESTRUCTURING_INDEX
                ), MutableJsonNumber(i)
            )
            val keyObject =
                WeaverExpressionCommand.thisCommand(dispatcher, context, computation.keyRepeatAst, notation, input)
            val key = keyObject.asStringOrNull() ?: CoerceCommand.thisCommand(
                dispatcher,
                context,
                keyObject,
                TypeCoercion.Type.String
            ).toString()
            val valueObject =
                WeaverExpressionCommand.thisCommand(dispatcher, context, computation.valueRepeatAst, notation, input)
            y2.put(key, valueObject)
        }
        return y2
    }

    private fun performCommonComputation(
        context: WeaverContext,
        computation: ComputeDeclaration,
        dispatcher: CommandDispatcher,
        notation: WeaverObjectNotationInvocation,
        input: JsonElement
    ): MutableJson {
        context.log("$name:Construct") {
            trace("Constructing from ${computation.compute}")
        }
        return WeaverExpressionCommand.thisCommand(dispatcher, context, computation.computeAst, notation, input)
    }

    fun thisCommand(
        computation: ComputationDeclaration,
        input: JsonElement,
        notation: WeaverObjectNotationInvocation,
        context: WeaverContext,
        dispatcher: CommandDispatcher,
    ): MutableJson {
        val res = dispatcher.dispatch(
            name,
            context,
            Keys.computation.holder(computation),
            Keys.input.holder(input),
            Keys.notation.holder(notation),
        )
        return if (res is CommandDispatcher.Result.Failure) {
            throw res.throwable
        } else {
            context.get(Keys.yield)
        }
    }
}