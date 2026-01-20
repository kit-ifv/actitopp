package edu.kit.ifv.mobitopp.actitoppNG.timebudgets

import edu.kit.ifv.mobitopp.actitoppNG.enums.ActivityType
import edu.kit.ifv.mobitopp.actitoppNG.modernization.MobilityStructure


/**
 * The activity synthesis is done, the tours are fixed, and so are the activities. They do not change anymore.
 *
 * this could be used in the daily time activity calculation. TODO this is now the MobilityPlan, which does not use
 * the dirty H(X) Fields.
 */

interface FinalizedActivityPattern {
    val workDays: Int
    val educationDays: Int
    val leisureDays: Int
    val shoppingDays: Int
    val transportDays: Int
    val workActivities: Int
    val educationActivities: Int
    val leisureActivities: Int
    val shoppingActivities: Int
    val transportActivities: Int

    companion object {

        fun fromModernPattern(mobilityStructure: MobilityStructure): FinalizedActivityPatternImpl {
            val activityMap = mobilityStructure.activityTypes().groupBy { it }.mapValues { it.value.size }
            return FinalizedActivityPatternImpl(
                workDays = mobilityStructure.amountOfDaysWith(ActivityType.WORK),
                educationDays = mobilityStructure.amountOfDaysWith(ActivityType.EDUCATION),
                leisureDays = mobilityStructure.amountOfDaysWith(ActivityType.LEISURE),
                shoppingDays = mobilityStructure.amountOfDaysWith(ActivityType.SHOPPING),
                transportDays = mobilityStructure.amountOfDaysWith(ActivityType.TRANSPORT),
                workActivities = activityMap[ActivityType.WORK] ?: 0,
                educationActivities = activityMap[ActivityType.EDUCATION] ?: 0,
                leisureActivities = activityMap[ActivityType.LEISURE] ?: 0,
                shoppingActivities = activityMap[ActivityType.SHOPPING] ?: 0,
                transportActivities = activityMap[ActivityType.TRANSPORT] ?: 0,
            )
        }
    }
}

data class FinalizedActivityPatternImpl(
    override val workDays: Int,
    override val educationDays: Int,
    override val leisureDays: Int,
    override val shoppingDays: Int,
    override val transportDays: Int,
    override val workActivities: Int,
    override val educationActivities: Int,
    override val leisureActivities: Int,
    override val shoppingActivities: Int,
    override val transportActivities: Int,
) : FinalizedActivityPattern


interface FinalizedPatternAttributes {
    fun amountOfWorkActivitiesInWeek(): Int
    fun amountOfEducationActivitiesInWeek(): Int
    fun amountOfLeisureActivitiesInWeek(): Int
    fun amountOfShoppingActivitiesInWeek(): Int
    fun amountOfTransportActivitiesInWeek(): Int

    fun amountOfDaysWithWorkActivityIs1(): Boolean
    fun amountOfDaysWithWorkActivityIs2(): Boolean
    fun amountOfDaysWithWorkActivityIs3(): Boolean
    fun amountOfDaysWithWorkActivityIs4(): Boolean
    fun amountOfDaysWithWorkActivityIs5(): Boolean
    fun amountOfDaysWithWorkActivityIs6(): Boolean

    fun amountOfDaysWithLeisureActivityIs1(): Boolean
    fun amountOfDaysWithLeisureActivityIs2(): Boolean
    fun amountOfDaysWithLeisureActivityIs3(): Boolean
    fun amountOfDaysWithLeisureActivityIs4(): Boolean
    fun amountOfDaysWithLeisureActivityIs5(): Boolean

    fun amountOfDaysWithEducationActivityIs2(): Boolean
    fun amountOfDaysWithEducationActivityIs3(): Boolean
    fun amountOfDaysWithEducationActivityIs4(): Boolean
    fun amountOfDaysWithEducationActivityIs5(): Boolean

    fun amountOfDaysWithShoppingActivityIs1(): Boolean
    fun amountOfDaysWithShoppingActivityIs2(): Boolean
    fun amountOfDaysWithShoppingActivityIs3(): Boolean
    fun amountOfDaysWithShoppingActivityIs4(): Boolean

    fun amountOfDaysWithTransportActivityIs1(): Boolean
}

class PatternAttributesByElement(val element: FinalizedActivityPattern) : FinalizedPatternAttributes {
    override fun amountOfWorkActivitiesInWeek(): Int {
        return element.workActivities
    }

    override fun amountOfEducationActivitiesInWeek(): Int {
        return element.educationActivities
    }

    override fun amountOfLeisureActivitiesInWeek(): Int = element.leisureActivities
    override fun amountOfShoppingActivitiesInWeek(): Int = element.shoppingActivities
    override fun amountOfTransportActivitiesInWeek(): Int = element.transportActivities
    override fun amountOfDaysWithWorkActivityIs1() = element.workDays == 1
    override fun amountOfDaysWithWorkActivityIs2() = element.workDays == 2
    override fun amountOfDaysWithWorkActivityIs3() = element.workDays == 3
    override fun amountOfDaysWithWorkActivityIs4() = element.workDays == 4
    override fun amountOfDaysWithWorkActivityIs5() = element.workDays == 5
    override fun amountOfDaysWithWorkActivityIs6() = element.workDays == 6

    override fun amountOfDaysWithEducationActivityIs2() = element.educationDays == 2
    override fun amountOfDaysWithEducationActivityIs3() = element.educationDays == 3
    override fun amountOfDaysWithEducationActivityIs4() = element.educationDays == 4
    override fun amountOfDaysWithEducationActivityIs5() = element.educationDays == 5

    override fun amountOfDaysWithLeisureActivityIs1() = element.leisureDays == 1
    override fun amountOfDaysWithLeisureActivityIs2() = element.leisureDays == 2
    override fun amountOfDaysWithLeisureActivityIs3() = element.leisureDays == 3
    override fun amountOfDaysWithLeisureActivityIs4() = element.leisureDays == 4
    override fun amountOfDaysWithLeisureActivityIs5() = element.leisureDays == 5

    override fun amountOfDaysWithShoppingActivityIs1() = element.shoppingDays == 1
    override fun amountOfDaysWithShoppingActivityIs2() = element.shoppingDays == 2
    override fun amountOfDaysWithShoppingActivityIs3() = element.shoppingDays == 3
    override fun amountOfDaysWithShoppingActivityIs4() = element.shoppingDays == 4


    override fun amountOfDaysWithTransportActivityIs1() = element.transportDays == 1

}