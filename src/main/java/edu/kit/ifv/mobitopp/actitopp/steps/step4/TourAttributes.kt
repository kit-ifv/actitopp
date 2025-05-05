package edu.kit.ifv.mobitopp.actitopp.steps.step4

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.HDay
import edu.kit.ifv.mobitopp.actitopp.HTour
import edu.kit.ifv.mobitopp.actitopp.RNGHelper
import edu.kit.ifv.mobitopp.actitopp.WeekRoutine
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.steps.scrapPath.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitopp.steps.step3.TourSituation
import edu.kit.ifv.mobitopp.actitopp.steps.step3.step4WithParams
import org.jetbrains.annotations.TestOnly

interface TourAttributes {
    fun isFirstTourOfDay(): Boolean
    fun isSecondTourOfDay(): Boolean
    fun isThirdTourOfDay(): Boolean
    fun isBeforeMainTour(): Boolean
}

class TourAttributesByElement(val element: HTour) : TourAttributes {
    override fun isFirstTourOfDay(): Boolean = element.index == element.day.lowestTourIndex
    override fun isSecondTourOfDay(): Boolean = element.index == element.day.lowestTourIndex + 1
    override fun isThirdTourOfDay(): Boolean = element.index == element.day.lowestTourIndex + 2
    override fun isBeforeMainTour(): Boolean = element.index < 0
}

data class SideTourInput(
    val person: ActitoppPerson,
    val routine: WeekRoutine,
    val day: HDay,
    val tracker: DayActivityTracker,
) {


}

fun interface GenerateSideActivityTypes {
    fun generate(input: SideTourInput): List<ActivityType>
    fun generate(person: ActitoppPerson, routine: WeekRoutine, day: HDay) =
        generate(SideTourInput(person, routine, day, DayActivityTracker(person, routine)))
//    fun generate(person: ActitoppPerson, routine: WeekRoutine, day: HDay): List<ActivityType> = generate(SideTourInput(person, routine, day))
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
        input: SideTourInput,
        lambda: DayActivityTracker.(HTour) -> ActivityType,
    ): List<ActivityType> {
        val day = input.day

        return day.tours.map {
            lambda.invoke(this, it).also { activity ->
                when (activity) {
                    ActivityType.WORK -> workingDays.add(day)
                    ActivityType.EDUCATION -> educationDays.add(day)
                    else -> {}
                }
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


class SideActivityDeterminer(val rngHelper: RNGHelper) : GenerateSideActivityTypes {
    override fun generate(input: SideTourInput): List<ActivityType> {


        val tracker = input.tracker
        val output = tracker.determineActivityTypes(input) { tour ->
            val day = tour.day
            val availableOptions = step4WithParams.registeredOptions().toMutableSet()
            if (!input.person.isAllowedToWork) availableOptions.remove(ActivityType.WORK)
            if (day.shouldNotBeWork()) availableOptions.remove(ActivityType.WORK)
            if (day.shouldNotBeEducation()) availableOptions.remove(ActivityType.EDUCATION)

            step4WithParams.select(availableOptions, rngHelper.randomValue) {
                TourSituation(it, input.person, input.routine, input.day, tour)
            }

        }
        return output


    }
}