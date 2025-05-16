package edu.kit.ifv.mobitopp.actitopp.modernization

import edu.kit.ifv.mobitopp.actitopp.RNGHelper
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitopp.steps.step5.DayAmountTracker

class Step5Generator(
    private val patternStructure: PatternStructure,
    val personWithRoutine: PersonWithRoutine,
    val rngHelper: RNGHelper,
) {

    private val relevantDays = patternStructure.mobileDays()
    val map = relevantDays.associateWith {
        DayAmountTracker(it, rngHelper, personWithRoutine)
    }
    private val dayMap: Map<DayStructure, Collection<BidirectionalIndexedValue<TourStructure>>> = relevantDays.associateWith {
        it.indexedElements()
    }
    private val output: Map<DayStructure, Map<BidirectionalIndexedValue<TourStructure>, ModifiablePlannedTourAmounts>> =
        relevantDays.associateWith {
            dayMap.getValue(it).associateWith { ModifiablePlannedTourAmounts() }
        }

    fun calculate() {
        map.entries.forEach { (day, tracker) ->
            val predecessors = tracker.generatePredecessors(dayMap.getValue(day))
            val mapy = output.getValue(day)
            predecessors.forEach { (k, v) ->
                mapy.getValue(k).precursorAmount = v
            }

        }

        map.entries.forEach { (day, tracker) ->
            val successors = tracker.generateSuccessors(dayMap.getValue(day))
            val mapy = output.getValue(day)
            successors.forEach { (k, v) ->
                mapy.getValue(k).successorAmount = v
            }

        }
    }

    fun output(): Map<DayStructure, Map<BidirectionalIndexedValue<TourStructure>, PlannedTourAmounts>> {
        return output
    }


}

