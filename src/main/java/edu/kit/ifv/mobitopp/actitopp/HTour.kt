package edu.kit.ifv.mobitopp.actitopp

import java.util.Collections
import java.util.NavigableMap
import java.util.NavigableSet
import java.util.TreeMap
import java.util.TreeSet


/**
 * @author Tim Hilgert
 */
class HTour(parent: HDay, val index: Int) {
    //stores all attributes that are not directly accessible by variables
    private val attributes: MutableMap<String, Double> = mutableMapOf()

    val day: HDay = parent
    val activities: Collection<HActivity> get() = betterActivities.values

    private val betterActivities: NavigableMap<Int, HActivity> = TreeMap()



    val person: ActitoppPerson = day.person



    fun addActivity(act: HActivity) {
        betterActivities[act.index] = act
    }

    var startTime: Int = -1
        get() {
            assert(field >= 0) { "start time is negative - start time: $field" }
            return field
        }
        set(chosenStartTime) {
            assert(chosenStartTime >= 0) { "start time is negative - start time: $chosenStartTime" }
            field = chosenStartTime
        }


    /**
     * check if tour is free of time gaps, i.e., all trips and activities follow directly one after another
     *
     * @return
     */
    fun tourisFreeofGaps(): Boolean {
        return activities.zipWithNext().none { (first, second) ->
            first.endTimeWeekContext != second.tripStartTimeBeforeActivityWeekContext
        }
    }



    /**
     * create start times for each activity of a tour
     */
    fun createStartTimesforActivities() {



        for (act in activities) {
            // first activity: start time is given by tour start time
            if (!act.startTimeisScheduled()) {
                if (act.isActivityFirstinTour) {
                    act.startTime = startTime + act.estimatedTripTimeBeforeActivity
                } else {
                    act.startTime = act.previousActivityinTour!!.endTime + act.estimatedTripTimeBeforeActivity
                }
            }
        }
        if (!tourisFreeofGaps()) System.err.println("tour has gaps! $this")
    }


    val isScheduled: Boolean
        get() = startTime != -1

    val isMainTouroftheDay: Boolean
        get() = this.index == 0

    val isFirstTouroftheDay: Boolean
        get() = this.index == day.lowestTourIndex


    val tourDuration: Int
        /**
         * returns tour duration including default trip durations
         *
         * @return
         */
        get() = actDuration + tripDuration

    val actDuration: Int
        /**
         * returns activity durations of this tour only (without default trip durations)
         *
         * @return
         */
        get() {
            return activities.filter{it.durationisScheduled()}.sumOf { it.duration }
        }

    val tripDuration: Int
        /**
         * returns trip durations of this tour only (without activity durations)
         *
         * @return
         */
        get() {
            val beforeSum = activities.filter{it.tripBeforeActivityisScheduled()}.sumOf { it.estimatedTripTimeBeforeActivity }
            val afterSum = activities.filter{it.tripAfterActivityisScheduled()}.sumOf { it.estimatedTripTimeAfterActivity }
            return beforeSum + afterSum
        }


    val endTime: Int
        get() = lastActivityInTour.endTime + lastActivityInTour.estimatedTripTimeAfterActivity

    /**
     * @param index
     * @return
     */
    fun getActivity(index: Int): HActivity {
        return betterActivities.getValue(index)
    }

    fun getActivityOrNull(index: Int): HActivity? = betterActivities[index]

    val lowestActivityIndex: Int
        get() {
            return activities.minOf { it.index }.also { require(it <= 0){
                "Apparently bad stuff happens when the index is positive  ¯\\_(ツ)_/¯"
            } }
        }

    val highestActivityIndex: Int
        get() {
            return activities.maxOf{it.index}.also { require(it >= 0) {"Negative index, Actitopp no Like  ¯\\_(ツ)_/¯"} }
        }


    val firstActivityInTour: HActivity
        get() = getActivity(lowestActivityIndex)

    val lastActivityInTour: HActivity
        get() = getActivity(highestActivityIndex)

    val amountOfActivities: Int
        get() = activities.size

    val startTimeWeekContext: Int
        get() = (1440 * day.index) + startTime

    val endTimeWeekContext: Int
        get() = (1440 * day.index) + endTime


    val previousTourinPattern: HTour?
        /**
         * return the previous tour in the pattern
         * (last tour of the previous day of previous tour of this day)
         *
         * @return
         */
        get() {
            val previousTour: HTour?
            if (index == day.lowestTourIndex) {
                var previousDaywithTour: HDay? = day
                do {
                    previousDaywithTour = previousDaywithTour!!.previousDay
                    if (previousDaywithTour != null && !previousDaywithTour.isHomeDay) break
                } while (previousDaywithTour != null)

                previousTour = previousDaywithTour?.lastTourOfDay
            } else {
                previousTour = day.getTour(index - 1)
            }
            return previousTour
        }

    val nextTourinPattern: HTour?
        /**
         * returns the next tour in the pattern
         * (first tour of the next day or next tour of this day)
         *
         * @return
         */
        get() {
            val nextTour: HTour?
            if (index == day.highestTourIndex) {
                var nextDaywithTour: HDay? = day
                do {
                    nextDaywithTour = nextDaywithTour!!.nextDay
                    if (nextDaywithTour != null && !nextDaywithTour.isHomeDay) break
                } while (nextDaywithTour != null)

                nextTour = nextDaywithTour?.firstTourOfDay
            } else {
                nextTour = day.getTour(index + 1)
            }
            return nextTour
        }

    /**
     * @param name specific attribute from map
     * @return
     */
    fun getAttributefromMap(name: String): Double {
        return attributes[name] ?: throw NoSuchElementException("No element of $name in $attributes")
    }

    /**
     * @param name  specific attribute for map
     * @param value
     */
    fun addAttributetoMap(name: String, value: Double) {
        attributes[name] = value
    }

    /**
     * @param name
     * @return
     */
    fun existsAttributeinMap(name: String?): Boolean {
        return attributes[name] != null
    }

    val attributesMap: Map<String, Double>
        /**
         * @return the attributes
         */
        get() = attributes
    override fun toString(): String {
        var tostring = ""
        tostring = if (this.isScheduled) {
            day.index.toString() + "/" + index +
                    " start: " + startTimeWeekContext +
                    " end: " + endTimeWeekContext +
                    " duration: " + tourDuration
        } else {
            day.index.toString() + "/" + index +
                    " start: --- " +
                    " end: --- " +
                    " duration: " + tourDuration
        }
        return tostring
    }
}
