package edu.kit.ifv.mobitopp.actitopp.enums

import java.util.Collections
import java.util.EnumSet

enum class ActivityType(val typeasChar: Char) {
    WORK('W'),
    EDUCATION('E'),
    LEISURE('L'),
    SHOPPING('S'),
    TRANSPORT('T'),
    HOME('H'),
    UNKNOWN('x');


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
                WORK,
                EDUCATION,
                LEISURE,
                SHOPPING,
                TRANSPORT,
                HOME
            )
        )

        fun getTypeFromChar(charValue: Char): ActivityType {
            val tocompare = charValue.uppercaseChar()

            for (type in EnumSet.allOf(ActivityType::class.java)) {
                if (type.typeasChar == tocompare) {
                    return type
                }
            }
            throw NoSuchElementException("There is no type $charValue as activity in actiTopp")
        }
    }
}
