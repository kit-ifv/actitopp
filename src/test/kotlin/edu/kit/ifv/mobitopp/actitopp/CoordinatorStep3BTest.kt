package edu.kit.ifv.mobitopp.actitopp

import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.steps.step3.DayWithBounds
import edu.kit.ifv.mobitopp.actitopp.steps.step3.GenerateSideToursFollowing
import edu.kit.ifv.mobitopp.actitopp.steps.step3.GenerateSideToursPreceeding
import edu.kit.ifv.mobitopp.actitopp.steps.step3.PreviousDaySituation
import edu.kit.ifv.mobitopp.actitopp.steps.step3.step3AWithParams
import edu.kit.ifv.mobitopp.actitopp.steps.step3.step3BWithParams
import edu.kit.ifv.mobitopp.actitopp.utils.zip
import edu.kit.ifv.mobitopp.actitopp.utils.zipWithPrevious
import edu.kit.ifv.mobitopp.generateHouseholds
import edu.kit.ifv.mobitopp.generatePersons
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.random.Random
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class CoordinatorStep3BTest: CoordinatorTestUtilities() {

    private val persons = generateHouseholds(200).flatMap { it.generatePersons(5) }


    @TestFactory
    fun testUtilityEquality(): Collection<DynamicTest> {
        return persons.map { person ->
            DynamicTest.dynamicTest("${person.household.householdIndex} main Activities") {
                val weekRoutine = randomWeekRoutine(person) // This is essentially all step 1
                weekRoutine.loadToAttributeMap(person.getMutableMapForTest())
                val activityTypes = randomMainActivityTypes(person) // This is the result of step 2
                person.weekPattern.loadActivities(activityTypes)
                // Verify proper loading Pre test assert
                ActivityType.FULLSET.forEach {
                    val expected = person.weekPattern.countDaysWithSpecificActivity(it)
                    val actual = activityTypes.count { a -> a == it }
                    assertEquals(expected, actual, "For Activity $it pattern = $expected generated = $actual $activityTypes")
                }
                person.weekPattern.days.zipWithPrevious().forEach {  (previous, day) ->

                    val oldUtilities = generateUtilities("3B", person, day) { it.toInt()}
                    val newUtilities = generateStep3BUtilitiies(person, weekRoutine, day, previous)
                    testDoubleMapEquality(oldUtilities, newUtilities)
                }

            }

        }
    }


    @TestFactory
    fun coordinatedStep3ActualCode(): Collection<DynamicTest> {
        return persons.map { person ->
            DynamicTest.dynamicTest("${person.household.householdIndex} main Activities") {
                val rngCopy = person.personalRNG.copy()
                val weekRoutine = randomWeekRoutine(person)
                weekRoutine.loadToAttributeMap(person.getMutableMapForTest())

                val activityTypes = randomMainActivityTypes(person) // This is the result of step 2
                person.weekPattern.loadActivities(activityTypes)

                val intArray = random7DaysIntArray(person)
                val randomPreceedingTours = randomPrecedingTours(person)
                randomPreceedingTours.zip(person.weekPattern.days).forEach {(amount, day) ->
                    repeat(amount) {
                        day.generatePrecedingTour()
                    }
                }
                val boundDays = person.weekPattern.days.zip(intArray.toList(), randomPreceedingTours).map { (a, b, c)->
                    DayWithBounds(
                        day = a,
                        lowerBoundFromJointAction =b,
                        amountOfTours = c
                    )



                }
                val expected = GenerateSideToursFollowing(rngCopy).generate(person, weekRoutine, boundDays)
                executeStep3("3B", person, intArray)

                val test = person.weekPattern.days.map { it.highestTourIndex }




                assertContentEquals(expected, test, message = "\nOld:$test\nNew:$expected\n")

            }

        }
    }
    private fun HWeekPattern.loadActivities(acts: Collection<ActivityType>) {
        days.zip(acts).forEach { (day, acts) ->
            val tour = day.generateMainTour()
            tour.generateMainActivity(acts)
        }
    }

    private fun generateStep3BUtilitiies(person: ActitoppPerson, routine: PersonWeekRoutine, day: HDay, previousDay: HDay?): Map<Int, Double> {
        // Sorting is necessary to ensure that the order of the map stays the same, which is relevant for the selection process.
        return step3BWithParams.utilities { PreviousDaySituation(it, day,  null, null, person, routine) }
    }

    private fun random7DaysIntArray(person: ActitoppPerson): IntArray {
        val rng = Random(person.age)
        return IntArray(7) {
            rng.nextInt(0, 5)
        }
    }
    private fun executeStep3(id: String, person: ActitoppPerson, numberoftoursperday_lowerboundduetojointactions: IntArray = intArrayOf(0, 0, 0, 0, 0, 0, 0)) {

        val pattern = person.weekPattern
        for (currentDay in pattern.days) {
            // skip day if person is at home
            if (currentDay.isHomeDay) {
                continue
            }

            // create attribute lookup
            val lookup = AttributeLookup(person, currentDay)

            // create step object
            val step = DCDefaultModelStep(id, fileBase, lookup, randomgenerator)

            // initialize minimum number of tours
            var minnumberoftours = 0

            // check if minimum number bound is already achieved
            if (currentDay.amountOfTours < numberoftoursperday_lowerboundduetojointactions[currentDay.index]) {
                val remainingnumberoftours =
                    numberoftoursperday_lowerboundduetojointactions[currentDay.index] - currentDay.amountOfTours
                // Half number of tours for step 3A as some of them will be modeled using step 3B
                if (id == "3A") minnumberoftours = Math.round((remainingnumberoftours / 2).toFloat())
                // set all remaining tours for step 3B
                if (id == "3B") minnumberoftours = remainingnumberoftours
            }

            // limit alternatives (lower bounds)
            step.limitLowerBoundOnly(minnumberoftours)

            // limit alternatives (upper bound using result from step 1k)
            if (Configuration.coordinated_modelling) {
                var maxnumberoftours = -1
                if (person.getAttributefromMap("anztourentag_mean") == 1.0) maxnumberoftours = 1
                if (person.getAttributefromMap("anztourentag_mean") == 2.0) maxnumberoftours = 2
                if (maxnumberoftours != -1) step.limitUpperBoundOnly((if (maxnumberoftours >= minnumberoftours) maxnumberoftours else minnumberoftours))
            }

            val rnd = person.personalRNG.randomValue
            // make selection
            val decision = step.doStep(rnd)

//            val utilities = step.utilities { it.toInt() }
//            println("Old: [$rnd] ${step.probabilities()}\n")
//            println("Old: [$rnd] $utilities")
//            println("Old: ${step.activeOptions()}")

            // create tours based on the decision and add them to the pattern
            for (j in 1..decision) {
                var tour: HTour? = null
                // 3A - tours before main tour
                if (id == "3A" && !currentDay.existsTour(-1 * j)) tour = HTour(currentDay, (-1) * j)
                // 3B - tours after main tour
                if (id == "3B" && !currentDay.existsTour(+1 * j)) tour = HTour(currentDay, (+1) * j)

                if (tour != null) currentDay.addTour(tour)
            }


            if (id == "3B") assert(currentDay.amountOfTours >= numberoftoursperday_lowerboundduetojointactions[currentDay.index]) { "wrong number of tours - violating lower bound due to joint actions" }
        }
    }

}