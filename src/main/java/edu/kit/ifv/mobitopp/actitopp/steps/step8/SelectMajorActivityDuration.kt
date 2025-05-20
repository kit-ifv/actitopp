package edu.kit.ifv.mobitopp.actitopp.steps.step8

import edu.kit.ifv.mobitopp.actitopp.modernization.durations.Step8BInput
import kotlin.time.Duration

fun interface SelectMajorActivityDuration {
    fun getDuration(input: Step8BInput): Duration
}