package edu.kit.ifv.mobitopp.actitoppNG.plandurations

import edu.kit.ifv.mobitopp.actitoppNG.modernization.durations.MobilityPlanInputs
import edu.kit.ifv.mobitopp.actitoppNG.modernization.plan.MobilityPlan
import kotlin.random.Random

context(rng: Random)
fun MobilityPlan.assignFirstMainActivities(strategy: SelectMainActivityDuration) {
    dayPlans.forEach { dayPlan ->
        val tourPlan = dayPlan.tourPlans.first()
        val activity = tourPlan.mainActivity
        activity.duration = strategy.getDuration(
            MobilityPlanInputs(
                mobilityPlan = this,
                person = person,
                dayPlan = dayPlan,
                tourPlan = tourPlan,
                activity = activity
            )
        )

    }
}
context(rng: Random)
fun MobilityPlan.assignSecondaryMainActivities(strategy: SelectMajorActivityDuration) {
    dayPlans.forEach { dayPlan ->
        dayPlan.tourPlans.drop(1).forEach { tourPlan ->
            val activity = tourPlan.mainActivity
            activity.duration = strategy.getDuration(
                MobilityPlanInputs(
                    mobilityPlan = this,
                    person = person,
                    dayPlan = dayPlan,
                    tourPlan = tourPlan,
                    activity = activity
                )
            )

        }
    }
}
context(rng: Random)
fun MobilityPlan.assignMinorActivities(strategy: SelectMinorActivityDuration) {
    dayPlans.forEach { dayPlan ->
        dayPlan.tourPlans.forEach { tourPlan ->
            tourPlan.minorActivities.forEach { activity ->
                activity.duration = strategy.getDuration(
                    MobilityPlanInputs(
                        mobilityPlan = this,
                        person = person,
                        dayPlan = dayPlan,
                        tourPlan = tourPlan,
                        activity = activity,
                    )
                )

            }
        }
    }
}