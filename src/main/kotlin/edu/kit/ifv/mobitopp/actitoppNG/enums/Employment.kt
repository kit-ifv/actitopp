package edu.kit.ifv.mobitopp.actitoppNG.enums

/**
 * TODO I have no Idea what the unknown codes should be, I can only implement the function found in the codebase. Robin, 25
 */
enum class Employment(val code: Int) {
    FULLTIME(1),
    PARTTIME(2),
    UNOCCUPIED(3),
    STUDENT(4),
    VOCATIONAL(5),
    HOUSEKEEPER(6),
    RETIRED(7),
    UNKNOWN_21(21),
    MARGINAL(22),
    STUDENT_PRIMARY(40),
    STUDENT_SECONDARY(41),
    STUDENT_TERTIARY(42),
    DEFINITELY_UNKNOWN(Int.MIN_VALUE);

    companion object {
        fun fromInt(code: Int): Employment = entries.firstOrNull { it.code == code } ?: DEFINITELY_UNKNOWN

    }
}

fun Employment.isParttime(): Boolean {
    return this == Employment.PARTTIME || this == Employment.UNKNOWN_21 || this == Employment.MARGINAL
}

fun Employment.isEarning() =
    this == Employment.FULLTIME || this == Employment.PARTTIME || this == Employment.UNKNOWN_21 || this == Employment.MARGINAL

fun Employment.isNotEarning() = this == Employment.UNOCCUPIED || this == Employment.HOUSEKEEPER
fun Employment.isEmployedAnywhere() = this.isEarning() || this == Employment.VOCATIONAL
fun Employment.isStudentOrAzubi() = this == Employment.VOCATIONAL || this.isStudent()
fun Employment.isStudent() =
    this == Employment.STUDENT || this == Employment.STUDENT_PRIMARY || this == Employment.STUDENT_SECONDARY || this == Employment.STUDENT_TERTIARY