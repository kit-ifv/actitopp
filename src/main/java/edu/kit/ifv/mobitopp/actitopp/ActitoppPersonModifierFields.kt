package edu.kit.ifv.mobitopp.actitopp

import kotlin.properties.Delegates

class ActitoppPersonModifierFields(val original: ActitoppPerson) {
    var amountOfWorkingDays: Int by Delegates.notNull()
    var amountOfEducationDays: Int by Delegates.notNull()
    var amountOfLeisureDays: Int by Delegates.notNull()
    var amountOfShoppingDays: Int by Delegates.notNull()
    var amountOfServiceDays: Int by Delegates.notNull()
    var amountOfImmobileDays: Int by Delegates.notNull()
    var averageAmountOfTours: Int by Delegates.notNull()
    var averageAmountOfActivities: Int by Delegates.notNull()

    fun toWeekRoutine(): PersonWeekRoutine {
        return PersonWeekRoutine(
            amountOfWorkingDays = amountOfWorkingDays,
            amountOfEducationDays = amountOfEducationDays,
            amountOfLeisureDays = amountOfLeisureDays,
            amountOfShoppingDays = amountOfShoppingDays,
            amountOfServiceDays = amountOfServiceDays,
            amountOfImmobileDays = amountOfImmobileDays,
            averageAmountOfTours = averageAmountOfTours,
            averageAmountOfActivities = averageAmountOfActivities
        )
    }
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
) {
    fun similarToAttributeMap(attributeMap: Map<String, Double>): Boolean {
        return amountOfWorkingDays == attributeMap["anztage_w"]?.toInt() &&
        amountOfEducationDays == attributeMap["anztage_e"]?.toInt() &&
        amountOfLeisureDays == attributeMap["anztage_l"]?.toInt() &&
        amountOfShoppingDays == attributeMap["anztage_s"]?.toInt() &&
        amountOfServiceDays == attributeMap["anztage_t"]?.toInt() &&
        amountOfImmobileDays == attributeMap["anztage_immobil"]?.toInt() &&
        averageAmountOfTours == attributeMap["anztourentag_mean"]?.toInt() &&
        averageAmountOfActivities == attributeMap["anzakttag_mean"]?.toInt()
    }
}
fun ActitoppPerson.toModifiable() = ActitoppPersonModifierFields(this)

