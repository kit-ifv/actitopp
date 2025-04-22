package edu.kit.ifv.mobitopp.actitopp

import kotlin.properties.Delegates

class ActitoppPersonModifierFields(val original: ActitoppPerson) {
    var amountOfWorkingDays: Int by Delegates.notNull()
    var amountOfEducationDays: Int by Delegates.notNull()
    var amountOfLeisureDays: Int by Delegates.notNull()
    var amountOfServiceDays: Int by Delegates.notNull()
}


fun ActitoppPerson.toModifiable() = ActitoppPersonModifierFields(this)