package edu.kit.ifv.mobitopp.actitopp.modernization

import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.modernization.plan.HomeDayPlan
import edu.kit.ifv.mobitopp.actitopp.modernization.plan.MovingDayPlan
import edu.kit.ifv.mobitopp.actitopp.modernization.plan.MovingDayPlanInput
import edu.kit.ifv.mobitopp.actitopp.modernization.plan.MutableDayPlan

import java.time.DayOfWeek
import kotlin.time.Duration

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

    override fun toString(): String {
        return "($element, $position indices = [$absoluteIndex, $offset])"
    }
}

/**
 * A bidirectional collection extends bidirectionally from a main element, where precursor elements are added to the
 * front and successor elements to the back of the ordering of the structure.
 */
interface BidirectionalCollection<T> {
    fun amountOfElements(): Int
    fun amountOfPrecursorElements(): Int
    fun amountOfSuccessorElements(): Int
    val size: Int
    operator fun get(index: Int = 0): T
    fun mainElement() = get(0)
    fun indexedElements(): Collection<BidirectionalIndexedValue<T>>

    fun elements(): Collection<T>
    fun precursors(): Collection<T>
    fun successors(): Collection<T>

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

    override fun indexedElements(): Collection<BidirectionalIndexedValue<T>> {
        return queue.withIndex().map { (index, value) -> BidirectionalIndexedValue(index, offset, value) }
    }

    override fun precursors(): Collection<T> {
        return queue.subList(0, offset)
    }
    override fun successors(): Collection<T> {
        return queue.subList(offset + 1, queue.size)
    }

    override fun elements(): Collection<T> {
        return queue
    }

    override val size: Int
        get() = queue.size

}
// TODO seal interface once HDay is killed
/**
 * The day structure contains the current tour structures that will be present on a given day, this is a readonly view.
 */
interface DayStructure {
    val startTimeDay: DurationDay
    val weekday: DayOfWeek
    val duration : Duration

    fun previousDaytime(): DurationDay {
        return startTimeDay.previous()
    }
    fun mainActivityType(): ActivityType
    fun amountOfPrecursorElements(): Int
    fun amountOfSuccessorElements(): Int
    fun amountOfElements(): Int
    // TODO elements could probably be an iterator
    fun elements(): Collection<TourStructure>
    fun indexedElements(): Collection<BidirectionalIndexedValue<TourStructure>>
    fun amountOfActivities() = elements().sumOf { it.size }
    fun getPlannedTourAmounts():PlannedTourAmounts = PlannedTourAmounts(amountOfPrecursorElements(), amountOfSuccessorElements())

    operator fun contains(activityType: ActivityType): Boolean {
        return elements().any { activityType in it }
    }
    val minimumAmountOfToursByJointActions: Int get() {throw UnsupportedOperationException("Nope")}
    val minimumAmountOfActivitiesByJointActions: Int get() {throw UnsupportedOperationException("Nope")}



    fun toDayPlan(movingDayPlanInput: MovingDayPlanInput): MutableDayPlan
}

class HomeDay(override val startTimeDay: DurationDay) : DayStructure {
    override val weekday: DayOfWeek = startTimeDay.weekday
    override val duration: Duration = startTimeDay.startOfDay
    override fun mainActivityType(): ActivityType = ActivityType.HOME

    override fun amountOfPrecursorElements(): Int = 0

    override fun amountOfSuccessorElements(): Int = 0
    override fun amountOfElements(): Int = 0
    override fun getPlannedTourAmounts(): PlannedTourAmounts = PlannedTourAmounts.NONE
    override val minimumAmountOfToursByJointActions: Int = 0
    override fun elements(): Collection<TourStructure> = emptySet()
    override fun indexedElements(): Collection<BidirectionalIndexedValue<TourStructure>> = emptySet()

    override fun toDayPlan(movingDayPlanInput: MovingDayPlanInput): MutableDayPlan {
        return HomeDayPlan(startTimeDay)
    }
}

class ModifiableDayStructure(override val startTimeDay: DurationDay, mainTourStructure: TourStructure) :
    BidirectionalQueue<TourStructure>(mainTourStructure), DayStructure {
    constructor(dayIndex: Int, mainTourStructure: TourStructure) : this(DurationDay(dayIndex), mainTourStructure)

    override val weekday: DayOfWeek = startTimeDay.weekday
    override val duration = startTimeDay.startOfDay
    // TODO maybe protect this field from modification, right now it is just a template holder for 3A
    override var minimumAmountOfToursByJointActions: Int = 0
    // TODO, joint action modelling should not be interweaved with normal structures.
    override val minimumAmountOfActivitiesByJointActions: Int = 0
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

    override fun mainActivityType(): ActivityType = mainTourActivityType()
    private fun mainTourActivityType(): ActivityType {
        return this[0][0]
    }

    override fun toString(): String {
        return "Week (${duration.inWholeDays / 7}) Main Act: [${mainTourActivityType()}] ${
            weekday.toString().substring(0, 3)
        } Planned Tours: (${elements().joinToString()})"
    }

    override fun toDayPlan(movingDayPlanInput: MovingDayPlanInput): MutableDayPlan {
        return MovingDayPlan.create(
            indexedElements(),
            movingDayPlanInput
        )
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




