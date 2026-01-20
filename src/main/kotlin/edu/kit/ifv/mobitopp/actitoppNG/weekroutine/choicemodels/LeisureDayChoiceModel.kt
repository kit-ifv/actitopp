package edu.kit.ifv.mobitopp.actitoppNG.weekroutine.choicemodels


import edu.kit.ifv.discretechoice.extensions.loadOptionsMap
import edu.kit.ifv.discretechoice.extensions.optionsIndexed
import edu.kit.ifv.mobitopp.actitoppNG.steps.PersonAlternative
import edu.kit.ifv.mobitopp.actitoppNG.utils.times
import edu.kit.ifv.mobitopp.actitoppNG.weekroutine.parameters.DefaultLeisureParameters
import edu.kit.ifv.mobitopp.actitoppNG.weekroutine.parameters.LeisureDaySet
import edu.kit.ifv.mobitopp.discretechoice.structure.DiscreteStructure
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.multinomialLogit

/**
 * The choice model determining the amount of days with a LEISURE activity within the week routine. The default option is
 * 0. The other options [1, 7] share a common utility function.
 */
val leisureDaysChoiceModel = DiscreteStructure<Int, PersonAlternative, LeisureDaySet> {
    loadOptionsMap(1..7) {_, it ->
        base +
                (it.isEarningMoney()) * employmentIsEarning +
                (it.isStudent()) * emplomentStudent +
                (it.areaTypeRural()) * areaTypeIsRural +
                (it.hasChildrenInHousehold()) * householdHasChildenBelowAge10 +
                (it.isAged61To70()) * ageIn61To70 +
                it.amountOfWorkingDays() * amountOfWorkingDays
    }
    option(0) {
        0.0
    }
}.multinomialLogit("Amount of leisure days in week routine").build(DefaultLeisureParameters)
