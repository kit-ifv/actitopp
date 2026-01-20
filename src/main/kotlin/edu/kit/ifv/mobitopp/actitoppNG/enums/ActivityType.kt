package edu.kit.ifv.mobitopp.actitoppNG.enums

import kotlinx.serialization.Serializable
import java.util.Collections
import java.util.EnumSet

@Serializable
enum class ActivityType(val defaultActivityTime: Int = 278) {
    EDUCATION(340),
    HOME(),
    LEISURE(130),
    SHOPPING(41),
    TRANSPORT(15),
    WORK(472);


    companion object {
        val OUTOFHOMEACTIVITY: Set<ActivityType> = Collections.unmodifiableSet(
            EnumSet.of(
                WORK,
                EDUCATION,
                LEISURE,
                SHOPPING,
                TRANSPORT
            )
        )

        val FULLSET: Set<ActivityType> = Collections.unmodifiableSet(
            EnumSet.of(
                EDUCATION,
                HOME,
                LEISURE,
                SHOPPING,
                TRANSPORT,
                WORK,
            )
        )

    }

    fun symbol() = toString().first().toString()
}
