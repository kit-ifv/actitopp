package edu.kit.ifv.mobitopp.actitopp.enums
import java.util.Collections
import java.util.EnumSet
// TODO find out whether Unknown is deliberately set to a lower case letter so that the getTypeFromChar Throws
enum class ActivityType(val typeasChar: Char, val defaultActivityTime: Int = 278) {
    WORK('W', 472),
    EDUCATION('E', 340),
    LEISURE('L', 130),
    SHOPPING('S', 41),
    TRANSPORT('T', 15),
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
            return entries.firstOrNull {it.typeasChar == charValue.uppercaseChar()} ?: throw NoSuchElementException("There is no type $charValue as activity in actiTopp")

        }
    }
}
