//package edu.kit.ifv.discretechoice.extensions
//
//import discreteChoice.EnumeratedDiscreteChoiceModel
//import discreteChoice.models.ChoiceAlternative
//import discreteChoice.structure.EnumeratedStructureBuilder
//import discreteChoice.structure.bulkList
//import kotlin.random.Random
//
//fun <R : Any, A : ChoiceAlternative<R>, P> EnumeratedDiscreteChoiceModel<R, A, P>.select(
//    random: Random,
//    converter: (R) -> A,
//): R {
//    return select(choices.map(converter).toSet(), random)
//
//}
//
//fun <R : Any, A : ChoiceAlternative<R>, P> EnumeratedDiscreteChoiceModel<R, A, P>.selectInjected(
//    choices: Set<A>,
//    injections: Map<R, (Double) -> Double>,
//    random: Random,
//): R {
//    return model.selectInjected(choices, injections, random)
//
//}
//
//fun <T, R : Any, A : ChoiceAlternative<R>, P : List<T>> EnumeratedStructureBuilder<R, A, P>.forOptions(
//    options: Iterable<R>,
//    utilityFunction: T.(A) -> Double,
//) {
//    bulkList(options.toList(), utilityFunction)
//}
//
//fun <T, R : Any, A : ChoiceAlternative<R>, P : List<T>> EnumeratedStructureBuilder<R, A, P>.forOptions(
//    vararg options: R,
//    utilityFunction: T.(A) -> Double,
//) {
//    bulkList(options.toList(), utilityFunction)
//}
