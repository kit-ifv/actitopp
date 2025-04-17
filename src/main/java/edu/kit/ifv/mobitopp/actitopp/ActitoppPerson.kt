package edu.kit.ifv.mobitopp.actitopp

import edu.kit.ifv.mobitopp.actitopp.enums.ActitoppPersonParameters
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.enums.JointStatus
import java.util.Collections
import kotlin.math.max


class ActitoppPerson@JvmOverloads constructor(
    val household: ActiToppHousehold,
    val persNrinHousehold: Int,
    val persIndex: Int,
    val age: Int,
    val employment: Int,
    val gender: Int,
    val commutingdistance_work: Double = 0.0,
    val commutingdistance_education: Double = .0,
) {
    private val attributes: MutableMap<String, Double> = mutableMapOf()
    val isAllowedToWork: Boolean = true
    var weekPattern: HWeekPattern? = null
        private set

    init {
        household.addHouseholdmember(this, persNrinHousehold)
        attributes["numbermodeledinhh"] = persNrinHousehold.toDouble()
    }

    constructor(tmppers: ActitoppPerson, tmphh: ActiToppHousehold) : this(
        tmphh,
        tmppers.persNrinHousehold,
        tmppers.persIndex,
        tmppers.age,
        tmppers.employment,
        tmppers.gender,
        tmppers.commutingdistance_work,
        tmppers.commutingdistance_education
    )


    // stores all attributes that are not directly accessible by variables


    /**
     * @return the weekPattern
     */
    // stores all activity information of the week pattern

    fun days() = weekPattern?.days ?: emptyList()

    /**
     * Robin: This function delegates the call to the weekpattern, which should not be exposed
     */
    fun countActivityTypes(activityType: ActivityType): Int {
        return weekPattern?.countActivitiesPerWeek(activityType) ?: 0
    }

    fun countTourTypes(activityType: ActivityType): Int = weekPattern?.countToursPerWeek(activityType) ?: 0
    fun countDaysWithSpecificActivity(activityType: ActivityType): Int =
        weekPattern?.countDaysWithSpecificActivity(activityType) ?: 0

    // Variables used for modeling of joint actions
    // based on linear regression model to determine modeling order within the household
    var probableshareofjointactions: Double = -1.0
        private set

    // List of joint actions to consider that are first created from other household members
    private val jointActivitiesforConsideration: MutableList<HActivity> = mutableListOf()


    /**
     * constructor to "clone" household including all persons in the household
     *
     * @param tmppers
     * @param tmphh
     */





    val children0_10: Int
        /**
         * @return the children0_10
         */
        get() = household.children0_10

    val children_u18: Int
        /**
         * @return the children_u18
         */
        get() = household.children_u18


    val areatype: Int
        /**
         * @return the areatype
         */
        get() = household.areatype


    val numberofcarsinhousehold: Int
        /**
         * @return the numberofcarsinhousehold
         */
        get() = household.numberofcarsinhousehold


    val commutingDuration_work: Int
        /**
         * @return the commutingduration_work [min]
         */
        get() {
            /*
              * mean commuting speed in kilometers/hour is calculated using commuting distance groups
              * based on data of commuting trips of the German Mobility Panel (2004-2013)
              */
            val commutingspeed_work = if (commutingdistance_work > 0 && commutingdistance_work <= 5) 16.0
            else if (commutingdistance_work > 5 && commutingdistance_work <= 10) 29.0
            else if (commutingdistance_work > 10 && commutingdistance_work <= 20) 38.0
            else if (commutingdistance_work > 20 && commutingdistance_work <= 50) 51.0
            else if (commutingdistance_work > 50) 67.0
            else 32.0

            // minimum trip duration: 1 Minute
            return max(
                1.0,
                Math.round((commutingdistance_work / commutingspeed_work) * 60).toDouble()
            ).toInt()
        }


    val commutingDuration_education: Int
        /**
         * @return the commutingduration_education [min]
         */
        get() {
            /*
              * mean commuting speed in kilometers/hour is calculated using commuting distance groups
              * based on data of commuting trips of the German Mobility Panel (2004-2013)
              */
            val commutingspeed_education = if (commutingdistance_education > 0 && commutingdistance_education <= 5) 12.0
            else if (commutingdistance_education > 5 && commutingdistance_education <= 10) 21.0
            else if (commutingdistance_education > 10 && commutingdistance_education <= 20) 28.0
            else if (commutingdistance_education > 20 && commutingdistance_education <= 50) 40.0
            else if (commutingdistance_education > 50) 55.0
            else 21.0

            // minimum trip duration: 1 Minute
            return max(
                1.0,
                Math.round((commutingdistance_education / commutingspeed_education) * 60).toDouble()
            ).toInt()
        }


    /**
     * @param name
     * @param value
     */
    fun addAttributetoMap(name: String, value: Double) {
        attributes[name] = value
    }

    /**
     * @param name
     * @return
     */
    fun getAttributefromMap(name: String): Double {
        return attributes[name]!!
    }

    /**
     * check existence of attribute
     *
     * @param name
     * @return
     */
    fun existsAttributeinMap(name: String): Boolean {
        return attributes[name] != null
    }


    val attributesMap: DefaultDoubleMap<String>
        /**
         * @return the attributes
         */
        get() = DefaultDoubleMap(attributes)

    /**
     * delete all attributes from map
     */
    fun clearAttributesMap() {
        attributes.clear()
    }

    /**
     * delete weekpattern of person
     */
    fun clearWeekPattern() {
        weekPattern = null
    }

    /**
     * delete all joint actions to consider
     */
    fun clearJointActivitiesforConsideration() {
        jointActivitiesforConsideration.clear()
    }

    /**
     * calculates the probable share of joint actions based on person characteristics
     * this share is used to determine the modeling order within the household
     *
     * @param fileBase
     */
    fun calculateProbableshareofjointactions(fileBase: ModelFileBase) {
        // create attribute lookup

        val lookup = AttributeLookup(this)

        // create modeling object (97estimates.csv contains modeling information)
        val regression = LinRegDefaultCalculation("97estimates", fileBase, lookup)

        regression.initializeEstimates()
        this.probableshareofjointactions = regression.calculateRegression()
    }


    override fun toString(): String {
        val message = StringBuffer()

        message.append("\n person information")

        message.append("\n - persindex : ")
        message.append(persIndex)

        message.append("\n - age : ")
        message.append(age)

        message.append("\n - employment type : ")
        message.append(employment)

        message.append("\n - gender : ")
        message.append(gender)

        message.append("\n - is allowed to work : ")
        message.append(isAllowedToWork)

        message.append("\n - commuting distance work : ")
        message.append(commutingdistance_work)

        message.append("\n - commuting distance education : ")
        message.append(commutingdistance_education)

        message.append("\n - household ")
        message.append(household)

        return message.toString()
    }

    /**
     * create activity schedule for this person
     *
     * @param modelbase
     * @param randomgenerator
     * @throws InvalidPatternException
     */
    @Throws(InvalidPatternException::class)
    fun generateSchedule(modelbase: ModelFileBase, randomgenerator: RNGHelper) {
        //create an empty Default-Pattern
        weekPattern = HWeekPattern(this)

        if (age < 10) {
//			System.err.println("actitopp can only create correct activity schedules for persons aged 10 years and older! - creating full-week home activity instead");
            weekPattern!!.addHomeActivity(HActivity(weekPattern!!.getDay(0), ActivityType.HOME, 10080, 0))
        } else {
            val modelCoordinator = Coordinator(this, modelbase, randomgenerator)
            modelCoordinator.executeModel()
        }
    }

    /**
     * create activity schedule for this person using debug loggers to log results
     *
     * @param modelbase
     * @param randomgenerator
     * @param debugloggers
     * @throws InvalidPatternException
     */
    @Throws(InvalidPatternException::class)
    fun generateSchedule(modelbase: ModelFileBase, randomgenerator: RNGHelper, debugloggers: DebugLoggers) {
        //create an empty Default-Pattern
        weekPattern = HWeekPattern(this)

        if (age < 10) {
//			System.err.println("actitopp can only create correct activity schedules for persons aged 10 years and older! - creating full-week home activity instead");
            weekPattern!!.addHomeActivity(HActivity(weekPattern!!.getDay(0), ActivityType.HOME, 10080, 0))
        } else {
            val modelCoordinator = Coordinator(this, modelbase, randomgenerator, debugloggers)
            modelCoordinator.executeModel()
        }
    }

    val allJointActivitiesforConsideration: List<HActivity>
        /**
         * @return
         */
        get() = jointActivitiesforConsideration

//    /**
//     * @param aktliste
//     */
//    fun setAllJointActivitiesforConsideration(aktliste: MutableList<HActivity>) {
//        this.jointActivitiesforConsideration = aktliste
//    }

    /**
     * add activity to list of joint actions to consider when there if no conflict
     *
     * @param act
     */
    fun addJointActivityforConsideration(act: HActivity) {
        //make sure the activity is joint

        assert(JointStatus.JOINTELEMENTS.contains(act.jointStatus)) { "no jointAct!" }

        // check if there is already an activity at the same time
        var activityconflict = false
        for (tmpact in jointActivitiesforConsideration) {
            if (act.overlaps(tmpact)) {
                activityconflict = true
                if (Configuration.debugenabled) {
                    System.err.println("HH" + household.householdIndex + "/P" + persIndex + ": activity was not added as joint acticity due to conflict with existing activity!")
                    System.err.println("act to add: $act")
                    System.err.println("existing act: $tmpact")
                }

                break
            }
        }

        if (!activityconflict) {
            jointActivitiesforConsideration.add(act)
        }
    }

    /**
     * determines if a person is anyway employed (full time, part time or in vocational program)
     *
     * @return
     */
    fun personisAnywayEmployed(): Boolean {
        val employmenttype = employment
        return (employmenttype == 1 || employmenttype == 2 || employmenttype == 21 || employmenttype == 22 || employmenttype == 5)
    }

    /**
     * determines if a person is in school or student
     *
     * @return
     */
    fun personisinEducation(): Boolean {
        val employmenttype = employment
        return (employmenttype == 4 || employmenttype == 40 || employmenttype == 41 || employmenttype == 42 || employmenttype == 5)
    }


    val isPersonWorkorSchoolCommuterAndMainToursAreScheduled: Boolean
        /**
         * @return
         */
        get() {
            if (personisAnywayEmployed() || personisinEducation()) {
                for (day in weekPattern!!.days) {
                    for (tour in day.tours) {
                        if (tour.getActivity(0).activityType == ActivityType.WORK || tour.getActivity(0)
                                .activityType == ActivityType.EDUCATION
                        ) {
                            return true
                        }
                    }
                }
            } else {
                return false
            }
            return false
        }


    companion object {
        /**
         * sort list of household members descending by their probable share of joint actions
         *
         * @param personList
         */
        fun sortPersonListOnProbabilityofJointActions_DESC(personList: List<ActitoppPerson>, fileBase: ModelFileBase) {
            checkNotNull(personList) { "nothing to sort!" }

            for (tmpperson in personList) {
                tmpperson.calculateProbableshareofjointactions(fileBase)
            }

            Collections.sort(
                personList
            ) { person1, person2 ->
                if (person1.probableshareofjointactions < person2.probableshareofjointactions) {
                    +1
                } else if (person1.probableshareofjointactions == person2.probableshareofjointactions) {
                    0
                } else {
                    -1
                }
            }
        }


        @JvmOverloads
        operator fun invoke(
            PersIndex: Int,
            children0_10: Int,
            children_u18: Int,
            age: Int,
            employment: Int,
            gender: Int,
            areatype: Int,
            numberofcarsinhousehold: Int = 0,
            commutingdistance_work: Double = .0,
            commutingdistance_education: Double = .0,
        ): ActitoppPerson {
            val household = ActiToppHousehold(
                PersIndex,
                children0_10,
                children_u18,
                areatype,
                numberofcarsinhousehold
            )

            val person = ActitoppPerson(
                household,
                1,
                PersIndex,
                age,
                employment,
                gender,
                commutingdistance_work,
                commutingdistance_education
            )

            return person

        }
    }
}

/**
 * Because very often if something doesnt exist legacy actitopp returned 0.0
 */
class DefaultDoubleMap<K>(val original: Map<K, Double>) : Map<K, Double> by original {
    override operator fun get(key: K): Double {
        return original.getOrDefault(key, 0.0)
    }
}