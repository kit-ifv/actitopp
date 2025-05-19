//package edu.kit.ifv.mobitopp.actitopp
//
//import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
//import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType.Companion.getTypeFromChar
//import edu.kit.ifv.mobitopp.actitopp.steps.step3.TourSituation
//import edu.kit.ifv.mobitopp.actitopp.steps.step3.step4WithParams
//import org.junit.jupiter.api.DynamicTest
//import org.junit.jupiter.api.TestFactory
//import edu.kit.ifv.mobitopp.actitopp.steps.step4.SubTourMainActivityDeterminer
//import edu.kit.ifv.mobitopp.actitopp.steps.SubTourInput
//import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonWithRoutine
//import kotlin.test.assertEquals
//
//
//class CoordinatorStep4Test : CoordinatorTestUtilities() {
//
//    @TestFactory
//    fun coordinatedStep4ActualCode(): Collection<DynamicTest> {
//        return persons.map { person ->
//            DynamicTest.dynamicTest("${person.household.householdIndex} main Activities") {
//                val rngCopy = person.personalRNG.copy()
//                val weekRoutine = randomWeekRoutine(person)
//                weekRoutine.loadToAttributeMap(person.getMutableMapForTest())
//
//                val activityTypes = randomMainActivityTypes(person) // This is the result of step 2
//                person.weekPattern.loadMainActivities(activityTypes)
//
//                val randomPreceedingTours = generateRandomPrecedingTours(person)
//
//                val randomFollowingTours = generateRandomFollowingTours(person)
//                val generator = SubTourMainActivityDeterminer(rngCopy)
//                val personWithRoutine = PersonWithRoutine(person, weekRoutine)
//                val actual = person.weekPattern.days.map { generator.debugInfo(personWithRoutine, it) }
//                val expected = executeStep4("4A",person)
//                assertEquals(expected.size, actual.size)
//                expected.zip(actual).forEach {(a, b) ->
//                    a.zip(b).forEach { (c, d) ->
//                        assertUtilityEquals(c, d)
//                    }
//
//                }
//
//
//
//            }
//
//        }
//    }
//
//    private fun SubTourMainActivityDeterminer.debugInfo(person: PersonWithRoutine, day: HDay): List<UtilityDebug<ActivityType>> {
//        val input = SubTourInput(person, day)
//        val tracker = input.personWithRoutine.tracker
//        val output = day.tours.filter{!it.mainActivityHasType()}.map { tour ->
//
//            val availableOptions = step4WithParams.registeredOptions().toMutableSet()
//            tracker.run {
//                val internalDay = tour.day
//
//                if (!input.person.isAllowedToWork) availableOptions.remove(ActivityType.WORK)
//                if (internalDay.shouldNotBeWork()) availableOptions.remove(ActivityType.WORK)
//                if (internalDay.shouldNotBeEducation()) availableOptions.remove(ActivityType.EDUCATION)
//            }
//
//
//            val rnd = rngHelper.randomValue
//            val     converter: (ActivityType) -> TourSituation = {
//                TourSituation(it, input.person, input.routine, input.day, tour)
//            }
//            val selection = step4WithParams.select(availableOptions, rnd, converter)
//            UtilityDebug(
//                availableOptions,
//                step4WithParams.utilities(availableOptions, converter),
//                step4WithParams.probabilities(availableOptions, converter),
//                rnd,
//                selection
//            ).also { when(selection) {
//                ActivityType.WORK -> tracker.addWorkday(day)
//                ActivityType.EDUCATION -> tracker.addEducationDay(day)
//                else -> {}
//            } }
//        }
//        return output
//    }
//    private fun executeStep4(id: String, person: ActitoppPerson): List<List<UtilityDebug<ActivityType>>> {
//        val pattern = person.weekPattern
//
//        val stackedOutput = mutableListOf<List<UtilityDebug<ActivityType>>>()
//        // STEP 4A Main activity for all other tours
//        for (currentDay in pattern.days) {
//            val output = mutableListOf<UtilityDebug<ActivityType>>()
//
//            // skip day if person is at home
//            if (currentDay.isHomeDay) {
//                stackedOutput.add(output)
//                continue
//            }
//
//            for (currentTour in currentDay.tours) {
//                /*
//                 * ignore tours if main activity purpose is already set
//                 */
//                if (!currentDay.existsActivityTypeforActivity(currentTour.index, 0)) {
//                    // create attribute lookup
//                    val lookup = AttributeLookup(person, currentDay, currentTour)
//
//                    // create step object
//                    val step = DCDefaultModelStep(id, fileBase, lookup, randomgenerator)
//
//                    // disable working activity if person is not allowed to work
//                    if (!person.isAllowedToWork) step.disableAlternative("W")
//
//                    // if number of working days is achieved, disable W as alternative
//                    if (person.getAttributefromMap("anztage_w") <= pattern.countDaysWithSpecificActivity(ActivityType.WORK) &&
//                        currentDay.getTotalNumberOfActivitites(ActivityType.WORK) == 0
//                    ) {
//                        step.disableAlternative("W")
//                    }
//                    // if number of education days is achieved, disable E as alternative
//                    if (person.getAttributefromMap("anztage_e") <= pattern.countDaysWithSpecificActivity(ActivityType.EDUCATION) &&
//                        currentDay.getTotalNumberOfActivitites(ActivityType.EDUCATION) == 0
//                    ) {
//                        step.disableAlternative("E")
//                    }
//                    val rnd = person.personalRNG.randomValue
//                    // make selection
//                    val decision = step.doStep(rnd)
//                    val activityType = getTypeFromChar(step.alternativeChosen[0])
//                    val options = step.activeOptions().map { ActivityType.getTypeFromChar(it[0]) }
//                    output.add(
//                        UtilityDebug(
//                            options = options,
//                            utilities = step.utilities {  ActivityType.getTypeFromChar(it[0]) }.filterKeys { it in options },
//                            probabilities = step.probabilities{ ActivityType.getTypeFromChar(it[0])}.filterKeys { it in options },
//                            randomNumber = rnd,
//                            selection = activityType
//                        )
//                    )
////            print
//                    var activity: HActivity? = null
//
//                    // if activity already exits, set activity type only
//                    if (currentDay.existsActivity(currentTour.index, 0)) {
//                        require(false) {
//                            "I make the bold statement: This code will never be reached"
//                        }
//                        activity = currentTour.getActivity(0)
//                        activity.activityType = activityType
//                    } else {
//                        activity = HActivity(currentTour, 0, activityType)
//                        currentTour.addActivity(activity)
//                    }
//                }
//            }
//            stackedOutput.add(output)
//        }
//        return stackedOutput
//    }
//
//
//}