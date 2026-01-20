package edu.kit.ifv.mobitopp.actitoppNG.weekroutine

import edu.kit.ifv.mobitopp.actitoppNG.enums.ActivityType

/**
 * A [WeekRoutine] captures the desired amount of days that a person would want to perform each activity type over
 * the given time frame. (Usually a week). It also contains the average amount of tours and activities (per day)
 * that this person should perform in the analysis time frame.
 */

interface WeekRoutine {

    val amountOfWorkingDays: Int
    val amountOfEducationDays: Int
    val amountOfLeisureDays: Int
    val amountOfShoppingDays: Int
    val amountOfServiceDays: Int
    val amountOfImmobileDays: Int
    val averageAmountOfTours: Int
    val averageAmountOfActivities: Int

    operator fun get(activityType: ActivityType): Int {
        return when (activityType) {
            ActivityType.EDUCATION -> amountOfEducationDays
            ActivityType.WORK -> amountOfWorkingDays
            ActivityType.LEISURE -> amountOfLeisureDays
            ActivityType.SHOPPING -> amountOfShoppingDays
            ActivityType.TRANSPORT -> amountOfServiceDays
            ActivityType.HOME -> amountOfImmobileDays
        }
    }
}