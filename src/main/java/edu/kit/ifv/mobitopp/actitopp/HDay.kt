package edu.kit.ifv.mobitopp.actitopp

import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import java.time.DayOfWeek

/**
 * @author Tim Hilgert
 */


class HDay(parent: HWeekPattern, val weekday: Int) {
    //stores all attributes that are not directly accessible by variables
    private val attributes: MutableMap<String, Double> = mutableMapOf()

    val pattern: HWeekPattern = parent
    val tours: MutableList<HTour> = mutableListOf()




    val person: ActitoppPerson
        get() = pattern.person

    fun dayss(){
        DayOfWeek.MONDAY.value
    }

    val index: Int
        /**
         * returns the weekday index
         *
         *
         * 0 - Monday
         * 1 - Tuesday
         * 2 - Wednesday
         * 3 - Thursday
         * 4 - Friday
         * 5 - Saturday
         * 6 - Sunday
         *
         * @return
         */
        get() = weekday - 1


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

    val isHomeDay: Boolean
        get() = (amountOfTours == 0)

    val allActivitiesoftheDay: List<HActivity>
        get() {
            val allactivities: MutableList<HActivity> = ArrayList()
            for (tmptour in this.tours) {
                allactivities.addAll(tmptour.activities)
            }
            return allactivities
        }

    val mainTourType: ActivityType?
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

    fun getTotalNumberOfActivitites(acttype: ActivityType?): Int {
        var sum = 0
        for (tour in this.tours) {
            for (act in tour.activities) {
                if (act.activitytypeisScheduled() && act.activityType == acttype) sum++
            }
        }
        return sum
    }

    val highestTourIndex: Int
        get() {
            var index = -99
            for (tour in this.tours) {
                if (tour.index > index) {
                    index = tour.index
                }
            }
            assert(index >= 0) { "maximum tour index is below 0 - index: $index" }
            return index
        }


    val lowestTourIndex: Int
        get() {
            var index = +99
            for (tour in this.tours) {
                if (tour.index < index) {
                    index = tour.index
                }
            }
            assert(index <= 0) { "minimum tour index is over 0 - index: $index" }
            return index
        }

    val firstTourOfDay: HTour
        get() = getTour(lowestTourIndex)

    val lastTourOfDay: HTour
        get() = getTour(highestTourIndex)

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

    val amountOfTours: Int
        get() {
            checkNotNull(tours) { "list of tour is not initialized" }
            return tours.size
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

    fun calculatedurationofmainactivitiesonday(): Int {
        var totalActivityTime = 0
        for (tour in this.tours) {
            totalActivityTime += tour.getActivity(0).duration
        }
        return totalActivityTime
    }
}
