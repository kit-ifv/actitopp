package edu.kit.ifv.mobitopp.actitopp.steps.scrapPath

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.HDay
import edu.kit.ifv.mobitopp.actitopp.WeekRoutine
import edu.kit.ifv.mobitopp.actitopp.RNGHelper
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType

fun interface GenerateMainActivities {
    fun generate(input: PersonWithRoutine, days: List<HDay>): List<ActivityType>

    fun generate(person: ActitoppPerson, routine: WeekRoutine, days: List<HDay>) = generate(PersonWithRoutine(person, routine), days)
}

/**
 * This is the logic separation that ususally occurs in the legacy code using the Configuration.coordinated_modelling
 * variable. In order to replace this variable, we implement the behaviour of each step with .coordinated modelling
 * using own implementations.
 */
class GenerateDefault(private val rngHelper: RNGHelper): GenerateMainActivities {
    override fun generate(input: PersonWithRoutine, days: List<HDay>): List<ActivityType> {
        return days.map { day ->
            val availableOptions = ActivityType.FULLSET.toMutableSet()
            // TODO implement behaviour of numberoftoursperday_lowerboundduetojointactions > 0
            // TODO remove day.amountOfTours from here, since there can not be a tour right now
            if(day.amountOfTours > 0) {
                availableOptions.remove(ActivityType.HOME)
            }
            if(!input.person.isAllowedToWork) availableOptions.remove(ActivityType.WORK)
            step2AWithParams.select(rngHelper.randomValue) {DaySituation(it, input, day)}
        }
    }
}

class GenerateCoordinated(private val rngHelper: RNGHelper): GenerateMainActivities {
    override fun generate(input: PersonWithRoutine, days: List<HDay>): List<ActivityType> {
        val person = input.person
        // Keep track of the individual activities placed during this loop in a local variable instead of changing the pattern state.
        val activityCounts: MutableMap<ActivityType, Int> = mutableMapOf<ActivityType, Int>().withDefault { 0 }

        val output = days.map { day ->
            val availableOptions = ActivityType.FULLSET.toMutableSet()
            // TODO implement behaviour of numberoftoursperday_lowerboundduetojointactions > 0
            // TODO remove day.amountOfTours from here, since there can not be a tour right now
            if(day.amountOfTours > 0) {
                availableOptions.remove(ActivityType.HOME)
            }
            if(!person.isAllowedToWork) availableOptions.remove(ActivityType.WORK)
            if(activityCounts.getValue(ActivityType.WORK) >= input.amountOfWorkingDays() &&
                // TODO the check whether a day has an activity can also be removed, since the day has nothing right now
                !day.hasActivity(ActivityType.WORK) &&
                person.isAnywayEmployed()
            ) {
                availableOptions.remove(ActivityType.WORK)
            }
            if(activityCounts.getValue(ActivityType.EDUCATION) >= input.amountOfEducationDays() &&
                // TODO the check whether a day has an activity can also be removed, since the day has nothing right now
                !day.hasActivity(ActivityType.EDUCATION) &&
                person.isinEducation()
            ) {
                availableOptions.remove(ActivityType.EDUCATION)
            }
            val rnd = rngHelper.randomValue
            coordinatedStep2AWithParams.select(availableOptions, rnd) {DaySituation(it, input, day)}.also {
                activityCounts[it] = activityCounts.getValue(it) + 1
            }
        }
        return output
    }
}

fun ActitoppPerson.generateMainActivities(weekRoutine: WeekRoutine, lambda: ActitoppPerson.() -> GenerateMainActivities): List<Pair<ActivityType, HDay>> {
    val strategy = this.lambda()
    return strategy.generate(this, weekRoutine, weekPattern.days).zip(weekPattern.days)

}

fun ActitoppPerson.assignMainActivities(activitiesPerDay: List<Pair<ActivityType, HDay>>) {
    activitiesPerDay.forEach { (activityType, day) ->
        val tour = day.generateMainTour()
        tour.generateMainActivity(activityType)
    }
}
