package edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes

import edu.kit.ifv.mobitopp.actitoppNG.RNGHelper
import edu.kit.ifv.mobitopp.actitoppNG.modernization.durations.MobilityPlanInputs
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.ActivityDurationHistograms
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.Identifier
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.MainDurationAlternative
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.generateHistogram
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.ArrayHistogram
import edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes.parameters.ParameterCollectionStep10S
import edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes.parameters.ParameterStep10S
import edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes.parameters.ParametersStep10S
import edu.kit.ifv.mobitopp.actitoppNG.utils.minus
import edu.kit.ifv.mobitopp.actitoppNG.utils.times
import kotlin.random.Random
import kotlin.time.Duration


private val standardUtilityFunction10S: ParameterStep10S.(MainDurationAlternative) -> Double = {
    base +

            (it.touristhaupttour()) * touristhaupttour +
            (it.anztourenamtag()) * anztourenamtag +
            (it.tour3destages()) * tour3destages +
            (it.dauer_akt_tag_4bis6std()) * dauer_akt_tag_4bis6std +
            (it.dauer_akt_tag_6bis8std()) * dauer_akt_tag_6bis8std +
            (it.dauer_akt_tag_8bis10std()) * dauer_akt_tag_8bis10std +
            (it.dauer_akt_tag_10bis12std()) * dauer_akt_tag_10bis12std +
            (it.dauer_akt_tag_12bis14std()) * dauer_akt_tag_12bis14std +
            (it.endetourvorher_Std_12()) * endetourvorher_Std_12 +
            (it.endetourvorher_Std_13()) * endetourvorher_Std_13 +
            (it.endetourvorher_Std_14()) * endetourvorher_Std_14 +
            (it.endetourvorher_Std_15()) * endetourvorher_Std_15 +
            (it.endetourvorher_Std_16()) * endetourvorher_Std_16 +
            (it.endetourvorher_Std_17()) * endetourvorher_Std_17 +
            (it.endetourvorher_Std_18()) * endetourvorher_Std_18 +
            (it.endetourvorher_Std_19()) * endetourvorher_Std_19 +
            (it.endetourvorher_Std_20()) * endetourvorher_Std_20 +
            (it.isStudent()) * beruf_schueler

}


class TourStartByHistogramsRelative<P>(

    private val startTimeHistograms: ActivityDurationHistograms<P>,
) :
    SelectTourStart {
    context(rng: Random)
    override fun selectStartTime(input: MobilityPlanInputs): Duration {
        val bounds = input.dayPlan.startTimeBoundsFor(input.tourPlan, disregardDayEnd = input.isLastDay)
        val startTime = bounds.start
        val relativeBounds = bounds - startTime
        return startTimeHistograms.select(relativeBounds, MainDurationAlternative( input)) + startTime
    }

    companion object {
        fun standard(): TourStartByHistogramsRelative<ParameterCollectionStep10S> {
            return TourStartByHistogramsRelative( OTHER_TOUR_HISTOGRAM)
        }
    }
}


val OTHER_TOUR_HISTOGRAM = ParametersStep10S.generateHistogram(
    ArrayHistogram.fromResource(
        identifier = Identifier.OTHER_TOUR_START_TIME
    ),
    standardUtilityFunction10S
)