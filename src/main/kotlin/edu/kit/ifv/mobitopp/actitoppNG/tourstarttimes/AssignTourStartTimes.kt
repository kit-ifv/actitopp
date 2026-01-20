package edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes

import edu.kit.ifv.mobitopp.actitoppNG.modernization.durations.MobilityPlanInputs
import edu.kit.ifv.mobitopp.actitoppNG.modernization.plan.MobilityPlan
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.ArrayHistogram
import kotlin.random.Random
import kotlin.time.Duration


fun interface SelectTourStart {
    context(rng: Random)
    fun selectStartTime(input: MobilityPlanInputs): Duration
}


interface SelectTourStartWithPreference : SelectTourStart {
    val preferredTourStart: ArrayHistogram?
    context(rng: Random)
    fun selectStartTime(input: MobilityPlanInputs, preferredTourStart: ArrayHistogram?): Duration
    context(rng: Random)
    override fun selectStartTime(input: MobilityPlanInputs): Duration {
        return selectStartTime(input, preferredTourStart)
    }
}
context(rng: Random)
fun MobilityPlan.assignFirstTourStarts(strategy: SelectTourStart) {
    dayPlans.forEach { dayPlan ->
        val tourPlan = dayPlan.tourPlans.first()
        val activity = tourPlan.mainActivity // TODO, make it so that input without activities does not need them.
        val tourStartTime = strategy.selectStartTime(MobilityPlanInputs(this, person, dayPlan, tourPlan, activity))
        tourPlan.setStartTime(tourStartTime + dayPlan.durationDay.startOfDay)
        require(this.isConsistent()) {
            "Mobility plan became inconsistent"
        }
    }
}
context(rng: Random)
fun MobilityPlan.assignSecondTourStarts(strategy: SelectTourStart) {
    dayPlans.forEach { dayPlan ->
        val tourPlan = dayPlan.tourPlans.getOrNull(1)

        tourPlan?.let {

            val activity = it.mainActivity // TODO, make it so that input without activities does not need them.
            val tourStartTime = strategy.selectStartTime(MobilityPlanInputs(this, person, dayPlan, it, activity))

            tourPlan.setStartTime(tourStartTime + dayPlan.durationDay.startOfDay)

        }
    }
}
context(rng: Random)
fun MobilityPlan.assignRemainingTourStarts(strategy: SelectTourStart) {
    dayPlans.forEach { dayPlan ->
        dayPlan.tourPlans.drop(2).forEach { tourPlan ->
            val activity = tourPlan.last() // TODO, make it so that input without activities does not need them.
            val tourStartTime = strategy.selectStartTime(MobilityPlanInputs(this, person, dayPlan, tourPlan, activity))

            tourPlan.setStartTime(tourStartTime + dayPlan.durationDay.startOfDay)

            require(isConsistent()) {
                "The plan is inconsistent"
            }
        }
    }
}