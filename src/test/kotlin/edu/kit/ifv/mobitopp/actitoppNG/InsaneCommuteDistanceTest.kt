package edu.kit.ifv.mobitopp.actitoppNG

import edu.kit.ifv.mobitopp.actitoppNG.enums.Employment
import edu.kit.ifv.mobitopp.actitoppNG.enums.Gender
import edu.kit.ifv.mobitopp.actitoppNG.modernization.DefaultPlanGeneration
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.random.Random

class InsaneCommuteDistanceTest {
    @Test
    fun insaneCommuteDistances() {
        assertThrows<IllegalArgumentException> {
            val targets = randomHouseholds(1000).map { it.generateInsanePerson() }

            val householdPlan = DefaultPlanGeneration()
            targets.forEach {
                householdPlan.generate(it)
            }

        }


    }

    private fun ActiToppHousehold.generateInsanePerson(): Person {
        return ActitoppPerson(this, PersonAttributes(
            gender = Gender.entries.random(),
            employment = Employment.entries.random(),
            age = (1..100).random(),
            commuteDistanceWork = Random.nextDouble(100.0, 10000000.0),
            commuteDistanceEducation = Random.nextDouble(100.0, 10000000.0),
            isAllowedToWork = true
        )
        )
    }
}