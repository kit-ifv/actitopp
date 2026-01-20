package edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.strats

import edu.kit.ifv.mobitopp.actitoppNG.RNGHelper
import edu.kit.ifv.mobitopp.actitoppNG.modernization.ByTracker
import edu.kit.ifv.mobitopp.actitoppNG.modernization.MobilityStructure
import edu.kit.ifv.mobitopp.actitoppNG.modernization.SecondaryActInput
import edu.kit.ifv.mobitopp.actitoppNG.modernization.TrackedSecondaryActivities
import kotlin.random.Random

fun interface SpawnSideActivities {
    context(rng: Random)
    fun spawnSideActivities(input: MobilityStructure)
}

class StandardImplementation() : SpawnSideActivities {
    context(rng: Random)
    override fun spawnSideActivities(input: MobilityStructure) {
        val activityTypeGeneration = TrackedSecondaryActivities(input)


        input.elements().forEach { day ->
            val plannedAmounts = ByTracker(day).determine(input)
            day.trackedElements().forEach { indexedTour ->
                val originalTour = indexedTour.element

                val plan = plannedAmounts[indexedTour]
                    ?: throw NoSuchElementException("There should be calculated tour amounts for $indexedTour")
                val (precursors, successors) = activityTypeGeneration.generateSecondaryActivityTypes(
                    SecondaryActInput(
                        day,
                        indexedTour,
                        plan
                    )
                )
                originalTour.loadPrecursors(precursors)
                originalTour.loadSuccessors(successors)
            }
        }
    }
}