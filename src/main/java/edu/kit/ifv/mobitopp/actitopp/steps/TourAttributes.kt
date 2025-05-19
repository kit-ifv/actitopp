package edu.kit.ifv.mobitopp.actitopp.steps

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.HDay
import edu.kit.ifv.mobitopp.actitopp.HTour
import edu.kit.ifv.mobitopp.actitopp.WeekRoutine
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.modernization.BidirectionalIndexedValue
import edu.kit.ifv.mobitopp.actitopp.modernization.Position
import edu.kit.ifv.mobitopp.actitopp.modernization.TourStructure
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonWithRoutine
import org.jetbrains.annotations.TestOnly

interface TourPositionAttributes {
    // These are all day attributes
    fun isFirstTourOfDay(): Boolean
    fun isSecondTourOfDay(): Boolean
    fun isThirdTourOfDay(): Boolean
    fun isBeforeMainTour(): Boolean
    fun isAfterMainTour(): Boolean
}

interface ActivityAmountAttributes {
    fun numActivitiesBeforeMainActivityIs1(): Boolean
    fun numActivitiesBeforeMainActivityIs2(): Boolean
    fun numActivitiesBeforeMainActivityIs3(): Boolean
}

class ActivityAmountByNumber(val element: Int) : ActivityAmountAttributes {
    override fun numActivitiesBeforeMainActivityIs1(): Boolean {
        return element == 1
    }

    override fun numActivitiesBeforeMainActivityIs2(): Boolean {
        return element == 2
    }

    override fun numActivitiesBeforeMainActivityIs3(): Boolean {
        return element == 3
    }
}

interface TourAttributes : TourPositionAttributes {

    fun tourMainActivityIsWork(): Boolean
    fun tourMainActivityIsEducation(): Boolean
    fun tourMainActivityIsShopping(): Boolean
    fun tourMainActivityIsTransport(): Boolean


    fun tourHas2Activities(): Boolean
    fun tourHas3Activities(): Boolean
    fun tourHas4Activities(): Boolean
}

class TourPositionAttributesByIndex(val absoluteIndex: Int, val position: Position) : TourPositionAttributes {
    override fun isFirstTourOfDay(): Boolean {
        return absoluteIndex == 0
    }

    override fun isSecondTourOfDay(): Boolean {
        return absoluteIndex == 1
    }

    override fun isThirdTourOfDay(): Boolean {
        return absoluteIndex == 2
    }

    override fun isBeforeMainTour(): Boolean {
        return position == Position.BEFORE
    }

    override fun isAfterMainTour(): Boolean {
        return position == Position.AFTER
    }
}

class TourAttributesByStructAndNumbers(
    private val indexedTour: BidirectionalIndexedValue<TourStructure>,
    val precursorActivityCount: Int,
    val successorActivityCount:Int ,

) : TourAttributes, TourPositionAttributes,  ActivityAmountAttributes{
    override fun tourMainActivityIsWork(): Boolean {
        return indexedTour.element.mainActivityType() == ActivityType.WORK
    }

    override fun tourMainActivityIsEducation(): Boolean {
        return indexedTour.element.mainActivityType() == ActivityType.EDUCATION
    }

    override fun tourMainActivityIsShopping(): Boolean {
        return indexedTour.element.mainActivityType() == ActivityType.SHOPPING
    }

    override fun tourMainActivityIsTransport(): Boolean {
        return indexedTour.element.mainActivityType() == ActivityType.TRANSPORT
    }

    override fun tourHas2Activities(): Boolean {
        return indexedTour.element.elements().size + precursorActivityCount + successorActivityCount == 2
    }

    override fun tourHas3Activities(): Boolean {
        return indexedTour.element.elements().size + precursorActivityCount + successorActivityCount == 3
    }

    override fun tourHas4Activities(): Boolean {
        return indexedTour.element.elements().size + precursorActivityCount + successorActivityCount == 4
    }

    override fun isFirstTourOfDay(): Boolean {
        return indexedTour.absoluteIndex == 0
    }

    override fun isSecondTourOfDay(): Boolean {
        return indexedTour.absoluteIndex == 1
    }

    override fun isThirdTourOfDay(): Boolean {
        return indexedTour.absoluteIndex == 2
    }

    override fun isBeforeMainTour(): Boolean {
        return indexedTour.position == Position.BEFORE
    }

    override fun isAfterMainTour(): Boolean {
        return indexedTour.position == Position.AFTER
    }

    override fun numActivitiesBeforeMainActivityIs1(): Boolean {
        return indexedTour.element.amountOfPrecursorElements() + precursorActivityCount == 1
    }

    override fun numActivitiesBeforeMainActivityIs2(): Boolean {
        return indexedTour.element.amountOfPrecursorElements() + precursorActivityCount == 2
    }

    override fun numActivitiesBeforeMainActivityIs3(): Boolean {
        return indexedTour.element.amountOfPrecursorElements() + precursorActivityCount ==3
    }
}

class TourAttributesByIndexedStructure(private val indexedTour: BidirectionalIndexedValue<TourStructure>) :
    TourAttributes, TourPositionAttributes, ActivityAmountAttributes {

    override fun isFirstTourOfDay(): Boolean {
        return indexedTour.absoluteIndex == 0
    }

    override fun isSecondTourOfDay(): Boolean {
        return indexedTour.absoluteIndex == 1
    }

    override fun isThirdTourOfDay(): Boolean {
        return indexedTour.absoluteIndex == 2
    }

    override fun isBeforeMainTour(): Boolean {
        return indexedTour.position == Position.BEFORE
    }

    override fun isAfterMainTour(): Boolean {
        return indexedTour.position == Position.AFTER
    }

    override fun tourMainActivityIsWork(): Boolean {
        return indexedTour.element.mainActivityType() == ActivityType.WORK
    }

    override fun tourMainActivityIsEducation(): Boolean {
        return indexedTour.element.mainActivityType() == ActivityType.EDUCATION
    }

    override fun tourMainActivityIsShopping(): Boolean {
        return indexedTour.element.mainActivityType() == ActivityType.SHOPPING
    }

    override fun tourMainActivityIsTransport(): Boolean {
        return indexedTour.element.mainActivityType() == ActivityType.TRANSPORT
    }

    override fun numActivitiesBeforeMainActivityIs1(): Boolean {
        return indexedTour.element.amountOfPrecursorElements() == 1
    }

    override fun numActivitiesBeforeMainActivityIs2(): Boolean {
        return indexedTour.element.amountOfPrecursorElements() == 2
    }

    override fun numActivitiesBeforeMainActivityIs3(): Boolean {
        return indexedTour.element.amountOfPrecursorElements() == 3
    }

    override fun tourHas2Activities(): Boolean {
        return indexedTour.element.amountOfElements() == 2
    }

    override fun tourHas3Activities(): Boolean {
        return indexedTour.element.amountOfElements() == 3
    }

    override fun tourHas4Activities(): Boolean {
        return indexedTour.element.amountOfElements() == 4
    }

}

class TourAttributesByElement(val element: HTour) : TourAttributes, TourPositionAttributes, ActivityAmountAttributes {
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
//    override fun mainActivityIsWork(): Boolean {
//        return element.mainActivity()?.activityType == ActivityType.WORK
//    }
//
//    override fun mainActivityIsEducation(): Boolean {
//        return element.mainActivity()?.activityType == ActivityType.EDUCATION
//    }
//
//    override fun mainActivityIsShopping(): Boolean {
//        return element.mainActivity()?.activityType == ActivityType.SHOPPING
//    }
//
//    override fun mainActivityIsTransport(): Boolean {
//        return element.mainActivity()?.activityType == ActivityType.TRANSPORT
//    }
}

data class SubTourInput(
    val personWithRoutine: PersonWithRoutine,
    val day: HDay,
) {
    val person = personWithRoutine.person
    val routine = personWithRoutine.routine
//    val tracker = personWithRoutine.tracker

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


