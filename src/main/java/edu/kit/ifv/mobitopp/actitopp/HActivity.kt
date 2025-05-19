package edu.kit.ifv.mobitopp.actitopp

import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.enums.JointStatus
import edu.kit.ifv.mobitopp.actitopp.enums.TripStatus
import java.time.DayOfWeek
import kotlin.math.max
import kotlin.math.min


/**
 * @author Tim Hilgert
 */
class HActivity @JvmOverloads constructor(
    parent: HTour,
    val index: Int,
    acttype: ActivityType = ActivityType.UNKNOWN,
    var jointStatus: JointStatus = JointStatus.UNKNOWN,
    var duration: Int = -1,
    starttime: Int = -1
) : Comparable<HActivity> {


    constructor(parent: HDay, type: ActivityType, duration: Int, starttime: Int): this(HTour(parent, -1), -1, acttype= type, duration = duration, starttime = starttime)

    val day: HDay = parent.day
    private val attributes: MutableMap<String, Double> = mutableMapOf()

    var creatorPersonIndex: Int = parent.day.person.persIndex

    val tour: HTour = parent

    /**
     * Start Time is a field that is badly set to -1 as invalid point, I have yet to figure out what it means
     */
    var startTime: Int = starttime
        set(starttime) {
            require(starttime >= 0) { "starttime is not >0: $starttime" }
            field = starttime
        }

    var activityType: ActivityType = acttype
        get() {
            if (!person.isAllowedToWork) assert(field != ActivityType.WORK) { "person is not allowed to work!" }
            return field
        }
        set(acttype) {
            if (!person.isAllowedToWork) assert(acttype != ActivityType.WORK) { "person is not allowed to work!" }
            field = acttype
        }


    /**
     * A nullable field for some reason, it is set if joint modelling is active
     */
    var tripbeforeactivity: HTrip? = null
    /**
     * @return the tripafteractivity
     */
    /**
     * @param tripafteractivity the tripafteractivity to set
     */
    var tripafteractivity: HTrip? = null
    private set


    private var jointParticipants: MutableList<ActitoppPerson> = ArrayList()


    val weekPattern: HWeekPattern get() = day.pattern

    val person: ActitoppPerson
        get() = day.person





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
     * TODO figure out if there is ever a scenario where an activity has no known start / end time and this weird
     *   index comparison shenanigans is actually necessary - The code suggests that this "could" happen, but inspections
     *   show no such occurrence.
     * @param other
     * @return
     */
    override fun compareTo(other: HActivity): Int {

        require(weekDay == other.weekDay) {
            "weekDay != other.weekDay"
        }

        return compareValuesBy(this, other, { it.weekDay }, { it.tour.index }, { it.index })
    }


    override fun toString(): String {
        var result = ""

        result = if (isHomeActivity) {
            dayIndex.toString() +
                    " start " + (if (startTimeisScheduled()) startTimeWeekContext else "n.a.") +
                    " end " + (if (startTimeisScheduled() && durationisScheduled()) endTimeWeekContext else "n.a.") +
                    " duration: " + (if (durationisScheduled()) duration else "n.a.") +
                    " type: " + (if (activityTypeIsSpecified()) activityType.typeasChar else "n.a.") +
                    " jointStatus: " + this.jointStatus
        } else {
            dayIndex.toString() + "/" + tour.index + "/" + index +
                    " start " + (if (startTimeisScheduled()) startTimeWeekContext else "n.a.") +
                    " end " + (if (startTimeisScheduled() && durationisScheduled()) endTimeWeekContext else "n.a.") +
                    " duration: " + (if (durationisScheduled()) duration else "n.a.") +
                    " type: " + (if (activityTypeIsSpecified()) activityType.typeasChar else "n.a.") +
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
    fun hasWorkCommutingTripAsFirstActivity(): Boolean {
        return (isActivityFirstinTour && activityType == ActivityType.WORK && (person.commutingdistance_work != 0.0))
    }

    /**
     * commuting trips AFTER are trips
     * - that end at home
     * - that start at work (activity type is home)
     *
     * @return
     */
    fun hasWorkCommutingTripAsLastActivity(): Boolean {
        return (isActivityLastinTour && activityType == ActivityType.WORK && (person.commutingdistance_work != 0.0))
    }

    /**
     * commuting trips BEFORE are trips
     * - that start at home
     * - that end at school/university (activity type is education)
     *
     * @return
     */
    fun hasEducationCommutingTripAsFirstActivity(): Boolean {
        return (isActivityFirstinTour && activityType == ActivityType.EDUCATION && (person.commutingdistance_education != 0.0))
    }

    /**
     * commuting trips AFTER are trips
     * - that end at home
     * - that start at school/university (activity type is home)
     *
     * @return
     */
    fun hasEducationCommutingTripAsLastActivity(): Boolean {
        return (isActivityLastinTour && activityType == ActivityType.EDUCATION && (person.commutingdistance_education != 0.0))
    }

    val isHomeActivity: Boolean
        /**
         * @return
         */
        get() = (activityTypeIsSpecified() && activityType == ActivityType.HOME)

    val isMainActivityoftheTour: Boolean = this.index == 0

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




    val endTime: Int
        /**
         * starttime + duration
         *
         * @return
         */
        get() = startTime + duration

    val weekDay: DayOfWeek = day.weekday
    // Can be held as field, since day does not change (anymore)
    val dayIndex: Int = day.index
    // Can be held, cause immutability
    val tourIndex: Int = tour.index

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
     * TODO why is this located on the object?, just to get the activity Type? There is no reason why this method should
     *   be specific to each activity. Every activity of the same type will produce the same result, when in the same pattern
     *
     * @return
     */
    fun calculateMeanTime(): Int {
        val timebudget = person.getAttributefromMap(activityType.toString() + "budget_exact")
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
            if (meantime >= Configuration.ACT_TIME_TIMECLASSES_LB[i] &&
                meantime <= Configuration.ACT_TIME_TIMECLASSES_UB[i])
                meantimecategory = i
        }
        assert(meantimecategory != -99) { "could not determine category!" }
        return meantimecategory
    }

    /**
     * calculates trip times (default or based on commuting distances)
     */
    fun createTripsforActivity() {

        require(tripbeforeactivity == null) {
            "Fat statement, assume that tripbeforeactivity has never been set before"
        }

        val actualTripTime = when {
            hasWorkCommutingTripAsFirstActivity() -> person.commutingDuration_work
            hasEducationCommutingTripAsFirstActivity() -> person.commutingDuration_education
            else -> Configuration.FIXED_TRIP_TIME_ESTIMATOR.inWholeMinutes.toInt()
        }
        tripbeforeactivity = HTrip(this, TripStatus.TRIP_BEFORE_ACT, actualTripTime)

        /*
         * trip AFTER activity
         */
        // TODO solve the modelling of beforeactivity/afteractivity by removing the concept: An activity has a preceding
        //   trip and a postceding trip, irregards of the position in the activity plan. :O
        if (isActivityLastinTour) {

            val badModelingTripTime = when {
                hasWorkCommutingTripAsLastActivity() -> person.commutingDuration_work
                hasEducationCommutingTripAsLastActivity() -> person.commutingDuration_education
                else -> Configuration.FIXED_TRIP_TIME_ESTIMATOR.inWholeMinutes.toInt()
            }
            require(tripafteractivity == null) {
                "Fat statement, I claim that this field is always null when we get here."
            }
            tripafteractivity = HTrip(this, TripStatus.TRIP_AFTER_ACT, badModelingTripTime)

        }
    }

    val hasScheduledDuration: Boolean
        get() = durationisScheduled() && startTimeisScheduled() && activityTypeIsSpecified() && (if (Configuration.modelJointActions) jointStatus != JointStatus.UNKNOWN else true)

    /**
     * The original method was called activitytypeIsScheduled, which is overlapping with the legaycy isScheduled () method.
     * Since these two have different behaviour, we renamed this function to activity type is specified, since this method
     * is only used to check whether the activity type is not unknown. In the future perhaps the activityType.Unknown could
     * be removed. In that instance this method would become obsolete
     */
    fun activityTypeIsSpecified(): Boolean {
        return this.activityType != ActivityType.UNKNOWN
    }

    fun tripBeforeActivityisScheduled(): Boolean {
        return tripbeforeactivity?.isScheduled ?: false
    }

    fun tripAfterActivityisScheduled(): Boolean {
        return tripafteractivity?.isScheduled ?: false
    }

    fun durationisScheduled(): Boolean {
        return duration != -1
    }

    fun startTimeisScheduled(): Boolean {
        return startTime != -1
    }

    /**
     * @param name
     * @param value
     */
    fun addAttributetoMap(name: String, value: Double) {
        assert(!attributes.containsKey(name)) { "attribute is already in map" }
        attributes[name] = value
    }

    /**
     * @param name
     * @return
     */
    fun getAttributefromMap(name: String): Double? {
        return attributes[name]
    }


    val attributesMap: DefaultDoubleMap<String>
        /**
         * @return the attributes
         */
        get() = DefaultDoubleMap(attributes)


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
        if (jointParticipants.size == 0) {
            jointStatus = (JointStatus.NOJOINTELEMENT)
        }
    }

    val defaultActivityTime: Int get()= activityType.defaultActivityTime / day.getTotalNumberOfActivitites(activityType)

    /* TODO these temporary fields should get removed when the other shenanigans with tripBeforeaActivity are cleaned up
         and the weekContext Field is killed by using Duration

     */
    private val startTimeNew: Int
        get() {
            return startTimeWeekContext - if (tripBeforeActivityisScheduled()) estimatedTripTimeBeforeActivity else 0
        }
    private val endTimeNew: Int
        get() {
            return (if (this.durationisScheduled()) this.endTimeWeekContext else this.startTimeWeekContext) +
                    if (this.tripAfterActivityisScheduled()) this.estimatedTripTimeAfterActivity else 0

        }

    /**
     * overlap checks do not belong in the companion object, since you already have a comparison object.....
     */
    fun overlaps(other: HActivity): Boolean {
        return startTimeNew < other.endTimeNew && endTimeNew > other.startTimeNew
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

    companion object {

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
         * (Spoiler: They can never be determined)
         * @param actliste
         */
        @Deprecated("This method does absolutely nothing...")
        fun createPossibleStarttimes(actliste: Collection<HActivity>) {
            for (act in actliste) {
                require(!act.startTimeisScheduled()) {
                    "Here I am uncertain - The code should trigger sometimes I hope"
                }

                if (!act.startTimeisScheduled()) {
                    /*
                 * if the previous activity in the tour has a determined startime time and duration,
                 * also the actual activity can be determined concerning start time.
                 */
                    if (!act.isActivityFirstinTour && act.previousActivityinTour!!.startTimeisScheduled()
                        && act.previousActivityinTour!!.durationisScheduled()
                    ) {
                        require(false) {
                            "I hereby proclaim, this cannot happen"
                        }
                        act.startTime = act.previousActivityinTour!!.endTime + act.estimatedTripTimeBeforeActivity
                    }

                    /*
                 * if the following activity in the tour has a determined startime time and duration,
                 * also the actual activity can be determined concerning start time.
                 */
                    if (!act.isActivityLastinTour && act.durationisScheduled()
                        && act.nextActivityinTour!!.startTimeisScheduled()
                    ) {
                        require(false) {
                            "I hereby proclaim, this cannot happen"
                        }
                        act.startTime = act.nextActivityinTour!!.tripStartTimeBeforeActivity - act.duration
                    }
                }
            }
        }
    }
}
