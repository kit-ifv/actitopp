package edu.kit.ifv.mobitopp.actitopp

import edu.kit.ifv.mobitopp.actitopp.steps.step3.TourSituationInt
import edu.kit.ifv.mobitopp.actitopp.steps.step5.step5AWithParams
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class CoordinatorStep5ATest : CoordinatorTestUtilities() {


    @TestFactory
    fun coordinatedStep5ActualCode(): Collection<DynamicTest> {
        return persons.map { person ->
            DynamicTest.dynamicTest("${person.household.householdIndex} main Activities") {
                val weekRoutine = randomWeekRoutine(person)
                weekRoutine.loadToAttributeMap(person.getMutableMapForTest())

                val activityTypes = randomMainActivityTypes(person) // This is the result of step 2
                person.weekPattern.loadMainActivities(activityTypes)

                val preTours = generateRandomPrecedingTours(person)
                preTours.forEach {
                    it.generateRandomMainActivity()
                }
                val folTours = generateRandomFollowingTours(person)
                folTours.forEach {
                    it.generateRandomMainActivity()
                }
                person.weekPattern.days.forEach { day ->
                    day.tours.forEach { tour ->
                        val utility = instantiate(person, day, tour)
                        val probabilities = utility.probabilities { it.toInt() }
                        val modernProbabilities = step5AWithParams.probabilities {
                            TourSituationInt(it, person, weekRoutine, day, tour)
                        }

                        testDoubleMapEquality(probabilities, modernProbabilities)
                    }
                }

            }

        }
    }
    private fun instantiate(person: ActitoppPerson, day: HDay, tour: HTour) : StepUtility {
        return StepUtility("5A") {
            AttributeLookup(person, day, tour)
        }
    }

}