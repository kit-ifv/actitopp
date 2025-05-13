package edu.kit.ifv.mobitopp.actitopp.modernization

import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.steps.TourAttributes

import java.time.DayOfWeek

class DayLayout(val mainActivity: ActivityType): BidirectionalQueue<UninitializedTour>(UninitializedTour()) {
    lateinit var main: ActivityType
    private val queue: ArrayDeque<UninitializedTour> = ArrayDeque()
    private var offset = 0

    fun addPrecursor() {
        queue.addFirst(UninitializedTour())
        offset++
    }

    fun addSuccessor() {
        queue.addLast(UninitializedTour())
    }

}

class UninitializedTour {
    fun initialize(activityType: ActivityType) = TourStructure(activityType)
}

/**
 * A bidirectional indexed element has the original element, but also the position in the element queue as absolute position
 * (i.e. the first element has index 0, which is the first precursor element or the main element if no precursors exist)
 *
 * Also the element knows its position relative to the main element.
 */

class BidirectionalIndexedValue<T>(

    val absoluteIndex: Int,
    val offset: Int,
    val element: T,
) {
    val position = Position.fromRelativeIndex(absoluteIndex - offset)
}

/**
 * A bidirectional collection extends bidirectionally from a main element, where precursor elements are added to the
 * front and successor elements to the back of the ordering of the structure.
 */
interface BidirectionalCollection<T> {
    fun amountOfElements(): Int
    fun amountOfPrecursorElements(): Int
    fun amountOfSuccessorElements(): Int

    operator fun get(index: Int = 0): T
    fun mainElement() = get(0)
    fun elements(): Collection<BidirectionalIndexedValue<T>>

}

abstract class BidirectionalQueue<T>(mainElement: T) : BidirectionalCollection<T> {
    private val queue: ArrayDeque<T> = ArrayDeque()
    private var offset = 0

    init {
        // In order to avoid inheritance issues, the queue is made private and the required main element is added here.
        queue.add(mainElement)
    }

    fun addPrecursor(element: T) {
        queue.addFirst(element)
        offset++
    }

    fun addSuccessor(element: T) {
        queue.addLast(element)
    }

    override fun amountOfElements() = queue.size
    override fun amountOfPrecursorElements() = offset
    override fun amountOfSuccessorElements() =
        queue.size - (offset + 1) // The main tour does not count towards the successosrs, thus + 1

    /**
     * Access the elements using a relative index centered around the main element.
     */
    override operator fun get(index: Int) = queue[index + offset]

    override fun elements(): Collection<BidirectionalIndexedValue<T>> {
        return queue.withIndex().map { (index, value) -> BidirectionalIndexedValue(index, offset, value) }
    }


}

class DayStructure(startTimeDay: DurationDay, mainTourStructure: TourStructure) :
    BidirectionalQueue<TourStructure>(mainTourStructure) {
    constructor(dayIndex: Int, mainTourStructure: TourStructure) : this(DurationDay(dayIndex), mainTourStructure)

    val weekday: DayOfWeek = startTimeDay.weekday
    val duration = startTimeDay.timePoint

    fun loadPrecursors(activityTypes: Collection<ActivityType>) {
        activityTypes.reversed().forEach {
            addPrecursor(TourStructure(it))
        }
    }

    fun loadSuccessors(activityTypes: Collection<ActivityType>) {
        activityTypes.forEach {
            addSuccessor(TourStructure(it))
        }
    }

    fun mainTourActivityType(): ActivityType {
        return this[0][0]
//        if (queue.isEmpty()) {
//            return ActivityType.HOME
//        }
//        return queue[0].mainActivityType()
    }

    override fun toString(): String {
        return "Week (${duration.inWholeDays / 7}) Main Act: [${mainTourActivityType()}] ${
            weekday.toString().substring(0, 3)
        } Planned Tours: (${amountOfElements()})"
    }
}

enum class Position {
    BEFORE, MAIN, AFTER;

    companion object {
        fun fromRelativeIndex(index: Int) = when (index) {
            in Int.MIN_VALUE..<0 -> BEFORE
            0 -> MAIN
            in 1..Int.MAX_VALUE -> AFTER
            else -> throw NoSuchElementException("The branches above should be exhaustive")
        }


    }
}

data class IndexedTourStructure(
    val tour: BidirectionalIndexedValue<TourStructure>,

    ) : TourAttributes {
    override fun isFirstTourOfDay(): Boolean = tour.absoluteIndex == 0

    override fun isSecondTourOfDay(): Boolean = tour.absoluteIndex == 1

    override fun isThirdTourOfDay(): Boolean = tour.absoluteIndex == 2

    override fun isBeforeMainTour(): Boolean = tour.position == Position.BEFORE

    override fun isAfterMainTour(): Boolean = tour.position == Position.AFTER

    override fun tourMainActivityIsWork(): Boolean = tour.element[0] == ActivityType.WORK

    override fun tourMainActivityIsEducation(): Boolean = tour.element[0] == ActivityType.EDUCATION

    override fun tourMainActivityIsShopping(): Boolean = tour.element[0] == ActivityType.SHOPPING

    override fun tourMainActivityIsTransport(): Boolean = tour.element[0] == ActivityType.TRANSPORT
    override fun numActivitiesBeforeMainActivityIs1(): Boolean = tour.element.amountOfPrecursorElements() == 1

    override fun numActivitiesBeforeMainActivityIs2(): Boolean = tour.element.amountOfPrecursorElements() == 2

    override fun numActivitiesBeforeMainActivityIs3(): Boolean = tour.element.amountOfPrecursorElements() == 3

    override fun tourHas2Activities(): Boolean = tour.element.amountOfElements() == 2

    override fun tourHas3Activities(): Boolean = tour.element.amountOfElements() == 3

    override fun tourHas4Activities(): Boolean = tour.element.amountOfElements() == 4
    override fun mainActivityIsWork(): Boolean {
        return tour.element.mainElement() == ActivityType.WORK
    }

    override fun mainActivityIsEducation(): Boolean {
        return tour.element.mainElement() == ActivityType.EDUCATION
    }

    override fun mainActivityIsShopping(): Boolean {
        return tour.element.mainElement() == ActivityType.SHOPPING
    }

    override fun mainActivityIsTransport(): Boolean {
        return tour.element.mainElement() == ActivityType.TRANSPORT
    }
}


