package edu.kit.ifv.mobitopp.actitoppNG.weekroutine.choicemodels

import edu.kit.ifv.discretechoice.extensions.loadOptionsMap
import edu.kit.ifv.discretechoice.extensions.optionsIndexed
import edu.kit.ifv.mobitopp.actitoppNG.steps.PersonAlternative
import edu.kit.ifv.mobitopp.actitoppNG.utils.times
import edu.kit.ifv.mobitopp.actitoppNG.weekroutine.parameters.DefaultShoppingParameters
import edu.kit.ifv.mobitopp.actitoppNG.weekroutine.parameters.ShoppingDaySet
import edu.kit.ifv.mobitopp.discretechoice.structure.DiscreteStructure
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.multinomialLogit

/**
 * THe choice model determining the amount of days with a SHOPPING activity within the week routine. The default option
 * is 0. The other options [1, 7] share a common utility function.
 */
val shoppingDaysChoiceModel = DiscreteStructure<Int, PersonAlternative, ShoppingDaySet> {
    loadOptionsMap((1..7)) {_, it ->
        base +
                (it.isEarningMoney()) * employmentIsEarning +
                (it.isStudent()) * emplomentStudent +
                (it.isVocational()) * employmentVocational +
                (it.areaTypeConurbation()) * areaTypeIsConurbation +
                (it.areaTypeRural()) * areaTypeIsRural +
                (it.isAged10To17()) * ageIn10To17 +
                it.amountOfWorkingDays() * amountOfWorkingDays +
                it.amountOfEducationDays() * amountOfEducationDays +
                it.amountOfLeisureDays() * amountOfLeisureDays
    }
    option(0) {
        0.0
    }
}.multinomialLogit("Amount of shopping days in week routine").build(DefaultShoppingParameters)
