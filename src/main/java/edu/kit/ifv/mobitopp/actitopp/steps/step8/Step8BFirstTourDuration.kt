package edu.kit.ifv.mobitopp.actitopp.steps.step8

import edu.kit.ifv.mobitopp.actitopp.HActivity
import kotlin.time.Duration

fun interface AssignDurationForFirstActivityInTour {
    fun assign(activity: HActivity): Duration
}