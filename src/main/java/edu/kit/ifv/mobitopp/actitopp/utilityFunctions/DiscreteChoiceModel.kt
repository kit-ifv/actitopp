package edu.kit.ifv.mobitopp.actitopp.utilityFunctions

import kotlin.random.Random

fun interface SelectionFunction<X> {
    fun calculateSelection(options: Map<X, Double>): X
}

open class DiscreteChoiceModel<X : Any, SIT : ChoiceSituation<X>, P>(
    protected open val distributionFunction: ExtractableDistributionFunction<X, SIT, P>,
    protected open val selectionFunction: SelectionFunction<SIT> = SelectionFunction {
        it.select(
            GlobalRandomizer.nextDouble()
        )
    },
) {

    var callback: (Map<SIT, Double>) -> Unit = {
//        it.keys.forEach { k -> println(k) }
//        println(it.entries.map { it.key.choice to it.value })
    }

    fun select(alternatives: Set<SIT>, parameters: P): X {
        return selectionFunction.calculateSelection(
            distributionFunction.calculateDebug(
                alternatives,
                parameters
            ).also { callback(it) }
        ).choice
    }

    fun select(alternatives: Set<SIT>, parameters: P, randomNumber: Double): X {
        return SelectionFunction<SIT> { it.select(randomNumber) }.calculateSelection(
            distributionFunction.calculateProbabilities(
                alternatives,
                parameters
            )
        ).choice
    }

    fun selectDebug(alternatives: Set<SIT>, parameters: P, callback: (Map<SIT, Double>) -> Unit): X {
        return selectionFunction.calculateSelection(
            distributionFunction.calculateDebug(
                alternatives,
                parameters,
                callback
            ).also { callback(it) }
        ).choice
    }

    fun select(singularOption: SIT, parameters: P): X = select(setOf(singularOption), parameters)

    fun probabilities(alternatives: Set<SIT>, parameters: P) = distributionFunction.calculateProbabilities(
        alternatives,
        parameters
    )

    fun utility(
        alternative: SIT,
        parameters: P,
    ) = distributionFunction.translation(alternative).calculateUtility(alternative, parameters)

}

class ModifiableDiscreteChoiceModel<X : Any, SIT : ChoiceSituation<X>, P>(
    override val distributionFunction: ModifiableDistributionFunction<X, SIT, P>,
    override var selectionFunction: SelectionFunction<SIT> = SelectionFunction {
        it.select(
            GlobalRandomizer.nextDouble()
        )
    },
) : DiscreteChoiceModel<X, SIT, P>(
    distributionFunction,
    selectionFunction = selectionFunction
) {
    fun modify(option: X, lambda: (UtilityFunction<SIT, P>) -> UtilityFunction<SIT, P>) {
        distributionFunction.modify(option, lambda)
    }

    fun utilities(parameters: P, converter: (X) -> SIT): Map<X, Double> {
        return utilities(distributionFunction.options, parameters, converter)
    }

    fun utilities(options: Set<X>, parameters: P, converter: (X) -> SIT): Map<X, Double> {
        return options.associateWith {
            val alternative = converter(it)
            distributionFunction.translation(alternative).calculateUtility(alternative, parameters)
        }
    }


    fun select(parameters: P, randomNumber: Double, situation: (X) -> SIT): X {
        return select(distributionFunction.options, parameters, randomNumber, situation)
    }

    fun select(options: Set<X>, parameters: P, randomNumber: Double, situation: (X) -> SIT): X {
        return select(options.map { situation(it) }.toSet(), parameters, randomNumber)
    }

    fun selectInjected(parameters: P, situation: (X) -> SIT, injections: Map<X, (Double) -> Double>): X {
        return selectionFunction.calculateSelection(
            distributionFunction.calculateProbabilitiesInjected(
                distributionFunction.options.map {
                    InjectedSituation(
                        situation(it),
                        injections.getOrDefault(it) { d -> d })
                }.toSet(),
                parameters
            )
        ).choice
    }

    fun select(parameters: P, situation: (X) -> SIT): X {
        return select(distributionFunction.options, parameters, situation)
    }

    fun select(options: Set<X>, parameters: P, situation: (X) -> SIT): X {
        return selectionFunction.calculateSelection(
            distributionFunction.calculateProbabilities(
                options.map(
                    situation
                ).toSet(), parameters
            )
        ).choice
    }
    fun probabilities(parameters: P, converter: (X) -> SIT): Map<X, Double> {
        return probabilities(distributionFunction.options, parameters, converter)
    }

    fun probabilities(options: Set<X>, parameters: P, converter: (X) -> SIT): Map<X, Double> {
        return probabilities(options.map(converter).toSet(),parameters).mapKeys { it.key.choice }
    }
}

class KnownDiscreteChoiceModel<X : Any, SIT : ChoiceSituation<X>, P>(
    override val distributionFunction: OptionDistributionFunction<X, SIT, P>,
) : DiscreteChoiceModel<X, SIT, P>(
    distributionFunction
) {
    fun select(converter: (X) -> SIT, parameters: P): X {
        return select(distributionFunction.options.map(converter).toSet(), parameters)
    }

    fun probabilities(parameters: P, converter: (X) -> SIT) = distributionFunction.calculateProbabilities(
        distributionFunction.options.map(converter).toSet(),
        parameters
    )


}

class ParametrizedDiscreteChoiceModel<X : Any, SIT : ChoiceSituation<X>, P>(
    val original: ModifiableDiscreteChoiceModel<X, SIT, P>,
    var parameters: P,
) {

    fun select(converter: (X) -> SIT) = original.select(parameters, converter)
    fun select(options: Set<X>, converter: (X) -> SIT) = original.select(options, parameters, converter)
    fun select(randomNumber: Double, converter: (X) -> SIT) = original.select(parameters, randomNumber, converter)
    fun select(options: Set<X>, randomNumber: Double, converter: (X) -> SIT) = original.select(options, parameters, randomNumber, converter)
    fun utilities(converter: (X) -> SIT) = original.utilities(parameters, converter)
    fun utilities(options: Set<X>, converter: (X) -> SIT) = original.utilities(options, parameters, converter)
    fun probabilities(converter: (X) -> SIT) = original.probabilities(parameters, converter)
    fun probabilities(options: Set<X>, converter: (X) -> SIT) = original.probabilities(options, parameters, converter)
    fun selectInjected(situation: (X) -> SIT, injections: Map<X, (Double) -> Double>): X = original.selectInjected(parameters, situation, injections)
}

fun <X : Any, SIT : ChoiceSituation<X>, PARAMS> ModifiableDiscreteChoiceModel<X, SIT, PARAMS>.initializeWithParameters(
    parameter: PARAMS,
): ParametrizedDiscreteChoiceModel<X, SIT, PARAMS> {
    return ParametrizedDiscreteChoiceModel<X, SIT, PARAMS>(this, parameter)
}

val GlobalRandomizer = Random(1)
