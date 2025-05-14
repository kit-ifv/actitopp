package edu.kit.ifv.mobitopp.actitopp.steps.step3

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.HDay
import edu.kit.ifv.mobitopp.actitopp.WeekRoutine
import edu.kit.ifv.mobitopp.actitopp.amountOfPreviousTours
import edu.kit.ifv.mobitopp.actitopp.modernization.DayStructure
import edu.kit.ifv.mobitopp.actitopp.modernization.DurationDay
import edu.kit.ifv.mobitopp.actitopp.modernization.ModifiablePlannedTourAmounts
import edu.kit.ifv.mobitopp.actitopp.modernization.PlannedTourAmounts
import edu.kit.ifv.mobitopp.actitopp.steps.PartialTourLayoutAttributes
import edu.kit.ifv.mobitopp.actitopp.steps.PlannedTourAttributes
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonAndRoutineAttributes
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonAndRoutineFrom
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ChoiceSituation

interface DayTourPlanAndPreviousTourPlan : PreviousDayAttributes, PlannedTourAttributes

class PlannedTourMap(
    private val mapping : Map<DurationDay, ModifiablePlannedTourAmounts>
) {
    constructor(initialDayStructures: Collection<DayStructure>) : this(
        initialDayStructures.associate {it.startTimeDay to ModifiablePlannedTourAmounts()}
    )
    fun getPreviousPlannedTourAmounts(day: DayStructure): PlannedTourAmounts? {
        val lowerEntry = mapping[day.startTimeDay.previous()]
        return lowerEntry
    }
    fun getModifiablePlannedTourAmounts(day: DayStructure): ModifiablePlannedTourAmounts {
        return mapping.getOrElse(day.startTimeDay) {throw NoSuchElementException("There is no day in $mapping")}
    }
    operator fun get(day: DayStructure): ModifiablePlannedTourAmounts? {
        return mapping[day.startTimeDay]
    }
    fun getCurrentPlannedPrecursorTours(day: DayStructure) = getModifiablePlannedTourAmounts(day).precursorAmount

    fun readOnly(): Map<DurationDay, PlannedTourAmounts> = mapping
}

class PreviousDaySituation private constructor(
    override val choice: Int,
    val previousDayAttributes: DayTourPlanAndPreviousTourPlan,
    val pAttr: PersonAndRoutineAttributes,
    val plannedTourAttributes: PartialTourLayoutAttributes,
) : ChoiceSituation<Int>(), DayTourPlanAndPreviousTourPlan by previousDayAttributes,
    PersonAndRoutineAttributes by pAttr, PartialTourLayoutAttributes by plannedTourAttributes {
    constructor(
        choice: Int,
        day: HDay,
        previousDayBeforeTours: Int?,
        previousDayAfterTours: Int?,
        person: ActitoppPerson,
        weekRoutine: WeekRoutine,
    ) : this(
        choice, PreviousDayAttributesNumeric(day, previousDayBeforeTours, previousDayAfterTours), PersonAndRoutineFrom(
            PersonWithRoutine(person, weekRoutine),

        ),
        PartialTourLayoutAttributes{day.amountOfPreviousTours()}
    )

    constructor(
        choice: Int,
        day: DayStructure,
        previousResults: PlannedTourAmounts?,
        personWithRoutine: PersonWithRoutine,
        plannedPrecursorTours: Int,
    ) : this(
        choice = choice,
        previousDayAttributes = PreviousDayAttributesNumeric(
            dayStructure = day,
            plannedTourAmounts = previousResults
        ),
        pAttr = PersonAndRoutineFrom(personWithRoutine),
        plannedTourAttributes = PartialTourLayoutAttributes {plannedPrecursorTours}
    )


}