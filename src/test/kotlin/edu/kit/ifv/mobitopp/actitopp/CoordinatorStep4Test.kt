package edu.kit.ifv.mobitopp.actitopp

import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType.Companion.getTypeFromChar
import edu.kit.ifv.mobitopp.actitopp.steps.step3.DayWithBounds
import edu.kit.ifv.mobitopp.actitopp.steps.step3.GenerateSideToursFollowing
import edu.kit.ifv.mobitopp.actitopp.utils.zip
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertContentEquals

class CoordinatorStep4Test : CoordinatorTestUtilities() {

    @TestFactory
    fun coordinatedStep4ActualCode(): Collection<DynamicTest> {
        return persons.map { person ->
            DynamicTest.dynamicTest("${person.household.householdIndex} main Activities") {
                val rngCopy = person.personalRNG.copy()
                val weekRoutine = randomWeekRoutine(person)
                weekRoutine.loadToAttributeMap(person.getMutableMapForTest())

                val activityTypes = randomMainActivityTypes(person) // This is the result of step 2
                person.weekPattern.loadActivities(activityTypes)


                val randomPreceedingTours = randomPrecedingTours(person)

//                val expected = GenerateSideToursFollowing(rngCopy).generate(person, weekRoutine, boundDays)
//                executeStep4("4A", person)
//
//                val test = person.weekPattern.days.map { it.highestTourIndex }
//
//
//
//
//                assertContentEquals(expected, test, message = "\nOld:$test\nNew:$expected\n")

            }

        }
    }
    private fun executeStep4(id: String, person: ActitoppPerson) {
        val pattern = person.weekPattern
        // STEP 4A Main activity for all other tours
        for (currentDay in pattern.days) {
            // skip day if person is at home
            if (currentDay.isHomeDay) {
                continue
            }

            for (currentTour in currentDay.tours) {
                /*
                 * ignore tours if main activity purpose is already set
                 */
                if (!currentDay.existsActivityTypeforActivity(currentTour.index, 0)) {
                    // create attribute lookup
                    val lookup = AttributeLookup(person, currentDay, currentTour)

                    // create step object
                    val step = DCDefaultModelStep(id, fileBase, lookup, randomgenerator)

                    // disable working activity if person is not allowed to work
                    if (!person.isAllowedToWork) step.disableAlternative("W")

                    // if number of working days is achieved, disable W as alternative
                    if (person.getAttributefromMap("anztage_w") <= pattern.countDaysWithSpecificActivity(ActivityType.WORK) &&
                        currentDay.getTotalNumberOfActivitites(ActivityType.WORK) == 0
                    ) {
                        step.disableAlternative("W")
                    }
                    // if number of education days is achieved, disable E as alternative
                    if (person.getAttributefromMap("anztage_e") <= pattern.countDaysWithSpecificActivity(ActivityType.EDUCATION) &&
                        currentDay.getTotalNumberOfActivitites(ActivityType.EDUCATION) == 0
                    ) {
                        step.disableAlternative("E")
                    }

                    // make selection
                    val decision = step.doStep()
                    val activityType = getTypeFromChar(step.alternativeChosen[0])

                    var activity: HActivity? = null

                    // if activity already exits, set activity type only
                    if (currentDay.existsActivity(currentTour.index, 0)) {
                        require(false) {
                            "I make the bold statement: This code will never be reached"
                        }
                        activity = currentTour.getActivity(0)
                        activity.activityType = activityType
                    } else {
                        activity = HActivity(currentTour, 0, activityType)
                        currentTour.addActivity(activity)
                    }
                }
            }
        }
    }


}