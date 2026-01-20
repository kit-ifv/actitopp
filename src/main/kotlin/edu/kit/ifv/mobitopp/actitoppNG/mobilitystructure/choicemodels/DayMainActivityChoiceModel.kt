package edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.choicemodels


import edu.kit.ifv.mobitopp.actitoppNG.enums.ActivityType
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.parameters.DayMainActivityParameters
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.parameters.DayMainActivitySet
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.parameters.DefaultDayMainActivityParameters
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.shenanigans.DayAlternative
import edu.kit.ifv.mobitopp.actitoppNG.utils.times
import edu.kit.ifv.mobitopp.discretechoice.structure.DiscreteStructure
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.multinomialLogit

val mainActivityChoiceModel =
    DiscreteStructure<ActivityType, DayAlternative, DayMainActivitySet> {

        option(ActivityType.WORK, parameters = { work }, {_, it ->
            (if (it.isEmployedAnywhere() && it.isStandardWorkingDay()) 1.3 else 1.0) * standardUtilityFunction(
                this,
                it
            )
        }
        )
        option(ActivityType.EDUCATION, parameters = { education }, {_, it ->
            (if (it.isStudentOrAzubi() && it.isStandardWorkingDay()) 1.3 else 1.0) * standardUtilityFunction(
                this,
                it
            )
        })
        option(ActivityType.LEISURE, parameters = { leisure }, { standardUtilityFunction(this, it.second) })
        option(ActivityType.SHOPPING, parameters = { shopping }, { standardUtilityFunction(this, it.second) })
        option(ActivityType.TRANSPORT, parameters = { transport }, { standardUtilityFunction(this, it.second) })
        option(ActivityType.HOME) {
            0.0
        }


    }.multinomialLogit("Main activity type of the day").build(DefaultDayMainActivityParameters)
private val standardUtilityFunction: DayMainActivityParameters.( DayAlternative) -> Double = {
    base +
            (it.isFulltimeEmployee()) * employmentFullTime +
            (it.isParttimeEmployee()) * employmentPartTime +
            (it.isNotEarningMoney()) * employmentNotEarning +
            (it.isStudent()) * employmentStudent +
            (it.isVocational()) * employmentVocational +
            (it.amountOfYouthsInHousehold()) * amountOfYouths +
            (it.has5WorkDays()) * has5WorkingDays +
            (it.has5EducationDays()) * has5EducationDays +
            (it.isTuesday()) * tuesday +
            (it.isWednesday()) * wednesday +
            (it.isThursday()) * thursday +
            (it.isFriday()) * friday +
            (it.isSaturday()) * saturday +
            (it.isSunday()) * sunday +
            (it.amountOfWorkingDays()) * amountOfWorkdays +
            (it.amountOfEducationDays()) * amountOfEducationDays +
            (it.amountOfLeisureDays()) * amountOfLeisureDays +
            (it.amountOfShoppingDays()) * amountOfShoppingDays +
            (it.amountOfServiceDays()) * amountOfTransportDays +
            (it.amountOfImmobileDays()) * amountOfImmobileDays
}