package edu.kit.ifv.mobitopp.actitoppNG.plandurations


import edu.kit.ifv.mobitopp.actitoppNG.Person
import edu.kit.ifv.mobitopp.actitoppNG.RNGHelper
import edu.kit.ifv.mobitopp.actitoppNG.enums.ActivityType
import edu.kit.ifv.mobitopp.actitoppNG.enums.Employment
import edu.kit.ifv.mobitopp.actitoppNG.enums.isParttime
import edu.kit.ifv.mobitopp.actitoppNG.enums.isStudent
import edu.kit.ifv.mobitopp.actitoppNG.enums.isStudentOrAzubi
import edu.kit.ifv.mobitopp.actitoppNG.modernization.Activity
import edu.kit.ifv.mobitopp.actitoppNG.modernization.durations.MobilityPlanInputs
import edu.kit.ifv.mobitopp.actitoppNG.modernization.plan.DayPlan
import edu.kit.ifv.mobitopp.actitoppNG.modernization.plan.MobilityPlan
import edu.kit.ifv.mobitopp.actitoppNG.modernization.plan.TourPlan
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.ArrayHistogram
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.TimeBudgets

import edu.kit.ifv.mobitopp.actitoppNG.utils.Position
import edu.kit.ifv.mobitopp.actitoppNG.utils.indexOfSearch
import edu.kit.ifv.mobitopp.actitoppNG.utils.sumOf
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.structure.DiscreteStructure
import edu.kit.ifv.mobitopp.discretechoice.structure.loadFromList
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.multinomialLogit
import java.time.DayOfWeek
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes


/**
 * Contains the histograms originally found in 8C. They are assigning the duration of the first main activity of
 * a day, Henceforth referenced as "Lead activity"
 */
open class ActivityDurationHistograms<P>(
    // TODO histograms could be a sorted Set, since they are supposed to cover respective ranges.
    open val histograms: List<ArrayHistogram>,
    open val choiceModel: FixedChoiceModel<ArrayHistogram, MainDurationAlternative>,
    val emergencyBehaviour:  (ClosedRange<Duration>, Random) -> Duration = { range, rng ->

        ((range.endInclusive - range.start) * rng.nextDouble() + range.start).inWholeMinutes.minutes
    },
) {

    /**
     * Find the proper histogram, select between histogram and neighbors, where main histogram gets a 1.1 boost.
     * Select duration from selected histogram.
     */
    context(rng: Random)
    fun chooseHistogramFromNeighbors(
        duration: Duration,
        bounds: ClosedRange<Duration>,
        converter: MainDurationAlternative,
    ): ArrayHistogram {
        val index = histograms.binarySearch { it.compareTo(duration.inWholeMinutes.toInt()) }.indexOfSearch()
        val mainHistogram = histograms[index]
        val previousHistogram = histograms.getOrNull(index - 1)
        val nextHistogram = histograms.getOrNull(index + 1)

        val choices = listOfNotNull(previousHistogram, mainHistogram, nextHistogram).filter { it.intersects(bounds) }.toSet()
        require(choices.isNotEmpty()) {
            "The choice set is empty, this happens because the duration is $duration and the bounds $bounds, ${histograms.map { it.toString() }}"
        }

        return context(converter) {
            choiceModel.selectInjected(choices, injections = mapOf(mainHistogram to { d: Double -> d * 1.1 }))
        }

    }
    context(rng: Random)
    fun select(converter: MainDurationAlternative): Duration {
        return context(converter) {
            choiceModel.select().select(rng.nextDouble())
        }
    }
    context(rng: Random)
    fun selectHistogram(
        bounds: ClosedRange<Duration>,
        converter: MainDurationAlternative,
    ): ArrayHistogram? {
        val options = histograms.filter { it.end >= bounds.start && it.start <= bounds.endInclusive }.toSet()
        // It can happen that no histogram fits, because they are trimmed. The default behaviour of legacy actitopp is to return a random value.
        if (options.isEmpty()) {
            return null
        }

        return context(converter) {
            choiceModel.select(options)
        }
    }
    context(rng: Random)
    fun select(
        bounds: ClosedRange<Duration>,
        converter: MainDurationAlternative,
    ): Duration {

        val concreteHistogram = selectHistogram(bounds, converter) ?: return emergencyBehaviour(bounds, rng)
        val output = concreteHistogram.select(
            bounds
        )
        return output
    }

    /**
     * The legacy code defaces,modifies and otherwise corrupts the histograms for each person individually. Thus, we need a taintable copy.
     */
    fun taint(): TaintedActivityDurationHistograms<P> {
        return TaintedActivityDurationHistograms(this)
    }

}

class TaintedActivityDurationHistograms<P>(
    val original: ActivityDurationHistograms<P>,
) {
    val histograms = original.histograms
    val choiceModel = original.choiceModel
    private val taintedHistograms = histograms.associateWith { it.copy() }

    context(rng: Random)
    fun selectAndTaint(
        bounds: ClosedRange<Duration>,
        duration: Duration,
        converter: MainDurationAlternative,
    ): Duration {

        val selectedHistogram = original.chooseHistogramFromNeighbors(duration, bounds, converter)
        val taint = taintedHistograms.getValue(selectedHistogram)


        return taint.select(bounds).also {
            taint.modify(it.inWholeMinutes.toInt())
        }
    }
    context(rng: Random)
    fun select(
        bounds: ClosedRange<Duration>,
        converter: MainDurationAlternative,
    ): Duration {

        val histogram =
            original.selectHistogram( bounds, converter) ?: return original.emergencyBehaviour(bounds, rng)
        val taint = taintedHistograms.getValue(histogram)
        return taint.select( bounds)
    }
}

enum class Identifier(val id: String, val resourceName: String) {

    WORK_TIME_BUDGETS("7B", "workBudgetHistograms"),
    EDUCATION_TIME_BUDGETS("7D", "educationBudgetHistograms"),
    LEISURE_TIME_BUDGETS("7F", "leisureBudgetHistograms"),
    SHOPPING_TIME_BUDGETS("7H", "shoppingBudgetHistograms"),
    TRANSPORT_TIME_BUDGETS("7J", "transportBudgetHistograms"),

    LEAD_ACTIVITY_DURATION("8C", "leadActivityDurationHistograms"),
    MAJOR_ACTIVITY_DURATION("8E", "majorActivityDurationHistograms"),
    MINOR_ACTIVITY_DURATION("8K", "minorActivityDurationHistograms"),

    FIRST_TOUR_START_TIME("10N", "firstTourStartHistograms"),
    SECOND_TOUR_START_TIME("10P", "secondTourStartHistograms"),
    OTHER_TOUR_START_TIME("10T", "otherTourStartHistograms");

    fun resourcePath(): String {
        return resourceName
    }
}

fun <P : List<T>, T> P.generateHistogram(
    histograms: List<ArrayHistogram>,
    utilityFunction: T.(MainDurationAlternative) -> Double,
): ActivityDurationHistograms<P> {

    val choiceModel = DiscreteStructure<ArrayHistogram, MainDurationAlternative, P> {
        loadFromList(histograms) { _, it ->
            utilityFunction(this, it)
        }
    }.multinomialLogit("Histogram selection Choice model").build(this)

    return ActivityDurationHistograms(histograms, choiceModel)

}


open class PlanAlternative<P : Any>(
    input: MobilityPlanInputs,

    )  {

    val mobilityPlan: MobilityPlan = input.mobilityPlan
    val dayPlan: DayPlan = input.dayPlan
    val tourPlan: TourPlan = input.tourPlan
    val activity: Activity = input.activity
    val planTimeBudgets: TimeBudgets = input.mobilityPlan.timeBudgets
    val person: Person = input.person
    val isLastTourOfDay: Boolean = input.isLastTourOfDay
    val activityType: ActivityType = activity.activityType

    // TODO communicate clearly that this interval check is an open end range, because the legacy code doesnt communicate that
    fun dauer_akt_tag_4bis6std(): Boolean = dayPlan.durationOfActivities() in 4.hours..<6.hours
    fun dauer_akt_tag_6bis8std(): Boolean = dayPlan.durationOfActivities() in 6.hours..<8.hours
    fun dauer_akt_tag_8bis10std(): Boolean = dayPlan.durationOfActivities() in 8.hours..<10.hours
    fun dauer_akt_tag_10bis12std(): Boolean = dayPlan.durationOfActivities() in 10.hours..<12.hours
    fun dauer_akt_tag_12bis14std(): Boolean = dayPlan.durationOfActivities() in 12.hours..<14.hours

    fun anztagemit_tourennachht(): Int {
        // TODO this number is always fixed and should be precalculated in the mobility plan
        return mobilityPlan.dayPlans.count { it.tourPlans.last().position == Position.AFTER }
    }

    fun householdHasYouths() = person.household.amountOfAllMinors() > 0
    fun anztagemit_tourenvorht(): Int {
        // TODO this number is also fixed once the mobility Plan is formulated.
        return mobilityPlan.dayPlans.count { it.tourPlans.first().position == Position.BEFORE }

    }

    fun anztourenamtag(): Int {
        return dayPlan.tourPlans.size
    }

    fun tour3destages() = tourPlan == dayPlan.tourPlans.getOrNull(2)
    fun isStudent() = person.employment.isStudent()
    fun isVocational() = person.employment == Employment.VOCATIONAL
    fun isAged10to17() = person.age in 10..17
    fun haupttour_work() = dayPlan.mainActivityType() == ActivityType.WORK
    fun haupttour_education() = dayPlan.mainActivityType() == ActivityType.EDUCATION
    fun touristhaupttour() = tourPlan.position == Position.MAIN
    fun dauer_hauptakt_tag_4bis6std(): Boolean {
        return dayPlan.durationOfMainActivities in 4.hours..<6.hours
    }

    fun dauer_hauptakt_tag_6bis8std(): Boolean {
        return dayPlan.durationOfMainActivities in 6.hours..<8.hours
    }

    fun dauer_hauptakt_tag_8bis10std(): Boolean {
        return dayPlan.durationOfMainActivities in 8.hours..<10.hours
    }

    fun dauer_hauptakt_tag_10bis12std(): Boolean {
        return dayPlan.durationOfMainActivities in 10.hours..<12.hours
    }

    fun dauer_hauptakt_tag_12bis14std(): Boolean {
        return dayPlan.durationOfMainActivities in 12.hours..<14.hours
    }

    fun dauer_hauptakt_tag_ueber14std(): Boolean {
        return dayPlan.durationOfMainActivities >= 14.hours
    }

    fun mittl_zeit_akt_1bis14min(): Boolean {
        return dayPlan.getBudget(activityType) in 1.minutes..<15.minutes
    }

    fun mittl_zeit_akt_15bis29min(): Boolean {
        return dayPlan.getBudget(activityType) in 15.minutes..<30.minutes
    }

    fun mittl_zeit_akt_30bis59min(): Boolean {
        return dayPlan.getBudget(activityType) in 30.minutes..<60.minutes
    }

    fun mittl_zeit_akt_60bis119min(): Boolean {
        return dayPlan.getBudget(activityType) in 60.minutes..<120.minutes
    }

    fun mittl_zeit_akt_120bis179min(): Boolean {
        return dayPlan.getBudget(activityType) in 120.minutes..<180.minutes
    }

    fun mittl_zeit_akt_180bis239min(): Boolean {
        return dayPlan.getBudget(activityType) in 180.minutes..<240.minutes
    }

    fun mittl_zeit_akt_240bis299min(): Boolean {
        return dayPlan.getBudget(activityType) in 240.minutes..<300.minutes
    }

    fun mittl_zeit_akt_300bis359min(): Boolean {
        return dayPlan.getBudget(activityType) in 300.minutes..<360.minutes
    }

    fun mittl_zeit_akt_360bis419min(): Boolean {
        return dayPlan.getBudget(activityType) in 360.minutes..<420.minutes
    }

    fun mittl_zeit_akt_420bis479min(): Boolean {
        return dayPlan.getBudget(activityType) in 420.minutes..<480.minutes

    }

    fun tag_so(): Boolean {
        return dayPlan.durationDay.weekday == DayOfWeek.SUNDAY
    }

    fun aktzweck_work(): Boolean {
        return activityType == ActivityType.WORK
    }

    fun aktzweck_education(): Boolean {
        return activityType == ActivityType.EDUCATION
    }

    fun aktzweck_shopping(): Boolean {
        return activityType == ActivityType.SHOPPING
    }

    fun aktzweck_transport(): Boolean {
        return activityType == ActivityType.TRANSPORT
    }

    fun taghat2akt(): Boolean {
        return dayPlan.amountOfActivities == 2
    }

    fun taghat3akt(): Boolean {
        return dayPlan.amountOfActivities == 3
    }

    fun taghat4akt(): Boolean {
        return dayPlan.amountOfActivities == 4
    }

    fun taghat5akt(): Boolean {
        return dayPlan.amountOfActivities == 5
    }

    fun taghat6akt(): Boolean {
        return dayPlan.amountOfActivities == 6
    }

    fun tourtyp_work(): Boolean {
        return tourPlan.mainActivity.activityType == ActivityType.WORK
    }

    fun tourtyp_education(): Boolean {
        return tourPlan.mainActivity.activityType == ActivityType.EDUCATION
    }

    fun tourtyp_shopping(): Boolean {
        return tourPlan.mainActivity.activityType == ActivityType.SHOPPING
    }

    fun tourtyp_transport(): Boolean {
        return tourPlan.mainActivity.activityType == ActivityType.TRANSPORT
    }

    fun tourhat2akt(): Boolean {
        return tourPlan.size == 2
    }

    fun taghat1tour(): Boolean {
        return dayPlan.tourPlans.size == 1
    }

    fun taghat2touren(): Boolean {
        return dayPlan.tourPlans.size == 2
    }

    fun taghat3touren(): Boolean {
        return dayPlan.tourPlans.size == 3
    }

    fun aktliegtvorhauptakt(): Boolean {
        return activity.position == Position.BEFORE
    }

    fun tourliegtvorhaupttour(): Boolean {
        return tourPlan.position == Position.BEFORE
    }

    fun tourliegtnachhaupttour(): Boolean {
        return tourPlan.position == Position.AFTER
    }

    fun tag_fr(): Boolean {
        return dayPlan.durationDay.weekday == DayOfWeek.FRIDAY
    }

    fun tag_sa(): Boolean {
        return dayPlan.durationDay.weekday == DayOfWeek.SATURDAY
    }

    fun dauer_akt_vorht_tag_1bis120(): Boolean {
        return dayPlan.tourPlans.filter { it.position == Position.BEFORE }
            .sumOf { it.activityDurationsWithTrips } in 1.minutes..<120.minutes
    }

    fun anztourenvorhaupttour() = dayPlan.tourPlans.count { it.position == Position.BEFORE }
    fun anztourennachhaupttour() = dayPlan.tourPlans.count { it.position == Position.AFTER }
    fun endetourvorher_Std_12() = dayPlan.endOfPreviousTour(tourPlan) in 12.hours..<13.hours

    fun endetourvorher_Std_13() = dayPlan.endOfPreviousTour(tourPlan) in 13.hours..<14.hours

    fun endetourvorher_Std_14() = dayPlan.endOfPreviousTour(tourPlan) in 14.hours..<15.hours

    fun endetourvorher_Std_15() = dayPlan.endOfPreviousTour(tourPlan) in 15.hours..<16.hours

    fun endetourvorher_Std_16() = dayPlan.endOfPreviousTour(tourPlan) in 16.hours..<17.hours

    fun endetourvorher_Std_17() = dayPlan.endOfPreviousTour(tourPlan) in 17.hours..<18.hours

    fun endetourvorher_Std_18() = dayPlan.endOfPreviousTour(tourPlan) in 18.hours..<19.hours

    fun endetourvorher_Std_19() = dayPlan.endOfPreviousTour(tourPlan) in 19.hours..<20.hours

    fun endetourvorher_Std_20() = dayPlan.endOfPreviousTour(tourPlan) in 20.hours..<21.hours

    fun dauer_akt_in_tour_0bis2std() = tourPlan.activityDurations in 0.hours..<2.hours
    fun dauer_akt_in_tour_2bis4std() = tourPlan.activityDurations in 2.hours..<4.hours
    fun dauer_akt_in_tour_4bis6std() = tourPlan.activityDurations in 4.hours..<6.hours
    fun dauer_akt_in_tour_6bis8std() = tourPlan.activityDurations in 6.hours..<8.hours
    fun dauer_akt_in_tour_8bis10std() = tourPlan.activityDurations in 8.hours..<10.hours
    fun dauer_akt_in_tour_10bis12std() = tourPlan.activityDurations in 10.hours..<12.hours
    fun anzaktwieanztagemitzweck(): Boolean {
        return mobilityPlan.regularActivities[activityType] ?: false
    }

    fun wochenzbudget_zweck_kat1(): Boolean {
        return planTimeBudgets.getCategory(activityType).matches(1)
    }

    fun wochenzbudget_zweck_kat2(): Boolean {
        return planTimeBudgets.getCategory(activityType).matches(2)
    }

    fun wochenzbudget_zweck_kat3(): Boolean {
        return planTimeBudgets.getCategory(activityType).matches(3)
    }

    fun wochenzbudget_zweck_kat4(): Boolean {
        return planTimeBudgets.getCategory(activityType).matches(4)
    }

    fun wochenzbudget_zweck_kat5(): Boolean {
        return planTimeBudgets.getCategory(activityType).matches(5)
    }

    fun tourhat1akt(): Boolean {
        return tourPlan.size == 1
    }

    fun tourhat3akt(): Boolean {
        return tourPlan.size == 3
    }

    fun letztetourdestages(): Boolean {
        return isLastTourOfDay
    }

    fun beruf_vollzeit(): Boolean {
        return person.employment == Employment.FULLTIME
    }

    fun beruf_teilzeit(): Boolean {
        return person.employment.isParttime()
    }

    fun beruf_schueler_azubi(): Boolean {
        return person.employment.isStudentOrAzubi()
    }

    fun taghat1akt(): Boolean {
        return dayPlan.amountOfActivities == 1
    }
}

class MainDurationAlternative(
    input: MobilityPlanInputs,
) : PlanAlternative<ArrayHistogram>(
    input
)

open class BooleanDecisionAlternative(
    input: MobilityPlanInputs,
) : PlanAlternative<Boolean>(
    input
)

class BooleanDecisionWithPreferenceCategory(

    input: MobilityPlanInputs,
    private val preferredHistogram: ArrayHistogram,
) : BooleanDecisionAlternative(
    input
) {
    fun std_start_T1_6_7_Uhr(): Boolean {
        return preferredHistogram.start in (6.hours - 15.minutes)..(6.hours + 15.minutes) // SOme buffer because the histograms could theroretically not have an entry precisely for 6 hours
    }

    fun std_start_T1_7_8_Uhr(): Boolean {
        return preferredHistogram.start in (7.hours - 15.minutes)..(7.hours + 15.minutes) // SOme buffer
    }
}

