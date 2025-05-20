package edu.kit.ifv.mobitopp.actitopp.utilityFunctions

/**
 * A utility function takes in an alternative and a parameter object and returns the utility of said alternative.
 */
fun interface UtilityFunction<SIT, PARAMS> {
    fun calculateUtility(alternative: SIT, parameterObject: PARAMS): Double
}

/**
 * An allocated function knows what options are available
 */
interface OptionDistributionFunction<X : Any, SIT : ChoiceSituation<X>, PARAMS> :
    ExtractableDistributionFunction<X, SIT, PARAMS> {
    val options: Set<X> get() = translation.keys
    val translation: Map<X, UtilityFunction<SIT, PARAMS>>
    override fun translation(target: SIT): UtilityFunction<SIT, PARAMS> = translation(target.choice)

    fun translation(target: X): UtilityFunction<SIT, PARAMS> = translation.getOrElse(target) {
        throw NoSuchElementException("There is no utility function for $target")
    }
}

/**
 * If we have this class we have the ability to predetermine the utility function for a given situation SIT
 */
interface ExtractableDistributionFunction<X : Any, SIT : ChoiceSituation<X>, PARAMS> :
    DistributionFunction<SIT, PARAMS> {
    val name get() = "Unnamed Distribution Function"
    fun translation(target: SIT): UtilityFunction<SIT, PARAMS>
    fun calculateProbabilities(alternatives: Set<SIT>, parameters: PARAMS): Map<SIT, Double> {
        return calculateProbabilities(
            alternatives.associateWith { translation(it).calculateUtility(it, parameters) },
            parameters
        )
    }

    fun calculateProbabilitiesInjected(
        alternatives: Set<InjectedSituation<SIT>>,
        parameters: PARAMS,
    ): Map<SIT, Double> {
        return calculateProbabilities(
            alternatives.associate {
                it.situation to
                        it.injection(translation(it.situation).calculateUtility(it.situation, parameters))

            },
            parameters
        )
    }

}

data class InjectedSituation<SIT>(
    val situation: SIT,
    val injection: (Double) -> Double = {it},
)

interface ModifiableDistributionFunction<X : Any, SIT : ChoiceSituation<X>, PARAMS> :
    OptionDistributionFunction<X, SIT, PARAMS> {
    fun modify(option: X, lambda: (UtilityFunction<SIT, PARAMS>) -> UtilityFunction<SIT, PARAMS>)
}

fun <X : Any, SIT : ChoiceSituation<X>, PARAMS> ExtractableDistributionFunction<X, SIT, PARAMS>.calculateDebug(
    alternatives: Set<SIT>,
    parameters: PARAMS,
    callback: (Map<SIT, Double>) -> Unit = {
    },
): Map<SIT, Double> {
    return calculateProbabilities(alternatives.associateWith {
        translation(it).calculateUtility(it, parameters)
    }.also { callback(it) }, parameters)
}

/**
 * A distribution function takes in a collection of situations with their associated utility functions already calculated,
 * a parameter object and returns a map of calculated probabilities from the given alternatives
 */
fun interface DistributionFunction<SIT, PARAMS> {
    fun calculateProbabilities(
        evaluators: Map<SIT, Double>,
        parameters: PARAMS,
    ): Map<SIT, Double>


}

fun interface UtilityFunctionAssociation<SIT, PARAMS> {
    fun associateFunction(to: SIT): UtilityFunction<SIT, PARAMS>
}

interface MapBasedAssociation<SIT, PARAMS> : UtilityFunctionAssociation<SIT, PARAMS> {
    val map: Map<SIT, UtilityFunction<SIT, PARAMS>>
    override fun associateFunction(to: SIT): UtilityFunction<SIT, PARAMS> {
        return map[to] ?: throw NoSuchElementException("No utility function located for $to")
    }
}

interface RuleBasedAssociation<X : Any, SIT : ChoiceSituation<X>, PARAMS> :
    UtilityFunctionAssociation<SIT, PARAMS>,
    ExtractableDistributionFunction<X, SIT, PARAMS> {
    val rules: List<Pair<(SIT) -> Boolean, UtilityFunction<SIT, PARAMS>>>
    override fun associateFunction(to: SIT): UtilityFunction<SIT, PARAMS> {
        val firstMatchingRule = rules.firstOrNull { it.first.invoke(to) }
            ?: throw NoSuchElementException(
                "The choice model: [$name] cannot associate the target element " +
                        "${to.choice} to a utility function. Is the option defined in the choice model?"
            )
        return firstMatchingRule.second
    }

    override fun translation(target: SIT): UtilityFunction<SIT, PARAMS> {
        return associateFunction(target)
    }
}

class UtilityCollector<X: Any, SIT:ChoiceSituation<X>, PARAMS>(
    val originalFunctions: (X) -> UtilityFunction<SIT, PARAMS>,
) {
    val registeredUtilityFunctions = mutableMapOf<X, UtilityFunction<SIT, PARAMS>>()

    fun option(option: X) {
        registeredUtilityFunctions[option] = originalFunctions(option)
    }
    fun option(option: X, modification: (UtilityFunction<SIT, PARAMS>) -> UtilityFunction<SIT, PARAMS>) {
        registeredUtilityFunctions[option] = modification(originalFunctions(option))
    }
}
fun <X: Any, SIT:ChoiceSituation<X>, PARAMS> OptionDistributionFunction<X, SIT, PARAMS>.selectNew(
    converter: (X) -> SIT,
    parameters: PARAMS,
    lambda: UtilityCollector<X, SIT, PARAMS>.() -> Unit): Map<SIT, Double> {
    val collector = UtilityCollector(::translation)
    collector.apply(lambda)


    return calculateProbabilities(collector.registeredUtilityFunctions.entries.associate {
        val situation = converter(it.key)
        situation to it.value.calculateUtility(situation, parameters)
    }, parameters)

}

fun <X: Any, SIT:ChoiceSituation<X>, PARAMS> ModifiableDiscreteChoiceModel<X, SIT, PARAMS>.selectNew(
    randomNumber: Double,
    converter: (X) -> SIT,
    parameters: PARAMS,
    collector: UtilityCollector<X, SIT, PARAMS>.() -> Unit
): X {
    return SelectionFunction<SIT> { it.select(randomNumber) }.calculateSelection(distributionFunction.selectNew(converter, parameters, collector)).choice

}

fun <X: Any, SIT:ChoiceSituation<X>, PARAMS> ParametrizedDiscreteChoiceModel<X, SIT, PARAMS>.selectNew(
    randomNumber: Double,
    converter: (X) -> SIT,
    collector: UtilityCollector<X, SIT, PARAMS>.() -> Unit
): X {
    return original.selectNew(randomNumber, converter, parameters, collector)
}