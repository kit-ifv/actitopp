package edu.kit.ifv.mobitopp.actitoppNG.timebudgets

import edu.kit.ifv.mobitopp.actitoppNG.Person
import edu.kit.ifv.mobitopp.actitoppNG.enums.ActivityType
import edu.kit.ifv.mobitopp.actitoppNG.enums.Category
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.choicemodels.educationHistograms
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.choicemodels.leisureHistograms
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.choicemodels.shoppingHistograms
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.choicemodels.transportHistograms
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.choicemodels.workHistograms
import kotlinx.serialization.Serializable
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

// TODO mention somewhere that the categories are indexed starting from 1 isntead of 0, and eventually change the index t 0based.
@Serializable
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
            workBudget = safeDivide(workBudget, dailyOccurrences.getOrDefault(ActivityType.WORK, 0)),
            educationBudget = safeDivide(educationBudget, dailyOccurrences.getOrDefault(ActivityType.EDUCATION, 0)),
            leisureBudget = safeDivide(leisureBudget, dailyOccurrences.getOrDefault(ActivityType.LEISURE, 0)),
            shoppingBudget = safeDivide(shoppingBudget, dailyOccurrences.getOrDefault(ActivityType.SHOPPING, 0)),
            transportBudget = safeDivide(transportBudget, dailyOccurrences.getOrDefault(ActivityType.TRANSPORT, 0)),
            workCategory = workCategory,
            educationCategory = educationCategory,
            leisureCategory = leisureCategory,
            shoppingCategory = shoppingCategory,
            transportCategory = transportCategory,
        )
    }

    private fun safeDivide(numerator: Duration, denominator: Int): Duration {
        if (numerator == Duration.ZERO) return Duration.ZERO
        if (denominator == 0) return Duration.INFINITE
        return numerator / denominator
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
        return when (activityType) {
            ActivityType.WORK -> workCategory
            ActivityType.EDUCATION -> educationCategory
            ActivityType.LEISURE -> leisureCategory
            ActivityType.SHOPPING -> shoppingCategory
            ActivityType.TRANSPORT -> transportCategory

            else -> throw NoSuchElementException("activityType $activityType not supported")

        }
    }

    companion object {
        val NONE = TimeBudgets(
            Duration.ZERO,
            Duration.ZERO,
            Duration.ZERO,
            Duration.ZERO,
            Duration.ZERO,
            Category.NONE_CHOSEN,
            Category.NONE_CHOSEN,
            Category.NONE_CHOSEN,
            Category.NONE_CHOSEN,
            Category.NONE_CHOSEN,
        )
    }
}


class HistogramPerActivity(
    val workHistograms: HistogramSelection,
    val educationHistograms: HistogramSelection,
    val leisureHistograms: HistogramSelection,
    val shoppingHistograms: HistogramSelection,
    val transportHistograms: HistogramSelection,
) {

    context(rng: Random)
    fun determineTimeBudgets(
        person: Person,
        finalizedActivityPattern: FinalizedActivityPattern,
    ): TimeBudgets {
        val workSelection = if (finalizedActivityPattern.workDays == 0) NO_TIME else
            workHistograms.pick(
                 finalizedActivityPattern, person
            )
        val educationSelection = if (finalizedActivityPattern.educationDays == 0) NO_TIME else
            educationHistograms.pick(finalizedActivityPattern, person)
        val leisureSelection = if (finalizedActivityPattern.leisureDays == 0) NO_TIME else
            leisureHistograms.pick( finalizedActivityPattern, person)
        val shoppingSelection = if (finalizedActivityPattern.shoppingDays == 0) NO_TIME else
            shoppingHistograms.pick( finalizedActivityPattern, person)
        val transportSelection = if (finalizedActivityPattern.transportDays == 0) NO_TIME else
            transportHistograms.pick( finalizedActivityPattern, person)
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

    context(rng: Random)
    private fun HistogramSelection.pick(
        finalizedActivityPattern: FinalizedActivityPattern,
        person: Person,
    ): Pair<Duration, Category> {
        val histogram = select( finalizedActivityPattern, person)
        return histogram.select() to histogram.categoryIndex
    }

    companion object {
        /**
         * Initialize the Histograms from files only once, as reading the files is rather time consuming.
         */
        val DEFAULT by lazy {
            HistogramPerActivity(
                workHistograms = workHistograms,
                educationHistograms = educationHistograms,
                leisureHistograms = leisureHistograms,
                shoppingHistograms = shoppingHistograms,
                transportHistograms = transportHistograms,

                )
        }

        /**
         * To indicate that a given category does not occur in the plan of a person. If an activity does not occur
         * the time available is 0.
         */
        private val NO_TIME: Pair<Duration, Category> = Duration.ZERO to Category.NONE_CHOSEN
    }
}
