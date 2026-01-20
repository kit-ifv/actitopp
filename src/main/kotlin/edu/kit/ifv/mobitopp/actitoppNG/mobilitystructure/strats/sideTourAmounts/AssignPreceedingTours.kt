package edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.strats.sideTourAmounts


import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitoppNG.modernization.DayStructure
import edu.kit.ifv.mobitopp.actitoppNG.modernization.PlannedTourAmounts
import kotlin.random.Random

data class PrecedingInput(
    val personInfo: PersonWithRoutine,
    val day: DayStructure,
    val currentPlannedTourAmounts: PlannedTourAmounts,
    val previousPlannedTourAmounts: PlannedTourAmounts?,

    )


fun interface GenerateSideTours {
    context(rng: Random)
    fun generate(precedingInput: PrecedingInput): Int

}
