package edu.kit.ifv.mobitopp.actitoppNG.modernization

import edu.kit.ifv.mobitopp.actitoppNG.RNGHelper
import edu.kit.ifv.mobitopp.actitoppNG.enums.ActivityType
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.choicemodels.tourMainActivityChoiceModel
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.shenanigans.TourAlternative
import edu.kit.ifv.mobitopp.actitoppNG.steps.TourPositionAttributesByIndex
import edu.kit.ifv.mobitopp.actitoppNG.utils.Position
import kotlin.random.Random

class DayWithPlans(
    val dayStructure: DayStructure,
    val personWithRoutine: PersonWithRoutine,
    val plannedTourAmounts: PlannedTourAmounts,
)


fun interface AssignMainActivityOfSideTour {
    context(rng: Random)
    fun generateSideTourActivities(input: DayWithPlans): Pair<List<ActivityType>, List<ActivityType>>
}


class AssignByUtilityFunction(private val mobilityStructure: MobilityStructure) :
    AssignMainActivityOfSideTour {
    context(rng: Random)
    override fun generateSideTourActivities(input: DayWithPlans): Pair<List<ActivityType>, List<ActivityType>> {
        val plannedPrecursors = input.plannedTourAmounts.precursorAmount
        val plannedSuccessors = input.plannedTourAmounts.successorAmount
        val x = (0..<plannedPrecursors).map {
            it
        }
        val y = (0..<plannedSuccessors).map {
            it + plannedPrecursors + 1
        }
        return x.calculate(Position.BEFORE, input) to y.calculate(Position.AFTER, input)


    }
    context(rng: Random)
    private fun List<Int>.calculate(position: Position, input: DayWithPlans): List<ActivityType> {
        return map { absoluteIndex ->
            mobilityStructure.generateTrackedActivity(input.dayStructure.startTimeDay) { day ->

                val routine = input.personWithRoutine.routine
                val availableOptions = tourMainActivityChoiceModel.choices.toMutableSet()
                if (!input.personWithRoutine.person.isAllowedToWork) availableOptions.remove(ActivityType.WORK)
                if (day.shouldNotBeWorkDay(routine)) availableOptions.remove(
                    ActivityType.WORK
                )
                if (day.shouldNotBeEducationDay(routine)) availableOptions.remove(
                    ActivityType.EDUCATION
                )
                context(TourAlternative(
                    input.personWithRoutine.person,
                    input.personWithRoutine.routine,
                    input.dayStructure,
                    TourPositionAttributesByIndex(absoluteIndex, position)
                )) {
                    tourMainActivityChoiceModel.select(availableOptions)
                }

            }
        }
    }
}