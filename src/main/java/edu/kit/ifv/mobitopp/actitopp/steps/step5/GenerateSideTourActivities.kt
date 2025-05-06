package edu.kit.ifv.mobitopp.actitopp.steps.step5

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.HDay
import edu.kit.ifv.mobitopp.actitopp.HTour
import edu.kit.ifv.mobitopp.actitopp.WeekRoutine

fun interface GenerateSideTourActivities {

    fun generateActivitiesFor(input: SideTourActivityInput): List<Pair<HTour, Int>>
}

data class SideTourActivityInput(
    val person: ActitoppPerson,
    val routine: WeekRoutine,
    val currentDay: HDay,
    val tour: HTour,
    val lowerBoundFromJointActions: Collection<Int>,
    val expectedLowerboundOfActivities: Int
)