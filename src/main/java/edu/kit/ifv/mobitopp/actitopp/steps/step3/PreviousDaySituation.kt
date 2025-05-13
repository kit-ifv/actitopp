package edu.kit.ifv.mobitopp.actitopp.steps.step3

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.HDay
import edu.kit.ifv.mobitopp.actitopp.WeekRoutine
import edu.kit.ifv.mobitopp.actitopp.amountOfPreviousTours
import edu.kit.ifv.mobitopp.actitopp.modernization.DayStructure
import edu.kit.ifv.mobitopp.actitopp.modernization.PlannedTourAmounts
import edu.kit.ifv.mobitopp.actitopp.steps.DayAttributes
import edu.kit.ifv.mobitopp.actitopp.steps.PartialTourLayoutAttributes
import edu.kit.ifv.mobitopp.actitopp.steps.PlannedTourAttributes
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonAndRoutineAttributes
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonAndRoutineFrom
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ChoiceSituation

interface DayTourPlanAndPreviousTourPlan : PreviousDayAttributes, PlannedTourAttributes
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