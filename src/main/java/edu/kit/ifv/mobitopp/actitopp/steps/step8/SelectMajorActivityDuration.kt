package edu.kit.ifv.mobitopp.actitopp.steps.step8

import edu.kit.ifv.mobitopp.actitopp.RNGHelper
import edu.kit.ifv.mobitopp.actitopp.modernization.durations.Step8BInput
import edu.kit.ifv.mobitopp.actitopp.modernization.durations.Step8JInput
import kotlin.time.Duration

fun interface SelectMajorActivityDuration {
    fun getDuration(input: Step8BInput): Duration
}

fun interface SelectMinorActivityDuration {
    fun getDuration(input: Step8JInput): Duration
}

class AssignMinorActivityDuration(val rngHelper: RNGHelper,
                                  private val histogram: ActivityDurationHistograms<ParameterCollectionStep8J> = MINOR): SelectMinorActivityDuration {
    override fun getDuration(input: Step8JInput): Duration {
        return input.run {
            val bounds = dayPlan.boundsFor(activity)
            histogram.select(rngHelper, bounds) {
                MainDurationSituation(
                    choice = it,
                    mobilityPlan = mobilityPlan,
                    dayPlan = dayPlan,
                    tourPlan = tourPlan,
                    activity = activity,
                    planTimeBudgets = mobilityPlan.timeBudgets,
                    person = person,
                    isLastTourOfDay = isLastTourOfDay,
                )
            }
        }


    }
}