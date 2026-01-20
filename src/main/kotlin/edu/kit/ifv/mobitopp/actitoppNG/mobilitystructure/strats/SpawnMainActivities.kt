package edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.strats

import edu.kit.ifv.mobitopp.actitoppNG.RNGHelper
import edu.kit.ifv.mobitopp.actitoppNG.modernization.MobilityStructure
import kotlin.random.Random

fun interface SpawnMainActivities {
    context(rng: Random)
    fun spawnMainActivities(input: MobilityStructure)
}

class SpawnWeek(
    private val dayStrategy: SpawnMainActivity = SpawnWithRespect(),
) : SpawnMainActivities {
    context(rng: Random)
    override fun spawnMainActivities(input: MobilityStructure) {
        repeat(7) {
            dayStrategy.generateNewDay(input)
        }
    }
}