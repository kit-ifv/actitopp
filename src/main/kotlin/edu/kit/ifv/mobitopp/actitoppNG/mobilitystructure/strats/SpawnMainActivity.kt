package edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.strats

import edu.kit.ifv.mobitopp.actitoppNG.RNGHelper
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.choicemodels.mainActivityChoiceModel
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.shenanigans.DayAlternative
import edu.kit.ifv.mobitopp.actitoppNG.modernization.MobilityStructure
import edu.kit.ifv.mobitopp.actitoppNG.modernization.Step2Tracking
import kotlin.random.Random

fun interface SpawnMainActivity {
    context(rng: Random)
    fun generateNewDay(mobilityStructure: MobilityStructure)
}

class SpawnWithRespect() : SpawnMainActivity {
    context(rng: Random)
    override fun generateNewDay(mobilityStructure: MobilityStructure) {
        val nextDay = mobilityStructure.nextDay()
        val availableOptions = Step2Tracking.determineAvailableOptions(
            mobilityStructure.getTracker(),
            mobilityStructure.weekRoutine,
            mainActivityChoiceModel.choices
        )
        val activityType = context(DayAlternative(mobilityStructure.weekRoutine, nextDay.weekday), rng) {
            mainActivityChoiceModel.select(availableOptions)
        }
        mobilityStructure.add(nextDay, activityType)

    }
}