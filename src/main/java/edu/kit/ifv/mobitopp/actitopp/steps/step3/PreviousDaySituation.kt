package edu.kit.ifv.mobitopp.actitopp.steps.step3

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.HDay
import edu.kit.ifv.mobitopp.actitopp.WeekRoutine
import edu.kit.ifv.mobitopp.actitopp.amountOfPreviousTours
import edu.kit.ifv.mobitopp.actitopp.modernization.DayStructure
import edu.kit.ifv.mobitopp.actitopp.modernization.DurationDay
import edu.kit.ifv.mobitopp.actitopp.modernization.ModifiablePlannedTourAmounts
import edu.kit.ifv.mobitopp.actitopp.modernization.PlannedTourAmounts
import edu.kit.ifv.mobitopp.actitopp.steps.DayAttributesFromElement
import edu.kit.ifv.mobitopp.actitopp.steps.DayAttributesFromStructure
import edu.kit.ifv.mobitopp.actitopp.steps.DayStructureAttributes
import edu.kit.ifv.mobitopp.actitopp.steps.PartialTourLayoutAttributes
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonAndRoutineAttributes
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonAndRoutineFrom
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ChoiceSituation



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
    val previousDayAttributes: PreviousDayAttributes,
    val pAttr: PersonAndRoutineAttributes,
    val plannedTourAttributes: PartialTourLayoutAttributes,
    val structureAttributes: DayStructureAttributes,
) : ChoiceSituation<Int>(), PreviousDayAttributes by previousDayAttributes,
    PersonAndRoutineAttributes by pAttr, PartialTourLayoutAttributes by plannedTourAttributes, DayStructureAttributes by structureAttributes {
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
        PartialTourLayoutAttributes{day.amountOfPreviousTours()},
        DayAttributesFromElement(day)
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
        plannedTourAttributes = PartialTourLayoutAttributes {plannedPrecursorTours},
        structureAttributes = DayAttributesFromStructure(day),
    )


}