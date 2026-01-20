package edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.shenanigans

import edu.kit.ifv.mobitopp.actitoppNG.modernization.DayStructure
import edu.kit.ifv.mobitopp.actitoppNG.modernization.PlannedTourAmounts

import edu.kit.ifv.mobitopp.actitoppNG.steps.DayAttributesFromStructure
import edu.kit.ifv.mobitopp.actitoppNG.steps.DayStructureAttributes


interface PreviousDayAttributes {
    fun previousDayHasNoBeforeTour(): Boolean
    fun previousDayHasOneBeforeTour(): Boolean
    fun previousDayHasNoAfterTour(): Boolean
    fun previousDayHasOneAfterTour(): Boolean
}


class PreviousDayAttributesNumeric(
    val dayAttributes: DayStructureAttributes,
    val previousDayBeforeTours: Int?,
    val previousDayAfterTours: Int?,
) : PreviousDayAttributes, DayStructureAttributes by dayAttributes {


    constructor(
        dayStructure: DayStructure,
        plannedTourAmounts: PlannedTourAmounts?,

        ) : this(
        dayAttributes = DayAttributesFromStructure(dayStructure),
        previousDayBeforeTours = plannedTourAmounts?.precursorAmount,
        previousDayAfterTours = plannedTourAmounts?.successorAmount,
    )

    override fun previousDayHasNoBeforeTour(): Boolean = previousDayBeforeTours == 0

    override fun previousDayHasOneBeforeTour(): Boolean = previousDayBeforeTours == 1

    override fun previousDayHasNoAfterTour(): Boolean = previousDayAfterTours == 0

    override fun previousDayHasOneAfterTour(): Boolean = previousDayAfterTours == 1

}

