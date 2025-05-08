package edu.kit.ifv.mobitopp.actitopp

import edu.kit.ifv.mobitopp.actitopp.IO.DebugLoggers
import edu.kit.ifv.mobitopp.actitopp.changes.Category
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType.Companion.getTypeFromChar
import edu.kit.ifv.mobitopp.actitopp.enums.JointStatus
import edu.kit.ifv.mobitopp.actitopp.enums.TripStatus
import edu.kit.ifv.mobitopp.actitopp.steps.step2.GenerateCoordinated
import edu.kit.ifv.mobitopp.actitopp.steps.step2.generateMainActivities
import edu.kit.ifv.mobitopp.actitopp.steps.step1.assignWeekRoutine
import edu.kit.ifv.mobitopp.actitopp.steps.step3.GenerateSideToursPreceeding
import edu.kit.ifv.mobitopp.actitopp.steps.step3.generatePrecedingTours
import edu.kit.ifv.mobitopp.actitopp.steps.step7.FinalizedActivityPattern
import edu.kit.ifv.mobitopp.actitopp.steps.step7.HistogramPerActivity
import kotlin.math.max
import kotlin.math.min

/**
 * @author Tim Hilgert
 *
 *
 * class to coordinate the modeling of week activity schedules
 * will be called from [ActitoppPerson] to generate schedules
 */
class Coordinator @JvmOverloads constructor(
    /** ///////////// */
    val person: ActitoppPerson,
    val fileBase: ModelFileBase,  private val debugloggers: DebugLoggers? = null,
) {

    val randomGenerator = person.personalRNG
    val rngCopy = randomGenerator.copy()
    /**///////////// */ //	declaration of variables
    private val pattern: HWeekPattern = person.weekPattern


    /*
     * - distributions for wrd (weighted random draw) model steps are personalized.
     * - they are dependent from step id, category and activity type
     *
     * They are needed to store modified distributions after modeling decisions (e.g. when people decide to perform
     * a 8-hour working activity, the distribution element (8 hours) will get a bonus that it is more likely to choose
     * 8 hours again the next time for this person. This ensures better stability of duration and starttime modeling.
     */
    private val personalWRDDistributions: HashMap<String, WRDDiscreteDistribution> = HashMap()


    // Important for modeling joint actions
    private val numberofactsperday_lowerboundduetojointactions = intArrayOf(0, 0, 0, 0, 0, 0, 0)
    private val numberoftoursperday_lowerboundduetojointactions = intArrayOf(0, 0, 0, 0, 0, 0, 0)


    private fun log(id: String, element: Any, other: Any) {
        // debugloggers?.let { it.getLogger(id)[element] = other.toString() }
    }

    /**
     * main method to coordinate all model steps
     *
     * @throws InvalidPatternException
     */
    @Throws(InvalidPatternException::class)
    fun executeModel() {
        if (Configuration.modelJointActions) {
            determineMinimumTourActivityBounds()
        }

        val weekRoutine = person.assignWeekRoutine(rngCopy)


        executeStep1("1A", "anztage_w") // Appears to determine amount of days working?
        executeStep1("1B", "anztage_e") // Determines the amount of days with education
        executeStep1("1C", "anztage_l") // The Amount of leisue
        executeStep1("1D", "anztage_s") // The days with shopping
        executeStep1("1E", "anztage_t") // Those with service
        executeStep1("1F", "anztage_immobil") // And those doing nothing.

        executeStep1("1K", "anztourentag_mean")
        executeStep1("1L", "anzakttag_mean")
        require(weekRoutine.similarToAttributeMap(person.attributesMap)) {
            "Mismatch between week routine and person map \n$weekRoutine \n${person.attributesMap}"
        }


        executeStep2("2A")
        val mainActivities = person.generateMainActivities(weekRoutine) {
            GenerateCoordinated(rngCopy)
        }.map { it.first }
        val legacyMainActivities = pattern.days.map { it.getTourOrNull(0)?.getActivity(0)?.activityType ?: ActivityType.HOME }
//        pattern.assignMainActivityCoordinated(PersonWithRoutine(person, weekRoutine), rngCopy)
        executeStep3("3A")

        val precedingTourAmounts = person.generatePrecedingTours(weekRoutine, numberoftoursperday_lowerboundduetojointactions.toList()) {
            GenerateSideToursPreceeding(rngCopy)
        }
        require(precedingTourAmounts.none { it.second.mainTourType == ActivityType.HOME && it.first != 0}) {
            "this should not occur"
        }
        executeStep3("3B")

        executeStep4("4A")

        executeStep5("5A") // Create Activities before main activity (?)
        executeStep5("5B") // Create Activities after  main activity (?)

        executeStep6("6A") // Determine Activity Type for all non main activities (?)

        createTripTimesforActivities()

        // joint activities
        if (Configuration.modelJointActions) {
            placeJointActivitiesIntoPattern()
        }

        val hiPer = HistogramPerActivity()

        val randomNumbers = (0..9).map{randomGenerator.randomValue}
        val output = hiPer.determineTimeBudgets(randomNumbers, FinalizedActivityPattern(person, pattern))

        executeStep7DC("7A", ActivityType.WORK, randomNumbers[0])
        executeStep7WRD("7B", ActivityType.WORK, randomNumbers[1])

        executeStep7DC("7C", ActivityType.EDUCATION, randomNumbers[2])
        executeStep7WRD("7D", ActivityType.EDUCATION, randomNumbers[3])

        executeStep7DC("7E", ActivityType.LEISURE, randomNumbers[4])
        executeStep7WRD("7F", ActivityType.LEISURE, randomNumbers[5])

        executeStep7DC("7G", ActivityType.SHOPPING, randomNumbers[6])
        executeStep7WRD("7H", ActivityType.SHOPPING, randomNumbers[7])

        executeStep7DC("7I", ActivityType.TRANSPORT, randomNumbers[8])
        executeStep7WRD("7J", ActivityType.TRANSPORT, randomNumbers[9])





        executeStep8A("8A")
        executeStep8_MainAct("8B", "8C")
        executeStep8_MainAct("8D", "8E")
        executeStep8_NonMainAct("8J", "8K")

        executeStep9A("9A")

        executeStep10A("10A")

        createTourStartTimesDueToScheduledActivities()

        executeStep10("10M", "10N", 1)
        executeStep10("10O", "10P", 2)

        executeStep10ST()

        if (Configuration.modelJointActions) {
            executeStep11("11")
            // select other persons to join activity or trip
            selectWithWhomforJointActions()
        }


        // finalizing activity schedules

        // 1) create and sort a list including all modeled activities of the whole week
        val allModeledActivities = pattern.allOutofHomeActivities.sortedBy { it.startTimeWeekContext }

        // 2) create home activities to be performed between tours
        createHomeActivities(allModeledActivities)

        // DEBUG
        if (Configuration.debugenabled) {
            pattern.printAllActivitiesList()
        }

        // first sanity checks: check for overlapping activities. if found, throw exception and redo activityweek
        // Robin: TODO i reaaaaly dislike the idea of communicating via errors or exceptions but apparently that is the desired behaviour
        if (!pattern.weekPatternisFreeofOverlaps()) {
            throw AssertionError("Some elements overlap in the pattern")
        }

    }

    /**
     * select the minimum number of tours and activities for each day
     * based on the list of known joint activities, the method decides for a minimum number of tours
     * and activities that are needed to perform these known joint activities
     */
    private fun determineMinimumTourActivityBounds() {
        /*
                 * idea:
                 *
                 * - decide for a minimum number of tours and activities for each day based on the list of known joint activities
                 *
                 * - model number of tours and activities (modeling steps 1-6) using the following the following constraints
                 * 		- there has to be at least one tour if joint activities or trips exists for the day
                 * 		- if there are more than 2 joint actions --> at least two tours
                 *
                 */

        for (act in person.allJointActivitiesforConsideration) {
            // Count number of activities
            numberofactsperday_lowerboundduetojointactions[act.dayIndex] += 1

            // Determine number of tours
            if (numberofactsperday_lowerboundduetojointactions[act.dayIndex] <= 2) {
                numberoftoursperday_lowerboundduetojointactions[act.dayIndex] = 1
            } else {
                numberoftoursperday_lowerboundduetojointactions[act.dayIndex] = 2
            }
        }
    }


    /**
     * @param id
     * @param variablenname
     */
    private fun executeStep1(id: String,
                             variablenname: String) {
        // create attribute lookup
        val lookup = AttributeLookup(person)

        // create step object
        val step = DCDefaultModelStep(id, fileBase, lookup, randomGenerator)
        val rand = randomGenerator.randomValue
        step.doStep(rand)
        // save result
        var decision = step.alternativeChosen.toDouble()
        // set anztage_w to 0 if person is not allowed to work (this may be configured for minors)
        if (variablenname === "anztage_w" && !person.isAllowedToWork) decision = 0.0

        person.addAttributetoMap(variablenname, decision)

        if (debugloggers != null && debugloggers.existsLogger(id)) {
            debugloggers.getLogger(id)[person] = decision.toString()
        }
    }

    /**
     * @param id
     */
    private fun executeStep2(id: String) {
        // STEP 2A Main tour and main activity
        for (currentDay in pattern.days) {
            // execute step if main activity type does not exist
            if (!currentDay.existsActivityTypeforActivity(0, 0)) {
                // create attribute lookup
                val lookup = AttributeLookup(person, currentDay)

                // create step object
                val step = DCDefaultModelStep(id, fileBase, lookup, randomGenerator)

                // if there are existing tours (e.g., from joint activities) , disable H as alternative as being at home is no longer a valid alternative
                if (currentDay.amountOfTours > 0 || numberoftoursperday_lowerboundduetojointactions[currentDay.index] > 0) {
                    require(false) {
                        "can this code even trigger?"
                    }
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
                        && step.alternativeisEnabled("W")) {
                        step.adaptUtilityFactor("W", 1.3)
                    }
                    // utility bonus for alternative E if person is in Education and day is from Monday to Friday
                    if (person.isinEducation() && currentDay.isStandardWorkingDay() && step.alternativeisEnabled("E")) {
                        step.adaptUtilityFactor("E", 1.3)
                    }
                }

                // make selection
                val decision = step.doStep()
                val activityType = getTypeFromChar(step.alternativeChosen[0])



                log(id, currentDay, activityType.toString())

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

    /**
     * @param id
     */
    private fun executeStep3(id: String) {
        for (currentDay in pattern.days) {
            // skip day if person is at home

            // We don't need this check if the days passed to step3 are only those that are not home days. The new system does that
            if (currentDay.isHomeDay) {
                continue
            }

            // create attribute lookup
            val lookup = AttributeLookup(person, currentDay)

            // create step object
            val step = DCDefaultModelStep(id, fileBase, lookup, randomGenerator)

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
            val decision = step.doStep()

            log(id, currentDay, decision.toString())

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

    /**
     * @param id
     */
    private fun executeStep4(id: String) {
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
                    val step = DCDefaultModelStep(id, fileBase, lookup, randomGenerator)

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

                    log(id, currentTour, activityType.toString())

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

    /**
     * @param id
     */
    private fun executeStep5(id: String) {
        for (currentDay in pattern.days) {
            // skip day if person is at home
            if (currentDay.isHomeDay) {
                continue
            }

            for (i in currentDay.lowestTourIndex..currentDay.highestTourIndex) {
                val currentTour = currentDay.getTour(i)
                require(currentTour.amountOfActivities == 1|| id != "5A") {
                    "Fat assumption, all tours have only their main activity right now. (Holds only for step 5A, but not for 5B"
                }
                // create attribute lookup
                val lookup = AttributeLookup(person, currentDay, currentTour)

                // create step object
                val step = DCDefaultModelStep(id, fileBase, lookup, randomGenerator)

                // initialize minimum number of activities
                var minimumnumberofactivities = 0

                // check if minimum number bound is already achieved
                if (currentDay.totalAmountOfActivitites < numberofactsperday_lowerboundduetojointactions[currentDay.index]) {
                    // calculate number of missing activities until bound is achieved
                    val remainingnumberofactivities =
                        numberofactsperday_lowerboundduetojointactions[currentDay.index] - currentDay.totalAmountOfActivitites

                    /*
                     * calculate number of remaining activity executions of step 5
                     *
                     * Equation: remainingnumberoftours * 2 (because 5A and 5B will be executed for each tour) - 1 (if this is step 5B and 5A is done)
                     */
                    val reaminingstepexecutions = 2 * (currentDay.highestTourIndex - i + 1) - (if (id == "5B") 1 else 0)
                    minimumnumberofactivities =
                        Math.round((remainingnumberofactivities / reaminingstepexecutions).toFloat())
                    // if this is the last tour of the day and step 5B, i.e., last executions of step 5 for the day, bound needs to be achieved
                    if (id == "5B" && currentTour.index == currentDay.highestTourIndex) minimumnumberofactivities =
                        remainingnumberofactivities
                }

                // limit alternatives
                step.limitLowerBoundOnly(minimumnumberofactivities)

                // make selection
                val decision = step.doStep()

                log(id, currentTour, decision.toString())

                // create activities based on the decision and add them to the pattern
                for (j in 1..decision) {
                    var act: HActivity? = null
                    // 5A - activity before main activity
                    if (id == "5A" && !currentDay.existsActivity(currentTour.index, -1 * j)) act =
                        HActivity(currentTour, (-1) * j)
                    // 5B - activity after main activity
                    if (id == "5B" && !currentDay.existsActivity(currentTour.index, +1 * j)) act =
                        HActivity(currentTour, (+1) * j)

                    if (act != null) currentTour.addActivity(act)
                }
            }
            if (id == "5B") assert(currentDay.totalAmountOfActivitites >= numberofactsperday_lowerboundduetojointactions[currentDay.index]) { "wrong number of activities - violating lower bound due to joint actions" }
        }
    }

    /**
     * @param id
     */
    private fun executeStep6(id: String) {
        // STEP 6A Non-Main-Activity Type Decision
        for (currentDay in pattern.days) {
            // skip day if person is at home
            if (currentDay.isHomeDay) {
                continue
            }

            for (currentTour in currentDay.tours) {
                for (currentActivity in currentTour.activities) {
                    // only use activities whose type has not been decided yet
                    if (!currentActivity.activityTypeIsSpecified()) {
                        // create attribute lookup
                        val lookup = AttributeLookup(person, currentDay, currentTour, currentActivity)

                        // create step object
                        val step = DCDefaultModelStep(id, fileBase, lookup, randomGenerator)

                        // disable working activity if person is not allowed to work
                        if (!person.isAllowedToWork) step.disableAlternative("W")

                        // if number of working days is achieved, disable W as alternative
                        if (person.getAttributefromMap("anztage_w") <= pattern.countDaysWithSpecificActivity(
                                ActivityType.WORK
                            ) &&
                            currentDay.getTotalNumberOfActivitites(ActivityType.WORK) == 0
                        ) {
                            step.disableAlternative("W")
                        }

                        // if number of education days is achieved, disable E as alternative
                        if (person.getAttributefromMap("anztage_e") <= pattern.countDaysWithSpecificActivity(
                                ActivityType.EDUCATION
                            ) &&
                            currentDay.getTotalNumberOfActivitites(ActivityType.EDUCATION) == 0
                        ) {
                            step.disableAlternative("E")
                        }

                        //make selection
                        val decision = step.doStep()

                        // set activity type
                        val activityType = getTypeFromChar(step.alternativeChosen[0])
                        currentActivity.activityType = activityType

                        log(id, currentActivity, activityType.toString())
                    }
                }
            }
        }
    }

    /**
     * determination of default trip times for all activities
     */
    private fun createTripTimesforActivities() {
        for (day in pattern.days) {
            for (tour in day.tours) {
                for (act in tour.activities) {
                    act.createTripsforActivity()
                }
            }
        }
    }


    /**
     * Placing of joint activities (and trips) created by another household member into an existing pattern.
     * Replaces an existing activity of the pattern with this joint activity
     *
     *
     * rule-based:
     * - if tour index of the originating activity exist, place it into this tour if possible
     * - if activity index of the originating activity exists, replace this activity if possible
     *
     *
     * - if tour or activity in this tour does not exist, use the nearest exiting activity that is no joint activity
     * example: originating activity is 1/1/3 (day/tour/act). Highest index is 1/1/2, so use this for replacement
     *
     *
     * - consider time overlaps. if list of joint activities is processed one by one, the last processed activity defines
     * lower temporal bound for the new one.
     *
     *
     * - joint activities of type 1 and type 3 starting at home are only possible as first activity in a tour
     *
     * @throws InvalidPatternException
     */
    @Throws(InvalidPatternException::class)
    private fun placeJointActivitiesIntoPattern() {
        val listjointact = person.allJointActivitiesforConsideration.sortedBy { it.startTimeWeekContext }

        /*
         * loop the list in week order and look for an existing activity to be replaced by activity actually processed
         */
        for (indexinliste in listjointact.indices) {
            val jointact = listjointact[indexinliste]

            val jointact_dayindey = jointact.dayIndex
            val jointact_tourindex = jointact.tour.index
            val jointact_actindex = jointact.index
            val jointact_jointStatus = jointact.jointStatus

            assert(JointStatus.JOINTELEMENTS.contains(jointact_jointStatus)) { "keine gemeinsame Aktivitaet in der Liste der gemeinsamen Aktivitaeten!" }


            /*
             * possible activities for replacement
             */
            var possibleact: MutableList<HActivity> = ArrayList()

            /*
             *  Step 1: All available activities of the day
             */
            run {
                for (act in pattern.getDay(jointact_dayindey).allActivitiesoftheDay) {
                    possibleact.add(act)
                }
                //HActivity.sortActivityListbyIndices(possibleact) TODO probably this sort can be killed, because the pattern should also return a sorted list, also the placeJointActivities is never triggered ???
            }

            /*
             *  Step 2: Check if there are already joint activities on that day. If yes new joint activities need to be AFTER the last joint activity
             */
            run {
                var lastactreplaced: HActivity? = null
                for (act in possibleact) {
                    if ((if (act.getAttributefromMap("actreplacedbyjointact") != null) act.getAttributefromMap("actreplacedbyjointact") else 0.0) == 1.0) lastactreplaced =
                        act
                }
                if (lastactreplaced != null) {
                    val possibleactlaterinweek: MutableList<HActivity> = ArrayList()
                    for (act in possibleact) {
                        if (act.compareTo(lastactreplaced) < 0) possibleactlaterinweek.add(act)
                    }
                    possibleact = possibleactlaterinweek

                    /*
                     * - if the last joint act is not directly before the new activity, replace the first possible act for replacement to avoid temporal gaps
                     * - remove the first possible activity if this will cause time overlaps
                     */
                    if ((lastactreplaced.jointStatus != JointStatus.JOINTTRIP && HActivity.getTimebetweenTwoActivities(
                            lastactreplaced,
                            jointact
                        ) != 0 && !lastactreplaced.isActivityLastinTour)
                        ||
                        (lastactreplaced.jointStatus != JointStatus.JOINTTRIP &&
                                HActivity.getTimebetweenTwoActivities(lastactreplaced, jointact) < 0)
                    ) possibleact.removeAt(0)
                }
            }

            /*
             * step 3: 	check for further joint activities on that day and remove the last X activities of that day for replacement
             */
            run {
                var furtherjointactonday = 0
                for (i in indexinliste + 1..<listjointact.size) {
                    val act = listjointact[i]
                    if (act.dayIndex == jointact_dayindey) furtherjointactonday += 1
                }
                if (furtherjointactonday > 0) {
                    for (i in 1..furtherjointactonday) {
                        val letzterindex = possibleact.size - 1
                        possibleact.removeAt(letzterindex)
                    }
                }
            }


            /*
             * step 4: check if list is empty (because of rules in step 2 and 3)
             */
            if (possibleact.size == 0) {
                if (Configuration.debugenabled) System.err.println("could not replace activity! step 4")
                jointact.removeJointParticipant(person)
                break
            }


            /*
             * step 5: 	if joint type is 1 or 3, i.e., there is a joint trip included, the activity needs to be the first in the tour if the originating
             * 					one is the first on in the tour too (of the household member created the activity)
             *
             * 					for such cases, only use the remaining activities that are the first one in their tour
             */
            run {
                if ((jointact_jointStatus == JointStatus.JOINTTRIPANDACTIVITY || jointact_jointStatus == JointStatus.JOINTTRIP) && jointact.isActivityFirstinTour) {
                    val possibleactersteaktintour: MutableList<HActivity> = ArrayList()
                    for (act in possibleact) {
                        if (act.isActivityFirstinTour) possibleactersteaktintour.add(act)
                    }
                    possibleact = possibleactersteaktintour
                }
            }


            /*
             * step 6: check if list is empty (because of rules in step 5)
             */
            if (possibleact.size == 0) {
                if (Configuration.debugenabled) System.err.println("could not replace activity! step 6")
                jointact.removeJointParticipant(person)
                break
            }


            /*
             * step 7:	check if tourindex of the originating activity still exists for replacement (first priority for replacement)
             * 					if yes check the same for the activityindex
             */
            run {
                // add all activities with the same tour index into an own list
                val possibleactsametourindex: MutableList<HActivity> = ArrayList()
                for (act in possibleact) {
                    if (act.tour.index == jointact_tourindex) {
                        possibleactsametourindex.add(act)
                    }
                }
                // use this list for further processing if not empty
                if (possibleactsametourindex.size != 0) {
                    possibleact = possibleactsametourindex

                    // add all activities with the same act index into an own list
                    val possibleactsameactindex: MutableList<HActivity> = ArrayList()
                    for (act in possibleact) {
                        if (act.index == jointact_actindex) {
                            possibleactsameactindex.add(act)
                        }
                    }
                    // use this list for further processing if not empty
                    if (possibleactsameactindex.size != 0) {
                        possibleact = possibleactsameactindex
                    }
                }
            }

            /*
             * step 8: if an activity is the last one of a tour and is followed directly by a joint activity, remove this activity from list as there is
             * 				 no time left for the home activity
             */
            run {
                if (indexinliste < listjointact.size - 1 && HActivity.getTimebetweenTwoActivities(
                        jointact,
                        listjointact[indexinliste + 1]
                    ) == 0
                ) {
                    val possibleactnichtletzte: MutableList<HActivity> = ArrayList()
                    for (act in possibleact) {
                        if (!act.isActivityLastinTour) {
                            possibleactnichtletzte.add(act)
                        }
                    }
                    possibleact = possibleactnichtletzte
                }
            }

            /*
             * step 9: check if list is empty (because of rules in step 8)
             */
            if (possibleact.size == 0) {
                if (Configuration.debugenabled) System.err.println("could not replace activity! step 9")
                jointact.removeJointParticipant(person)
                break
            }

            /*
             * step 10: choose randomly on of the remaining activities
             */
            val rnd = randomGenerator.getRandomValueBetween(0, possibleact.size - 1)
            val actforreplacement = possibleact[rnd]

            /*
             * step 11: replace activity
             */
            run {
                // get activity properties
                val gemakt_duration = jointact.duration
                val gemakt_starttime = jointact.startTime
                val gemakt_acttype = jointact.activityType
                val gemakt_creatorPersonIndex = jointact.creatorPersonIndex

                val gemakt_durationtripbefore = jointact.estimatedTripTimeBeforeActivity

                actforreplacement.addAttributetoMap("actreplacedbyjointact", 1.0)

                // replace different properties depending on type of joint action
                when (jointact_jointStatus) {
                    JointStatus.JOINTTRIPANDACTIVITY -> {
                        // replace properties
                        actforreplacement.duration = gemakt_duration
                        actforreplacement.startTime = gemakt_starttime
                        actforreplacement.activityType = gemakt_acttype
                        actforreplacement.jointStatus = jointact_jointStatus
                        actforreplacement.creatorPersonIndex = gemakt_creatorPersonIndex

                        // recalculate trip times
                        actforreplacement.createTripsforActivity()

                        // generate trip to the activity
                        actforreplacement.tripbeforeactivity =
                            HTrip(actforreplacement, TripStatus.TRIP_BEFORE_ACT, gemakt_durationtripbefore)
                    }

                    JointStatus.JOINTACTIVITY -> {
                        // replace properties
                        actforreplacement.duration = gemakt_duration
                        actforreplacement.startTime = gemakt_starttime
                        actforreplacement.activityType = gemakt_acttype
                        actforreplacement.jointStatus = jointact_jointStatus
                        actforreplacement.creatorPersonIndex = gemakt_creatorPersonIndex

                        // recalculate trip times
                        actforreplacement.createTripsforActivity()
                    }

                    JointStatus.JOINTTRIP -> {
                        // replace properties
                        actforreplacement.jointStatus = jointact_jointStatus
                        actforreplacement.creatorPersonIndex = gemakt_creatorPersonIndex

                        // generate trip
                        actforreplacement.tripbeforeactivity =
                            HTrip(actforreplacement, TripStatus.TRIP_BEFORE_ACT, gemakt_durationtripbefore)
                        actforreplacement.startTime = gemakt_starttime
                    }

                    else -> throw RuntimeException()
                }
            }

            // step 12: check again for temporal overlaps
            for (act in pattern.getDay(jointact_dayindey).allActivitiesoftheDay) {
                if ((act.startTimeisScheduled()
                            && act.overlaps(actforreplacement))
                    ||
                    (act.hasScheduledDuration && act.isActivityLastinTour && actforreplacement.isActivityFirstinTour && act.tourIndex != actforreplacement.tourIndex && HActivity.getTimebetweenTwoActivities(
                        act,
                        actforreplacement
                    ) == 0)
                    ||
                    (act.hasScheduledDuration && act.isActivityFirstinTour && actforreplacement.isActivityLastinTour && act.tourIndex != actforreplacement.tourIndex && HActivity.getTimebetweenTwoActivities(
                        act,
                        actforreplacement
                    ) == 0)
                ) {
                    val errormsg = "Activity overlapping when adding joint activity"
                    throw InvalidPatternException("Household", pattern, errormsg)
                }
            }
        }


        //TODO  ensure order of the activities by index is identical with order by start time
    }


    /**
     * @param id
     * @param variablenname
     */
    private fun executeStep7DC(id: String, activitytype: ActivityType, randomNumber: Double? = null) {
        if (pattern.countActivitiesPerWeek(activitytype) > 0) {
            // create attribute lookup
            val lookup = AttributeLookup(person)

            // create step object
            val step = DCDefaultModelStep(id, fileBase, lookup, randomGenerator)
            val decisionIndex = randomNumber?.let{step.doStep(it) } ?: step.doStep()
            val decision = step.alternativeChosen.toInt()

            log(id, person, decision.toString())
            /* This is insanity, the budget_category_index only exists to map to the file, because someone has managed
               to mismatch the indices of the files 0..n-1 and categories in the model file 1..n Given that this entire
               project is based on convoluted indices bullshittery (which in itself is criminally bad) it is even worse
               to see that whoever did this managed to fuck up their own index magic and required a software crutch.

               Failing at failing is not a double negative.
             */
            person.addAttributetoMap(activitytype.toString() + "budget_category_index", decisionIndex.toDouble())
            person.addAttributetoMap(
                activitytype.toString() + "budget_category_alternative",
                step.alternativeChosen.toDouble()
            )
        }

        // special case: if there is exactly no activity allocated for work, than we must set cat to 0
        // needed to achieve value for Attribute zeitbudget_work_ueber_kat2

        // TODO figure out why this line of code could cause issues when missing (Assumption: The entry won't be in the map)
        if (activitytype == ActivityType.WORK && pattern.countActivitiesPerWeek(activitytype) == 0) {
            person.addAttributetoMap(activitytype.toString() + "budget_category_alternative", 0.0)
        }
    }

    /**
     * @param id
     * @param activitytype
     */
    private fun executeStep7WRD(id: String, activitytype: ActivityType, randomNumber: Double? = null) {
        if (pattern.countActivitiesPerWeek(activitytype) > 0) {
            // get decision from step 7 DC
            val chosenIndex = person.getAttributefromMap(activitytype.toString() + "budget_category_index")
            //TODO why is get Attribute returning a double, which is then cast to an int. Skip the intermediate step
            val step = WRDDefaultModelStep(id, Category(chosenIndex.toInt()), activitytype, this)
            val chosenTime = randomNumber?.let{step.doStep(it)}?: step.doStep()

            log(id, person, chosenTime.toString())

            person.addAttributetoMap(activitytype.toString() + "budget_exact", chosenTime.toDouble())
        }
    }

    /**
     * @param id
     */
    private fun executeStep8A(id: String) {
        // STEP8a: yes/no decision for "activity is in average time class xyz".
        // only applies to main activities
        for (currentDay in pattern.days) {
            // skip day if person is at home
            if (currentDay.isHomeDay) {
                continue
            }

            for (currentTour in currentDay.tours) {
                val currentActivity = currentTour.getActivity(0)

                if (!currentActivity.durationisScheduled()) {
                    // create attribute lookup
                    val lookup = AttributeLookup(person, currentDay, currentTour, currentActivity)

                    // create step object
                    val step = DCDefaultModelStep(id, fileBase, lookup, randomGenerator)
                    val decision = step.doStep()

                    log(id, currentActivity, step.alternativeChosen.toString())

                    // save attribute for work and education activities if coordinated modeling is enabled
                    if (Configuration.coordinated_modelling && (currentActivity.activityType == ActivityType.WORK || currentActivity.activityType == ActivityType.EDUCATION)) {
                        currentActivity.addAttributetoMap(
                            "standarddauer",
                            (if (step.alternativeChosen == "yes") 1.0 else 0.0)
                        )
                    } else {
                        currentActivity.addAttributetoMap("standarddauer", 0.0)
                    }
                }
            }
        }
    }


    /**
     * @param id_dc
     * @param id_wrd
     * @throws InvalidPatternException
     */
    @Throws(InvalidPatternException::class)
    private fun executeStep8_MainAct(id_dc: String, id_wrd: String) {
        for (currentDay in pattern.days) {
            // skip day if person is at home
            if (currentDay.isHomeDay) {
                continue
            }

            for (currentTour in currentDay.tours) {
                var running = false
                if (id_dc == "8B" && currentTour.isFirstTouroftheDay) running = true // 8B for first tour of the day

                if (id_dc == "8D" && !currentTour.isFirstTouroftheDay) running = true // 8D for all other tours


                if (running) {
                    val currentActivity = currentTour.getActivity(0)

                    /*
                     *
                     * DC-step (8B, 8D)
                     *
                     */
                    if (!currentActivity.durationisScheduled()) {
                        // create attribute lookup
                        val lookup = AttributeLookup(person, currentDay, currentTour, currentActivity)

                        // create step object
                        val step_dc = DCDefaultModelStep(
                            id_dc, fileBase, lookup,
                            randomGenerator
                        )

                        // limit alternatives if needed
                        if (currentActivity.attributesMap["standarddauer"] == 1.0) {
                            val timeCategory = currentActivity.calculateMeanTimeCategory()

                            log("meantime", currentActivity, timeCategory.toString())

                            // lower bound minimum is 0
                            val from = max((timeCategory - 1).toDouble(), 0.0).toInt()
                            // upper bound maximum is last time category
                            val to = min(
                                (timeCategory + 1).toDouble(),
                                (Configuration.NUMBER_OF_ACT_DURATION_CLASSES - 1).toDouble()
                            ).toInt()

                            step_dc.limitUpperandLowerBound(from, to)
                            // add utility bonus of 10% to average time class (middle of the 3 selected)
                            step_dc.adaptUtilityFactor(timeCategory, 1.1)
                        }

                        // set durations bound because of other determined activities
                        val durationBounds = calculateDurationBoundsDueToOtherActivities(currentActivity)
                        val loc_lowerbound = getDurationTimeClassforExactDuration(durationBounds[0])
                        val loc_upperbound = getDurationTimeClassforExactDuration(durationBounds[1])

                        assert(loc_lowerbound <= loc_upperbound)

                        // if bounds are identical, duration is set
                        if (loc_lowerbound == loc_upperbound) {
                            step_dc.limitUpperandLowerBound(loc_lowerbound, loc_upperbound)
                        } else {
                            // limit upper bound if not yet set or below old upper bound
                            if (loc_upperbound <= step_dc.upperBound || step_dc.upperBound == -1) step_dc.limitUpperBoundOnly(
                                loc_upperbound
                            )

                            // limit lower bound if higher than old lower bound
                            if (loc_lowerbound >= step_dc.lowerBound) step_dc.limitLowerBoundOnly(loc_lowerbound)

                            // limit lower bound if bound is now higher than upper bound
                            if (step_dc.lowerBound >= step_dc.upperBound) step_dc.limitLowerBoundOnly(step_dc.upperBound)
                        }

                        assert(step_dc.lowerBound <= step_dc.upperBound)

                        // make selection
                        val decision = step_dc.doStep()

                        log(id_dc, currentActivity, decision.toString())

                        currentActivity.addAttributetoMap("actdurcat_index", decision.toDouble())

                        /*
                         *
                         * WRD-step (8C, 8E)
                         *
                         */
                        // initialize object based on chosen time category
                        val chosenTimeCategory = currentActivity.attributesMap["actdurcat_index"]
                        val step_wrd = WRDDefaultModelStep(
                            id_wrd, Category(chosenTimeCategory.toInt()), currentActivity.activityType,
                            this
                        )

                        step_wrd.setRangeBounds(durationBounds[0], durationBounds[1])

                        if (currentActivity.attributesMap["standarddauer"] == 1.0) step_wrd.setModifydistribution(true)

                        // make selection
                        val chosenTime = step_wrd.doStep()

                        log(id_wrd, currentActivity, chosenTime.toString())

                        currentActivity.duration = chosenTime

                        HActivity.createPossibleStarttimes(currentTour.activities)
                    }
                }
            }
        }
    }


    /**
     * @param id_dc
     * @param id_wrd
     * @throws InvalidPatternException
     */
    @Throws(InvalidPatternException::class)
    private fun executeStep8_NonMainAct(id_dc: String, id_wrd: String) {
        for (currentDay in pattern.days) {
            // skip day if person is at home
            if (currentDay.isHomeDay) {
                continue
            }

            for (currentTour in currentDay.tours) {
                for (currentActivity in currentTour.activities) {
                    /*
                     *
                     * DC-step
                     *
                     */

                    if (currentActivity.index != 0 && !currentActivity.durationisScheduled()) {
                        // create attribute lookup
                        val lookup = AttributeLookup(person, currentDay, currentTour, currentActivity)

                        // create step object
                        val step_dc = DCDefaultModelStep(
                            id_dc, fileBase, lookup,
                            randomGenerator
                        )

                        // limit bounds because of determined durations
                        val durationBounds = calculateDurationBoundsDueToOtherActivities(currentActivity)
                        val loc_lowerbound = getDurationTimeClassforExactDuration(durationBounds[0])
                        val loc_upperbound = getDurationTimeClassforExactDuration(durationBounds[1])

                        assert(loc_lowerbound <= loc_upperbound)

                        // duration is set if bounds are identical
                        if (loc_lowerbound == loc_upperbound) {
                            step_dc.limitUpperandLowerBound(loc_lowerbound, loc_upperbound)
                        } else {
                            // limit upper bound if not yet set or below old upper bound
                            if (loc_upperbound <= step_dc.upperBound || step_dc.upperBound == -1) step_dc.limitUpperBoundOnly(
                                loc_upperbound
                            )

                            // limit lower bound if higher than old lower bound
                            if (loc_lowerbound >= step_dc.lowerBound) step_dc.limitLowerBoundOnly(loc_lowerbound)

                            // limit lower bound if bound is now higher than upper bound
                            if (step_dc.lowerBound >= step_dc.upperBound) step_dc.limitLowerBoundOnly(step_dc.upperBound)
                        }

                        assert(step_dc.lowerBound <= step_dc.upperBound)

                        // make selection
                        val decision = step_dc.doStep()

                        log(id_dc, currentActivity, decision.toString())

                        currentActivity.addAttributetoMap("actdurcat_index", decision.toDouble())

                        /*
                         *
                         * WRD-step
                         *
                         */
                        // initialize object based on chosen time category
                        val chosenTimeCategory = currentActivity.attributesMap["actdurcat_index"]!!
                        val step_wrd = WRDDefaultModelStep(
                            id_wrd, Category(chosenTimeCategory.toInt()), currentActivity.activityType,
                            this
                        )

                        step_wrd.setRangeBounds(durationBounds[0], durationBounds[1])

                        // make selection
                        val chosenTime = step_wrd.doStep()

                        log(id_wrd, currentActivity, chosenTime.toString())

                        currentActivity.duration = chosenTime

                        HActivity.createPossibleStarttimes(currentTour.activities)
                    }
                }
            }
        }
    }


    /**
     * @param id
     */
    private fun executeStep9A(id: String) {
        // Step 9A: standard start time category for fist tours during the week

        if (person.isPersonWorkorSchoolCommuterAndMainToursAreScheduled) {
            // create attribute lookup
            val lookup = AttributeLookup(person)

            // create step object
            val step = DCDefaultModelStep(id, fileBase, lookup, randomGenerator)
            val decision = step.doStep()

            log(id, person, decision.toString())

            person.addAttributetoMap("first_tour_default_start_cat", decision.toDouble())
        }
    }


    /**
     * @param id
     */
    private fun executeStep10A(id: String) {
        // Step 10a: check if first tour is work/edu lies within standard start time (applies only to work/edu persons)
        if (person.isPersonWorkorSchoolCommuterAndMainToursAreScheduled) {
            for (currentDay in pattern.days) {
                if (currentDay.isHomeDay) {
                    continue
                }
                val currentTour = currentDay.firstTourOfDay
                val tourtype = currentTour.getActivity(0).activityType
                if (tourtype == ActivityType.WORK || tourtype == ActivityType.EDUCATION) {
                    // create attribute lookup
                    val lookup = AttributeLookup(person, currentDay, currentTour)

                    // create step object
                    val step = DCDefaultModelStep(id, fileBase, lookup, randomGenerator)
                    val decision = step.doStep()

                    log(id, currentTour, step.alternativeChosen.toString())

                    currentTour.addAttributetoMap(
                        "default_start_cat_yes",
                        (if (step.alternativeChosen == "yes") 1.0 else 0.0)
                    )
                }
            }
        }
    }


    /**
     * determine tour start times for tours where start time is known because of joint activities originating from other household members
     *
     * @throws InvalidPatternException
     */
    @Throws(InvalidPatternException::class)
    private fun createTourStartTimesDueToScheduledActivities() {
        for (currentDay in pattern.days) {
            if (currentDay.isHomeDay) {
                continue
            }

            for (currentTour in currentDay.tours) {
                if (!currentTour.isScheduled) {
                    var startTimeDueToScheduledActivities = 99999

                    var tripdurations = 0
                    var activitydurations = 0

                    for (tmpact in currentTour.activities) {
                        /*
                         * if start time of an activity is determined, set this as fixed element and subtract all activity and trip durations until then
                         */
                        if (tmpact.startTimeisScheduled()) {
                            startTimeDueToScheduledActivities =
                                tmpact.tripStartTimeBeforeActivity - tripdurations - activitydurations
                            break
                        } else {
                            tripdurations += tmpact.estimatedTripTimeBeforeActivity
                            activitydurations += tmpact.duration
                        }
                    }

                    /*
                     * there may be negative tour start times because of other determined activities in rare cases
                     * example: the activity is generated by another person and close to midnight. if the actual person has another commuting duration
                     * the start time (leaving the house) for this tour may be negative!
                     */
                    if (startTimeDueToScheduledActivities < 0) {
                        throw InvalidPatternException(
                            "Person", pattern,
                            "TourStartTimes <0 $currentTour"
                        )
                    }

                    if (startTimeDueToScheduledActivities != 99999) {
                        currentTour.startTime = startTimeDueToScheduledActivities
                        currentTour.createStartTimesforActivities()
                    }
                }
            }
        }
    }


    /**
     * @param id_dc
     * @param id_wrd
     * @param tournrdestages
     * @throws InvalidPatternException
     */
    @Throws(InvalidPatternException::class)
    private fun executeStep10(id_dc: String, id_wrd: String, tournrdestages: Int) {
        // STEP 10: determine time class for the start of the x tour of the day

        for (currentDay in pattern.days) {
            if (currentDay.isHomeDay || currentDay.amountOfTours < tournrdestages) {
                continue
            }

            val currentTour = currentDay.getTour(currentDay.lowestTourIndex + (tournrdestages - 1))

            if (!currentTour.isScheduled) {
                /*
                                *
                                * DC-step
                                *
                                */

                // create attribute lookup

                val lookup = AttributeLookup(person, currentDay, currentTour)

                // create step object
                val step_dc = DCDefaultModelStep(id_dc, fileBase, lookup, randomGenerator)

                // limit alternatives
                val bounds_dc = calculateStartingBoundsForTours(currentTour, true)
                val lowerbound = bounds_dc[0]
                val upperbound = bounds_dc[1]
                step_dc.limitUpperandLowerBound(lowerbound, upperbound)

                if (Configuration.coordinated_modelling) {
                    if (currentTour.existsAttributeinMap("default_start_cat_yes") && currentTour.getAttributefromMap("default_start_cat_yes") == 1.0) {
                        val defaultcat = person.getAttributefromMap("first_tour_default_start_cat").toInt()
                        if (defaultcat >= lowerbound && defaultcat <= upperbound) step_dc.limitUpperandLowerBound(
                            defaultcat,
                            defaultcat
                        )
                    }
                }

                // make selection
                val decision = step_dc.doStep()

                log(id_dc, currentTour, decision.toString())

                currentTour.addAttributetoMap("tourStartCat_index", decision.toDouble())


                /*
                 *
                 * WRD-step
                 *
                 */
                val chosenStartCategory = currentTour.attributesMap["tourStartCat_index"] as Double
                val step_wrd = WRDDefaultModelStep(
                    id_wrd, Category(chosenStartCategory.toInt()), currentTour.getActivity(0).activityType,
                    this
                )

                val bounds_mc = calculateStartingBoundsForTours(currentTour, false)
                step_wrd.setRangeBounds(bounds_mc[0], bounds_mc[1])

                // make selection
                val chosenStartTime = step_wrd.doStep()

                log(id_wrd, currentTour, chosenStartTime.toString())

                currentTour.startTime = chosenStartTime
                currentTour.createStartTimesforActivities()

                //TODO previousTour needs to be scheduled if modeling is chronological

                // ensure that there are no temporal overlaps
                val previousTour = currentTour.previousTourinPattern
                if (previousTour != null && previousTour.isScheduled) assert(currentTour.startTimeWeekContext > previousTour.endTimeWeekContext) { "Tours are overlapping!" }
            }
        }
    }


    /**
     * @throws InvalidPersonPatternException
     */
    @Throws(InvalidPatternException::class)
    private fun executeStep10ST() {
        // Step 10s and Step10t: determine home time before tour starts and then define tour start time
        //											 only for the fourth tour if the day and following

        for (currentDay in pattern.days) {
            if (currentDay.isHomeDay) {
                continue
            }
            for (j in currentDay.lowestTourIndex..currentDay.highestTourIndex) {
                val currentTour = currentDay.getTour(j)
                // determine home time for all non-scheduled tours
                if (!currentTour.isScheduled) {
                    // 10S

                    // create attribute lookup

                    val lookup = AttributeLookup(person, currentDay, currentTour)

                    // create step object
                    val dcstep = DCDefaultModelStep("10S", fileBase, lookup, randomGenerator)

                    // limit alternatives
                    val dcbounds = calculateBoundsForHomeTime(currentTour, true)
                    val lowerbound = dcbounds[0]
                    val upperbound = dcbounds[1]
                    dcstep.limitUpperandLowerBound(lowerbound, upperbound)

                    // make selection
                    val decision = dcstep.doStep()

                    log("10S", currentTour, decision.toString())

                    val chosenHomeTimeCategory = decision

                    // 10T
                    val step_wrd = WRDDefaultModelStep(
                        "10T", Category(chosenHomeTimeCategory), currentTour.getActivity(0).activityType,
                        this
                    )
                    val wrdbounds = calculateBoundsForHomeTime(currentTour, false)
                    step_wrd.setRangeBounds(wrdbounds[0], wrdbounds[1])

                    // make selection
                    val chosenTime = step_wrd.doStep()


                    log("10T", currentTour, chosenTime.toString())

                    val starttimetour = currentDay.getTour(currentTour.index - 1).endTime + chosenTime
                    currentTour.startTime = starttimetour
                    currentTour.createStartTimesforActivities()
                }
            }
        }
    }


    /**
     * @param id
     */
    private fun executeStep11(id: String) {
        // STEP 11 - Decision on joint activities

        for (currentDay in pattern.days) {
            // skip day if person is at home
            if (currentDay.isHomeDay) {
                continue
            }

            for (currentTour in currentDay.tours) {
                for (currentActivity in currentTour.activities) {
                    /*
                     * skip the activity if activity was generated by another household member
                     */
                    if (currentActivity.creatorPersonIndex != person.persIndex) {
                        continue
                    }

                    /*
                     * if person is the last one modeled in the household, no other members are available to join activities. Thus, skip decision then.
                     */
                    if (person.getAttributefromMap("numbermodeledinhh")
                            .toInt() != person.household.numberofPersonsinHousehold
                    ) {
                        // create attribute lookup
                        val lookup = AttributeLookup(person, currentDay, currentTour, currentActivity)

                        // create step object
                        val step = DCDefaultModelStep(id, fileBase, lookup, randomGenerator)
                        val decision = step.doStep()

                        log(id, currentActivity, step.alternativeChosen.toString())

                        currentActivity.jointStatus = JointStatus.getTypeFromInt(step.alternativeChosen.toInt())
                    } else {
                        currentActivity.jointStatus = JointStatus.NOJOINTELEMENT
                    }
                }
            }
        }
    }


    /**
     * determines lower and upper bound for activity durations due to other planned activities
     *
     * @param act
     * @return [0] = lower bound [1] = upper bound
     * @throws InvalidPatternException
     */
    @Throws(InvalidPatternException::class)
    private fun calculateDurationBoundsDueToOtherActivities(act: HActivity): IntArray {
        val dayofact = act.day


        /*
         * main idea of lower bound calculation
         *
         * 1. starting point (descending priority)
         * - There is another activity earlier this day having a determined starting time
         * - The last activity of the previous day ends after midnight
         * - begin of the day (1 minute past midnight to allow buffer for home time)
         *
         * 2. Calculate all activity durations between actual activity and starting point
         * 3. Calculate all trip durations between actual activity and starting point
         * 4. Calculate home time buffers for all tours between between actual activity and starting point
         *
         *
         * main idea of upper bound calculation
         *
         * 1. starting point (descending priority)
         * - There is another activity later this day having a determined starting time
         * - end of the day
         *
         * step 2-4 identical
         */


        /*
         * 1.
         *
         * starting points
         *
         */
        var last_act_scheduled: HActivity? = null
        var next_act_scheduled: HActivity? = null

        for (tmpact in dayofact.allActivitiesoftheDay) {
            // Search for earlier activity with determined starting time

            if (act < tmpact) {
                //System.out.println(tmpact.getTour().getIndex() + "/" + tmpact.getIndex());
                if (tmpact.startTimeisScheduled() && (last_act_scheduled == null || tmpact.startTime > last_act_scheduled.startTime)) last_act_scheduled =
                    tmpact
            }

            // Search for later activity with determined starting time
            if (act > tmpact) {
                //System.out.println(tmpact.getTour().getIndex() + "/" + tmpact.getIndex());
                if (tmpact.startTimeisScheduled() && (next_act_scheduled == null || tmpact.startTime < next_act_scheduled.startTime)) next_act_scheduled =
                    tmpact
            }
        }

        /*
         * starting point for lower bound
         */
        var startingpointlowerbound = 1
        if (last_act_scheduled != null) {
            startingpointlowerbound =
                last_act_scheduled.startTime + (if (last_act_scheduled.durationisScheduled()) last_act_scheduled.duration else last_act_scheduled.defaultActivityTime)
        } else {
            // check if last activity of the previous day ends after midnight
            val previousDay = dayofact.previousDay
            if (previousDay != null && !previousDay.isHomeDay) {
                val lastactpreviousday = previousDay.lastTourOfDay.lastActivityInTour
                if (lastactpreviousday.startTimeisScheduled()) {
                    val endlastactpreviousday = lastactpreviousday.startTime +
                            (if (lastactpreviousday.durationisScheduled()) lastactpreviousday.duration else 0) +
                            (if (lastactpreviousday.tripAfterActivityisScheduled()) lastactpreviousday.estimatedTripTimeAfterActivity else 0)
                    if (endlastactpreviousday > 1440) {
                        // +1 to allow at least one minute home time
                        startingpointlowerbound = endlastactpreviousday - 1440 + 1
                    }
                }
            }
        }

        /*
         * starting point for upper bound
         */
        var startingpointupperbound = 0
        if (next_act_scheduled != null) {
            startingpointupperbound = next_act_scheduled.startTime
        } else {
            //TODO WHY IS THIS 1620 ?
//            startingpointupperbound = 1620
            startingpointupperbound = 1439
            val nextday = dayofact.nextDay
            if (nextday != null && !nextday.isHomeDay) {
                val firstactnextday = nextday.firstTourOfDay.firstActivityInTour
                if (firstactnextday.startTimeisScheduled()) {
                    val startingtimefirstactnextday = firstactnextday.tripStartTimeBeforeActivity
                    if (startingtimefirstactnextday < 180) {
                        // -1  to allow at least one minute home time
                        startingpointupperbound = 1440 + startingtimefirstactnextday - 1
                    }
                }
            }
        }


        /*
         * 2.
         *
         * calculate activity durations
         *
         */
        val activitydurationsincelastscheduled =
            countActivityDurationsbetweenActivitiesofOneDay(last_act_scheduled, act)
        val activitydurationuntilnextscheduled =
            countActivityDurationsbetweenActivitiesofOneDay(act, next_act_scheduled)

        /*
         * 3.
         *
         * calculate trip durations
         *
         */
        val tripdurationssincelastscheduled = countTripDurationsbetweenActivitiesofOneDay(last_act_scheduled, act)
        val tripdurationsuntilnextscheduled = countTripDurationsbetweenActivitiesofOneDay(act, next_act_scheduled)

        /*
         * 4.
         *
         * calculate home time buffers (1 minute for each tour)
         *
         */

        /*
         * Before
         */
        var timeforhomeactsincelastscheduled = 0
        timeforhomeactsincelastscheduled += if (last_act_scheduled == null) {
            (act.tour.index - act.day.lowestTourIndex)
        } else {
            (act.tour.index - last_act_scheduled.tour.index)
        }

        /*
         * Afters
         */
        var timeforhomeactuntilnextscheduled = 0
        timeforhomeactuntilnextscheduled += if (next_act_scheduled == null) {
            (act.day.highestTourIndex - act.tour.index)
        } else {
            (next_act_scheduled.tour.index - act.tour.index)
        }

        /*
         * 5.
         *
         * calculate bound and maximum durations
         *
         */
        var lowerbound =
            startingpointlowerbound + activitydurationsincelastscheduled + tripdurationssincelastscheduled + timeforhomeactsincelastscheduled
        val upperbound =
            startingpointupperbound - activitydurationuntilnextscheduled - tripdurationsuntilnextscheduled - timeforhomeactuntilnextscheduled

        /*
         * if activity already has a determined starting time, this is the lower bound
         */
        if (act.startTimeisScheduled()) lowerbound = act.startTime

        var maxduration = upperbound - lowerbound
        var minduration = 1

        // set maximum to one day if upper bound exceeds one day
        maxduration = min(maxduration.toDouble(), 1440.0).toInt()

        // error handling if upper bound <= lower bound
        if (upperbound <= lowerbound) {
            // household exception as conflict results from joint act of other household members
            if (next_act_scheduled != null && next_act_scheduled.creatorPersonIndex != person.persIndex && last_act_scheduled != null && last_act_scheduled.creatorPersonIndex != person.persIndex) {
                val errorMsg =
                    "Duration Bounds incompatible Act" + act.dayIndex + "/" + act.tour.index + "/" + act.index + " : UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")"
                throw InvalidPatternException("Household", pattern, errorMsg)
            } else {
                val errorMsg =
                    "Duration Bounds incompatible Act " + act.dayIndex + "/" + act.tour.index + "/" + act.index + " : UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")"
                throw InvalidPatternException("Person", pattern, errorMsg)
            }
        }

        /*
         * check if previous and following activity in this tour are already determined.
         * If so, duration and thus bounds are fixed.
         */
        if (!act.isActivityFirstinTour && !act.isActivityLastinTour) {
            val lastact = act.previousActivityinTour
            val nextact = act.nextActivityinTour

            if (lastact!!.startTimeisScheduled() && lastact!!.durationisScheduled() && nextact!!.startTimeisScheduled()) {
                minduration = maxduration
            }
        }
        /*
         * check if activity itself and following activity in this tour have a determined starting time.
         * If so, duration and thus bounds are fixed.
         */
        if (act.startTimeisScheduled() && !act.isActivityLastinTour) {
            if (act.nextActivityinTour!!.startTimeisScheduled()) minduration = maxduration
        }


        /*
         * return bounds
         */
        val durationBounds = IntArray(2)
        durationBounds[0] = minduration
        durationBounds[1] = maxduration

        return durationBounds
    }


    /**
     * reverse determination of a time class based on an exact value
     *
     * @param maxduration
     * @return
     */
    private fun getDurationTimeClassforExactDuration(maxduration: Int): Int {
        var timeClass = -1
        for (i in 0..<Configuration.NUMBER_OF_ACT_DURATION_CLASSES) {
            if (maxduration >= Configuration.ACT_TIME_TIMECLASSES_LB[i] && maxduration <= Configuration.ACT_TIME_TIMECLASSES_UB[i]) {
                timeClass = i
            }
        }
        assert(timeClass != -1) { "could not determine timeclass!" }
        return timeClass
    }

    /**
     * calculates activity durations between two activities
     *
     * @param actfrom
     * @param actto
     * @return
     */
    private fun countActivityDurationsbetweenActivitiesofOneDay(actfrom: HActivity?, actto: HActivity?): Int {
        var result = 0
        val listofdayactivities = if (actfrom == null) {
            actto!!.day.allActivitiesoftheDay
        } else {
            actfrom.day.allActivitiesoftheDay
        }

        for (tmpact in listofdayactivities) {
            if ((actfrom == null && actto != null && actto.compareTo(tmpact) < 0)
                || (actfrom != null && actto != null && actfrom.compareTo(tmpact) > 0 && actto.compareTo(tmpact) < 0)
                || (actfrom != null && actto == null && actfrom.compareTo(tmpact) > 0)
            ) {
                result += if (tmpact.durationisScheduled()) {
                    tmpact.duration
                } else {
                    tmpact.defaultActivityTime
                }
            }
        }
        return result
    }


    /**
     * calculates trip durations between two activities
     *
     * @param actfrom
     * @param actto
     * @return
     */
    private fun countTripDurationsbetweenActivitiesofOneDay(actfrom: HActivity?, actto: HActivity?): Int {
        var result = 0
        val listofdayactivities = if (actfrom == null) {
            actto!!.day.allActivitiesoftheDay
        } else {
            actfrom.day.allActivitiesoftheDay
        }

        for (tmpact in listofdayactivities) {
            if ((actfrom == null && actto != null && actto.compareTo(tmpact) <= 0)
                || (actfrom != null && actto != null && actfrom.compareTo(tmpact) >= 0 && actto.compareTo(tmpact) <= 0)
                || (actfrom != null && actto == null && actfrom.compareTo(tmpact) >= 0)
            ) {
                if (actto != null && actto.compareTo(tmpact) == 0) {
                    result += tmpact.estimatedTripTimeBeforeActivity
                } else if (actfrom != null && actfrom.compareTo(tmpact) == 0) {
                    if (tmpact.isActivityLastinTour) result += tmpact.estimatedTripTimeAfterActivity
                } else {
                    result += tmpact.estimatedTripTimeBeforeActivity
                    if (tmpact.isActivityLastinTour) result += tmpact.estimatedTripTimeAfterActivity
                }
            }
        }
        return result
    }


    /**
     * calculates lower and upper bound for tour starting times based on other determined starting times and durations
     *
     * @param categories decides returning categories or exact values
     * @param tour
     * @return
     * @throws InvalidPatternException
     */
    @Throws(InvalidPatternException::class)
    private fun calculateStartingBoundsForTours(tour: HTour, categories: Boolean): IntArray {
        /*
                 * main idea of lower bound calculation
                 *
                 * 1. starting point (descending priority)
                 * - The tour is not the last tour of the day -> ending time of the previous tour
                 * - The last activity of the previous day ends after midnight
                 * - begin of the day (1 minute past midnight to allow buffer for home time)
                 *
                 *
                 * main idea of upper bound calculation
                 *
                 * 1. starting point (descending priority)
                 * - There is another tour later this day having a determined starting time
                 * - There is another tour scheduled until 3am the next day
                 * - "end of the day" (3am next day)
                 *
                 * 2. subtract all planned tours including activity and trip durations until upper bound starting point
                 * 3. buffer for home times
                 */

        val tourday = tour.day

        var lowercat = -1
        var uppercat = -1

        /*
         *
         * lower bound
         *
         */
        var startingpointlowerbound = 1


        // if this is not the first tour, starting point is the end of the previous tour
        if (tour.index != tourday.lowestTourIndex) {
            startingpointlowerbound = tourday.getTour(tour.index - 1).endTime + 1
        } else {
            val previousDay = tourday.previousDay
            if (previousDay != null && !previousDay.isHomeDay) {
                val lastactpreviousday = previousDay.lastTourOfDay.lastActivityInTour
                if (lastactpreviousday.startTimeisScheduled()) {
                    val endlastactpreviousday = lastactpreviousday.startTime +
                            (if (lastactpreviousday.durationisScheduled()) lastactpreviousday.duration else 0) +
                            (if (lastactpreviousday.tripAfterActivityisScheduled()) lastactpreviousday.estimatedTripTimeAfterActivity else 0)
                    if (endlastactpreviousday > 1440) {
                        // +1 to allow at least one minute home time
                        startingpointlowerbound = endlastactpreviousday - 1440 + 1
                    }
                }
            }
        }


        /*
         *
         * upper bound
         *
         */

        /*
         * 1. starting point
         */
        var startingpointupperbound = 1440
        var nexttourscheduled: HTour? = null

        for (i in tour.index + 1..tourday.highestTourIndex) {
            val tmptour = tourday.getTour(i)
            if (tmptour.isScheduled) {
                nexttourscheduled = tmptour
                startingpointupperbound = tmptour.startTime
                break
            }
        }
        if (nexttourscheduled == null) {
            val nextday = tourday.nextDay
            if (nextday != null && !nextday.isHomeDay) {
                val firstactnextday = nextday.firstTourOfDay.firstActivityInTour
                if (firstactnextday.startTimeisScheduled()) {
                    val startfirstactnextday = firstactnextday.startTime -
                            (if (firstactnextday.tripBeforeActivityisScheduled()) firstactnextday.estimatedTripTimeBeforeActivity else 0)
                    if (startfirstactnextday < (startingpointupperbound - 1440)) {
                        startingpointupperbound = 1439 + startfirstactnextday
                    }
                }
            }
        }

        /*
         * 2. activity and trip durations until upper bound starting point
         * 3. home time buffers
         */
        var tmptourdurations = 0
        var hometimebuffer = 0

        val tourindexforsearch = if (nexttourscheduled != null) {
            nexttourscheduled.index - 1
        } else {
            tourday.highestTourIndex
        }
        val other = (tour.index..tourindexforsearch).map{tourday.getTour(it)}
        val otherDur = other.sumOf{it.tourDuration}
        val homeDurs = other.size
        for (i in tour.index..tourindexforsearch) {
            val tmptour = tourday.getTour(i)
            tmptourdurations += tmptour.tourDuration

            hometimebuffer += 1
        }


        /*
         *
         * calculate bound and get categories if needed
         *
         */
        val lowerbound = startingpointlowerbound
        var upperbound = startingpointupperbound - tmptourdurations - hometimebuffer

        // limit upper bound to 1439 as no starting times after midnight are possible
        if (upperbound > 1439) upperbound = 1439


        // error handling
        if (upperbound < lowerbound) {
            val errorMsg =
                "TourStartTimes Tour " + tourday.index + "/" + tour.index + " : UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")"
            throw InvalidPatternException("Person", pattern, errorMsg)
        }


        // time classes for first tours of the day
        if (categories && tour.index == tourday.lowestTourIndex) {
            for (i in 0..<Configuration.NUMBER_OF_FIRST_START_TIME_CLASSES) {
                if (lowerbound >= Configuration.FIRST_TOUR_START_TIMECLASSES_LB[i] && lowerbound <= Configuration.FIRST_TOUR_START_TIMECLASSES_UB[i]) {
                    lowercat = i
                }
                if (upperbound >= Configuration.FIRST_TOUR_START_TIMECLASSES_LB[i] && upperbound <= Configuration.FIRST_TOUR_START_TIMECLASSES_UB[i]) {
                    uppercat = i
                }
            }
        }

        // time classes for all other tours of the day
        if (categories && tour.index != tourday.lowestTourIndex) {
            for (i in 0..<Configuration.NUMBER_OF_SECTHR_START_TIME_CLASSES) {
                if (lowerbound >= Configuration.SECTHR_TOUR_START_TIMECLASSES_LB[i] && lowerbound <= Configuration.SECTHR_TOUR_START_TIMECLASSES_UB[i]) {
                    lowercat = i
                }
                if (upperbound >= Configuration.SECTHR_TOUR_START_TIMECLASSES_LB[i] && upperbound <= Configuration.SECTHR_TOUR_START_TIMECLASSES_UB[i]) {
                    uppercat = i
                }
            }
        }

        // error handling for non existing categories
        if (categories) {
            if (uppercat == -1 || lowercat == -1) {
                val errorMsg =
                    "TourStartTimes Tour " + tour.index + " : Could not identify categories - UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")"
                throw InvalidPatternException("Person", pattern, errorMsg)
            }
        }

        val bounds = IntArray(2)
        if (categories) {
            bounds[0] = lowercat
            bounds[1] = uppercat
        }
        if (!categories) {
            bounds[0] = lowerbound
            bounds[1] = upperbound
        }
        return bounds
    }

    /**
     * calculate bounds for home time
     *
     * @param day
     * @param tour
     * @param categories decides returning categories or exact values
     * @return
     * @throws InvalidPatternException
     */
    @Throws(InvalidPatternException::class)
    private fun calculateBoundsForHomeTime(tour: HTour, categories: Boolean): IntArray {
        val tourday = tour.day

        val lowerbound = 1
        var upperbound = -1

        var lowercat = -1
        var uppercat = -1

        var starttime_nexttourscheduled = 1620

        // get upper bound based on starting times later this day
        var tmptourdurations = 0
        for (i in tour.index..tourday.highestTourIndex) {
            val tmptour = tourday.getTour(i)

            // use the first scheduled tour as starting point to calculate
            if (tmptour.isScheduled) {
                starttime_nexttourscheduled = tmptour.startTime
                break
            } else {
                // +1 to allow at least one minute home time
                tmptourdurations += tmptour.tourDuration + 1
            }
        }
        if (starttime_nexttourscheduled == 1620) {
            val folgetag = tourday.nextDay
            if (folgetag != null && !folgetag.isHomeDay) {
                val ersteaktfolgetag = folgetag.firstTourOfDay.firstActivityInTour
                if (ersteaktfolgetag.startTimeisScheduled()) {
                    val startersteaktfolgetag = ersteaktfolgetag.startTime -
                            (if (ersteaktfolgetag.tripBeforeActivityisScheduled()) ersteaktfolgetag.estimatedTripTimeBeforeActivity else 0)
                    if (startersteaktfolgetag < 180) {
                        starttime_nexttourscheduled = 1439 + startersteaktfolgetag
                    }
                }
            }
        }

        upperbound = starttime_nexttourscheduled - tmptourdurations - tourday.getTour(tour.index - 1).endTime
        if (upperbound > 1439) upperbound = 1439

        // error handling
        if (upperbound < lowerbound) {
            val errorMsg =
                "HomeTime Tour " + tour.index + " : UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")"
            throw InvalidPatternException("Person", pattern, errorMsg)
        }

        if (categories) {
            for (i in 0..<Configuration.NUMBER_OF_HOME_DURATION_CLASSES) {
                if (lowerbound >= Configuration.HOME_TIME_TIMECLASSES_LB[i] && lowerbound <= Configuration.HOME_TIME_TIMECLASSES_UB[i]) {
                    lowercat = i
                }
                if (upperbound >= Configuration.HOME_TIME_TIMECLASSES_LB[i] && upperbound <= Configuration.HOME_TIME_TIMECLASSES_UB[i]) {
                    uppercat = i
                }
            }
        }

        // error handling
        if (categories) {
            if (uppercat == -1 || lowercat == -1) {
                val errorMsg =
                    "HomeTime Tour " + tour.index + " : Could not identify categories - UpperBound (" + upperbound + ") < LowerBound (" + lowerbound + ")"
                throw InvalidPatternException("Person", pattern, errorMsg)
            }
        }

        val bounds = IntArray(2)
        if (categories) {
            bounds[0] = lowercat
            bounds[1] = uppercat
        }
        if (!categories) {
            bounds[0] = lowerbound
            bounds[1] = upperbound
        }
        return bounds
    }


    /**
     * method to create home activities between two tours
     *
     * @param allmodeledActivities
     * @throws InvalidPersonPatternException
     */
    @Throws(InvalidPatternException::class)
    private fun createHomeActivities(allmodeledActivities: List<HActivity>) {
        val homeact = ActivityType.HOME

        if (allmodeledActivities.size != 0) {
            // create home activity before starting the first tour
            val duration1 = allmodeledActivities[0].tripStartTimeBeforeActivityWeekContext

            //assert duration1>0 : "person error - no home activity possible at beginning of the week!";
            if (duration1 <= 0) throw InvalidPatternException(
                "person",
                this.pattern,
                "person error - no home activity possible at beginning of the week!"
            )

            val home = HActivity(pattern.getDay(0), homeact, duration1, 0)
            pattern.addHomeActivity(home)

            // loop through all activities and create home activities after last activity in a tour
            for (i in 0..<allmodeledActivities.size - 1) {
                val act = allmodeledActivities[i]
                if (act.isActivityLastinTour) {
                    val acttour = act.tour
                    val nexttour = allmodeledActivities[i + 1].tour

                    val ende_tour = acttour.endTimeWeekContext
                    val start_next_tour = nexttour.startTimeWeekContext

                    // calculate buffer
                    val duration2 = start_next_tour - ende_tour

                    //assert (duration2>0) : "person error - no home activity possible after end of the tour! - " + start_next_tour + " // " + ende_tour;
                    if (duration2 <= 0) throw InvalidPatternException(
                        "person", this.pattern,
                        "person error - no home activity possible after end of the tour! - $start_next_tour // $ende_tour"
                    )
                    // get corresponding day for home activity
                    var day = ende_tour / 1440
                    var starttime = ende_tour % 1440

                    // if an activity start after end of day 7, the activity will still be part of day 7 since there is no day 8 modeled
                    if (day == 7) {
                        day = 6
                        starttime = starttime + 1440
                    }
                    // add home activity
                    pattern.addHomeActivity(HActivity(pattern.getDay(day), homeact, duration2, starttime))
                }
            }

            // check the remaining time after the last activity of the week
            val lastact = allmodeledActivities[allmodeledActivities.size - 1]
            val ende_lastTour = lastact.tour.endTimeWeekContext
            if (ende_lastTour < 10080) {
                // calculate buffer
                val duration3 = 10080 - ende_lastTour
                // get corresponding day for home activity
                val day = ende_lastTour / 1440
                // add home activity
                pattern.addHomeActivity(HActivity(pattern.getDay(day), homeact, duration3, ende_lastTour % 1440))
            }
        } else {
            pattern.addHomeActivity(HActivity(pattern.getDay(0), homeact, 10080, 0))
        }
    }

    /**
     * select possible other household members as participants for a joint activity
     */
    private fun selectWithWhomforJointActions() {
        for (tmpactivity in pattern.allOutofHomeActivities) {
            /*
             * consider activity if activity has a joint status and was created by the person itself
             */
            if (tmpactivity.jointStatus != JointStatus.NOJOINTELEMENT && tmpactivity.creatorPersonIndex == person.persIndex) {
                val otherunmodeledpersinhh: MutableMap<Int, ActitoppPerson> = HashMap()
                // first add all other household members
                otherunmodeledpersinhh.putAll(person.household.getHouseholdmembersDeprecated())

                val keyValues: List<Int> = ArrayList(otherunmodeledpersinhh.keys)
                for (key in keyValues) {
                    val tmpperson = otherunmodeledpersinhh[key]
                    // delete all members that are already modeled or the person itself
                    if (tmpperson!!.weekPattern != null || tmpperson.persIndex == person.persIndex) {
                        otherunmodeledpersinhh.remove(key)
                    }
                    // remove person if activity is working and other person is not allowed to work
                    if (tmpactivity.activityType == ActivityType.WORK && tmpactivity.jointStatus != JointStatus.JOINTTRIP && !tmpperson.isAllowedToWork) {
                        otherunmodeledpersinhh.remove(key)
                    }
                }

                if (otherunmodeledpersinhh.size > 0) {
                    // decide number of household members joining the activity
                    var numberofadditionalmembers = 99
                    val randomvalue = randomGenerator.randomValue
                    val hhgro = person.household.numberofPersonsinHousehold

                    /*
                     * probabilities are calculated using MOP data
                     */
                    if (hhgro == 2) {
                        numberofadditionalmembers = 1
                    }
                    if (hhgro == 3) {
                        if (randomvalue < 0.75) numberofadditionalmembers = 1
                        if (randomvalue >= 0.75) numberofadditionalmembers = 2
                    }
                    if (hhgro == 4) {
                        if (0 <= randomvalue && randomvalue < 0.73) numberofadditionalmembers = 1
                        if (0.73 <= randomvalue && randomvalue < 0.89) numberofadditionalmembers = 2
                        if (0.89 <= randomvalue && randomvalue <= 1) numberofadditionalmembers = 3
                    }
                    if (hhgro >= 5) {
                        if (0 <= randomvalue && randomvalue < 0.79) numberofadditionalmembers = 1
                        if (0.79 <= randomvalue && randomvalue < 0.92) numberofadditionalmembers = 2
                        if (0.92 <= randomvalue && randomvalue < 0.95) numberofadditionalmembers = 3
                        if (0.95 <= randomvalue && randomvalue <= 1) numberofadditionalmembers = 4
                    }

                    //TODO improvement: make the choice sensitive of the context (e.g., two pensioners is more likely than a pensioner and a student, ...) Robin: This TODO aint from me
                    val anzahlweiterepers =
                        min(numberofadditionalmembers.toDouble(), otherunmodeledpersinhh.size.toDouble()).toInt()
                    for (i in 1..anzahlweiterepers) {
                        // choose randomly one unmodeled member
                        val keys: List<Int> = ArrayList(otherunmodeledpersinhh.keys)
                        val randomkey = keys[randomGenerator.getRandomPersonKey(keys.size)]

                        // add activity to the other member's schedule
                        val otherperson = otherunmodeledpersinhh[randomkey]
                        otherperson!!.addJointActivityforConsideration(tmpactivity)

                        // add other person as participant for the actual person
                        tmpactivity.addJointParticipant(otherperson)

                        // remove person from map and proceed
                        otherunmodeledpersinhh.remove(randomkey)
                    }
                }
            }
        }
    }

    /**
     * Robin: This function appears to generate a [WRDDiscreteDistribution]. This seems to be only used in the WRD Model
     * step. The nullable return can be removed, since wrd starts a callback to this object to please generate a default
     * value if none exists.
     */
    fun getOrCreateWeightedDistribution(
        id: String,
        category: Category,
        activityType: ActivityType,
    ): WRDDiscreteDistribution {
        return personalWRDDistributions[id + category + activityType] ?: run {
            val distribution = fileBase.getDistributionFor(id, category)
            WRDDiscreteDistribution(distribution).also {
                personalWRDDistributions[id + category + activityType] = it

            }
        }
    }
}
