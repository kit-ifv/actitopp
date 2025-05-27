package edu.kit.ifv.mobitopp.actitopp.steps.step7

import edu.kit.ifv.mobitopp.actitopp.HDay
import edu.kit.ifv.mobitopp.actitopp.HWeekPattern
import edu.kit.ifv.mobitopp.actitopp.RNGHelper
import edu.kit.ifv.mobitopp.actitopp.changes.Category
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
// TODO mention somewhere that the categories are indexed starting from 1 isntead of 0, and eventually change the index t 0based.
data class TimeBudgets(

    val workBudget: Duration,
    val educationBudget: Duration,
    val leisureBudget: Duration,
    val shoppingBudget: Duration,
    val transportBudget: Duration,

    val workCategory: Category,
    val educationCategory: Category,
    val leisureCategory: Category,
    val shoppingCategory: Category,
    val transportCategory: Category,
) {
    fun toDayTimeBudget(dailyOccurrences: Map<ActivityType, Int>): TimeBudgets {
        return TimeBudgets(
            workBudget = workBudget / dailyOccurrences.getOrDefault(ActivityType.WORK, 0),
            educationBudget = educationBudget / dailyOccurrences.getOrDefault(ActivityType.EDUCATION, 0),
            leisureBudget = leisureBudget / dailyOccurrences.getOrDefault(ActivityType.LEISURE, 0),
            shoppingBudget = shoppingBudget / dailyOccurrences.getOrDefault(ActivityType.SHOPPING, 0),
            transportBudget = transportBudget / dailyOccurrences.getOrDefault(ActivityType.TRANSPORT, 0),
            workCategory = workCategory,
            educationCategory = educationCategory,
            leisureCategory = leisureCategory,
            shoppingCategory = shoppingCategory,
            transportCategory = transportCategory,
        )
    }

    operator fun get(activityType: ActivityType): Duration {
        return when (activityType) {
            ActivityType.WORK -> workBudget
            ActivityType.EDUCATION -> educationBudget
            ActivityType.LEISURE -> leisureBudget
            ActivityType.SHOPPING -> shoppingBudget
            ActivityType.TRANSPORT -> transportBudget
            ActivityType.HOME -> 0.minutes
            else -> throw NoSuchElementException("activityType $activityType not supported")
        }
    }
    fun getCategory(activityType: ActivityType): Category {
        return when(activityType) {
            ActivityType.WORK -> workCategory
            ActivityType.EDUCATION -> educationCategory
            ActivityType.LEISURE -> leisureCategory
            ActivityType.SHOPPING -> shoppingCategory
            ActivityType.TRANSPORT -> transportCategory

            else -> throw NoSuchElementException("activityType $activityType not supported")

        }
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
        return determineTimeBudgets((0..<10).map { rngHelper.randomValue }, finalizedActivityPattern)
    }

    fun determineTimeBudgets(
        randomValues: List<Double>,
        finalizedActivityPattern: FinalizedActivityPattern,
    ): TimeBudgets {
        val workSelection = workHistograms.select(randomValues[0], randomValues[1], finalizedActivityPattern)
        val educationSelection = educationHistograms.select(randomValues[2], randomValues[3], finalizedActivityPattern)
        val leisureSelection = leisureHistograms.select(randomValues[4], randomValues[5], finalizedActivityPattern)
        val shoppingSelection = shoppingHistograms.select(randomValues[6], randomValues[7], finalizedActivityPattern)
        val transportSelection = transportHistograms.select(randomValues[8], randomValues[9], finalizedActivityPattern)
        return TimeBudgets(
            workBudget = workSelection.first,
            workCategory = workSelection.second,
            educationBudget = educationSelection.first,
            leisureBudget = leisureSelection.first,
            shoppingBudget = shoppingSelection.first,
            transportBudget = transportSelection.first,
            educationCategory = educationSelection.second,
            leisureCategory = leisureSelection.second,
            shoppingCategory = shoppingSelection.second,
            transportCategory = transportSelection.second,
        )
    }

    private fun HistogramSelection.select(
        rngHelper: RNGHelper,
        finalizedActivityPattern: FinalizedActivityPattern,
    ): Pair<Duration, Category> {
        val histogram = select(rngHelper.randomValue, finalizedActivityPattern)
        return histogram.select(rngHelper.randomValue) to histogram.categoryIndex
    }

    private fun HistogramSelection.select(
        firstRnd: Double,
        secondRnd: Double,
        finalizedActivityPattern: FinalizedActivityPattern,
    ): Pair<Duration, Category> {
        val histogram = select(firstRnd, finalizedActivityPattern)
        return histogram.select(secondRnd) to histogram.categoryIndex
    }
}
