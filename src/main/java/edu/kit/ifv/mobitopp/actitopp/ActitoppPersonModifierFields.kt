package edu.kit.ifv.mobitopp.actitopp

import kotlin.properties.Delegates

class ActitoppPersonModifierFields(val original: ActitoppPerson) {
    var amountOfWorkingDays: Int by Delegates.notNull()
    var amountOfEducationDays: Int by Delegates.notNull()
    var amountOfLeisureDays: Int by Delegates.notNull()
    var amountOfServiceDays: Int by Delegates.notNull()
}



data class PersonWeekRoutine(
    val amountOfWorkingDays: Int,
    val amountOfEducationDays: Int,
    val amountOfLeisureDays: Int,
    val amountOfShoppingDays: Int,
    val amountOfServiceDays: Int,

    val amountOfImmobileDays: Int,
    val averageAmountOfTours: Int,
    val averageAmountOfActivities: Int,
)
fun ActitoppPerson.toModifiable() = ActitoppPersonModifierFields(this)

