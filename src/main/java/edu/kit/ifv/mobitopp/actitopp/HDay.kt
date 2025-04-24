package edu.kit.ifv.mobitopp.actitopp

import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import java.time.DayOfWeek
import java.util.NavigableSet
import java.util.TreeSet

/**
 * @author Tim Hilgert
 */


class HDay(parent: HWeekPattern, val weekday: DayOfWeek) {
    //stores all attributes that are not directly accessible by variables
    private val attributes: MutableMap<String, Double> = mutableMapOf()

    val pattern: HWeekPattern = parent
    val tours: MutableList<HTour> = mutableListOf()
    val premiumTours: NavigableSet<HTour> = TreeSet { t1, t2 -> t1.index.compareTo(t2.index) }

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
    val highestTourIndex: Int get() = tours.maxOf { it.index }


    val lowestTourIndex: Int get() = tours.minOf { it.index }

    val firstTourOfDay: HTour get() = getTour(lowestTourIndex)

    val lastTourOfDay: HTour get() = getTour(highestTourIndex)

    val isHomeDay: Boolean get() = tours.isEmpty()
    val amountOfTours: Int get() = tours.size


    val allActivitiesoftheDay: List<HActivity>
        get() {
            val allactivities: MutableList<HActivity> = ArrayList()
            for (tmptour in this.tours) {
                allactivities.addAll(tmptour.activities)
            }
            return allactivities
        }

    val mainTourType: ActivityType
        get() = getTour(0).getActivity(0).activityType


    val totalAmountOfActivitites: Int
        /**
         * returns the amount of activities without home activities
         *
         * @return
         */
        get() {
            var sum = 0
            for (tour in this.tours) {
                sum += tour.amountOfActivities
            }

            return sum
        }


    val totalAmountOfActivityTime: Int
        /**
         * returns total activity duration on that day
         *
         * @return
         */
        get() {
            var totalTime = 0
            for (tour in this.tours) {
                totalTime += tour.actDuration
            }
            return totalTime
        }

    val totalAmountOfTripTime: Int
        /**
         * returns total trip duration on that day
         *
         * @return
         */
        get() {
            var totalTime = 0
            for (tour in this.tours) {
                totalTime += tour.tripDuration
            }
            return totalTime
        }

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
        assert(tour.index != -99) { "index of the tour is not initialized" }
        var tourindexexisitiert = false
        for (tmptour in tours) {
            if (tmptour.index == tour.index) tourindexexisitiert = true
        }
        assert(!tourindexexisitiert) { "a tour using this index already exists" }
        tours.add(tour)
    }

    fun getTotalNumberOfActivitites(acttype: ActivityType): Int {
        var sum = 0
        for (tour in this.tours) {
            for (act in tour.activities) {
                if (act.activitytypeisScheduled() && act.activityType == acttype) sum++
            }
        }
        return sum
    }


    /**
     * @param index
     * @return
     */
    fun getTour(index: Int): HTour {
        var indextour: HTour? = null
        for (tour in this.tours) {
            if (tour.index == index) {
                indextour = tour
            }
        }
        checkNotNull(indextour) { "could not determine tour - index: $index" }
        return indextour
    }


    /**
     * @param index
     * @return
     */
    fun existsTour(index: Int): Boolean {
        var result = false
        var indextour: HTour? = null
        for (tour in this.tours) {
            if (tour.index == index) {
                indextour = tour
            }
        }
        if (indextour != null) result = true
        return result
    }

    /**
     * Determine whether a day is a normal working day (Mo-Fr) by comparing against the numeric value of DayOfWeek
     */
    fun isStandardWorkingDay(): Boolean {
        return weekday.value in 1..5
    }

    /**
     * check if activity given tour and activity index exists on that day
     *
     * @param tourindex
     * @param activityindex
     * @return
     */
    fun existsActivity(tourindex: Int, activityindex: Int): Boolean {
        var result = false
        if (existsTour(tourindex)) {
            var indexact: HActivity? = null
            for (act in getTour(tourindex).activities) {
                if (act.index == activityindex) {
                    indexact = act
                }
            }
            if (indexact != null) result = true
        }
        return result
    }

    /**
     * check if activity given tour and activity index exists on that day and has a scheduled activity type
     *
     * @param tourindex
     * @param activityindex
     * @return
     */
    fun existsActivityTypeforActivity(tourindex: Int, activityindex: Int): Boolean {
        var result = false
        if (existsActivity(tourindex, activityindex) && getTour(tourindex).getActivity(activityindex)
                .activitytypeisScheduled()
        ) result = true
        return result
    }


    /**
     * returns total activity time (without trips) from reference tour until last tour of the day
     *
     * @param referencePoint
     * @return
     */
    fun getTotalAmountOfRemainingActivityTime(referencePoint: HTour): Int {
        var totalTime = 0

        for (i in referencePoint.index..highestTourIndex) {
            totalTime += getTour(i).actDuration
        }
        return totalTime
    }

    /**
     * returns total activity time (without trips) from reference tour until main tour of the day
     *
     * @param referencePoint
     * @return
     */
    fun getTotalAmountOfActivityTimeUntilMainTour(referencePoint: HTour): Int {
        var totalTime = 0

        for (i in referencePoint.index..-1) {
            totalTime += getTour(i).actDuration
        }
        return totalTime
    }


    fun calculatedurationofmainactivitiesonday(): Int {
        var totalActivityTime = 0
        for (tour in this.tours) {
            totalActivityTime += tour.getActivity(0).duration
        }
        return totalActivityTime
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
