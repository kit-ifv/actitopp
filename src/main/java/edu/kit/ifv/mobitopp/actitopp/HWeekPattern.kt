package edu.kit.ifv.mobitopp.actitopp

/**
 * @author Tim Hilgert
 */
class HWeekPattern(
    /**
     * @return the person
     */
    @JvmField val person: ActitoppPerson
) {
    val days: MutableList<HDay> = ArrayList()

    private val homeactivitities: MutableList<HActivity> = ArrayList()

    /**
     * Constructor
     */
    init {
        for (i in 0..6) {
            days.add(HDay(this, i + 1))
        }
    }
    
    /**
     * @param index
     * @return
     */
    fun getDay(index: Int): HDay {
        return days[index]
    }


    val allOutofHomeActivities: List<HActivity>
        /**
         * returns activities of the week (only out of home, no home activities) in a list
         *
         * @return
         */
        get() {
            val actList: MutableList<HActivity> = ArrayList()
            for (day in this.days) {
                for (tour in day.tours) {
                    for (act in tour.activities) {
                        actList.add(act)
                    }
                }
            }
            return actList
        }

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

    val allHomeActivities: List<HActivity>
        /**
         * returns activities of the week (only home activities) in a list
         *
         * @return
         */
        get() = homeactivitities


    val allActivities: List<HActivity>
        /**
         * returns all activities of the week (out of home + home activities) in a list
         *
         * @return
         */
        get() {
            val tmpliste: MutableList<HActivity> = ArrayList()
            tmpliste.addAll(allOutofHomeActivities)
            tmpliste.addAll(allHomeActivities)
            HActivity.Companion.sortActivityListbyWeekStartTimes(tmpliste)
            return tmpliste
        }


    val allTrips: List<HTrip>
        /**
         * returns all trips of the week in a list
         *
         * @return
         */
        get() {
            val tmpliste: MutableList<HTrip> = ArrayList()
            for (tmpact in allActivities) {
                if (tmpact.tripBeforeActivityisScheduled()) tmpliste.add(tmpact.tripbeforeactivity!!)
                if (tmpact.tripAfterActivityisScheduled()) tmpliste.add(tmpact.tripafteractivity!!)
            }
            return tmpliste
        }


    val allTours: List<HTour?>
        /**
         * returns all tours of the week in a list
         *
         * @return
         */
        get() {
            val tourList: MutableList<HTour?> = ArrayList()
            for (day in this.days) {
                for (tour in day.tours) {
                    tourList.add(tour)
                }
            }
            return tourList
        }


    val totalAmountOfOutofHomeActivities: Int
        /**
         * returns number of activities in the week (only out of home, no home activities)
         *
         * @return
         */
        get() {
            var activities = 0
            for (d in this.days) {
                activities += d.totalAmountOfActivitites
            }

            return activities
        }


    /**
     * returns number of activities in the week (only out of home, no home activities) for a specific activity type
     *
     * @param activityType
     * @return
     */
    fun countActivitiesPerWeek(activityType: ActivityType?): Int {
        var ctr = 0
        for (day in this.days) {
            for (tour in day.tours) {
                for (act in tour.activities) {
                    if (act.activityType == activityType) ctr++
                }
            }
        }
        return ctr
    }

    /**
     * returns number of tours in the week for a specific activity type
     *
     * @param activityType
     * @return
     */
    fun countToursPerWeek(activityType: ActivityType): Int {
        var ctr = 0
        for (day in this.days) {
            for (tour in day.tours) {
                if (tour.getActivity(0).activityType == activityType) ctr++
            }
        }
        return ctr
    }

    /**
     * returns number of days in the week where an activity of a specific activity type exists
     *
     * @param activityType
     * @return
     */
    fun countDaysWithSpecificActivity(activityType: ActivityType?): Int {
        var ctr = 0
        for (currentDay in days) {
            if (currentDay.getTotalNumberOfActivitites(activityType) > 0) ctr++
        }
        return ctr
    }


    fun printOutofHomeActivitiesList() {
        printActivities(allOutofHomeActivities)
    }

    fun printAllActivitiesList() {
        printActivities(allActivities)
    }


    private fun printActivities(listtoprint: List<HActivity>) {
        HActivity.Companion.sortActivityListbyWeekStartTimes(listtoprint)

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
        val listtoprint = allJointActivities
        HActivity.Companion.sortActivityListbyWeekStartTimes(listtoprint)

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
        assert(act.activityType == ActivityType.HOME) { "no home activity" }
        homeactivitities.add(act)
    }

    /**
     * check if pattern has activity overlaps
     *
     * @return
     */
    fun weekPatternisFreeofOverlaps(): Boolean {
        val freeofOverlaps = true

        val allActivities = this.allActivities
        HActivity.Companion.sortActivityListbyWeekStartTimes(allActivities)

        for (i in 0..<allActivities.size - 1) {
            val aktuelleakt = allActivities[i]
            val naechsteakt = allActivities[i + 1]

            assert(
                !HActivity.Companion.checkActivityOverlapping(
                    aktuelleakt,
                    naechsteakt
                )
            ) { "activities are overlapping $aktuelleakt vs $naechsteakt" }
        }
        return freeofOverlaps
    }
}
