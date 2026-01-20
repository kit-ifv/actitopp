package edu.kit.ifv.mobitopp.actitoppNG.weekroutine.choicemodels

import edu.kit.ifv.discretechoice.extensions.loadOptionsMap
import edu.kit.ifv.discretechoice.extensions.optionsIndexed
import edu.kit.ifv.mobitopp.actitoppNG.steps.PersonAlternative
import edu.kit.ifv.mobitopp.actitoppNG.utils.times
import edu.kit.ifv.mobitopp.actitoppNG.weekroutine.parameters.DefaultServiceParameters
import edu.kit.ifv.mobitopp.actitoppNG.weekroutine.parameters.ServiceDaySet
import edu.kit.ifv.mobitopp.discretechoice.structure.DiscreteStructure
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.multinomialLogit

/**
 * The choice model determining the amount of days with a SHOPPING activity within the week routine. The default option
 * is 0. The other options [1,7] share a common utility function.
 */
val serviceDaysChoiceModel = DiscreteStructure<Int, PersonAlternative, ServiceDaySet> {
    loadOptionsMap(1..7) {_, it ->
        base +
                (it.isFulltimeEmployee()) * employmentIsFulltime +
                (it.isParttimeEmployee()) * employmentIsParttime +
                (it.isAged10To17()) * ageIn10to17 +
                (it.isAged26To35()) * ageIn26to35 +
                (it.isAged36To50()) * ageIn36to50 +
                (it.areaTypeConurbation()) * areaTypeIsConurbation +
                (it.hasChildrenInHousehold()) * householdHasChildren +
                (it.amountOfYouthsInHousehold()) * householdAmountYouths +
                (it.isMale()) * isMale

    }
    option(0) {
        0.0
    }
}.multinomialLogit("Amount of service/transport days in week routine").build(DefaultServiceParameters)
