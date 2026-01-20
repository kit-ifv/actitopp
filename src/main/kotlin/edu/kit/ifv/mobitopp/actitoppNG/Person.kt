package edu.kit.ifv.mobitopp.actitoppNG

import edu.kit.ifv.mobitopp.actitoppNG.enums.Employment
import edu.kit.ifv.mobitopp.actitoppNG.enums.Gender

interface Person {
    val employment: Employment
    val age: Int
    val gender: Gender
    val maxCommute: Double
    val isAllowedToWork: Boolean
    val commutingdistanceWork: Double
    val commutingdistanceEducation: Double

    val id: Int
    val household: Household
    fun isAnywayEmployed(): Boolean
    fun isinEducation(): Boolean
    fun spawnRandomGenerator(offset: Long = 0L): RNGHelper {
        return RNGHelper(id.toLong() + offset)
    }
}