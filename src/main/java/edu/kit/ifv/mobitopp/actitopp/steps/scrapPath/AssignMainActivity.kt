package edu.kit.ifv.mobitopp.actitopp.steps.scrapPath

import edu.kit.ifv.mobitopp.actitopp.HWeekPattern
import edu.kit.ifv.mobitopp.actitopp.RNGHelper
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType


fun HWeekPattern.assignMainActivity(input: PersonWithRoutine) {

    val output = days.map { day ->
        val availableOptions = ActivityType.FULLSET.toMutableSet()
        if(day.amountOfTours > 0) {
            availableOptions.remove(ActivityType.HOME)
        }
        if(!input.person.isAllowedToWork) availableOptions.remove(ActivityType.WORK)
        step2AWithParams.select {DaySituation(it, input, day)}
    }
    println(output)
}

fun HWeekPattern.assignMainActivityCoordinated(input: PersonWithRoutine, rngHelper: RNGHelper): List<ActivityType> {
    val person = input.person
    val output = days.map { day ->
        val availableOptions = ActivityType.FULLSET.toMutableSet()
        if(day.amountOfTours > 0) {
            availableOptions.remove(ActivityType.HOME)
        }
        if(!person.isAllowedToWork) availableOptions.remove(ActivityType.WORK)
        if(countDaysWithSpecificActivity(ActivityType.WORK) >= input.amountOfWorkingDays() &&
            !day.hasAnyActivity(ActivityType.WORK) &&
            person.isAnywayEmployed()
            ) {
            availableOptions.remove(ActivityType.WORK)
        }
        if(countDaysWithSpecificActivity(ActivityType.EDUCATION) >= input.amountOfEducationDays() &&
            !day.hasAnyActivity(ActivityType.EDUCATION) &&
            person.isinEducation()
            ) {
            availableOptions.remove(ActivityType.EDUCATION)
        }
        val rnd = rngHelper.randomValue
        val message = coordinatedStep2AWithParams.utilities(availableOptions) { DaySituation(it, input, day) }
        println("New: [$rnd] $message")
        println("New: [$rnd] ${coordinatedStep2AWithParams.probabilities(availableOptions) { DaySituation(it, input, day) }}")
        coordinatedStep2AWithParams.select(availableOptions, rnd) {DaySituation(it, input, day)}
    }
    return output
}