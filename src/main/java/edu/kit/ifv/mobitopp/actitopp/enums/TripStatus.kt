package edu.kit.ifv.mobitopp.actitopp.enums

/**
 * This enum is only present to differentiate between the last activity in a tour [TRIP_AFTER_ACT] and any other trip
 * This could probably be simplified.
 */
enum class TripStatus {
    TRIP_BEFORE_ACT,
    TRIP_AFTER_ACT
}
