package edu.kit.ifv.mobitopp.actitoppNG.enums

enum class Gender(val code: Int) {
    MALE(1),
    FEMALE(2),
    UNKNOWN(-1);

    companion object {
        fun fromCode(code: Int): Gender {
            return entries.firstOrNull { it.code == code } ?: UNKNOWN
        }
    }
}