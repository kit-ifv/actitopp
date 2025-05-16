package edu.kit.ifv.mobitopp.actitopp.steps

import edu.kit.ifv.mobitopp.actitopp.HDay
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.modernization.DayStructure
import edu.kit.ifv.mobitopp.actitopp.modernization.DurationDay
import java.time.DayOfWeek

interface DayAttributes {
    fun isMonday(): Boolean
    fun isTuesday(): Boolean
    fun isWednesday(): Boolean
    fun isThursday(): Boolean
    fun isFriday(): Boolean
    fun isSaturday(): Boolean
    fun isSunday(): Boolean

    fun isStandardWorkingDay(): Boolean




}

interface DayStructureAttributes : DayAttributes {

    fun dayMainActivityIsWork(): Boolean
    fun dayMainActivityIsEducation(): Boolean
    fun dayMainActivityIsShopping(): Boolean
    fun dayMainActivityIsTransport(): Boolean
}

/**
 * When the day is completely structured, we can determine the amount of tours.
 */
interface FullyQualifiedDayStructureAttributes : DayStructureAttributes {
    fun amountOfToursIs1():Boolean
    fun amountOfToursIs2():Boolean
}

fun interface PartialTourLayoutAttributes {
    fun amountOfBeforeTours():Int
}



class DayAttributesFromElement(private val element: HDay) :  PartialTourLayoutAttributes, DayStructureAttributes, FullyQualifiedDayStructureAttributes {
    override fun isMonday() = element.weekday == DayOfWeek.MONDAY
    override fun isTuesday() = element.weekday == DayOfWeek.TUESDAY
    override fun isWednesday() = element.weekday == DayOfWeek.WEDNESDAY
    override fun isThursday() = element.weekday == DayOfWeek.THURSDAY
    override fun isFriday() = element.weekday == DayOfWeek.FRIDAY
    override fun isSaturday() = element.weekday == DayOfWeek.SATURDAY
    override fun isSunday() = element.weekday == DayOfWeek.SUNDAY
    override fun isStandardWorkingDay(): Boolean = element.weekday in DayOfWeek.MONDAY..DayOfWeek.FRIDAY
    override fun amountOfBeforeTours(): Int = -1 * element.lowestTourIndex
    override fun amountOfToursIs1(): Boolean = element.amountOfTours == 1
    override fun amountOfToursIs2(): Boolean = element.amountOfTours == 2
    override fun dayMainActivityIsWork(): Boolean = element.mainTourType == ActivityType.WORK
    override fun dayMainActivityIsEducation(): Boolean = element.mainTourType == ActivityType.EDUCATION
    override fun dayMainActivityIsShopping(): Boolean = element.mainTourType == ActivityType.SHOPPING
    override fun dayMainActivityIsTransport(): Boolean = element.mainTourType == ActivityType.TRANSPORT
}

class DayAttributesFromStructure(private val element: DayStructure):  PartialTourLayoutAttributes, DayStructureAttributes, FullyQualifiedDayStructureAttributes {

    override fun isMonday() = element.weekday == DayOfWeek.MONDAY
    override fun isTuesday() = element.weekday == DayOfWeek.TUESDAY
    override fun isWednesday() = element.weekday == DayOfWeek.WEDNESDAY
    override fun isThursday() = element.weekday == DayOfWeek.THURSDAY
    override fun isFriday() = element.weekday == DayOfWeek.FRIDAY
    override fun isSaturday() = element.weekday == DayOfWeek.SATURDAY
    override fun isSunday() = element.weekday == DayOfWeek.SUNDAY
    override fun isStandardWorkingDay(): Boolean = element.weekday in DayOfWeek.MONDAY..DayOfWeek.FRIDAY
    override fun amountOfBeforeTours(): Int = element.amountOfPrecursorElements()
    override fun amountOfToursIs1(): Boolean = element.amountOfElements() ==1
    override fun amountOfToursIs2(): Boolean = element.amountOfElements() ==2
    override fun dayMainActivityIsWork(): Boolean = element.mainActivityType() == ActivityType.WORK
    override fun dayMainActivityIsEducation(): Boolean = element.mainActivityType() == ActivityType.EDUCATION
    override fun dayMainActivityIsShopping(): Boolean = element.mainActivityType() == ActivityType.SHOPPING
    override fun dayMainActivityIsTransport(): Boolean = element.mainActivityType() == ActivityType.TRANSPORT
}

class DayAttributesFromWeekday(private val element: DayOfWeek): DayAttributes {

    constructor(element: DurationDay) : this(element.weekday)
    override fun isMonday(): Boolean = element == DayOfWeek.MONDAY
    override fun isTuesday(): Boolean = element == DayOfWeek.TUESDAY
    override fun isWednesday(): Boolean = element == DayOfWeek.WEDNESDAY
    override fun isThursday(): Boolean = element == DayOfWeek.THURSDAY
    override fun isFriday(): Boolean = element == DayOfWeek.FRIDAY
    override fun isSaturday(): Boolean = element == DayOfWeek.SATURDAY
    override fun isSunday(): Boolean = element == DayOfWeek.SUNDAY
    override fun isStandardWorkingDay(): Boolean = element in DayOfWeek.MONDAY..DayOfWeek.FRIDAY
}