package edu.kit.ifv.mobitopp.actitoppNG.plandurations

import edu.kit.ifv.mobitopp.actitoppNG.RNGHelper
import edu.kit.ifv.mobitopp.actitoppNG.modernization.durations.MobilityPlanInputs
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.choicemodels.MINOR
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.parameters.ParameterCollectionStep8J
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

fun interface SelectMajorActivityDuration {
    context(rng: Random)
    fun getDuration(input: MobilityPlanInputs): Duration
}

fun interface SelectMinorActivityDuration {
    context(rng: Random)
    fun getDuration(input: MobilityPlanInputs): Duration
}

class AssignMinorActivityDuration(
    private val histogram: ActivityDurationHistograms<ParameterCollectionStep8J> = MINOR,
) : SelectMinorActivityDuration {
    context(rng: Random)
    override fun getDuration(input: MobilityPlanInputs): Duration {
        return input.run {
            val bounds = dayPlan.activityDurationBounds(activity)
            if(bounds.isEmpty()) return 0.minutes
            histogram.select(
                bounds,
                MainDurationAlternative(
                    this
                )
            )

        }


    }
}