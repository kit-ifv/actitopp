package edu.kit.ifv.mobitopp.actitopp

/**
 * @author Tim Hilgert
 */
class ActiToppHousehold {
    /**
     * @return the householdIndex
     */

    val householdIndex: Int

    val householdmembers: MutableMap<Int, ActitoppPerson>

    /**
     * @return the children0_10
     */
    /**
     * @param children0_10 the children0_10 to set
     */
    // household properties
    
    var children0_10: Int
    /**
     * @return the children_u18
     */
    /**
     * @param children_u18 the children_u18 to set
     */
    
    var children_u18: Int
    /**
     * @return the areatype
     */
    /**
     * @param areatype the areatype to set
     */
    
    var areatype: Int
    /**
     * @return the numberofcarsinhousehold
     */
    /**
     * @param numberofcarsinhousehold the numberofcarsinhousehold to set
     */
    
    var numberofcarsinhousehold: Int = 0

    /**
     * constructor with number of cars in household
     *
     * @param householdIndex
     * @param children0_10
     * @param children_u18
     * @param areatype
     * @param numberofcarsinhousehold
     */
    constructor(
        householdIndex: Int,
        children0_10: Int,
        children_u18: Int,
        areatype: Int,
        numberofcarsinhousehold: Int
    ) : super() {
        this.householdIndex = householdIndex

        this.children0_10 = children0_10
        this.children_u18 = children_u18
        this.areatype = areatype
        this.numberofcarsinhousehold = numberofcarsinhousehold

        this.householdmembers = HashMap()
    }

    /**
     * constructor without number of cars in household
     *
     * @param householdIndex
     * @param children0_10
     * @param children_u18
     * @param areatype
     */
    constructor(householdIndex: Int, children0_10: Int, children_u18: Int, areatype: Int) : super() {
        this.householdIndex = householdIndex

        this.children0_10 = children0_10
        this.children_u18 = children_u18
        this.areatype = areatype

        this.householdmembers = HashMap()
    }

    /**
     * constructor used to "clone" household including all persons in the household
     *
     * @param tmphh
     */
    constructor(tmphh: ActiToppHousehold) : this(
        tmphh.householdIndex,
        tmphh.children0_10,
        tmphh.children_u18,
        tmphh.areatype,
        tmphh.numberofcarsinhousehold
    ) {
        // "clone" all householdmembers
        for (tmppers in tmphh.householdmembersasList) {
            ActitoppPerson(tmppers, this)
        }
    }

    /**
     * @return the householdmembers
     */
    fun getHouseholdmembersDeprecated(): Map<Int, ActitoppPerson> {
        return householdmembers
    }

    val householdmembersasList: List<ActitoppPerson>
        /**
         * @return the householdmembers
         */
        get() {
            val tmpliste: MutableList<ActitoppPerson> = ArrayList()

            for ((_, value) in getHouseholdmembersDeprecated()) {
                tmpliste.add(value)
            }

            return tmpliste
        }

    /**
     * @param persnrinhousehold
     * @return the person in the household
     */
    fun getHouseholdMember(persnrinhousehold: Int): ActitoppPerson {
        val tmpperson =
            checkNotNull(getHouseholdmembersDeprecated()[persnrinhousehold]) { "Person does not exist in this household!" }
        return tmpperson
    }

    /**
     * @param member
     * @param persnr
     */
    fun addHouseholdmember(member: ActitoppPerson, persnr: Int) {
        checkNotNull(member) { "Householdmember is null" }
        assert(householdmembers[persnr] == null) { "Householdmember using this identifier already exists - persnr $persnr" }
        householdmembers[persnr] = member
    }


    val numberofPersonsinHousehold: Int
        /**
         * @return the numberofpersonsinhousehold
         */
        get() = householdmembers.size

    /**
     * resets all modeling results for this household
     */
    fun resetHouseholdModelingResults() {
        for (actperson in householdmembersasList) {
            actperson.clearAttributesMap()
            actperson.clearWeekPattern()
            actperson.clearJointActivitiesforConsideration()
        }
    }


    override fun toString(): String {
        val message = StringBuffer()

        message.append("\n household information")

        message.append("\n - HH-index : ")
        message.append(householdIndex)

        message.append("\n - #HH-members : ")
        message.append(numberofPersonsinHousehold)

        message.append("\n - #children 0-10 : ")
        message.append(children0_10)

        message.append("\n - #children <18 : ")
        message.append(children_u18)

        message.append("\n - area type : ")
        message.append(areatype)

        message.append("\n - #car in HH : ")
        message.append(numberofcarsinhousehold)

        return message.toString()
    }

    /**
     * generates activity schedules for the household (i.e. for each hh member)
     *
     * @param fileBase
     * @param randomgenerator
     * @throws InvalidPatternException
     */

    fun generateSchedules(fileBase: ModelFileBase, randomgenerator: RNGHelper) {
        val hhmembers = householdmembersasList
        if (Configuration.model_joint_actions) ActitoppPerson.Companion.sortPersonListOnProbabilityofJointActions_DESC(
            hhmembers,
            fileBase
        )

        for (i in hhmembers.indices) {
            val actperson = hhmembers[i]

            var personscheduleOK = false
            while (!personscheduleOK) {
                try {
                    // stores the modeling order of persons within the household
                    actperson.addAttributetoMap("numbermodeledinhh", (i + 1).toDouble())

                    // generates week schedule
                    actperson.generateSchedule(fileBase, randomgenerator)

                    personscheduleOK = true
                } catch (e: InvalidPatternException) {
                    //System.err.println(e.getReason());

                    /*
                     * When modeling joint actions, errors on person level are passed to household level (here). As household members
                     * are connected through joint actions, we need to remodel the whole household.
                     *
                     * When ignoring modeling joint actions, errors on person level are handled there and we need to remodel the error
                     * person only.
                     */

                    if (Configuration.model_joint_actions) {
                        throw InvalidPatternException("Household", actperson.weekPattern, "Remodel Household")
                    }
                }
            }
        }
    }

    /**
     * generates activity schedules for the household (i.e. for each hh member) using debug loggers to log results
     *
     * @param fileBase
     * @param randomgenerator
     * @param debugloggers
     * @throws InvalidPatternException
     */

    fun generateSchedules(fileBase: ModelFileBase, randomgenerator: RNGHelper, debugloggers: DebugLoggers) {
        val hhmembers = householdmembersasList
        if (Configuration.model_joint_actions) ActitoppPerson.sortPersonListOnProbabilityofJointActions_DESC(
            hhmembers,
            fileBase
        )

        for (i in hhmembers.indices) {
            val actperson = hhmembers[i]

            var personscheduleOK = false
            while (!personscheduleOK) {
                try {
                    // stores the modeling ordner of persons within the household
                    actperson.addAttributetoMap("numbermodeledinhh", (i + 1).toDouble())

                    // generates week schedule
                    actperson.generateSchedule(fileBase, randomgenerator, debugloggers)

                    personscheduleOK = true
                } catch (e: InvalidPatternException) {
                    //System.err.println(e.getReason());
                    debugloggers.deleteInformationforPerson(actperson)

                    /*
                     * When modeling joint actions, errors on person level are passed to household level (here). As household members
                     * are connected through joint actions, we need to remodel the whole household.
                     *
                     * When ignoring modeling joint actions, errors on person level are handled there and we need to remodel the error
                     * person only.
                     */
                    // TODO fix this insanity.
                    if (Configuration.model_joint_actions) {
                        throw InvalidPatternException("Household", actperson.weekPattern, "Remodel Household")
                    }
                }
            }
        }
    }
}
