package edu.kit.ifv.mobitopp.actitopp.modernization.durations

import edu.kit.ifv.mobitopp.actitopp.IPerson
import edu.kit.ifv.mobitopp.actitopp.modernization.LinkedActivity
import edu.kit.ifv.mobitopp.actitopp.modernization.plan.DayPlan
import edu.kit.ifv.mobitopp.actitopp.modernization.plan.MobilityPlan
import edu.kit.ifv.mobitopp.actitopp.modernization.plan.TourPlan

data class Step8BInput(
    val mobilityPlan: MobilityPlan,
    val person: IPerson,
    val dayPlan: DayPlan,
    val tourPlan: TourPlan,
    val isLastTourOfDay: Boolean
) {
    val tourMainActivityType = tourPlan.mainActivity.activityType
}


data class Step8JInput(
    val mobilityPlan: MobilityPlan,
    val person: IPerson,
    val dayPlan: DayPlan,
    val tourPlan: TourPlan,
    val activity: LinkedActivity,
    val isLastTourOfDay: Boolean
)