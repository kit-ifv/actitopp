package edu.kit.ifv.mobitopp.actitopp

import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitopp.steps.step3.DayWithBounds
import edu.kit.ifv.mobitopp.actitopp.steps.step3.GenerateSideToursPreceeding
import edu.kit.ifv.mobitopp.actitopp.steps.step3.PrecedingInput
import edu.kit.ifv.mobitopp.actitopp.steps.step3.PreviousDaySituation
import edu.kit.ifv.mobitopp.actitopp.steps.step3.step3AWithParams
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertContentEquals

class CoordinatorStep3ATest : CoordinatorTestUtilities() {


    @TestFactory
    fun coordinatedStep3ActualCode(): Collection<DynamicTest> {
        return persons.map { person ->
            DynamicTest.dynamicTest("${person.household.householdIndex} main Activities") {
                val rngCopy = person.personalRNG.copy()
                val weekRoutine = randomWeekRoutine(person)
                weekRoutine.loadToAttributeMap(person.getMutableMapForTest())

                val activityTypes = randomMainActivityTypes(person) // This is the result of step 2
                person.weekPattern.loadMainActivities(activityTypes)

                val intArray = random7DaysIntArray(person)
                val debugOutput = executeStep3("3A", person, intArray)
                val expectedDebugOutput =
                    GenerateSideToursPreceeding(rngCopy).debugInfo(person, weekRoutine, intArray.toList())

                debugOutput.zip(expectedDebugOutput).forEach { (a, b) ->
                    assertUtilityEquals(a, b)
                }
                val test = person.weekPattern.days.map { if (it.hasTours()) -it.lowestTourIndex else 0 }
                val expected = expectedDebugOutput.map { it.selection }



                assertContentEquals(expected, test, message = "\n$test\n$expected\n")

            }

        }
    }



    private fun GenerateSideToursPreceeding.debugInfo(
        person: ActitoppPerson,
        routine: WeekRoutine,
        lowerBounds: Collection<Int>,
    ): List<UtilityDebug<Int>> {
        val precedingInput = PrecedingInput(
            PersonWithRoutine(person, routine),
            lowerBounds.zip(person.weekPattern.days).map { DayWithBounds(it.second, it.first) })
        var previousResult: Int? = null
        return precedingInput.run {

            days.map { day ->

                val availableOptions = determineAvailableOptions(day, precedingInput.personInfo.routine)

                val rnd = rngHelper.randomValue
                val converter: (Int) -> PreviousDaySituation = {
                    createChoiceSituation(it, day, previousResult, precedingInput.personInfo)

                }
                val selection = choiceModel.select(availableOptions, rnd, converter)


                UtilityDebug(
                    availableOptions,
                    choiceModel.utilities(availableOptions, converter),
                    choiceModel.probabilities(availableOptions, converter),
                    rnd,
                    selection
                ).also {
                    previousResult = selection
                }
            }
        }
    }


    private fun generateStep3AUtilitiies(
        person: ActitoppPerson,
        routine: WeekRoutine,
        day: HDay,
        previousDay: HDay?,
    ): Map<Int, Double> {
        // Sorting is necessary to ensure that the order of the map stays the same, which is relevant for the selection process.
        return step3AWithParams.utilities { PreviousDaySituation(it, day, null, null, person, routine) }
    }



    private fun executeStep3(
        id: String,
        person: ActitoppPerson,
        numberoftoursperday_lowerboundduetojointactions: IntArray = intArrayOf(0, 0, 0, 0, 0, 0, 0),
    ): List<UtilityDebug<Int>> {
        val pattern = person.weekPattern
        val output = mutableListOf<UtilityDebug<Int>>()
        for (currentDay in pattern.days) {
            // skip day if person is at home

            val rnd = person.personalRNG.randomValue
            if (currentDay.isHomeDay) {
                output.add(
                    UtilityDebug(
                        setOf(0),
                        utilities = mapOf(0 to 0.0),
                        probabilities = mapOf(0 to 1.0),
                        rnd, 0
                    )
                )
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


            // make selection
            val decision = step.doStep(rnd)


            val options = step.activeOptions().map { it.toInt() }
            output.add(
                UtilityDebug(
                    options,
                    step.utilities { it.toInt() }.filterKeys { it in options },
                    step.probabilities().filterKeys { it in options },
                    rnd,
                    decision
                )
            )
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
        return output
    }

}