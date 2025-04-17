package edu.kit.ifv.mobitopp.actitopp.enums

/**
 * The legacy usage of the old area code is dumb, but encoding it in naked ints is even dumber. Thats why we pack it into an enum
 */
enum class AreaType(val code: Int) {
    RURAL(1),
    PROVINCIAL(2),
    CITYOUTSKIRT(3),
    METROPOLITAN(4),
    CONURBATION(5),
    UNKNOWN(-1);
    companion object {
        fun fromCode(code: Int): AreaType {
            return entries.firstOrNull { it.code == code } ?: UNKNOWN
        }
    }
}