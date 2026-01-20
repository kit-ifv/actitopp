package edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.shenanigans


import edu.kit.ifv.mobitopp.actitoppNG.enums.ActivityType
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitoppNG.steps.DayAttributes
import edu.kit.ifv.mobitopp.actitoppNG.steps.DayAttributesFromWeekday
import edu.kit.ifv.mobitopp.actitoppNG.steps.HouseholdAttributes
import edu.kit.ifv.mobitopp.actitoppNG.steps.HouseholdAttributesFromElement
import edu.kit.ifv.mobitopp.actitoppNG.steps.PersonAttributes
import edu.kit.ifv.mobitopp.actitoppNG.steps.PersonAttributesFromElement
import edu.kit.ifv.mobitopp.actitoppNG.steps.RoutineAttributes
import edu.kit.ifv.mobitopp.actitoppNG.steps.RoutineAttributesFromElement
import java.time.DayOfWeek


interface PersonAndRoutineAttributes : PersonAttributes, RoutineAttributes
data class PersonAndRoutineFrom(
    val element: PersonWithRoutine,
    private val routine: RoutineAttributes = RoutineAttributesFromElement(element.routine),
    private val person: PersonAttributes = PersonAttributesFromElement(element.person),
) : PersonAndRoutineAttributes, RoutineAttributes by routine, PersonAttributes by person

class DayAlternative private constructor(
    private val personAttributesFromElement: PersonAndRoutineAttributes,
    private val dayAttributesFromElement: DayAttributes,
    private val householdAttributesFromElement: HouseholdAttributes,
) :  PersonAttributes by personAttributesFromElement,
    RoutineAttributes by personAttributesFromElement,
    DayAttributes by dayAttributesFromElement,
    HouseholdAttributes by householdAttributesFromElement {
    constructor( personRoutine: PersonWithRoutine, week: DayOfWeek) : this(
        PersonAndRoutineFrom(personRoutine),
        DayAttributesFromWeekday(week),
        HouseholdAttributesFromElement(personRoutine.household)
    )

}


