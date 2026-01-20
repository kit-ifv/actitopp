package edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.choicemodels

import edu.kit.ifv.mobitopp.actitoppNG.enums.ActivityType
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.parameters.DefaultSideTourMainActivityParameters
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.parameters.SideTourMainActivityParameters
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.parameters.SideTourMainActivitySet
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.shenanigans.TourAlternative
import edu.kit.ifv.mobitopp.actitoppNG.utils.times
import edu.kit.ifv.mobitopp.discretechoice.structure.DiscreteStructure
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.multinomialLogit

val tourMainActivityChoiceModel =
    DiscreteStructure<ActivityType, TourAlternative, SideTourMainActivitySet> {
        option(ActivityType.WORK, parameters = { work }, { standardUtilityFunction(this, it.second) })
        option(ActivityType.EDUCATION, parameters = { education }, { standardUtilityFunction(this, it.second) })
        option(ActivityType.LEISURE) { 0.0 }
        option(ActivityType.SHOPPING, parameters = { shopping }, { standardUtilityFunction(this, it.second) })
        option(ActivityType.TRANSPORT, parameters = { transport }, { standardUtilityFunction(this, it.second) })

    }.multinomialLogit("Main Activity of the tours that are not the main tour.")
        .build(DefaultSideTourMainActivityParameters)
private val standardUtilityFunction: SideTourMainActivityParameters.(TourAlternative) -> Double = {
    base +
            (it.isFulltimeEmployee()) * employmentFullTime +
            (it.isParttimeEmployee()) * employmentPartTime +
            (it.isNotEarningMoney()) * employmentNotEarning +
            (it.isStudent()) * employmentStudent +
            (it.isVocational()) * employmentVocational +
            (it.dayMainActivityIsWork()) * mainTourWork +
            (it.dayMainActivityIsEducation()) * mainTourEducation +
            (it.dayMainActivityIsShopping()) * mainTourShopping +
            (it.dayMainActivityIsTransport()) * mainTourTransport +
            (it.isFriday()) * friday +
            (it.isSaturday()) * saturday +
            (it.isSunday()) * sunday +
            (it.isFirstTourOfDay()) * firstTourOfDay +
            (it.isSecondTourOfDay()) * secondTourOfDay +
            (it.isThirdTourOfDay()) * thirdTourOfDay +
            (it.isBeforeMainTour()) * beforeMainTour +
            (it.amountOfWorkingDaysIs0()) * noWorkDays +
            (it.amountOfEducationDaysIs0()) * noEducationDays +
            (it.amountOfShoppingDaysIs0()) * noShoppingDays +
            (it.amountOfServiceDaysIs0()) * noTransportDays +
            (it.amountOfWorkingDaysIs1()) * oneWorkDay +
            (it.amountOfEducationDaysIs1()) * oneEducationDay +
            (it.amountOfShoppingDaysIs1()) * oneShoppingDay +
            (it.amountOfServiceDaysIs1()) * oneTransportDay
}