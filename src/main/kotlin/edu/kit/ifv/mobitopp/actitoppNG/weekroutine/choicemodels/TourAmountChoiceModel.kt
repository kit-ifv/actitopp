package edu.kit.ifv.mobitopp.actitoppNG.weekroutine.choicemodels

import edu.kit.ifv.discretechoice.extensions.optionsIndexed
import edu.kit.ifv.mobitopp.actitoppNG.steps.PersonAlternative
import edu.kit.ifv.mobitopp.actitoppNG.utils.times
import edu.kit.ifv.mobitopp.actitoppNG.weekroutine.parameters.DefaultTourAmountParameters
import edu.kit.ifv.mobitopp.actitoppNG.weekroutine.parameters.TourAmountSet
import edu.kit.ifv.mobitopp.discretechoice.structure.DiscreteStructure
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.multinomialLogit

/**
 * The choice model for selecting the average amount of tours per day in the week routine. The default option of 1
 * is normalized and has 0.0 utility. The other options [2, 4] share a common utility function.
 */
val tourAmountChoiceModel = DiscreteStructure<Int, PersonAlternative, TourAmountSet> {
    option(1) {
        0.0
    }
    optionsIndexed(2..4) { _, it ->
        base +
                (it.isFulltimeEmployee()) * employmentFulltime +
                (it.isParttimeEmployee()) * employmentParttime +
                (it.isStudent()) * employmentStudent +
                (it.isAged26To35()) * ageIn26To35 +
                (it.isAged36To50()) * ageIn36To50 +
                (it.isAged51To60()) * ageIn51To60 +
                (it.isAged61To70()) * ageIn61To70 +
                (it.areaTypeConurbation()) * areaTypeConurbation +
                (it.areaTypeRural()) * areaTypeRural +
                (it.commuteOver50km()) * commuteOver50km +
                (it.commuteIn0To5km()) * commuteIn0To5Km +
                (it.hasChildrenInHousehold()) * householdHasChildren +
                (it.isMale()) * isMale
    }
}.multinomialLogit("Average amount of tours per day in week routine ").build(DefaultTourAmountParameters)
