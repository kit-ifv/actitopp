package edu.kit.ifv.mobitopp.actitopp.steps.step1

import edu.kit.ifv.mobitopp.actitopp.ActiToppHousehold
import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.ActitoppPersonModifierFields
import edu.kit.ifv.mobitopp.actitopp.WeekRoutine
import edu.kit.ifv.mobitopp.actitopp.RNGHelper
import edu.kit.ifv.mobitopp.actitopp.steps.PersonSituation
import edu.kit.ifv.mobitopp.actitopp.toModifiable
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ParametrizedDiscreteChoiceModel

fun ActitoppPerson.assignWeekRoutine(rng: RNGHelper = this.personalRNG): WeekRoutine {
    return toModifiable().run {
        amountOfWorkingDays = step1AWithParams.select(rng.randomValue, this)
        amountOfEducationDays = step1BWithParams.select(rng.randomValue, this)
        amountOfLeisureDays = step1CWithParams.select(rng.randomValue, this)
        amountOfShoppingDays = step1DWithParams.select(rng.randomValue, this)
        amountOfServiceDays = step1EWithParams.select(rng.randomValue, this)
        amountOfImmobileDays = step1FWithParams.select(rng.randomValue, this)
        averageAmountOfTours = step1KWithParams.select(rng.randomValue, this)
        averageAmountOfActivities = step1LWithParams.select(rng.randomValue, this)
        toWeekRoutine()
    }
}

fun <T> ParametrizedDiscreteChoiceModel<Int, PersonSituation, T>.select(rand: Double, modifierField: ActitoppPersonModifierFields): Int {
    return select(rand) {PersonSituation(it, modifierField)}
}