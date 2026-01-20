package edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure

import edu.kit.ifv.mobitopp.actitoppNG.Person
import edu.kit.ifv.mobitopp.actitoppNG.weekroutine.WeekRoutine

fun interface GenerateMobilityStructure {
    fun generate(person: Person, weekRoutine: WeekRoutine): IMobilityStructure
}