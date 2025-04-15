package edu.kit.ifv.mobitopp.actitopp

import java.util.Collections
import kotlin.math.max
import kotlin.math.min


/**
 * @author Tim Hilgert
 */
class HActivity {
    //stores all attributes that are not directly accessible by variables
    private lateinit var attributes: MutableMap<String, Double>

    val day: HDay
    lateinit var tour: HTour
        private set

    private var acttype = ActivityType.UNKNOWN

    var index: Int = -99
        get() {
            assert(field != -99) { "index is not set" }
            return field
        }
        set(value) {
            field = value
        }
    var duration = -1
    private var starttime = -1


    /**
     * @return the tripbeforeactivity
     */
    /**
     * @param tripbeforeactivity the tripbeforeactivity to set
     */
    var tripbeforeactivity: HTrip? = null
    /**
     * @return the tripafteractivity
     */
    /**
     * @param tripafteractivity the tripafteractivity to set
     */
    var tripafteractivity: HTrip? = null

    var jointStatus = JointStatus.UNKNOWN

    private var jointParticipants: MutableList<ActitoppPerson> = ArrayList()

    /**
     * constructor
     *
     * @param parent
     * @param index
     */
    constructor(parent: HTour, index: Int) {
        checkNotNull(parent) { "tour is not initialized" }
        this.tour = parent

        this.day = parent.day
        this.index = index

        this.attributes = HashMap()
        creatorPersonIndex = day.person.persIndex
    }

    /**
     * constructor
     *
     * @param parent
     * @param index
     * @param type
     */
    constructor(parent: HTour, index: Int, type: ActivityType) : this(parent, index) {
        assert(type != ActivityType.UNKNOWN) { "unknown activity type!" }
        activityType = type
    }

    /**
     * constructor for home activities
     *
     * @param parent
     * @param type
     * @param duration
     * @param starttime
     */
    constructor(parent: HDay, type: ActivityType, duration: Int, starttime: Int) {
        checkNotNull(parent) { "day is not initialized" }
        this.day = parent
        activityType = type
        this.duration = duration
        startTime = starttime
        jointStatus = JointStatus.NOJOINTELEMENT
    }
    
    val weekPattern: HWeekPattern
        get() = day.pattern

    val person: ActitoppPerson
        get() = day.person

    var activityType: ActivityType
        get() {
            assert(ActivityType.FULLSET.contains(acttype)) { "unknown activity type:$acttype" }
            if (!person!!.isAllowedToWork) assert(acttype != ActivityType.WORK) { "person is not allowed to work!" }
            return acttype
        }
        set(acttype) {
            assert(ActivityType.FULLSET.contains(acttype)) { "unknown activity type:$acttype" }
            if (!person!!.isAllowedToWork) assert(acttype != ActivityType.WORK) { "person is not allowed to work!" }
            this.acttype = acttype
        }

    
    var startTime: Int
        get() {
            assert(starttime != -1) { "starttime is not set" }
            return starttime
        }
        set(starttime) {
            assert(starttime >= 0) { "starttime is not >0: $starttime" }
            this.starttime = starttime
        }
    

    val estimatedTripTimeBeforeActivity: Int
        /**
         * @return the estimatedTripTimeBeforeActivity
         */
        get() {
            assert(tripBeforeActivityisScheduled()) { "trip before activity in not intialized" }
            val tmptriptimebefore = tripbeforeactivity!!.duration
            return tmptriptimebefore
        }

    val estimatedTripTimeAfterActivity: Int
        /**
         * @return the estimatedTripTimeAfterActivity
         */
        get() {
            assert(tripAfterActivityisScheduled()) { "trip after activity in not intialized" }
            val tmptriptimeafter = tripafteractivity!!.duration
            return tmptriptimeafter
        }

    /**
     * compare two activities concerning indices
     *
     *
     * 0  - same activity
     * 1  - acttocompare is AFTER the other activity (higher day, tour or activity index)
     * -1 - acttocompare is BEFORE the other activity
     *
     * @param acttocompare
     * @return
     */
    fun compareTo(acttocompare: HActivity): Int {
        var result = 99
        if (acttocompare.weekDay > this.weekDay) result = 1
        if (acttocompare.weekDay < this.weekDay) result = -1
        if (acttocompare.weekDay == this.weekDay) {
            if (acttocompare.tour!!.index > tour!!.index) result = 1
            if (acttocompare.tour!!.index < tour!!.index) result = -1
            if (acttocompare.tour!!.index == tour!!.index) {
                if (acttocompare.index > this.index) result = 1
                if (acttocompare.index < this.index) result = -1
                if (acttocompare.index == this.index) result = 0
            }
        }
        assert(result != 99) { "Could not compare these two activities! - Act1: $this - Act2: $acttocompare" }
        return result
    }


    override fun toString(): String {
        var result = ""

        result = if (isHomeActivity) {
            dayIndex.toString() +
                    " start " + (if (startTimeisScheduled()) startTimeWeekContext else "n.a.") +
                    " end " + (if (startTimeisScheduled() && durationisScheduled()) endTimeWeekContext else "n.a.") +
                    " duration: " + (if (durationisScheduled()) duration else "n.a.") +
                    " type: " + (if (activitytypeisScheduled()) acttype.typeasChar else "n.a.") +
                    " jointStatus: " + this.jointStatus
        } else {
            dayIndex.toString() + "/" + tour!!.index + "/" + index +
                    " start " + (if (startTimeisScheduled()) startTimeWeekContext else "n.a.") +
                    " end " + (if (startTimeisScheduled() && durationisScheduled()) endTimeWeekContext else "n.a.") +
                    " duration: " + (if (durationisScheduled()) duration else "n.a.") +
                    " type: " + (if (activitytypeisScheduled()) acttype.typeasChar else "n.a.") +
                    " jointStatus: " + this.jointStatus +
                    " trip before: " + (if (tripBeforeActivityisScheduled()) estimatedTripTimeBeforeActivity else "n.a.") +
                    " trip after: " + (if (tripAfterActivityisScheduled()) estimatedTripTimeAfterActivity else "n.a.")
        }
        return result
    }


    val isActivityFirstinTour: Boolean
        get() = tour.lowestActivityIndex == index

    val isActivityLastinTour: Boolean
        get() = tour.highestActivityIndex == index


    /**
     * commuting trips BEFORE are trips
     * - that start at home
     * - that end at work (activity type is work)
     *
     * @return
     */
    fun hasWorkCommutingTripbeforeActivity(): Boolean {
        return (if (isActivityFirstinTour && activityType == ActivityType.WORK && (person!!.commutingdistance_work != 0.0)) true else false)
    }

    /**
     * commuting trips AFTER are trips
     * - that end at home
     * - that start at work (activity type is home)
     *
     * @return
     */
    fun hasWorkCommutingTripafterActivity(): Boolean {
        return (if (isActivityLastinTour && activityType == ActivityType.WORK && (person!!.commutingdistance_work != 0.0)) true else false)
    }

    /**
     * commuting trips BEFORE are trips
     * - that start at home
     * - that end at school/university (activity type is education)
     *
     * @return
     */
    fun hasEducationCommutingTripbeforeActivity(): Boolean {
        return (if (isActivityFirstinTour && activityType == ActivityType.EDUCATION && (person!!.commutingdistance_education != 0.0)) true else false)
    }

    /**
     * commuting trips AFTER are trips
     * - that end at home
     * - that start at school/university (activity type is home)
     *
     * @return
     */
    fun hasEducationCommutingTripafterActivity(): Boolean {
        return (if (isActivityLastinTour && activityType == ActivityType.EDUCATION && (person!!.commutingdistance_education != 0.0)) true else false)
    }

    val isHomeActivity: Boolean
        /**
         * @return
         */
        get() = (activitytypeisScheduled() && activityType == ActivityType.HOME)

    val isMainActivityoftheTour: Boolean
        /**
         * @return
         */
        get() = this.index == 0

    val isMainActivityoftheDay: Boolean
        /**
         * @return
         */
        get() = isMainActivityoftheTour && tour.isMainTouroftheDay

    val previousActivityinTour: HActivity?
        /**
         * @return
         */
        get() {
            val previousActivity = if (!isActivityFirstinTour) {
                tour.getActivity(index - 1)
            } else {
                null
            }
            return previousActivity
        }

    val nextActivityinTour: HActivity?
        /**
         * @return
         */
        get() {
            val nextActivity = if (!isActivityLastinTour) {
                tour.getActivity(index + 1)
            } else {
                null
            }
            return nextActivity
        }


    val previousOutOfHomeActivityinPattern: HActivity?
        /**
         * @return
         */
        get() {
            var previousact: HActivity? = null

            // if this is the first actitvity, get the last one from previous tour
            if (isActivityFirstinTour) {
                val previousTour = tour.previousTourinPattern
                if (previousTour != null) previousact = previousTour.lastActivityInTour
            } else {
                previousact = previousActivityinTour
            }
            return previousact
        }

    val nextOutOfHomeActivityinPattern: HActivity?
        /**
         * @return
         */
        get() {
            var nextact: HActivity? = null

            // if this is the last actitvity, get the first one from next tour
            if (isActivityLastinTour) {
                val nexttour = tour.nextTourinPattern
                if (nexttour != null) nextact = nexttour.firstActivityInTour
            } else {
                nextact = nextActivityinTour
            }
            return nextact
        }


    val endTime: Int
        /**
         * starttime + duration
         *
         * @return
         */
        get() = startTime + duration

    val weekDay: Int
        get() = day.weekday

    val dayIndex: Int
        get() = day.index

    val tourIndex: Int
        get() = tour!!.index

    val startTimeWeekContext: Int
        get() = 1440 * dayIndex + startTime

    val endTimeWeekContext: Int
        get() = startTimeWeekContext + duration

    val tripStartTimeBeforeActivity: Int
        get() = tripbeforeactivity!!.startTime

    val tripStartTimeBeforeActivityWeekContext: Int
        get() = tripbeforeactivity!!.startTimeWeekContext

    val tripStartTimeAfterActivity: Int
        get() = tripafteractivity!!.startTime

    val tripStartTimeAfterActivityWeekContext: Int
        get() = tripafteractivity!!.startTimeWeekContext

    /**
     * mean time calculation
     *
     * @return
     */
    fun calculateMeanTime(): Int {
        val timebudget = person!!.getAttributefromMap(activityType.toString() + "budget_exact")
        val daysWithAct = weekPattern.countDaysWithSpecificActivity(activityType).toDouble()
        val specificActivitiesForCurrentDay = day.getTotalNumberOfActivitites(activityType).toDouble()

        // calculation (between 0 and 1440)
        var meantime =
            max((timebudget / daysWithAct) * (1 / specificActivitiesForCurrentDay), 1.0).toInt().toDouble()
        meantime = min(meantime, 1440.0)

        return meantime.toInt()
    }

    /**
     * mean time calculation (category)
     *
     * @return
     */
    fun calculateMeanTimeCategory(): Int {
        val meantime = calculateMeanTime()
        var meantimecategory = -99
        for (i in 0..<Configuration.NUMBER_OF_ACT_DURATION_CLASSES) {
            if (meantime >= Configuration.ACT_TIME_TIMECLASSES_LB[i] && meantime <= Configuration.ACT_TIME_TIMECLASSES_UB[i]) meantimecategory =
                i
        }
        assert(meantimecategory != -99) { "could not determine category!" }
        return meantimecategory
    }

    /**
     * calculates trip times (default or based on commuting distances)
     */
    fun createTripsforActivity() {
        /*
                 * trip BEFORE activity
                 */

        // default

        var actualTripTime_beforeTrip = Configuration.FIXED_TRIP_TIME_ESTIMATOR
        // better than default if we have commuting information
        if (hasWorkCommutingTripbeforeActivity()) actualTripTime_beforeTrip = person!!.commutingDuration_work
        if (hasEducationCommutingTripbeforeActivity()) actualTripTime_beforeTrip = person!!.commutingDuration_education
        if (tripbeforeactivity == null) {
            tripbeforeactivity = HTrip(this, TripStatus.TRIP_BEFORE_ACT, actualTripTime_beforeTrip)
        } else {
            tripbeforeactivity!!.duration = (actualTripTime_beforeTrip)
        }

        /*
         * trip AFTER activity
         */
        if (isActivityLastinTour) {
            // default
            var actualTripTime_afterTrip = Configuration.FIXED_TRIP_TIME_ESTIMATOR
            // better than default if we have commuting information
            if (hasWorkCommutingTripafterActivity()) actualTripTime_afterTrip = person!!.commutingDuration_work
            if (hasEducationCommutingTripafterActivity()) actualTripTime_afterTrip =
                person!!.commutingDuration_education
            if (tripafteractivity == null) {
                tripafteractivity = HTrip(this, TripStatus.TRIP_AFTER_ACT, actualTripTime_afterTrip)
            } else {
                tripafteractivity!!.duration = (actualTripTime_afterTrip)
            }
        }
    }

    val isScheduled: Boolean
        get() = durationisScheduled() && startTimeisScheduled() && activitytypeisScheduled() && (if (Configuration.model_joint_actions) jointStatus != JointStatus.UNKNOWN else true)

    fun activitytypeisScheduled(): Boolean {
        return this.acttype != ActivityType.UNKNOWN
    }

    fun tripBeforeActivityisScheduled(): Boolean {
        return tripbeforeactivity?.isScheduled ?: false
    }

    fun tripAfterActivityisScheduled(): Boolean {
        return tripafteractivity?.isScheduled?:false
    }

    fun durationisScheduled(): Boolean {
        return duration != -1
    }

    fun startTimeisScheduled(): Boolean {
        return starttime != -1
    }

    /**
     * @param name
     * @param value
     */
    fun addAttributetoMap(name: String, value: Double) {
        assert(!attributes!!.containsKey(name)) { "attribute is already in map" }
        attributes!![name] = value
    }

    /**
     * @param name
     * @return
     */
    fun getAttributefromMap(name: String): Double? {
        return attributes!![name]
    }


    val attributesMap: DefaultDoubleMap<String>
        /**
         * @return the attributes
         */
        get() = DefaultDoubleMap(attributes)

    var creatorPersonIndex: Int
        /**
         * returns personindex of person that first created this activity.
         * if modeling joint actions this may be another person of the household that was modeled previous
         * in modeling order and that first created this joint activity.
         *
         * @return
         */
        get() {
            val result: Int = if (attributes!!.containsKey("CreatorPersonIndex")) {
                getAttributefromMap("CreatorPersonIndex")!!.toInt()
            } else {
                person.persIndex
            }
            assert(result != -1) { "could not determine CreatorPersonIndex!" }
            return result
        }
        /**
         * sets the personindex of person that first created this activity.
         *
         * @param persindex
         */
        set(persindex) {
            if (attributes!!.containsKey("CreatorPersonIndex")) {
                attributes!!.remove("CreatorPersonIndex")
            }
            attributes!!["CreatorPersonIndex"] = persindex.toDouble()
        }

    /**
     * @return the JointParticipants
     */
    fun getJointParticipants(): List<ActitoppPerson> {
        return jointParticipants
    }

    /**
     * @param gemJointParticipants the JointParticipants to set
     */
    fun setJointParticipants(gemJointParticipants: MutableList<ActitoppPerson>) {
        this.jointParticipants = gemJointParticipants
    }

    /**
     * @param person
     */
    fun addJointParticipant(person: ActitoppPerson) {
        jointParticipants.add(person)
    }


    /**
     * @param person
     */
    fun removeJointParticipant(person: ActitoppPerson) {
        jointParticipants.remove(person)

        // if there is no other jointParticipant left, remove jointStatus of the activity
        if (jointParticipants.size == 0) {jointStatus = (JointStatus.NOJOINTELEMENT) }
    }

    val defaultActivityTime: Int
        /**
         * determines a default activity time based on activity type and the amount of activities for this type on the specific day
         *
         *
         * empirical values are based on MEDIAN-values of duration per day [min] based on German Mobility Panel data
         * default activity time: defaulttime = empirical value / numberofactivites(type, day)
         *
         * @return
         */
        get() {
            var defaulttime = -1
            defaulttime = when (activityType) {
                ActivityType.WORK -> 472 / day.getTotalNumberOfActivitites(ActivityType.WORK)
                ActivityType.EDUCATION -> 340 / day.getTotalNumberOfActivitites(ActivityType.EDUCATION)

                ActivityType.LEISURE -> 130 / day.getTotalNumberOfActivitites(ActivityType.LEISURE)
                ActivityType.SHOPPING -> 41 / day.getTotalNumberOfActivitites(ActivityType.SHOPPING)
                ActivityType.TRANSPORT -> 15 / day.getTotalNumberOfActivitites(ActivityType.TRANSPORT)

                else -> 278 / day.totalAmountOfActivitites
            }
            assert(defaulttime != -1) { "could not determine defaultTime!" }
            return defaulttime
        }

    /* TODO these temporary fields should get removed when the other shenanigans with tripBeforeaActivity are cleaned up
         and the weekContext Field is killed by using Duration

     */
    private val startTimeNew: Int get() {
        return startTimeWeekContext - if (tripBeforeActivityisScheduled()) estimatedTripTimeBeforeActivity else 0
    }
    private val endTimeNew: Int get() {
        return (if (this.durationisScheduled()) this.endTimeWeekContext else this.startTimeWeekContext) +
        if (this.tripAfterActivityisScheduled()) this.estimatedTripTimeAfterActivity else 0

    }

    /**
     * overlap checks do not belong in the companion object, since you already have a comparison object.....
     */
    fun overlaps(other: HActivity) : Boolean {
        return startTimeNew < other.endTimeNew && endTimeNew > other.startTimeNew
    }
    companion object {
        /**
         * sorts list of activities ascending by week-order start time
         *
         * @param actList
         */
        fun sortActivityListbyWeekStartTimes(actList: MutableList<HActivity>) {
            actList.sortBy { it.startTimeWeekContext }

        }

        /**
         * sorts list of activities ascending by week-order indices
         *
         * @param list
         */
        fun sortActivityListbyIndices(list: List<HActivity>) {
            checkNotNull(list) { "list is empty" }

            Collections.sort(list) { o1, o2 ->
                var result = 99
                result = if (o1.dayIndex < o2.dayIndex) -1
                else if (o1.dayIndex > o2.dayIndex) +1
                else {
                    if (o1.tour!!.index < o2.tour!!.index) -1
                    else if (o1.tour!!.index > o2.tour!!.index) +1
                    else {
                        if (o1.index < o2.index) -1
                        else if (o1.index > o2.index) +1
                        else 0
                    }
                }

                assert(result != 99) { "Could not compare these two activities! - Act1: $o1 - Act2: $o2" }
                result
            }
        }

        /**
         * calculates duration between end of first activity (including trip after activity if there is one)
         * and the beginning of a second activity (including trip before activity if there is one)
         *
         * @param firstact
         * @param secondact
         * @return
         */
        fun getTimebetweenTwoActivities(firstact: HActivity, secondact: HActivity): Int {
            var result = -999999

            // end of first activity
            var endezeitersteakt = firstact.endTimeWeekContext
            if (firstact.isActivityLastinTour) {
                endezeitersteakt += firstact.estimatedTripTimeAfterActivity
            }

            // beginning second activity
            val anfangszeitzweiteakt = secondact.tripStartTimeBeforeActivityWeekContext


            result = anfangszeitzweiteakt - endezeitersteakt
            assert(result != -999999) { "Could not determine time between these two activities" }
            return result
        }


        /**
         * check if two activities are overlapping
         * false = no overlapping
         * true = overlapping
         *
         * @param act1
         * @param act2
         * @return
         */
        @Deprecated("This should never be used. Use act.overlaps(act2) instead")
        fun checkActivityOverlapping(act1: HActivity, act2: HActivity): Boolean {
            var result = false

            // check time occupation of first activity
            val starttime_first =
                act1.startTimeWeekContext - (if (act1.tripBeforeActivityisScheduled()) act1.estimatedTripTimeBeforeActivity else 0)
            val endtime_first =
                (if (act1.durationisScheduled()) act1.endTimeWeekContext else act1.startTimeWeekContext) + (if (act1.tripAfterActivityisScheduled()) act1.estimatedTripTimeAfterActivity else 0)

            // check time occupation of second activity
            val starttime_second =
                act2.startTimeWeekContext - (if (act2.tripBeforeActivityisScheduled()) act2.estimatedTripTimeBeforeActivity else 0)
            val endtime_second =
                (if (act2.durationisScheduled()) act2.endTimeWeekContext else act2.startTimeWeekContext) + (if (act2.tripAfterActivityisScheduled()) act2.estimatedTripTimeAfterActivity else 0)

            if ( // start or end of second activity is within time occupation of the first
                (starttime_second > starttime_first && starttime_second < endtime_first) ||
                (endtime_second > starttime_first && endtime_second < endtime_first)
                ||  // start or end of first activity is within time occupation of the second
                (starttime_first > starttime_second && starttime_first < endtime_second) ||
                (endtime_first > starttime_second && endtime_first < endtime_second)
            ) {
                result = true
            }

            return result
        }

        /**
         * create start times for activities in list when they can be determined
         *
         * @param actliste
         */
        fun createPossibleStarttimes(actliste: List<HActivity>) {
            for (act in actliste) {
                if (!act.startTimeisScheduled()) {
                    /*
                 * if the previous activity in the tour has a determined startime time and duration,
                 * also the actual activity can be determined concerning start time.
                 */
                    if (!act.isActivityFirstinTour && act.previousActivityinTour!!.startTimeisScheduled()
                        && act.previousActivityinTour!!.durationisScheduled()
                    ) {
                        act.startTime = act.previousActivityinTour!!.endTime + act.estimatedTripTimeBeforeActivity
                    }

                    /*
                 * if the following activity in the tour has a determined startime time and duration,
                 * also the actual activity can be determined concerning start time.
                 */
                    if (!act.isActivityLastinTour && act.durationisScheduled()
                        && act.nextActivityinTour!!.startTimeisScheduled()
                    ) {
                        act.startTime = act.nextActivityinTour!!.tripStartTimeBeforeActivity - act.duration
                    }
                }
            }
        }
    }
}
