package edu.kit.ifv.mobitopp.actitopp.steps.step8

import edu.kit.ifv.mobitopp.actitopp.modernization.durations.Step8BInput
import edu.kit.ifv.mobitopp.actitopp.modernization.plan.MobilityPlan

fun MobilityPlan.assignFirstMainActivities(strategy: SelectMainActivityDuration) {
    dayPlans.forEach { dayPlan ->
        val tourPlan = dayPlan.tourPlans.first()
        tourPlan.mainActivity.duration = strategy.getDuration(
            Step8BInput(
                person = person,
                dayPlan = dayPlan,
                tourPlan = tourPlan
            )
        )

    }
}

fun MobilityPlan.assignSecondaryMainActivities(strategy: SelectMajorActivityDuration) {
    dayPlans.forEach { dayPlan ->
        dayPlan.tourPlans.drop(1).forEach { tourPlan ->
            tourPlan.mainActivity.duration = strategy.getDuration(
                Step8BInput(
                    person = person,
                    dayPlan = dayPlan,
                    tourPlan = tourPlan
                )
            )

        }
    }
}