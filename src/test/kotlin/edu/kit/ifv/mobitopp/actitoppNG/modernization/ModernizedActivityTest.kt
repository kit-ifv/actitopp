package edu.kit.ifv.mobitopp.actitoppNG.modernization

import edu.kit.ifv.mobitopp.actitoppNG.enums.ActivityType
import edu.kit.ifv.mobitopp.actitoppNG.utils.Position
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class ModernizedActivityTest {
    @Test
    fun serialization() {
        val activity = ModernizedActivity(
            activityType = ActivityType.WORK,
            startTime = 1.hours,
            duration = 30.minutes,
            position = Position.MAIN
        )
        val string = Json.encodeToString(ModernizedActivity.serializer(), activity)
        val newActivity = Json.decodeFromString<ModernizedActivity>(string)
        assertEquals(activity, newActivity)
    }
}