package edu.kit.ifv.mobitopp.actitopp.modernization

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.HDay
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.steps.step3.PreviousDaySituation
import edu.kit.ifv.mobitopp.actitopp.steps.step3.step3AWithParams
import java.time.DayOfWeek
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days


class DayStructure(val startTimeDay: Duration) {
    constructor(dayIndex: Int): this(dayIndex.days)
    val weekDay = DayOfWeek.of(startTimeDay.inWholeDays.toInt() % 7 + 1)
    private val queue: ArrayDeque<TourStructure> = ArrayDeque()
    private var offset = 0


    fun addMainActivity(activityType: ActivityType) {
        require(queue.isEmpty()) {
            "When a tour is already placed, we cannot trivially spawn a main activity"
        }
        queue.add(TourStructure(activityType))
    }

    fun addPrecursor(tour: TourStructure) {
        queue.addFirst(tour)
        offset++
    }

    fun addSuccessor(tour: TourStructure) {
        queue.addLast(tour)
    }

    operator fun get(index: Int): TourStructure {
        return queue[index + offset]

    }
    fun mainActivityType(): ActivityType {
        if(queue.isEmpty()) {return ActivityType.HOME}
        return queue[0].mainActivityType()
    }

    fun tours() = queue.toList()

    override fun toString(): String {
        return "Week (${startTimeDay.inWholeDays / 7}) Main Act: [${mainActivityType()}] ${weekDay.toString().substring(0, 3)} Planned Tours: (${queue.size})"
    }
}

fun roll(person: ActitoppPerson, days: Collection<HDay>): List<Pair<Int, Int>> {

    val previousResult = 0
    return TODO() // step3AWithParams.select { PreviousDaySituation(it, person) }
}
