package edu.kit.ifv.mobitopp.actitopp

import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.steps.step3.TourSituationInt
import edu.kit.ifv.mobitopp.actitopp.steps.step5.step5BWithParams
import edu.kit.ifv.mobitopp.actitopp.steps.step6.ActivitySituation
import edu.kit.ifv.mobitopp.actitopp.steps.step6.step6WithParams
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class CoordinatorStep6Test : CoordinatorTestUtilities() {


    @TestFactory
    fun coordinatedStep6ActualCode(): Collection<DynamicTest> {
        return persons.map { person ->
            DynamicTest.dynamicTest("${person.household.householdIndex} main Activities") {
                val weekRoutine = randomWeekRoutine(person)
                weekRoutine.loadToAttributeMap(person.getMutableMapForTest())

                val activityTypes = randomMainActivityTypes(person)
                person.weekPattern.loadMainActivities(activityTypes)

                val preTours = generateRandomPrecedingTours(person)
                preTours.forEach {
                    it.generateRandomMainActivity()
                    it.generateRandomSideActivitiesBefore()
                    it.generateRandomSideActivitiesAfter()
                }
                val folTours = generateRandomFollowingTours(person)
                folTours.forEach {
                    it.generateRandomMainActivity()
                    it.generateRandomSideActivitiesBefore()
                    it.generateRandomSideActivitiesAfter()
                }
                person.weekPattern.days.forEach { day ->
                    day.tours.forEach { tour ->
                        tour.activities.forEach { activity ->
                            val utility = instantiate(person, day, tour, activity)
                            val probabilities = utility.probabilities { ActivityType.getTypeFromChar(it[0]) }
                            val modernProbabilities = step6WithParams.probabilities {
                                ActivitySituation(it, person, weekRoutine, day, tour, activity)
                            }

                            testDoubleMapEquality(probabilities, modernProbabilities)
                        }

                    }
                }

            }

        }
    }

    private fun instantiate(person: ActitoppPerson, day: HDay, tour: HTour, activity: HActivity): StepUtility {
        return StepUtility("6A") {
            AttributeLookup(person, day, tour, activity)
        }
    }

}