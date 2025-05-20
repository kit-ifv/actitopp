package edu.kit.ifv.mobitopp.actitopp.modernization.durations

import edu.kit.ifv.mobitopp.actitopp.IPerson
import edu.kit.ifv.mobitopp.actitopp.modernization.plan.DayPlan
import edu.kit.ifv.mobitopp.actitopp.modernization.plan.TourPlan

data class Step8BInput(
    val person: IPerson,
    val dayPlan: DayPlan,
    val tourPlan: TourPlan,
) {
    val activityType = tourPlan.mainActivity.activityType
}


