package edu.kit.ifv.mobitopp.actitopp

import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.steps.step4.DayActivityTracker
import edu.kit.ifv.mobitopp.actitopp.steps.step4.SideTourInput

data class UtilityDebug<T>(
    val options: Collection<T>,
    val utilities: Map<T, Double>,
    val probabilities: Map<T, Double>,
    val randomNumber: Double,
    val selection: T,
)

