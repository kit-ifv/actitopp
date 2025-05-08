package edu.kit.ifv.mobitopp.actitopp.steps.step7

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.HWeekPattern
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType


/**
 * The activity synthesis is done, the tours are fixed, and so are the activities. They do not change anymore.
 */
class FinalizedActivityPattern(
    val person: ActitoppPerson,
    pattern: HWeekPattern,
) {
    val workDays = pattern.days.filter { it.hasActivity(ActivityType.WORK) }
    val educationDays = pattern.days.filter { it.hasActivity(ActivityType.EDUCATION) }
    val leisureDays = pattern.days.filter { it.hasActivity(ActivityType.LEISURE) }
    val shoppingDays = pattern.days.filter { it.hasActivity(ActivityType.SHOPPING) }
    val transportDays = pattern.days.filter { it.hasActivity(ActivityType.TRANSPORT) }

    val workActivities = pattern.allActivities.filter { it.activityType == ActivityType.WORK }
    val educationActivities = pattern.allActivities.filter { it.activityType == ActivityType.EDUCATION }
    val leisureActivities = pattern.allActivities.filter { it.activityType == ActivityType.LEISURE }
    val shoppingActivities = pattern.allActivities.filter { it.activityType == ActivityType.SHOPPING }
    val transportActivities = pattern.allActivities.filter { it.activityType == ActivityType.TRANSPORT }

}

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

class PatternAttributesByElement(val element: FinalizedActivityPattern): FinalizedPatternAttributes {
    override fun amountOfWorkActivitiesInWeek(): Int {
        return element.workActivities.size
    }

    override fun amountOfEducationActivitiesInWeek(): Int {
        return element.educationActivities.size
    }
    override fun amountOfLeisureActivitiesInWeek(): Int = element.leisureActivities.size
    override fun amountOfShoppingActivitiesInWeek(): Int = element.shoppingActivities.size
    override fun amountOfTransportActivitiesInWeek(): Int = element.transportActivities.size
    override fun amountOfDaysWithWorkActivityIs1() = element.workDays.size == 1
    override fun amountOfDaysWithWorkActivityIs2() = element.workDays.size == 2
    override fun amountOfDaysWithWorkActivityIs3() = element.workDays.size == 3
    override fun amountOfDaysWithWorkActivityIs4() = element.workDays.size == 4
    override fun amountOfDaysWithWorkActivityIs5() = element.workDays.size == 5
    override fun amountOfDaysWithWorkActivityIs6() = element.workDays.size == 6

    override fun amountOfDaysWithEducationActivityIs2() = element.educationDays.size == 2
    override fun amountOfDaysWithEducationActivityIs3() = element.educationDays.size == 3
    override fun amountOfDaysWithEducationActivityIs4() = element.educationDays.size == 4
    override fun amountOfDaysWithEducationActivityIs5() = element.educationDays.size == 5

    override fun amountOfDaysWithLeisureActivityIs1() = element.leisureDays.size == 1
    override fun amountOfDaysWithLeisureActivityIs2() = element.leisureDays.size == 2
    override fun amountOfDaysWithLeisureActivityIs3() = element.leisureDays.size == 3
    override fun amountOfDaysWithLeisureActivityIs4() = element.leisureDays.size == 4
    override fun amountOfDaysWithLeisureActivityIs5() = element.leisureDays.size == 5

    override fun amountOfDaysWithShoppingActivityIs1() = element.shoppingDays.size == 1
    override fun amountOfDaysWithShoppingActivityIs2() = element.shoppingDays.size == 2
    override fun amountOfDaysWithShoppingActivityIs3() = element.shoppingDays.size == 3
    override fun amountOfDaysWithShoppingActivityIs4() = element.shoppingDays.size == 4


    override fun amountOfDaysWithTransportActivityIs1() = element.transportDays.size == 4

}