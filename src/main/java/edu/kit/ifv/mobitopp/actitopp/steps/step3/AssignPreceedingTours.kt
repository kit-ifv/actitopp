package edu.kit.ifv.mobitopp.actitopp.steps.step3

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.HDay
import edu.kit.ifv.mobitopp.actitopp.WeekRoutine
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonWithRoutine

data class PrecedingInput(
    val input: PersonWithRoutine,
    val days: List<DayWithBounds>,
) {
    constructor(input: PersonWithRoutine, ints: Collection<Int>, days: List<HDay>): this(input, ints.zip(days).map{DayWithBounds(it.second, it.first)})
}

data class DayWithBounds(
    val day: HDay,
    val lowerBoundFromJointAction: Int,
    val amountOfTours: Int = 1  // In step 3A we can safely assume that there be only 1 tour, the main tour
)

fun interface GenerateSideTours {
    fun generate(precedingInput: PrecedingInput): List<Int>

}


fun ActitoppPerson.generatePrecedingTours(weekRoutine: WeekRoutine, lowerBoundsFromJointActions: Collection<Int>, lambda: ActitoppPerson.() -> GenerateSideTours): List<Pair<Int, HDay>> {
    val strategy = this.lambda()
    val input = PrecedingInput(
        PersonWithRoutine(this, weekRoutine),
        lowerBoundsFromJointActions,
        weekPattern.days
    )
    return strategy.generate(input).zip(weekPattern.days)

}

fun ActitoppPerson.assignPrecedingTours(activitiesPerDay: List<Pair<Int, HDay>>) {
    activitiesPerDay.forEach { (amount, day) ->
        repeat(amount) {
            day.generatePrecedingTour()
        }
    }
}

