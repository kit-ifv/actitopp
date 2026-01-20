package edu.kit.ifv.mobitopp.actitoppNG.weekroutine

import kotlinx.serialization.Serializable
import kotlin.properties.Delegates

/**
 * This class provides a mutable state of a [WeekRoutine]. The fields are deliberately set to Delegates.notNull() instead
 * of = 0. This way preemptive access of uninitialized fields causes an error, rather than a silent calculation with 0.
 * (We assume that this silent calculation mismatch is not desired, and thus guarded with errors)
 */
@Serializable
class ModifiableWeekRoutine : WeekRoutine {
    override var amountOfWorkingDays: Int by Delegates.notNull()
    override var amountOfEducationDays: Int by Delegates.notNull()
    override var amountOfLeisureDays: Int by Delegates.notNull()
    override var amountOfShoppingDays: Int by Delegates.notNull()
    override var amountOfServiceDays: Int by Delegates.notNull()
    override var amountOfImmobileDays: Int by Delegates.notNull()
    override var averageAmountOfTours: Int by Delegates.notNull()
    override var averageAmountOfActivities: Int by Delegates.notNull()
}



