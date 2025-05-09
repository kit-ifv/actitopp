package edu.kit.ifv.mobitopp.actitopp

import edu.kit.ifv.mobitopp.actitopp.steps.DayActivityTracker
import org.jetbrains.annotations.TestOnly
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

    fun toWeekRoutine(): WeekRoutine {
        return WeekRoutine(
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
fun ActitoppPerson.toModifiable() = ActitoppPersonModifierFields(this)

data class WeekRoutine(
    val amountOfWorkingDays: Int,
    val amountOfEducationDays: Int,
    val amountOfLeisureDays: Int,
    val amountOfShoppingDays: Int,
    val amountOfServiceDays: Int,
    val amountOfImmobileDays: Int,
    val averageAmountOfTours: Int,
    val averageAmountOfActivities: Int,
) {
    /**
     * Generates a tracker instance, where none of the days are set to be work or education respectively.
     */
    fun instantiateTracker() : DayActivityTracker {
        return DayActivityTracker(amountOfWorkingDays, amountOfEducationDays, emptySet(), emptySet())
    }
    // TODO this is only required for testing against the legacy code base, once established this can be removed.
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

    /**
     * To enable testing, we need to be able to load the person attributes based on the Person Week Routine.
     */
    @TestOnly
    // TODO this is only required for testing against the legacy code base, once established this can be removed.
    fun loadToAttributeMap(attributeMap: MutableMap<String, Double>) {
        attributeMap["anztage_w"] = amountOfWorkingDays.toDouble()
        attributeMap["anztage_e"] = amountOfEducationDays.toDouble()
        attributeMap["anztage_l"] = amountOfLeisureDays.toDouble()
        attributeMap["anztage_s"] = amountOfShoppingDays.toDouble()
        attributeMap["anztage_t"] = amountOfServiceDays.toDouble()
        attributeMap["anztage_immobil"] = amountOfImmobileDays.toDouble()
        attributeMap["anztourentag_mean"] = averageAmountOfTours.toDouble()
        attributeMap["anzakttag_mean"] = averageAmountOfActivities.toDouble()
    }
}



