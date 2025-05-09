package edu.kit.ifv.mobitopp.actitopp.modernization

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.RNGHelper
import edu.kit.ifv.mobitopp.actitopp.WeekRoutine
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.steps.step1.assignWeekRoutine
import edu.kit.ifv.mobitopp.actitopp.steps.step2.DaySituation
import edu.kit.ifv.mobitopp.actitopp.steps.step2.step2AWithParams

class PatternStructure(val weekRoutine: WeekRoutine,
    val dayStructures: Collection<DayStructure> = (0..6).map { DayStructure(it) }
) {

    private val activityTracker: MutableMap<ActivityType, MutableSet<DayStructure>> = mutableMapOf()
    val workingDays: MutableSet<DayStructure> = mutableSetOf()
    val educationDays: MutableSet<DayStructure> = mutableSetOf()


    fun determineMainActivity(dayStructure: DayStructure) {

        val activityType = step2AWithParams.select { DaySituation(it) }
        if(activityType != ActivityType.HOME) {
            dayStructure.addMainActivity(activityType)
        }
    }

}

fun main() {
    val person = ActitoppPerson()
    val pattern = PatternStructure(person.assignWeekRoutine(rng = RNGHelper(1)))
    println(pattern)
}