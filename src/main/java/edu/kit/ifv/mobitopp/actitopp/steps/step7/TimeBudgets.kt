package edu.kit.ifv.mobitopp.actitopp.steps.step7

import edu.kit.ifv.mobitopp.actitopp.HDay
import edu.kit.ifv.mobitopp.actitopp.HWeekPattern
import edu.kit.ifv.mobitopp.actitopp.RNGHelper
import edu.kit.ifv.mobitopp.actitopp.changes.Category
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType

data class TimeBudgets(

    val workBudget: Int,

    val educationBudget: Int,

    val leisureBudget: Int,

    val shoppingBudget: Int,

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

class HistogramPerActivity(
    val workHistograms: WorkHistograms = WorkHistograms.fromResourcePath(),
    val educationHistograms: EducationHistograms = EducationHistograms.fromResourcePath(),
    val leisureHistograms: LeisureHistograms = LeisureHistograms.fromResourcePath(),
    val shoppingHistograms: ShoppingHistograms = ShoppingHistograms.fromResourcePath(),
    val transportHistograms: TransportHistograms = TransportHistograms.fromResourcePath(),
) {
    fun determineTimeBudgets(finalizedActivityPattern: FinalizedActivityPattern, rngHelper: RNGHelper): TimeBudgets {
        return TimeBudgets(
            workBudget = workHistograms.select(rngHelper, finalizedActivityPattern),
            educationBudget = educationHistograms.select(rngHelper, finalizedActivityPattern),
            leisureBudget = leisureHistograms.select(rngHelper, finalizedActivityPattern),
            shoppingBudget = shoppingHistograms.select(rngHelper, finalizedActivityPattern),
            transportBudget = transportHistograms.select(rngHelper, finalizedActivityPattern)
        )
    }
    fun determineTimeBudgets(randomValues: List<Double>, finalizedActivityPattern: FinalizedActivityPattern): TimeBudgets {
        return TimeBudgets(
            workBudget = workHistograms.select(randomValues[0], randomValues[1], finalizedActivityPattern),
            educationBudget = educationHistograms.select(randomValues[2], randomValues[3], finalizedActivityPattern),
            leisureBudget = leisureHistograms.select(randomValues[4], randomValues[5], finalizedActivityPattern),
            shoppingBudget = shoppingHistograms.select(randomValues[6], randomValues[7], finalizedActivityPattern),
            transportBudget = transportHistograms.select(randomValues[8], randomValues[9], finalizedActivityPattern)
        )
    }
    private fun HistogramSelection.select(rngHelper: RNGHelper, finalizedActivityPattern: FinalizedActivityPattern): Int {
        return select(rngHelper.randomValue, finalizedActivityPattern).select(rngHelper.randomValue)
    }

    private fun HistogramSelection.select(firstRnd: Double, secondRnd: Double, finalizedActivityPattern: FinalizedActivityPattern): Int {
        return select(firstRnd, finalizedActivityPattern).select(secondRnd)
    }
}
