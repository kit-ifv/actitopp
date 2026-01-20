package edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.shenanigans

import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitoppNG.modernization.DayStructure
import edu.kit.ifv.mobitopp.actitoppNG.modernization.PlannedTourAmounts
import edu.kit.ifv.mobitopp.actitoppNG.modernization.TourStructure
import edu.kit.ifv.mobitopp.actitoppNG.steps.ActivityAttributes
import edu.kit.ifv.mobitopp.actitoppNG.steps.DayAttributesFromStructure
import edu.kit.ifv.mobitopp.actitoppNG.steps.FullyQualifiedDayStructureAttributes
import edu.kit.ifv.mobitopp.actitoppNG.steps.PersonAttributes
import edu.kit.ifv.mobitopp.actitoppNG.steps.RoutineAttributes
import edu.kit.ifv.mobitopp.actitoppNG.steps.TourAttributes
import edu.kit.ifv.mobitopp.actitoppNG.steps.TourAttributesByStructAndNumbers
import edu.kit.ifv.mobitopp.actitoppNG.utils.BidirectionalIndexedValue
import edu.kit.ifv.mobitopp.actitoppNG.utils.Position


class ActivityAlternative private constructor(
    personAndRoutineAttributes: PersonAndRoutineAttributes,
    dayAttributes: FullyQualifiedDayStructureAttributes,
    tourAttributes: TourAttributes,
    activityAttributes: ActivityAttributes,
) :
    TourAttributes by tourAttributes, PersonAttributes by personAndRoutineAttributes,
    RoutineAttributes by personAndRoutineAttributes, FullyQualifiedDayStructureAttributes by dayAttributes,
    ActivityAttributes by activityAttributes {


    constructor(

        personWithRoutine: PersonWithRoutine,
        dayStructure: DayStructure,
        tourStructure: BidirectionalIndexedValue<TourStructure>,
        position: Position,
        plannedTourAmounts: PlannedTourAmounts,
    ) : this(
        PersonAndRoutineFrom(personWithRoutine),
        DayAttributesFromStructure(dayStructure),
        TourAttributesByStructAndNumbers(
            tourStructure,
            plannedTourAmounts.precursorAmount,
            plannedTourAmounts.successorAmount
        ),
        ActivityAttributes { position == Position.BEFORE }
    )

}


