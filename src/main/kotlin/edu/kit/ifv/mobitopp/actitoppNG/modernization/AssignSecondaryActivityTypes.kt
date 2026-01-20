package edu.kit.ifv.mobitopp.actitoppNG.modernization

import edu.kit.ifv.mobitopp.actitoppNG.RNGHelper
import edu.kit.ifv.mobitopp.actitoppNG.enums.ActivityType
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.choicemodels.sideActivityChoiceModel
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.shenanigans.ActivityAlternative
import edu.kit.ifv.mobitopp.actitoppNG.utils.BidirectionalIndexedValue
import edu.kit.ifv.mobitopp.actitoppNG.utils.Position
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModel
import kotlin.random.Random

data class SecondaryActInput(
    val dayStructure: DayStructure,
    val tourStructure: BidirectionalIndexedValue<TourStructure>,
    val plannedTourAmounts: PlannedTourAmounts,
)

interface AssignSecondaryActivityTypes {
    context(rng: Random)
    fun generateSecondaryActivityTypes(input: SecondaryActInput): Pair<List<ActivityType>, List<ActivityType>>
}

/**
 * Generate the secondary activities, with respect to the week routine, so that the options for work and education
 * are removed if the week routine is already satisfied.
 */
class TrackedSecondaryActivities(
    private val mobilityStructure: MobilityStructure,
    private val choiceModel: FixedChoiceModel<ActivityType, ActivityAlternative> = sideActivityChoiceModel,
) : AssignSecondaryActivityTypes {
    val personWithRoutine: PersonWithRoutine = mobilityStructure.weekRoutine
    context(rng: Random)
    override fun generateSecondaryActivityTypes(input: SecondaryActInput):
            Pair<List<ActivityType>, List<ActivityType>> {
        val precursors = input.plannedTourAmounts.precursorAmount
        val successors = input.plannedTourAmounts.successorAmount

        return (0..<precursors).calculate(Position.BEFORE, input) to
                (0..<successors).calculate(Position.AFTER, input)
    }
    context(rng: Random)
    private fun Iterable<Int>.calculate(position: Position, input: SecondaryActInput): List<ActivityType> {
        val day = input.dayStructure.startTimeDay
        val routine = personWithRoutine.routine
        return map { absoluteIndex ->
            mobilityStructure.generateTrackedActivity(input.dayStructure.startTimeDay) {
                val availableOptions = choiceModel.choices.toMutableSet()
                if (!personWithRoutine.person.isAllowedToWork) availableOptions.remove(ActivityType.WORK)
                if (day.shouldNotBeWorkDay(routine)) availableOptions.remove(
                    ActivityType.WORK
                )
                if (day.shouldNotBeEducationDay(routine)) availableOptions.remove(
                    ActivityType.EDUCATION
                )

                context(
                    ActivityAlternative(
                        personWithRoutine,
                        input.dayStructure,
                        input.tourStructure, position, input.plannedTourAmounts
                    )
                ) {
                    choiceModel.select(availableOptions)
                }
            }
        }
    }


}