package edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure

import edu.kit.ifv.mobitopp.actitoppNG.modernization.DayStructure
import edu.kit.ifv.mobitopp.actitoppNG.modernization.plan.DetermineTripDuration
import edu.kit.ifv.mobitopp.actitoppNG.modernization.plan.MobilityPlan
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.TimeBudgets
import java.util.NavigableSet

interface IMobilityStructure {
    val days: NavigableSet<DayStructure>

    fun toPlan(tripDuration: DetermineTripDuration, timeBudgets: TimeBudgets): MobilityPlan
}