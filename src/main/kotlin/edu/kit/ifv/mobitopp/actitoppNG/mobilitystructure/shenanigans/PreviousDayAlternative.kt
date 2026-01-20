package edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.shenanigans


import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitoppNG.modernization.DayStructure
import edu.kit.ifv.mobitopp.actitoppNG.modernization.DurationDay
import edu.kit.ifv.mobitopp.actitoppNG.modernization.ModifiablePlannedTourAmounts
import edu.kit.ifv.mobitopp.actitoppNG.modernization.PlannedTourAmounts
import edu.kit.ifv.mobitopp.actitoppNG.steps.DayAttributesFromStructure
import edu.kit.ifv.mobitopp.actitoppNG.steps.DayStructureAttributes
import edu.kit.ifv.mobitopp.actitoppNG.steps.HouseholdAttributes
import edu.kit.ifv.mobitopp.actitoppNG.steps.HouseholdAttributesFromElement
import edu.kit.ifv.mobitopp.actitoppNG.steps.PartialTourLayoutAttributes


class PlannedTourMap(
    private val mapping: Map<DurationDay, ModifiablePlannedTourAmounts>,
) {
    constructor(initialDayStructures: Collection<DayStructure>) : this(
        initialDayStructures.associate { it.startTimeDay to ModifiablePlannedTourAmounts() }
    )

    fun getPreviousPlannedTourAmounts(day: DayStructure): PlannedTourAmounts? {
        val lowerEntry = mapping[day.startTimeDay.previous()]
        return lowerEntry
    }

    fun getModifiablePlannedTourAmounts(day: DayStructure): ModifiablePlannedTourAmounts {
        return mapping.getOrElse(day.startTimeDay) { throw NoSuchElementException("There is no day in $mapping") }
    }

    operator fun get(day: DayStructure): ModifiablePlannedTourAmounts? {
        return mapping[day.startTimeDay]
    }

    fun readOnly(): Map<DurationDay, PlannedTourAmounts> = mapping
}

class PreviousDayAlternative private constructor(
    val previousDayAttributes: PreviousDayAttributes,
    val pAttr: PersonAndRoutineAttributes,
    val plannedTourAttributes: PartialTourLayoutAttributes,
    val structureAttributes: DayStructureAttributes,
    val householdAttributes: HouseholdAttributes,
) : PreviousDayAttributes by previousDayAttributes,
    PersonAndRoutineAttributes by pAttr, PartialTourLayoutAttributes by plannedTourAttributes,
    DayStructureAttributes by structureAttributes,
    HouseholdAttributes by householdAttributes {


    constructor(
        day: DayStructure,
        previousResults: PlannedTourAmounts?,
        personWithRoutine: PersonWithRoutine,
        plannedPrecursorTours: Int,
    ) : this(
        previousDayAttributes = PreviousDayAttributesNumeric(
            dayStructure = day,
            plannedTourAmounts = previousResults
        ),
        pAttr = PersonAndRoutineFrom(personWithRoutine),
        plannedTourAttributes = PartialTourLayoutAttributes { plannedPrecursorTours },
        structureAttributes = DayAttributesFromStructure(day),
        householdAttributes = HouseholdAttributesFromElement(personWithRoutine.household)
    )


}