package edu.kit.ifv.mobitopp.actitoppNG.weekroutine.choicemodels


import edu.kit.ifv.discretechoice.extensions.loadOptionsMap
import edu.kit.ifv.mobitopp.actitoppNG.steps.PersonAlternative

import edu.kit.ifv.discretechoice.extensions.optionsIndexed
import edu.kit.ifv.mobitopp.actitoppNG.utils.times
import edu.kit.ifv.mobitopp.actitoppNG.weekroutine.parameters.DefaultEducationParameters
import edu.kit.ifv.mobitopp.actitoppNG.weekroutine.parameters.EducationDaySet
import edu.kit.ifv.mobitopp.discretechoice.structure.DiscreteStructure
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.multinomialLogit

/**
 * The choice model of selecting the amount of days with an education activity in the week routine. The default option
 * is 0 days. The other options [1, 7] share a common utility function.
 */
val educationDaysChoiceModel = DiscreteStructure<Int, PersonAlternative, EducationDaySet> {
    loadOptionsMap(1..7) {_, it ->
        base +
                (it.isEarningMoney()) * employmentIsEarning +
                (it.isStudent()) * emplomentStudent +
                (it.isVocational()) * employmentVocational +
                (it.isAged10To17()) * ageIn10to17 +
                (it.isAged18To25()) * ageIn18To25 +
                (it.isAged26To35()) * ageIn26To35 +
                (it.isAged36To50()) * ageIn36To50 +
                it.amountOfWorkingDays() * amountOfWorkingDays
    }
    option(0) {
        0.0
    }
}.multinomialLogit("Amount of education days in week routine")
    .build(DefaultEducationParameters)
