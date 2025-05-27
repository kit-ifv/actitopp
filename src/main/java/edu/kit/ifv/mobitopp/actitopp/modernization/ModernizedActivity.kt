package edu.kit.ifv.mobitopp.actitopp.modernization

import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.modernization.plan.DetermineTripDuration
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonWithRoutine
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

interface Action {
    val startTime: Duration?
    val duration: Duration?
    val endTime: Duration?
}

interface LinkedAction : Action {
    val previous: LinkedAction?
    val next: LinkedAction?

    fun estimatedDuration(defaultDuration: Map<ActivityType, Duration>): Duration
}

interface Activity : Action {
    override val startTime: Duration?
    override val duration: Duration?
    override val endTime: Duration?
    val activityType: ActivityType
    val position: Position
}

/**
 *
 */
interface MutableActivity : Activity {
    override var startTime: Duration?
    override var duration: Duration?
}

operator fun Duration.plus(nullable: Duration?): Duration {
    return nullable?.let { it + this } ?: this
}

class ModernizedActivity(
    override val activityType: ActivityType,
    override var startTime: Duration? = null,
    override var duration: Duration? = null,
    override val position: Position
) : MutableActivity {


    override val endTime get() = startTime?.let { it + duration }


}

class LinkedActivity(
    val original: ModernizedActivity,
    var previousTrip: ModernizedTrip? = null,
    var nextTrip: ModernizedTrip? = null,
) : MutableActivity by original, LinkedAction {


    override val previous: LinkedAction?
        get() = previousTrip
    override val next: LinkedAction?
        get() = nextTrip

    /**
     * If the duration is not yet set, estimate the duration based on the amount of occurences of a given
     * activity during a day.
     */
    override fun estimatedDuration(defaultDuration: Map<ActivityType, Duration>): Duration {
        return duration ?: defaultDuration.getValue(activityType)
    }

    fun link(other: LinkedActivity, duration: Duration = 15.minutes) {
        val trip = ModernizedTrip(
            duration = duration,
            previousActivity = this,
            nextActivity = other
        )

        this.nextTrip = trip
        other.previousTrip = trip
    }


    fun iterator(): Sequence<LinkedAction> {
        return LinkedActionIterator(this).asSequence()
    }

    fun backwardIterator(): Sequence<LinkedAction> {
        return BackwardLinkedActionIterator(this).asSequence()
    }

    override fun toString(): String {
        return "$activityType start=($startTime) duration=($duration)"
    }

    companion object {
        fun homeDay(): LinkedActivity = LinkedActivity(ModernizedActivity(ActivityType.HOME, position = Position.MAIN))
    }
}

class LinkedActionIterator(start: LinkedAction) : Iterator<LinkedAction> {
    private var current: LinkedAction? = start
    override fun hasNext(): Boolean {
        return current != null
    }

    override fun next(): LinkedAction {
        return current!!.also { current = it.next }
    }
}

class BackwardLinkedActionIterator(start: LinkedAction) : Iterator<LinkedAction> {
    private var current: LinkedAction? = start
    override fun hasNext(): Boolean {
        return current != null
    }

    override fun next(): LinkedAction {
        return current!!.also { current = it.previous }
    }
}

fun List<LinkedActivity>.linkByHomeActivity(
    other: Collection<LinkedActivity>,
    person: PersonWithRoutine,
    tripDuration: DetermineTripDuration,
): List<LinkedActivity> {
    val homeActivity = LinkedActivity.homeDay()
    homeActivity.duration = 1.minutes // Scale home activities to their actual times later, r.n. its 1 minute
    val lastElement = this.last()
    val nextElement = other.first()
    lastElement.link(
        homeActivity, tripDuration.lastTourTrip(
            person = person,
            activityType = lastElement.activityType
        )
    )
    homeActivity.link(nextElement, tripDuration.firstTourTrip(person, nextElement.activityType))
    return this.toMutableList().also { it.add(homeActivity) }
}

class ModernizedTrip(
    override val duration: Duration,
    val previousActivity: LinkedActivity,
    val nextActivity: LinkedActivity,
) : LinkedAction {
    init {

        require(!(previousActivity.activityType == ActivityType.HOME && nextActivity.activityType == ActivityType.HOME)) {
         " This is bad"
        }
    }

    override fun estimatedDuration(defaultDuration: Map<ActivityType, Duration>): Duration {
        return duration
    }

    override val startTime: Duration? get() = previousActivity.endTime
    override val endTime: Duration? get() = nextActivity.startTime
    override val previous: LinkedAction
        get() = previousActivity
    override val next: LinkedAction
        get() = nextActivity

    override fun toString(): String {
        return "Trip ($duration) ${previousActivity.activityType} (#${
            previousActivity.hashCode().toString().substring(0, 3)
        }) ${nextActivity.activityType} (#${nextActivity.hashCode().toString().substring(0, 3)})"
    }
}
