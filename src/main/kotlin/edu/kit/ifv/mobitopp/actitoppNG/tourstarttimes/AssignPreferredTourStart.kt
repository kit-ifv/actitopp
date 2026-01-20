package edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes

import edu.kit.ifv.mobitopp.actitoppNG.enums.ActivityType
import edu.kit.ifv.mobitopp.actitoppNG.modernization.durations.MobilityPlanInputs
import edu.kit.ifv.mobitopp.actitoppNG.modernization.plan.MobilityPlan
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.ArrayHistogram
import kotlin.random.Random

context(rng: Random)
fun MobilityPlan.assignPreferredTourStart(strategy: PersonPreferredTourStart): ArrayHistogram? {
    if (!person.isAnywayEmployed() && !person.isinEducation()) return null
    // TODO Invert this boolean expressions
    if (mainActivityMap[ActivityType.WORK]?.isNotEmpty() == true || mainActivityMap[ActivityType.EDUCATION]?.isNotEmpty() == true) {
        return strategy.determinePreferredTourStart(
            MobilityPlanInputs(
                this,
                this.person,
                this.dayPlans.first(), // TODO repair it so that for this choice function we don't require a dayplan
                this.dayPlans.first().mainTour, // TODO this should also not ne needed as an argument,
                this.dayPlans.first().tourPlans.first().mainActivity // TODO and this also not.
            )

        )
    }
    return null
}