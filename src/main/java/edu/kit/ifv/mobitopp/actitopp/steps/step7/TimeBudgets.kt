package edu.kit.ifv.mobitopp.actitopp.steps.step7

import edu.kit.ifv.mobitopp.actitopp.HDay
import edu.kit.ifv.mobitopp.actitopp.HWeekPattern
import edu.kit.ifv.mobitopp.actitopp.changes.Category
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType

data class TimeBudgets(

    val workCategory: Category,
    val workBudget: Int,

    val educationCategory: Category,
    val educationBudget: Int,

    val leisureCategory: Category,
    val leisureBudget: Int,

    val shoppingCategory: Category,
    val shoppingBudget: Int,

    val transportCategory: Category,
    val transportBudget: Int,
) {
    fun calculateMeanTime(day: HDay, activityType: ActivityType): Int {
        val daysWithActivity = day.pattern.countDaysWithSpecificActivity(activityType)
        val activitiesForThisDay = day.getTotalNumberOfActivitites(activityType)

        val factor = when(activityType) {
            ActivityType.WORK -> workBudget
            ActivityType.EDUCATION -> educationBudget
            ActivityType.LEISURE -> leisureBudget
            ActivityType.SHOPPING -> shoppingBudget
            ActivityType.TRANSPORT -> transportBudget
            else -> throw NotImplementedError("There is no way to calculate the mean time for an activity of the type $activityType")
        }

        return (factor.toDouble() / daysWithActivity / activitiesForThisDay).toInt()
    }
}