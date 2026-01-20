package edu.kit.ifv.mobitopp.actitoppNG.modernization

import edu.kit.ifv.mobitopp.actitoppNG.RNGHelper
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.strats.subsubTourAmount.DayAmountTracker
import edu.kit.ifv.mobitopp.actitoppNG.utils.BidirectionalIndexedValue
import kotlin.random.Random

fun interface DetermineTourAmounts {
    context(rng: Random)
    fun determine(input: MobilityStructure): Map<BidirectionalIndexedValue<TourStructure>, PlannedTourAmounts>
}

class ByTracker(val day: DayStructure) : DetermineTourAmounts {
    context(rng: Random)
    override fun determine(input: MobilityStructure): Map<BidirectionalIndexedValue<TourStructure>, PlannedTourAmounts> {
        val tracker = DayAmountTracker(day,  input.weekRoutine)
        val predecessors = tracker.generatePredecessors(day.indexedElements())
        val successors = tracker.generateSuccessors(day.indexedElements())

        return predecessors.entries.associate { (k, v) -> k to PlannedTourAmounts(v, successors[k]!!) }

    }
}


