package edu.kit.ifv.mobitopp.actitopp

import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import java.time.DayOfWeek
import java.util.NavigableMap
import java.util.NavigableSet
import java.util.SortedMap
import java.util.TreeMap
import java.util.TreeSet


fun <K, V> SortedMap<K, V>.lastValue(): V = this.getValue(lastKey())
fun <K, V> SortedMap<K, V>.firstValue(): V = this.getValue(firstKey())

/**
 * @author Tim Hilgert
 */
class HDay(parent: HWeekPattern, val weekday: DayOfWeek) {
    //stores all attributes that are not directly accessible by variables
    private val attributes: MutableMap<String, Double> = mutableMapOf()

    val pattern: HWeekPattern = parent

    private val premiumTours: NavigableMap<Int, HTour> = TreeMap()
    val tours: List<HTour> get() = premiumTours.values.toList()

    // Does not need to be get() method if person never changes
    val person: ActitoppPerson = pattern.person

    /**
     * Does not need to be get(). Returns the index of the Day similar to DayOfWeek.oridnal()
     * Mo = 0, So = 6
     */
    val index: Int = weekday.value - 1

    /**
     * Return the highest tour index, the original code had an assert where negative indices would cause an assertion
     * that would trigger if either the day has no tours or only "preMainActivity" tours with negative indices. It can
     * be assumed that this decision was a programmer oversight and not deliberate.
     */
    val highestTourIndex: Int get() = premiumTours.lastValue().index


    val lowestTourIndex: Int get() = premiumTours.firstValue().index

    val firstTourOfDay: HTour get() = premiumTours.firstValue()

    val lastTourOfDay: HTour get() = premiumTours.lastValue()

    val isHomeDay: Boolean get() = premiumTours.isEmpty()
    val amountOfTours: Int get() = premiumTours.size


    val allActivitiesoftheDay: List<HActivity>
        get() {
            return premiumTours.values.flatMap { it.activities }
        }

    val mainTourType: ActivityType
        get() = getTour(0).getActivity(0).activityType


    val totalAmountOfActivitites: Int get() = tours.sumOf { it.amountOfActivities }
    val totalAmountOfActivityTime: Int get() = tours.sumOf { it.actDuration }
    val totalAmountOfTripTime: Int get() = tours.sumOf { it.tripDuration }


    val previousDay: HDay?
        get() {
            val previousDay = if (index == 0) {
                null
            } else {
                pattern.getDay(index - 1)
            }

            return previousDay
        }

    val nextDay: HDay?
        get() {
            val nextDay = if (index == 6) {
                null
            } else {
                pattern.getDay(index + 1)
            }

            return nextDay
        }

    fun hasActivity(activityType: ActivityType): Boolean {
        return tours.any { t -> t.activities.any { it.activityType == activityType && it.isScheduled } }
    }

    fun addTour(tour: HTour) {
        require(!premiumTours.containsKey(tour.index)) {
            "Cannot insert tour as another tour with that index is already found, " +
                    "TODO maybe kill the require, the old code didn't fail at this point but caused wild behaviour"

        }
        premiumTours[tour.index] = tour

    }
    // TODO when activitytypeIsScheduled() is just a comparision against activityType Unknown the expression would collapse.
    fun getTotalNumberOfActivitites(acttype: ActivityType): Int {
        return tours.sumOf { it.activities.count { it.activitytypeisScheduled() && it.activityType == acttype } }
    }


    fun getTour(index: Int): HTour = premiumTours.getValue(index)

    fun existsTour(index: Int): Boolean = index in premiumTours

    fun isStandardWorkingDay(): Boolean = weekday.value in 1..5


    /**
     * check if activity given tour and activity index exists on that day
     *
     * @param tourindex
     * @param activityindex
     * @return
     */
    fun existsActivity(tourindex: Int, activityindex: Int): Boolean {
        if (!premiumTours.containsKey(tourindex)) return false // No tour with specified index exists
        return premiumTours.getValue(tourindex).activities.any { it.index == activityindex }
    }

    /**
     * check if activity given tour and activity index exists on that day and has a scheduled activity type
     *
     * @param tourindex
     * @param activityindex
     * @return
     */
    fun existsActivityTypeforActivity(tourindex: Int, activityindex: Int): Boolean  = premiumTours[tourindex]?.getActivityOrNull(activityindex)?.activitytypeisScheduled() ?: false


    /**
     * returns total activity time (without trips) from reference tour until last tour of the day
     *
     * @param referencePoint
     * @return
     */
    fun getTotalAmountOfRemainingActivityTime(referencePoint: HTour): Int {
        return premiumTours.tailMap(referencePoint.index, true).values.sumOf { it.actDuration }

    }

    /**
     * returns total activity time (without trips) from reference tour until main tour of the day
     *
     * @param referencePoint
     * @return
     */
    fun getTotalAmountOfActivityTimeUntilMainTour(referencePoint: HTour): Int {
        return premiumTours.subMap(referencePoint.index, true, -1, true).values.sumOf { it.actDuration }
    }


    fun calculatedurationofmainactivitiesonday(): Int {
        return premiumTours.values.sumOf { it.getActivity(0).duration }
    }

    /**
     * @param name  specific attribute for map
     * @param value
     */
    fun addAttributetoMap(name: String, value: Double) {
        attributes[name] = value
    }


    val attributesMap: Map<String, Double>
        /**
         * @return the attributes
         */
        get() = attributes


    /**
     * returns total tour time (activities + trips) from reference tour until last tour of the day
     *
     * @param referencePoint
     * @return
     */
    fun getTotalAmountOfRemainingTourTime(referencePoint: HTour): Int {
        var totalTime = 0

        for (i in referencePoint.index..highestTourIndex) {
            totalTime += getTour(i).tourDuration
        }
        return totalTime
    }

    /**
     * returns total tour time (activities + trips) from reference tour until main tour of the day
     *
     * @param referencePoint
     * @return
     */
    fun getTotalAmountOfTourTimeUntilMainTour(referencePoint: HTour): Int {
        var totalTime = 0

        for (i in referencePoint.index..-1) {
            totalTime += getTour(i).tourDuration
        }
        return totalTime
    }

    override fun toString(): String {
        return "Wochentag " + weekday +
                " #Touren " + amountOfTours +
                " Haupttour: " + mainTourType
    }

    fun printDayPattern(): String {
        var result = "Day " + index + " // "

        for (tmpact in allActivitiesoftheDay) {
            result = result + " " + tmpact.index + " " + tmpact.activityType.typeasChar
            if (tmpact.isActivityLastinTour) result = "$result //"
        }
        return result
    }
}
