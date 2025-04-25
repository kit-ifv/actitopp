package edu.kit.ifv.mobitopp.actitopp

import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType.Companion.getTypeFromChar
import edu.kit.ifv.mobitopp.actitopp.steps.scrapPath.DaySituation
import edu.kit.ifv.mobitopp.actitopp.steps.scrapPath.GenerateCoordinated
import edu.kit.ifv.mobitopp.actitopp.steps.scrapPath.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitopp.steps.scrapPath.coordinatedStep2AWithParams
import edu.kit.ifv.mobitopp.generateHouseholds
import edu.kit.ifv.mobitopp.generatePersons
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.random.Random
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue

class CoordinatorStep2Test: CoordinatorTestUtilities() {

    private val persons = generateHouseholds(200).flatMap { it.generatePersons(5) }
    @TestFactory
    fun coordinatedStep2Utilities(): Collection<DynamicTest> {
            return persons.map { person ->
                DynamicTest.dynamicTest("${person.household.householdIndex} main Activities") {
                    val weekRoutine = randomWeekRoutine(person)
                    person.weekPattern.days.forEach {  day ->
                        weekRoutine.loadToAttributeMap(person.getMutableMapForTest())
                        val oldUtilities = generateUtilities("2A", person, day) {ActivityType.getTypeFromChar(it[0])}
                        val newUtilities = generateMainActivityUtilities(person, weekRoutine, day)
                        testDoubleMapEquality(oldUtilities, newUtilities)
                    }

                }

            }
        }

    @TestFactory
    fun coordinatedStep2ActualCode(): Collection<DynamicTest> {
        return persons.map { person ->
            DynamicTest.dynamicTest("${person.household.householdIndex} main Activities") {
                val rngCopy = person.personalRNG.copy()
                val weekRoutine = randomWeekRoutine(person)
                weekRoutine.loadToAttributeMap(person.getMutableMapForTest())
                // Test ordering matters, since the old code produces side effects.
                val expected = GenerateCoordinated(rngCopy).generate(PersonWithRoutine(person, weekRoutine), person.weekPattern.days)
                executeStep2("2A", person)

                val test = person.weekPattern.days.map { it.getTourOrNull(0)?.getActivity(0)?.activityType ?: ActivityType.HOME }




                assertContentEquals(expected, test, message = "\n$test\n$expected\n")

            }

        }
    }
    @TestFactory
    fun coordinatedWhenSpawningTooManyAlternatives(): Collection<DynamicTest> {
        return persons.map {person ->
            DynamicTest.dynamicTest("Generating too many activities for person") {

                val weekRoutine = randomWeekRoutine(person)
                weekRoutine.loadToAttributeMap(person.getMutableMapForTest())
                val weekPattern = person.weekPattern
                val actualWorkdays = person.getAttributefromMap("anztage_w")
                val targetNumber = actualWorkdays.toInt()
                (0..targetNumber).forEach {
                    val day = weekPattern.getDay(it)
                    val tour = HTour(day, 0)
                    day.addTour(tour)
                    val activity = HActivity(tour, 0, ActivityType.WORK)
                    tour.addActivity(activity)
                }
                val expected = weekPattern.countDaysWithSpecificActivity(ActivityType.WORK)
                assertTrue(expected >= actualWorkdays, "\n expected: $actualWorkdays , actual: $expected \n $weekPattern")
            }
        }
    }

    private fun generateMainActivityUtilities(person: ActitoppPerson, routine: PersonWeekRoutine, day: HDay): Map<ActivityType, Double> {
        // Sorting is necessary to ensure that the order of the map stays the same, which is relevant for the selection process.
        return coordinatedStep2AWithParams.utilities { DaySituation(it, PersonWithRoutine(person, routine), day) }.toSortedMap(comparator =  { o1, o2 -> o1.name.compareTo(o2.name) })
    }

    private fun randomWeekRoutine(person: ActitoppPerson): PersonWeekRoutine {
        val rng = Random(person.persIndex)
        return PersonWeekRoutine(
            rng.nextInt(0, 7),
            rng.nextInt(0, 7),
            rng.nextInt(0, 7),
            rng.nextInt(0, 7),
            rng.nextInt(0, 7),
            rng.nextInt(0, 7),
            rng.nextInt(0, 7),
            rng.nextInt(0, 7),
        )
    }
    private val numberoftoursperday_lowerboundduetojointactions = intArrayOf(0, 0, 0, 0, 0, 0, 0)
    private fun executeStep2(id: String, person: ActitoppPerson) {
        val pattern = person.weekPattern
        // STEP 2A Main tour and main activity
        for (currentDay in pattern.days) {
            // execute step if main activity type does not exist
            if (!currentDay.existsActivityTypeforActivity(0, 0)) {
                // create attribute lookup
                val lookup = AttributeLookup(person, currentDay)

                // create step object
                val step = DCDefaultModelStep(id, fileBase, lookup, randomgenerator)

                initializeStep(currentDay, step, person, pattern)

                // make selection
                val rnd = person.personalRNG.randomValue

                val decision = step.doStep(rnd)
                val activityType = getTypeFromChar(step.alternativeChosen[0])
                val utilities = step.utilities { getTypeFromChar(it[0])}

                if (activityType != ActivityType.HOME) {
                    // add a new tour into the pattern if not existing
                    var mainTour: HTour? = null
                    if (!currentDay.existsTour(0)) {

                        mainTour = HTour(currentDay, 0)
                        currentDay.addTour(mainTour)
                    } else {
                        require(false) {
                            "can this code even trigger?"
                        }
                        mainTour = currentDay.getTour(0)
                    }

                    // add a new activity into the pattern if not existing or set activity type
                    var activity: HActivity? = null
                    if (!currentDay.existsActivity(0, 0)) {
                        activity = HActivity(mainTour, 0, activityType)
                        mainTour.addActivity(activity)
                    } else {
                        require(false) {
                            "can this code even trigger?"
                        }
                        activity = currentDay.getTour(0).getActivity(0)
                        activity.activityType = activityType
                    }
                }
            }
        }
    }

    private fun initializeStep(
        currentDay: HDay,
        step: DCDefaultModelStep,
        person: ActitoppPerson,
        pattern: HWeekPattern,
    ) {
        // if there are existing tours (e.g., from joint activities) , disable H as alternative as being at home is no longer a valid alternative
        if (currentDay.amountOfTours > 0 || numberoftoursperday_lowerboundduetojointactions[currentDay.index] > 0) {
            step.disableAlternative("H")
        }

        // disable working activity if person is not allowed to work
        if (!person.isAllowedToWork) step.disableAlternative("W")

        if (Configuration.coordinated_modelling) {
            // if number of working days is achieved, disable W as alternative
            if (person.getAttributefromMap("anztage_w") <=
                pattern.countDaysWithSpecificActivity(ActivityType.WORK)
                && currentDay.getTotalNumberOfActivitites(
                    ActivityType.WORK
                ) == 0 &&
                person.isAnywayEmployed()
            ) {
                step.disableAlternative("W")
            }
            // if number of education days is achieved, disable E as alternative
            if (person.getAttributefromMap("anztage_e") <=
                pattern.countDaysWithSpecificActivity(ActivityType.EDUCATION)
                && currentDay.getTotalNumberOfActivitites(
                    ActivityType.EDUCATION
                ) == 0 &&
                person.isinEducation()
            ) {
                step.disableAlternative("E")
            }

            // utility bonus for alternative W if person is employed and day is from Monday to Friday
            if (person.isAnywayEmployed() && currentDay.isStandardWorkingDay()
                && step.alternativeisEnabled("W")
            ) {
                step.adaptUtilityFactor("W", 1.3)
            }
            // utility bonus for alternative E if person is in Education and day is from Monday to Friday
            if (person.isinEducation() && currentDay.isStandardWorkingDay() && step.alternativeisEnabled("E")) {
                step.adaptUtilityFactor("E", 1.3)
            }
        }
    }

}