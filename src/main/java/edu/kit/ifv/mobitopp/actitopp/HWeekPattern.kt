package edu.kit.ifv.mobitopp.actitopp

/**
 * @author Tim Hilgert
 */
class HWeekPattern(
    val person: ActitoppPerson,
) {
    val days: MutableList<HDay> = ArrayList()

    private val homeactivitities: MutableList<HActivity> = ArrayList()


    init {
        for (i in 0..6) {
            days.add(HDay(this, i + 1))
        }
    }

    fun getDay(index: Int): HDay {
        return days[index]
    }

    /**
     * returns activities of the week (only out of home, no home activities) in a list.
     * Robin: It appears that in the modelling process everything that is part of a tour is an activity that takes place
     * out of home. In the original code there was no filter at all, just a concatenation of all activities
     *
     * @return
     */
    val allOutofHomeActivities: List<HActivity> get() = days.flatMap { it.tours.flatMap { it.activities } }


    val allJointActivities: List<HActivity>
        /**
         * returns activities of the week (only joint activities) in a list
         *
         * @return
         */
        get() {
            val tmpliste: MutableList<HActivity> = ArrayList()
            for (act in allActivities) {
                if (JointStatus.JOINTELEMENTS.contains(act.jointStatus)) tmpliste.add(act)
            }
            return tmpliste
        }

    /**
     * returns activities of the week (only home activities) in a list
     * Robin: Somehow this only maps to the private list homeactivities, weird, why do we need that field?
     *
     */
    private val allHomeActivities: List<HActivity> get() = homeactivitities


    /**
     * returns all activities of the week (out of home + home activities) in a list
     * Robin: Why is the sortFunction defined in HActivity. this needs to be fixed.
     *
     * @return
     */
    val allActivities: List<HActivity>
        get() {
            val actList = allOutofHomeActivities + allHomeActivities
            return actList.sortedBy { it.startTimeWeekContext }
        }

    /**
     * returns all trips of the week in a list
     *
     * @return
     */
    val allTrips: List<HTrip>
        get() {
            val tmpliste: MutableList<HTrip> = ArrayList()
            for (tmpact in allActivities) {
                if (tmpact.tripBeforeActivityisScheduled()) tmpliste.add(tmpact.tripbeforeactivity!!)
                if (tmpact.tripAfterActivityisScheduled()) tmpliste.add(tmpact.tripafteractivity!!)
            }
            return tmpliste
        }


    val allTours: List<HTour> get() = days.flatMap { it.tours }


    val totalAmountOfOutofHomeActivities: Int get() = days.sumOf { it.totalAmountOfActivitites }

    /**
     * returns number of activities in the week (only out of home, no home activities) for a specific activity type
     *
     * @param activityType
     * @return
     */
    fun countActivitiesPerWeek(activityType: ActivityType): Int = allOutofHomeActivities.count {it.activityType == activityType}

    /**
     * returns number of tours in the week for a specific activity type
     *
     * @param activityType
     * @return
     */
    fun countToursPerWeek(activityType: ActivityType): Int = days.sumOf { it.tours.count { t -> t.getActivity(0).activityType == activityType } }


    /**
     * returns number of days in the week where an activity of a specific activity type exists
     *
     * @param activityType
     * @return
     */
    fun countDaysWithSpecificActivity(activityType: ActivityType?): Int = days.count {it.totalAmountOfActivitites > 0}



    fun printOutofHomeActivitiesList() {
        printActivities(allOutofHomeActivities)
    }

    fun printAllActivitiesList() {
        printActivities(allActivities)
    }


    private fun printActivities(listtoprinte: List<HActivity>) {
        val listtoprint = listtoprinte.sortedBy { it.startTimeWeekContext }

        println("")
        println(" -------------- activity list --------------")
        println("")

        for (i in listtoprint.indices) {
            val act = listtoprint[i]
            if (!act.isHomeActivity) {
                println(i.toString() + " " + act.tripbeforeactivity)
            }

            println("$i act : $act")

            if (!act.isHomeActivity && act.isActivityLastinTour) {
                println(i.toString() + " last " + act.tripafteractivity)
            }
        }
    }


    fun printJointActivitiesList() {
        val listtoprint = allJointActivities.sortedBy { it.startTimeWeekContext }

        println("")
        println(" -------------- list of joint activities --------------")
        println("")

        for (i in listtoprint.indices) {
            val act = listtoprint[i]
            println(i.toString() + " akt : " + act + " // creator: " + act.creatorPersonIndex)
        }
    }

    /**
     * @param act
     */
    fun addHomeActivity(act: HActivity) {
        require(act.activityType == ActivityType.HOME) { "no home activity" }
        homeactivitities.add(act)
    }

    /**
     * check if pattern has activity overlaps
     *
     * @return
     */
    fun weekPatternisFreeofOverlaps(): Boolean {

        val allActivities = this.allActivities.sortedBy { it.startTimeWeekContext }
        return allActivities.zipWithNext().none { (first, second) -> first.overlaps(second) }

    }
}
