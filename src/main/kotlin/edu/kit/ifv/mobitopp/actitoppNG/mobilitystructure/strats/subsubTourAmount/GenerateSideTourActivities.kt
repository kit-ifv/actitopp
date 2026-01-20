package edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.strats.subsubTourAmount

import edu.kit.ifv.mobitopp.actitoppNG.Person
import edu.kit.ifv.mobitopp.actitoppNG.modernization.DayStructure
import edu.kit.ifv.mobitopp.actitoppNG.modernization.TourStructure
import edu.kit.ifv.mobitopp.actitoppNG.utils.BidirectionalIndexedValue
import edu.kit.ifv.mobitopp.actitoppNG.weekroutine.WeekRoutine
import kotlin.random.Random

fun interface GenerateSideTourActivities {
    context(rng: Random)
    fun generateActivityAmount(input: SideTourActivityInput): Int
}

data class SideTourActivityInput(
    val person: Person,
    val routine: WeekRoutine,
    val currentDay: DayStructure,
    val tour: BidirectionalIndexedValue<TourStructure>,
    val minimumAmountOfActivities: Int,
    val amountOfActivitiesBeforeMainAct: Int,
)