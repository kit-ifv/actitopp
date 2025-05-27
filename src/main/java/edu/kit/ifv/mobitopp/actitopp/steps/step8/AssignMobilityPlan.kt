package edu.kit.ifv.mobitopp.actitopp.steps.step8

import edu.kit.ifv.mobitopp.actitopp.modernization.durations.Step8BInput
import edu.kit.ifv.mobitopp.actitopp.modernization.durations.Step8JInput
import edu.kit.ifv.mobitopp.actitopp.modernization.plan.MobilityPlan

fun MobilityPlan.assignFirstMainActivities(strategy: SelectMainActivityDuration) {
    dayPlans.forEach { dayPlan ->
        val tourPlan = dayPlan.tourPlans.first()
        tourPlan.mainActivity.duration = strategy.getDuration(
            Step8BInput(
                mobilityPlan = this,
                person = person,
                dayPlan = dayPlan,
                tourPlan = tourPlan,
                isLastTourOfDay = tourPlan == dayPlan.tourPlans.last()
            )
        )

    }
}

fun MobilityPlan.assignSecondaryMainActivities(strategy: SelectMajorActivityDuration) {
    dayPlans.forEach { dayPlan ->
        dayPlan.tourPlans.drop(1).forEach { tourPlan ->
            tourPlan.mainActivity.duration = strategy.getDuration(
                Step8BInput(
                    mobilityPlan = this,
                    person = person,
                    dayPlan = dayPlan,
                    tourPlan = tourPlan,
                    isLastTourOfDay = tourPlan == dayPlan.tourPlans.last()
                )
            )

        }
    }
}

fun MobilityPlan.assignMinorActivities(strategy: SelectMinorActivityDuration) {
    dayPlans.forEach { dayPlan ->
        dayPlan.tourPlans.forEach { tourPlan ->
            tourPlan.minorActivities.forEach { activity ->
                activity.duration = strategy.getDuration(
                    Step8JInput(
                        mobilityPlan = this,
                        person = person,
                        dayPlan = dayPlan,
                        tourPlan = tourPlan,
                        activity = activity,
                        isLastTourOfDay = tourPlan == dayPlan.tourPlans.last()
                    )
                )

            }
        }
    }
}