package edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure

import edu.kit.ifv.mobitopp.actitoppNG.Person
import edu.kit.ifv.mobitopp.actitoppNG.weekroutine.WeekRoutine

data class PersonWithRoutine(
    val person: Person,
    val routine: WeekRoutine,
) : Person by person {


    fun amountOfWorkingDays() = routine.amountOfWorkingDays
    fun amountOfLeisureDays() = routine.amountOfLeisureDays
    fun amountOfEducationDays() = routine.amountOfEducationDays

}