package edu.kit.ifv.mobitopp.actitoppNG.modernization.durations

import edu.kit.ifv.mobitopp.actitoppNG.Person
import edu.kit.ifv.mobitopp.actitoppNG.modernization.LinkedActivity
import edu.kit.ifv.mobitopp.actitoppNG.modernization.plan.DayPlan
import edu.kit.ifv.mobitopp.actitoppNG.modernization.plan.MobilityPlan
import edu.kit.ifv.mobitopp.actitoppNG.modernization.plan.TourPlan

data class MobilityPlanInputs(
    val mobilityPlan: MobilityPlan,
    val person: Person,
    val dayPlan: DayPlan,
    val tourPlan: TourPlan,
    val activity: LinkedActivity,
) {
    val tourMainActivityType = tourPlan.mainActivity.activityType
    val isLastTourOfDay: Boolean = dayPlan.tourPlans.last() == tourPlan

    val isLastDay = mobilityPlan.dayPlans.last() == dayPlan
}


