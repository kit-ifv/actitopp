package edu.kit.ifv.mobitopp.actitopp.enums

import java.util.Collections
import java.util.EnumSet

enum class JointStatus(val typeasInt: Int) {
    JOINTTRIPANDACTIVITY(1),
    JOINTACTIVITY(2),
    JOINTTRIP(3),
    NOJOINTELEMENT(4),
    UNKNOWN(-1);


    companion object {
        val JOINTELEMENTS: Set<JointStatus> = Collections.unmodifiableSet(
            EnumSet.of(
                JOINTTRIPANDACTIVITY,
                JOINTACTIVITY,
                JOINTTRIP
            )
        )
        val FULLSET: Set<JointStatus> = Collections.unmodifiableSet(
            EnumSet.of(
                JOINTTRIPANDACTIVITY,
                JOINTACTIVITY,
                JOINTTRIP,
                NOJOINTELEMENT
            )
        )


        fun getTypeFromInt(intValue: Int): JointStatus {
            return entries.firstOrNull { it.typeasInt == intValue }
                ?: throw NoSuchElementException("No type with value $intValue")
        }
    }
}
