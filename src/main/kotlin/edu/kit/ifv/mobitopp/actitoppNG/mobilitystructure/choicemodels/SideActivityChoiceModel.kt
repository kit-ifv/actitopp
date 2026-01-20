package edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.choicemodels

import edu.kit.ifv.mobitopp.actitoppNG.enums.ActivityType
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.parameters.DefaultSideActivityParameters
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.parameters.SideActivityParameters
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.parameters.SideActivitySet
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.shenanigans.ActivityAlternative
import edu.kit.ifv.mobitopp.actitoppNG.utils.times
import edu.kit.ifv.mobitopp.discretechoice.structure.DiscreteStructure
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.multinomialLogit

val sideActivityChoiceModel =
    DiscreteStructure<ActivityType, ActivityAlternative, SideActivitySet> {
        option(ActivityType.WORK, parameters = { work }) { standardUtilityFunction(this, it.second) }
        option(ActivityType.EDUCATION, parameters = { education }) { standardUtilityFunction(this, it.second) }
        option(ActivityType.LEISURE) { 0.0 }
        option(ActivityType.SHOPPING, parameters = { shopping }) { standardUtilityFunction(this, it.second) }
        option(ActivityType.TRANSPORT, parameters = { transport }) { standardUtilityFunction(this, it.second) }

    }.multinomialLogit("Determine Activity type of secondary activities.").build(DefaultSideActivityParameters)
private val standardUtilityFunction: SideActivityParameters.(ActivityAlternative) -> Double = {
    base +
            (it.isBeforeMainTour()) * tourliegtvorhaupttour +
            (it.isAfterMainTour()) * tourliegtnachhaupttour +
            (it.isBeforeMainActivity()) * aktliegtvorhauptakt +
            (it.tourMainActivityIsWork()) * tourtyp_work +
            (it.tourMainActivityIsEducation()) * tourtyp_education +
            (it.tourMainActivityIsShopping()) * tourtyp_shopping +
            (it.tourMainActivityIsTransport()) * tourtyp_transport +
            (it.isFriday()) * tag_fr +
            (it.isSaturday()) * tag_sa +
            (it.isSunday()) * tag_so +
            (it.dayMainActivityIsWork()) * haupttour_work +
            (it.dayMainActivityIsEducation()) * haupttour_education +
            (it.dayMainActivityIsShopping()) * haupttour_shopping +
            (it.dayMainActivityIsTransport()) * haupttour_transport +
            (it.tourHas2Activities()) * tourhat2akt +
            (it.tourHas3Activities()) * tourhat3akt +
            (it.tourHas4Activities()) * tourhat4akt +
            (it.amountOfWorkingDaysIs0()) * anzahl_arbeitstage0 +
            (it.amountOfEducationDaysIs0()) * anzahl_bildungstage0 +
            (it.amountOfShoppingDaysIs0()) * anzahl_shoppingtage0 +
            (it.amountOfServiceDaysIs0()) * anzahl_transporttage0 +
            (it.amountOfWorkingDaysIs1()) * anzahl_arbeitstage1 +
            (it.amountOfEducationDaysIs1()) * anzahl_bildungstage1 +
            (it.amountOfShoppingDaysIs1()) * anzahl_shoppingtage1 +
            (it.amountOfServiceDaysIs1()) * anzahl_transporttage1


}