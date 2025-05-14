package edu.kit.ifv.mobitopp.actitopp.modernization

import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitopp.steps.step3.TourSituation
import edu.kit.ifv.mobitopp.actitopp.steps.step3.step4WithParams

class DayWithPlans(
    val dayStructure: DayStructure,
    val personWithRoutine: PersonWithRoutine,
    val plannedTourAmounts: PlannedTourAmounts,
)


fun interface AssignMainActivities {
    fun generateSideTourActivities(input: DayWithPlans): DayStructure
}

class AssignByUtilityFunction(val patternStructure: PatternStructure) : AssignMainActivities {
    override fun generateSideTourActivities(input: DayWithPlans): DayStructure {

        patternStructure.generateTrackedActivity(input.dayStructure.startTimeDay) {
            val availableOptions = step4WithParams.registeredOptions().toMutableSet()
            if (!input.personWithRoutine.person.isAllowedToWork) availableOptions.remove(ActivityType.WORK)
            if (amountOfDaysWith(ActivityType.WORK) >= input.personWithRoutine.routine.amountOfWorkingDays) availableOptions.remove(
                ActivityType.WORK
            )
            if (amountOfDaysWith(ActivityType.EDUCATION) >= input.personWithRoutine.routine.amountOfEducationDays) availableOptions.remove(
                ActivityType.EDUCATION
            )
            step4WithParams.select(availableOptions) {
                TourSituation(
                    it,
                    input.personWithRoutine.person,
                    input.personWithRoutine.routine,
                    input.dayStructure.startTimeDay,

                )
            }
        }
    }
}