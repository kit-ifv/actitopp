package edu.kit.ifv.mobitopp.actitopp.steps.step8


import edu.kit.ifv.mobitopp.actitopp.RNGHelper
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.modernization.durations.Step8BInput
import kotlin.time.Duration

fun interface SelectMainActivityDuration {
    fun getDuration(input: Step8BInput): Duration
}

object StandardStep8B : SelectMainActivityDuration {
    val histogram = MajorDurationHistograms.DEFAULT
    val rng = RNGHelper(1)
    override fun getDuration(input: Step8BInput): Duration {
        input.run {
            return when(activityType) {
                ActivityType.WORK -> calculateFixed(this)
                ActivityType.EDUCATION -> calculateFixed(this)
                else -> calculateDefault(this)
            }
        }
    }

    fun calculateFixed(input: Step8BInput): Duration {
        return input.run {
            val meanActivityDuration = dayPlan.getBudget(activityType)
            histogram.chooseWithinNeighbors(rng, meanActivityDuration) {
                MainDurationSituation(it)
            }

        }
    }

    fun calculateDefault(input: Step8BInput): Duration {
        return histogram.select(rng) {
            MainDurationSituation(it)
        }
    }
}