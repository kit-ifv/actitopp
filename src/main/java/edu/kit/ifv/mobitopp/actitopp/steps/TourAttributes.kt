package edu.kit.ifv.mobitopp.actitopp.steps

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.HDay
import edu.kit.ifv.mobitopp.actitopp.HTour
import edu.kit.ifv.mobitopp.actitopp.WeekRoutine
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonWithRoutine
import org.jetbrains.annotations.TestOnly

interface TourAttributes {
    fun isFirstTourOfDay(): Boolean
    fun isSecondTourOfDay(): Boolean
    fun isThirdTourOfDay(): Boolean
    fun isBeforeMainTour(): Boolean
    fun isAfterMainTour(): Boolean

    fun tourMainActivityIsWork(): Boolean
    fun tourMainActivityIsEducation(): Boolean
    fun tourMainActivityIsShopping(): Boolean
    fun tourMainActivityIsTransport(): Boolean

    fun numActivitiesBeforeMainActivityIs1(): Boolean
    fun numActivitiesBeforeMainActivityIs2(): Boolean
    fun numActivitiesBeforeMainActivityIs3(): Boolean

    fun tourHas2Activities(): Boolean
    fun tourHas3Activities(): Boolean
    fun tourHas4Activities(): Boolean
}

class TourAttributesByElement(val element: HTour) : TourAttributes {
    override fun isFirstTourOfDay(): Boolean = element.index == element.day.lowestTourIndex
    override fun isSecondTourOfDay(): Boolean = element.index == element.day.lowestTourIndex + 1
    override fun isThirdTourOfDay(): Boolean = element.index == element.day.lowestTourIndex + 2
    override fun isBeforeMainTour(): Boolean = element.index < 0
    override fun isAfterMainTour(): Boolean = element.index > 0
    override fun tourMainActivityIsWork(): Boolean = element.mainActivity()?.activityType == ActivityType.WORK
    override fun tourMainActivityIsEducation(): Boolean = element.mainActivity()?.activityType == ActivityType.EDUCATION
    override fun tourMainActivityIsShopping(): Boolean = element.mainActivity()?.activityType == ActivityType.SHOPPING
    override fun tourMainActivityIsTransport(): Boolean = element.mainActivity()?.activityType == ActivityType.TRANSPORT

    override fun numActivitiesBeforeMainActivityIs1(): Boolean = element.lowestActivityIndex == -1
    override fun numActivitiesBeforeMainActivityIs2(): Boolean = element.lowestActivityIndex == -2
    override fun numActivitiesBeforeMainActivityIs3(): Boolean = element.lowestActivityIndex == -3

    override fun tourHas2Activities(): Boolean = element.amountOfActivities == 2
    override fun tourHas3Activities(): Boolean = element.amountOfActivities == 3
    override fun tourHas4Activities(): Boolean = element.amountOfActivities == 4
}

data class SubTourInput(
    val personWithRoutine: PersonWithRoutine,
    val day: HDay,
) {
    val person = personWithRoutine.person
    val routine = personWithRoutine.routine
    val tracker = personWithRoutine.tracker
    constructor(
        person: ActitoppPerson,
        routine: WeekRoutine, day: HDay,
    ) : this(PersonWithRoutine(person, routine), day)
}

/**
 * Keep track of the days that have a work/education activity.
 */
class DayActivityTracker(
    private val targetWorkingDays: Int,
    private val targetEducationDays: Int,
    workingDays: Set<HDay>,
    educationDays: Set<HDay>,
) : ActivityTypeFilter {
    constructor(person: ActitoppPerson, routine: WeekRoutine) : this(
        routine.amountOfWorkingDays,
        routine.amountOfEducationDays,
        person.days().filter { it.hasActivity(ActivityType.WORK) }.toSet(),
        person.days().filter { it.hasActivity(ActivityType.EDUCATION) }.toSet(),
    )

    private val workingDays: MutableSet<HDay> = workingDays.toMutableSet()
    private val educationDays: MutableSet<HDay> = educationDays.toMutableSet()

    @TestOnly
    fun addWorkday(day: HDay) = workingDays.add(day)

    @TestOnly
    fun addEducationDay(day: HDay) = educationDays.add(day)

    private fun hasReachedWorkQuota(): Boolean {
        return targetWorkingDays - workingDays.size <= 0
    }

    private fun hasReachedEducationQuota(): Boolean {
        return targetEducationDays - educationDays.size <= 0
    }

    fun HDay.shouldNotBeWork(): Boolean {
        return this !in workingDays && hasReachedWorkQuota()
    }

    fun HDay.shouldNotBeEducation(): Boolean {
        return this !in educationDays && hasReachedEducationQuota()
    }

    fun determineActivityTypes(
        input: SubTourInput,
        lambda: DayActivityTracker.(HTour) -> ActivityType,
    ): List<ActivityType> {
        val day = input.day

        return day.tours.filter { !it.mainActivityHasType() }.map {
            lambda.invoke(this, it).also { activity ->
                when (activity) {
                    ActivityType.WORK -> workingDays.add(day)
                    ActivityType.EDUCATION -> educationDays.add(day)
                    else -> {}
                }
            }
        }
    }

    fun determineMainActivities(
        input: PersonWithRoutine,
        day: HDay,
        determineActivity: DayActivityTracker.() -> ActivityType,
    ): ActivityType {
        return determineActivity().also { activity ->
            when (activity) {
                ActivityType.WORK -> workingDays.add(day)
                ActivityType.EDUCATION -> educationDays.add(day)
                else -> {}
            }
        }
    }

    override fun filter(day: HDay): Set<ActivityType> {
        val options = ActivityType.FULLSET.toMutableSet()
        if (day.shouldNotBeWork()) options.remove(ActivityType.WORK)
        if (day.shouldNotBeEducation()) options.remove(ActivityType.EDUCATION)

        return options
    }
}

interface ActivityTypeFilter {
    fun filter(day: HDay): Set<ActivityType>
}


