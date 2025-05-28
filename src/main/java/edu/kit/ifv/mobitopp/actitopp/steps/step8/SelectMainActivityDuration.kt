package edu.kit.ifv.mobitopp.actitopp.steps.step8


import edu.kit.ifv.mobitopp.actitopp.RNGHelper
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.modernization.durations.Step8BInput
import kotlin.time.Duration

fun interface SelectMainActivityDuration {
    fun getDuration(input: Step8BInput): Duration
}

class StandardStep8B<P>(
    val rng: RNGHelper,
    histogram: ActivityDurationHistograms<P>,
) : SelectMainActivityDuration, SelectMajorActivityDuration {
    private val taintedHistograms = histogram.taint()
    override fun getDuration(input: Step8BInput): Duration {
        input.run {
            return when (tourMainActivityType) {
                ActivityType.WORK -> calculateFixed(this)
                ActivityType.EDUCATION -> calculateFixed(this)
                else -> calculateDefault(this)
            }
        }
    }

    fun calculateFixed(input: Step8BInput): Duration {
        return input.run {
            val bounds = dayPlan.boundsFor(tourPlan.mainActivity)
            println(bounds)
            val meanActivityDuration = dayPlan.getBudget(tourMainActivityType)
            taintedHistograms.selectAndTaint(rng, meanActivityDuration) {
                MainDurationSituation(
                    it, mobilityPlan, dayPlan, tourPlan, tourPlan.mainActivity, mobilityPlan.timeBudgets, person,
                    isLastTourOfDay = isLastTourOfDay
                )
            }

        }
    }

    fun calculateDefault(input: Step8BInput): Duration {

        return input.run {
            val bounds = dayPlan.boundsFor(tourPlan.mainActivity)
            taintedHistograms.select(rng, bounds) {
                MainDurationSituation(
                    it,
                    mobilityPlan,
                    dayPlan,
                    tourPlan,
                    tourPlan.mainActivity,
                    mobilityPlan.timeBudgets,
                    person,
                    isLastTourOfDay
                )
            }
        }
    }
}