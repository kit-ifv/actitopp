package edu.kit.ifv.mobitopp.actitopp.enums

/**
 * TODO I have no Idea what the unknown codes should be, I can only implement the function ound in the codebase.
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
    UNKNOWN_22(22),
    UNKNOWN_40(40),
    UNKNOWN_41(41),
    UNKNOWN_42(42),
    DEFINITELY_UNKNOWN(Int.MIN_VALUE);

    companion object {
        fun fromInt(code: Int): Employment = entries.firstOrNull { it.code == code } ?: DEFINITELY_UNKNOWN

    }
}

fun Employment.isParttime(): Boolean {
    return this == Employment.PARTTIME || this == Employment.UNKNOWN_21 || this == Employment.UNKNOWN_22
}
fun Employment.isEarning() = this == Employment.FULLTIME || this == Employment.PARTTIME || this == Employment.UNKNOWN_21 || this == Employment.UNKNOWN_22
fun Employment.isNotEarning() = this == Employment.UNOCCUPIED || this == Employment.HOUSEKEEPER
fun Employment.isEmployedAnywhere() = this.isEarning() || this == Employment.VOCATIONAL
fun Employment.isStudentOrAzubi() =  this == Employment.VOCATIONAL || this.isStudent()
fun Employment.isStudent() =
    this == Employment.STUDENT || this == Employment.UNKNOWN_40 || this == Employment.UNKNOWN_41 || this == Employment.UNKNOWN_42