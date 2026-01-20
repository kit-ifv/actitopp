package edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes

import edu.kit.ifv.mobitopp.actitoppNG.RNGHelper
import edu.kit.ifv.mobitopp.actitoppNG.modernization.durations.MobilityPlanInputs
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.ActivityDurationHistograms
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.MainDurationAlternative
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.ArrayHistogram
import kotlin.random.Random
import kotlin.time.Duration


class TourStartWithPreference<P>(
    private val startTimeHistograms: ActivityDurationHistograms<P>,
    override val preferredTourStart: ArrayHistogram? = null,
    private val strategy: UsePreferredTourStart,

    ) :
    SelectTourStartWithPreference {
    context(rng: Random)
    override fun selectStartTime(input: MobilityPlanInputs, preferredTourStart: ArrayHistogram?): Duration {
        val bounds = input.dayPlan.startTimeBoundsFor(input.tourPlan)



        preferredTourStart?.let {

            if (strategy.usePreferredTourStart(input, it) && it.intersects(bounds)) {
                // In this instance we discard rnd1 because we already know the histogram we want to use.
                // The preferredTourStart field. However for testability we need to consume rnd1.
                rng.nextDouble() // Important for tests to keep the randomness at the same state
                return it.select(
                    bounds.start,
                    bounds.endInclusive
                )
            }

        }
        return startTimeHistograms.select(bounds, MainDurationAlternative(input))
    }
}