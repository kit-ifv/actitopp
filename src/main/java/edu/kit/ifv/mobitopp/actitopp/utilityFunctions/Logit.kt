package edu.kit.ifv.mobitopp.actitopp.utilityFunctions
import jdk.jshell.execution.Util
import kotlin.math.exp

class Logit<X, P> : DistributionFunction<X, P> {

    override fun calculateProbabilities(evaluators: Map<X, Double>, parameters: P): Map<X, Double> {
        val currentExp = evaluators.entries.associate {
            it.key to
                exp(it.value)
        }
        val sum = currentExp.values.sum()

        return currentExp.mapValues { it.value / sum }
    }
}
class ChangableUtilityFunction<X, P>(private var utilityFunction: UtilityFunction<X, P>): UtilityFunction<X, P> {
    fun changeTo(new: UtilityFunction<X, P>) {
        utilityFunction = new
    }

    override fun calculateUtility(alternative: X, parameterObject: P): Double {
        return utilityFunction.calculateUtility(alternative, parameterObject)
    }

}

fun <X, P> UtilityFunction<X, P>.toMutableFunction(): ChangableUtilityFunction<X, P> {
    return ChangableUtilityFunction(this)
}
class AllocatedLogit<X : Any, SIT : ChoiceSituation<X>, P>(
    private val optionsMap: Map<X, ChangableUtilityFunction<SIT, P>>,
    override var rules: List<Pair<(SIT) -> Boolean, ChangableUtilityFunction<SIT, P>>>,
    override val name: String = "Unnamed allocated logit",

) : RuleBasedAssociation<X, SIT, P>, ModifiableDistributionFunction<X, SIT, P> {
    override val options = optionsMap.keys
    override val translation: Map<X, UtilityFunction<SIT, P>> = emptyMap()

    override fun calculateProbabilities(evaluators: Map<SIT, Double>, parameters: P): Map<SIT, Double> {
        return Logit<SIT, P>().calculateProbabilities(evaluators, parameters)
    }

    override fun translation(target: SIT): UtilityFunction<SIT, P> {
        return super<RuleBasedAssociation>.translation(target)
    }
    override fun modify(option: X, lambda: (UtilityFunction<SIT, P>) -> UtilityFunction<SIT, P>) {
        val originalUtilityFunction = optionsMap[option] ?: return
        val newFunction = lambda(originalUtilityFunction)
        originalUtilityFunction.changeTo(newFunction)
    }
    companion object {

        private const val DEFAULT_NAME = "Unnamed MNL model"

        class LogitBuilder<X : Any, SIT : ChoiceSituation<X>, PARAMS>(preknownOptions: Collection<X>) :
            OptionBasedSituationBuilder<X, SIT, PARAMS>, RuleBasedSituationBuilder<X, SIT, PARAMS> {
            val rules: MutableList<Pair<(SIT) -> Boolean, ChangableUtilityFunction<SIT, PARAMS>>> = mutableListOf()
            val options: MutableMap<X, ChangableUtilityFunction<SIT, PARAMS>> = preknownOptions.associateWith { UtilityFunction<SIT, PARAMS>{ _, _ -> 0.0}.toMutableFunction() }.toMutableMap()
            override fun addUtilityFunctionByIdentifier(x: X, utilityFunction: UtilityFunction<SIT, PARAMS>) {
                val mutableUtilityFunction = utilityFunction.toMutableFunction()
                rules.add({ sit: SIT -> sit.choice == x } to mutableUtilityFunction)
                options[x] = mutableUtilityFunction
            }

            override fun addUtilityFunctionByRule(
                rule: (SIT) -> Boolean,
                utilityFunction: UtilityFunction<SIT, PARAMS>
            ) {
                rules.add(rule to utilityFunction.toMutableFunction())
            }
        }

        fun <X : Any, SIT : ChoiceSituation<X>, PARAMS> create(
            options: Collection<X>,
            name: String = DEFAULT_NAME,
            lambda: LogitBuilder<X, SIT, PARAMS>.() -> Unit
        ): AllocatedLogit<X, SIT, PARAMS> {
            val builder = LogitBuilder<X, SIT, PARAMS>(options)
            builder.apply(lambda)

            return AllocatedLogit(builder.options, builder.rules, name = name)
        }

        fun <X : Any, SIT : ChoiceSituation<X>, PARAMS> create(
            name: String = DEFAULT_NAME,
            lambda: LogitBuilder<X, SIT, PARAMS>.() -> Unit
        ): AllocatedLogit<X, SIT, PARAMS> = create(emptySet(), name, lambda)
    }
}
