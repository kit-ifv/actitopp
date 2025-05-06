package edu.kit.ifv.mobitopp.actitopp.steps

import edu.kit.ifv.mobitopp.actitopp.HDay
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import java.time.DayOfWeek

interface DayAttributes {
    fun isMonday(): Boolean
    fun isTuesday(): Boolean
    fun isWednesday(): Boolean
    fun isThursday(): Boolean
    fun isFriday(): Boolean
    fun isSaturday(): Boolean
    fun isSunday(): Boolean

    fun amountOfBeforeTours(): Int

    fun mainActivityIsWork(): Boolean
    fun mainActivityIsEducation(): Boolean
    fun mainActivityIsShopping(): Boolean
    fun mainActivityIsTransport(): Boolean

    fun amountOfToursIs1():Boolean
    fun amountOfToursIs2():Boolean
}

class DayAttributesFromElement(private val element: HDay) : DayAttributes {
    override fun isMonday() = element.weekday == DayOfWeek.MONDAY
    override fun isTuesday() = element.weekday == DayOfWeek.TUESDAY
    override fun isWednesday() = element.weekday == DayOfWeek.WEDNESDAY
    override fun isThursday() = element.weekday == DayOfWeek.THURSDAY
    override fun isFriday() = element.weekday == DayOfWeek.FRIDAY
    override fun isSaturday() = element.weekday == DayOfWeek.SATURDAY
    override fun isSunday() = element.weekday == DayOfWeek.SUNDAY

    override fun amountOfBeforeTours(): Int = -1 * element.lowestTourIndex
    override fun amountOfToursIs1(): Boolean = element.amountOfTours == 1
    override fun amountOfToursIs2(): Boolean = element.amountOfTours == 2
    override fun mainActivityIsWork(): Boolean = element.mainTourType == ActivityType.WORK
    override fun mainActivityIsEducation(): Boolean = element.mainTourType == ActivityType.EDUCATION
    override fun mainActivityIsShopping(): Boolean = element.mainTourType == ActivityType.SHOPPING
    override fun mainActivityIsTransport(): Boolean = element.mainTourType == ActivityType.TRANSPORT
}