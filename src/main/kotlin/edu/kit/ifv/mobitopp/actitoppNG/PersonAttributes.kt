package edu.kit.ifv.mobitopp.actitoppNG

import edu.kit.ifv.mobitopp.actitoppNG.enums.Employment
import edu.kit.ifv.mobitopp.actitoppNG.enums.Gender
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class PersonAttributes(
    val gender: Gender,
    val employment: Employment,
    val age: Int,
    val commuteDistanceWork: Double? = null,
    val commuteDistanceEducation: Double? = null,
    val isAllowedToWork: Boolean = true,
) {
    companion object {
        fun random(rng: Random): PersonAttributes {
            return PersonAttributes(
                age = rng.nextInt(0, 100),
                employment = Employment.fromInt(rng.nextInt(0, 42)),
                gender = Gender.fromCode(rng.nextInt(0, 3)),
                commuteDistanceWork = rng.nextDouble(),
                commuteDistanceEducation = rng.nextDouble()
            )
        }
    }
}