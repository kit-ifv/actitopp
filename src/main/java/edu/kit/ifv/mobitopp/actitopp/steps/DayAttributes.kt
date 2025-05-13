package edu.kit.ifv.mobitopp.actitopp.steps

import edu.kit.ifv.mobitopp.actitopp.HDay
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.modernization.DayStructure
import edu.kit.ifv.mobitopp.actitopp.steps.step3.PreviousDayAttributes
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
    fun amountOfToursIs1():Boolean
    fun amountOfToursIs2():Boolean
}

fun interface PartialTourLayoutAttributes {
    fun amountOfBeforeTours():Int
}

interface PlannedTourAttributes {
    fun mainActivityIsWork(): Boolean
    fun mainActivityIsEducation(): Boolean
    fun mainActivityIsShopping(): Boolean
    fun mainActivityIsTransport(): Boolean


}

interface DayAndTourPlanAttributes: DayAttributes, PlannedTourAttributes

interface DayAndPartialTourLayout: DayAttributes, PartialTourLayoutAttributes


class PartialLayout private constructor(val previousDayAttributes: PreviousDayAttributes,
    val plannedPrecursorTours:Int): DayAndPartialTourLayout, PreviousDayAttributes by previousDayAttributes   {
    override fun amountOfBeforeTours(): Int {
        return plannedPrecursorTours
    }

}

class DayAttributesFromElement(private val element: HDay) : DayAndTourPlanAttributes, PartialTourLayoutAttributes, DayStructureAttributes {
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
    override fun mainActivityIsWork(): Boolean = element.mainTourType == ActivityType.WORK
    override fun mainActivityIsEducation(): Boolean = element.mainTourType == ActivityType.EDUCATION
    override fun mainActivityIsShopping(): Boolean = element.mainTourType == ActivityType.SHOPPING
    override fun mainActivityIsTransport(): Boolean = element.mainTourType == ActivityType.TRANSPORT
}

class DayAttributesFromStructure(private val element: DayStructure): DayAndTourPlanAttributes, PartialTourLayoutAttributes, DayStructureAttributes {

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
    override fun mainActivityIsWork(): Boolean = element.mainTourActivityType() == ActivityType.WORK
    override fun mainActivityIsEducation(): Boolean = element.mainTourActivityType() == ActivityType.EDUCATION
    override fun mainActivityIsShopping(): Boolean = element.mainTourActivityType() == ActivityType.SHOPPING
    override fun mainActivityIsTransport(): Boolean = element.mainTourActivityType() == ActivityType.TRANSPORT
}

class DayAttributesFromWeekday(private val element: DayOfWeek): DayAttributes {
    override fun isMonday(): Boolean = element == DayOfWeek.MONDAY
    override fun isTuesday(): Boolean = element == DayOfWeek.TUESDAY
    override fun isWednesday(): Boolean = element == DayOfWeek.WEDNESDAY
    override fun isThursday(): Boolean = element == DayOfWeek.THURSDAY
    override fun isFriday(): Boolean = element == DayOfWeek.FRIDAY
    override fun isSaturday(): Boolean = element == DayOfWeek.SATURDAY
    override fun isSunday(): Boolean = element == DayOfWeek.SUNDAY
    override fun isStandardWorkingDay(): Boolean = element in DayOfWeek.MONDAY..DayOfWeek.FRIDAY
}