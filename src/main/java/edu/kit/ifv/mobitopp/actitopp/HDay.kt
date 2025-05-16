package edu.kit.ifv.mobitopp.actitopp

import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.modernization.BidirectionalIndexedValue
import edu.kit.ifv.mobitopp.actitopp.modernization.DayStructure
import edu.kit.ifv.mobitopp.actitopp.modernization.DurationDay
import edu.kit.ifv.mobitopp.actitopp.modernization.TourStructure
import edu.kit.ifv.mobitopp.actitopp.steps.step1.times
import java.time.DayOfWeek
import java.util.NavigableMap
import java.util.SortedMap
import java.util.TreeMap
import kotlin.time.Duration

// TODO move these extension functions to something called utils, they should not loiter here
fun <K, V> SortedMap<K, V>.lastValue(): V = this.getValue(lastKey())
fun <K, V> SortedMap<K, V>.firstValue(): V = this.getValue(firstKey())

/**
 * @author Tim Hilgert
 */
class HDay(parent: HWeekPattern, override val weekday: DayOfWeek) : DayStructure {
    //stores all attributes that are not directly accessible by variables
    private val attributes: MutableMap<String, Double> = mutableMapOf()

    val pattern: HWeekPattern = parent
    override val startTimeDay: DurationDay = DurationDay(weekday.value)
    override val duration: Duration = startTimeDay.timePoint

    override fun mainActivityType() = this.mainTourType

    override fun amountOfPrecursorElements(): Int = this.amountOfPreviousTours()

    override fun amountOfSuccessorElements(): Int = this.amountOfLaterTours()

    override fun amountOfElements(): Int {
        TODO("Not yet implemented")
    }

    override fun elements(): Collection<TourStructure> {
        TODO("Not yet implemented")
    }

    override fun indexedElements(): Collection<BidirectionalIndexedValue<TourStructure>> {
        TODO("Not yet implemented")
    }


    private val mappedTours: NavigableMap<Int, HTour> = TreeMap()
    val tours: Collection<HTour> get() = mappedTours.values

    // Does not need to be get() method if person never changes
    val person: ActitoppPerson = pattern.person
    val mainActivity: HActivity? get() = getTourOrNull(0)?.getActivityOrNull(0)

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
    val highestTourIndex: Int get() = mappedTours.lastValue().index

    // The original code returned +99 if no tour exists. This is always caught before because home activity is treated
    // special, but the correct implementation would be to return a nullable field.
    val lowestTourIndex: Int get() = mappedTours.firstValue().index
    val firstTourOfDay: HTour get() = mappedTours.firstValue()

    val lastTourOfDay: HTour get() = mappedTours.lastValue()

    val isHomeDay: Boolean get() = mappedTours.isEmpty()
    fun hasTours() = !isHomeDay
    val amountOfTours: Int get() = mappedTours.size


    val allActivitiesoftheDay: List<HActivity>
        get() {
            return mappedTours.values.flatMap { it.activities }
        }

    val mainTourType: ActivityType
        get() = getTourOrNull(0)?.getActivity(0)?.activityType ?: ActivityType.HOME


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
        return tours.any { t -> t.activities.any { it.activityType == activityType && it.activityTypeIsSpecified() } }
    }

    fun addTour(tour: HTour) {
        require(!mappedTours.containsKey(tour.index)) {
            "Cannot insert tour as another tour with that index is already found, " +
                    "TODO maybe kill the require, the old code didn't fail at this point but caused wild behaviour"

        }
        mappedTours[tour.index] = tour

    }

    /**
     * To avoid handling naked indices, we will provide named options for functions to keep the index spam outside of
     * the model classes to a minimum
     * @see generateTour
     */
    fun generateMainTour() = generateTour(0)

    /**
     * When referencing the lowest index we can always generate a preceding tour by adding one with an even lower index
     */
    fun generatePrecedingTour() = generateTour(lowestTourIndex - 1)
    fun generateFollowingTour() = generateTour(highestTourIndex + 1)

    /**
     * Generate an empty tour for the day, with the specified tour index.
     */
    fun generateTour(tourIndex: Int): HTour {
        require(tourIndex !in mappedTours) {
            "There is already a tour with index $tourIndex for day $this"
        }
        val tour = HTour(this, tourIndex)
        addTour(tour)
        return tour
    }

    // TODO when activitytypeIsScheduled() is just a comparision against activityType Unknown the expression would collapse.
    fun getTotalNumberOfActivitites(acttype: ActivityType): Int {
        return tours.sumOf { it.activities.count { act -> act.activityType == acttype && act.activityTypeIsSpecified() } }
    }

    fun getTour(index: Int): HTour = mappedTours.getValue(index)
    fun getTourOrNull(index: Int) = mappedTours[index]
    fun existsTour(index: Int): Boolean = index in mappedTours

    fun isStandardWorkingDay(): Boolean = weekday.value in 1..5


    /**
     * check if activity given tour and activity index exists on that day
     *
     * @param tourindex
     * @param activityindex
     * @return
     */
    fun existsActivity(tourindex: Int, activityindex: Int): Boolean {
        if (!mappedTours.containsKey(tourindex)) return false // No tour with specified index exists
        return mappedTours.getValue(tourindex).activities.any { it.index == activityindex }
    }

    /**
     * check if activity given tour and activity index exists on that day and has a scheduled activity type
     *
     * @param tourindex
     * @param activityindex
     * @return
     */
    fun existsActivityTypeforActivity(tourindex: Int, activityindex: Int): Boolean =
        mappedTours[tourindex]?.getActivityOrNull(activityindex)?.activityTypeIsSpecified() ?: false


    /**
     * returns total activity time (without trips) from reference tour until last tour of the day
     *
     * @param referencePoint
     * @return
     */
    fun getTotalAmountOfRemainingActivityTime(referencePoint: HTour): Int {
        return mappedTours.tailMap(referencePoint.index, true).values.sumOf { it.actDuration }

    }

    /**
     * returns total activity time (without trips) from reference tour until main tour of the day
     *
     * @param referencePoint
     * @return
     */
    fun getTotalAmountOfActivityTimeUntilMainTour(referencePoint: HTour): Int {
        if (referencePoint.index > -1) {
            return 0
        }
        return mappedTours.subMap(referencePoint.index, true, -1, true).values.sumOf { it.actDuration }
    }


    fun calculatedurationofmainactivitiesonday(): Int {
        return mappedTours.values.sumOf { it.getActivity(0).duration }
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

// TODO find out whether a home day could feasibly generate a previous tour somehow, otherwise these methods are irrelevant
fun HDay.hasPreviousTours() = !isHomeDay && lowestTourIndex != 0
fun HDay.hasNoPreviousTours() = !hasPreviousTours()
fun HDay.amountOfPreviousTours() = if (isHomeDay) 0 else -lowestTourIndex

fun HDay.hasLaterTours() = !isHomeDay && highestTourIndex != 0
fun HDay.hasNoLaterTours() = !hasLaterTours()
fun HDay.amountOfLaterTours() = if (isHomeDay) 0 else highestTourIndex
