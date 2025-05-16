package edu.kit.ifv.mobitopp.actitopp.steps.step5

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.HDay
import edu.kit.ifv.mobitopp.actitopp.HTour
import edu.kit.ifv.mobitopp.actitopp.WeekRoutine
import edu.kit.ifv.mobitopp.actitopp.modernization.BidirectionalIndexedValue
import edu.kit.ifv.mobitopp.actitopp.modernization.DayStructure
import edu.kit.ifv.mobitopp.actitopp.modernization.TourStructure

fun interface GenerateSideTourActivities {

    fun generateActivityAmount(input: SideTourActivityInput): Int
}

data class SideTourActivityInput(
    val person: ActitoppPerson,
    val routine: WeekRoutine,
    val currentDay: DayStructure,
    val tour: BidirectionalIndexedValue<TourStructure>,
    val minimumAmountOfActivities: Int,
    val amountOfActivitiesBeforeMainAct: Int,
)