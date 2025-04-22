package edu.kit.ifv.mobitopp.actitopp

import edu.kit.ifv.mobitopp.actitopp.changes.ParameterCollectionStep1B
import edu.kit.ifv.mobitopp.actitopp.changes.ParameterSet1A
import edu.kit.ifv.mobitopp.actitopp.changes.ParameterSet1B
import edu.kit.ifv.mobitopp.actitopp.changes.ParameterSet1C
import edu.kit.ifv.mobitopp.actitopp.changes.PersonSituation
import edu.kit.ifv.mobitopp.actitopp.changes.Situation1A
import edu.kit.ifv.mobitopp.actitopp.changes.Situation1B
import edu.kit.ifv.mobitopp.actitopp.changes.Situation1C
import edu.kit.ifv.mobitopp.actitopp.changes.step1AModel
import edu.kit.ifv.mobitopp.actitopp.changes.step1BModel
import edu.kit.ifv.mobitopp.actitopp.changes.step1CModel
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ModifiableDiscreteChoiceModel
import edu.kit.ifv.mobitopp.generateHouseholds
import edu.kit.ifv.mobitopp.generatePersons
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue


class CoordinatorTest {
    private  val fileBase: ModelFileBase = ModelFileBase()
    private val randomgenerator: RNGHelper = RNGHelper(1234)

    private val persons = generateHouseholds(200).flatMap { it.generatePersons(5) }
    @TestFactory
    fun generateDaysOfWorkingActivities(): Collection<DynamicTest> {
        return persons.withIndex().map {(i, person) ->
            DynamicTest.dynamicTest("person $i working Days") {
                val step = DCDefaultModelStep("1A", fileBase, AttributeLookup(person), randomgenerator)
                val randomNumber = randomgenerator.randomValue
                step.doStep(randomNumber)
                val stepChoice = step.alternativeChosen.toInt()
                val alternatives = (0..7).map { Situation1A(it, person) }.toSet()
                val modernizedChoice = step1AModel.select(alternatives, ParameterSet1A, randomNumber)
                assertEquals(stepChoice, modernizedChoice)
            }
        }
    }
    private fun ActitoppPersonModifierFields.generateWorkingDays() {
        val workingDays = step1AModel.select((0..7).map { Situation1A(it, original) }.toSet(), ParameterSet1A, randomgenerator.randomValue)
        amountOfWorkingDays = workingDays
        original.addAttributetoMap("anztage_w", workingDays.toDouble())
    }

    private fun generateUtilities() {

    }
    @TestFactory
    fun generateDaysOfEducationActivities(): Collection<DynamicTest> {
        return persons.withIndex().map {(i, person) ->
            DynamicTest.dynamicTest("person $i education Days") {
                val modifiableFields = ActitoppPersonModifierFields(person)
                modifiableFields.generateWorkingDays()
                val step = DCDefaultModelStep("1B", fileBase, AttributeLookup(person), randomgenerator)
                val randomNumber = randomgenerator.randomValue
                step.doStep(randomNumber)
                val probabilities = step.probabilities()
                val stepChoice = step.alternativeChosen.toInt()

                val alternatives = (0..7).map { Situation1B(it, modifiableFields) }.toSet()
                val modernizedChoice = step1BModel.select(alternatives, ParameterSet1B, randomNumber)
                val modernizedProbabilities = step1BModel.probabilities(alternatives, ParameterSet1B).mapKeys { it.key.choice }
                val equality = testProbabilityMapEquality(probabilities, modernizedProbabilities)
                assertTrue(equality, "\n$probabilities\n$modernizedProbabilities \n${step.utilities()}\n ${step1BModel.utilities(
                    ParameterSet1B){Situation1B(it, modifiableFields)}} Unequal for person $person")
                assertEquals(stepChoice, modernizedChoice)
            }
        }
    }
    @Test
    fun failureWhenPreceedingStepsAreMissing() {
        assertThrows<IllegalStateException> {
            step1BModel.select(persons.first(), ParameterSet1B)
        }
    }

    @TestFactory
    fun generateDaysOfLeisureActivities(): Collection<DynamicTest> {
        return persons.withIndex().map {(i, person) ->
            DynamicTest.dynamicTest("person $i leisure Days") {
                val modifiableFields = ActitoppPersonModifierFields(person)
                modifiableFields.generateWorkingDays()
                val step = DCDefaultModelStep("1C", fileBase, AttributeLookup(person), randomgenerator)
                val randomNumber = randomgenerator.randomValue
                step.doStep(randomNumber)
                val stepChoice = step.alternativeChosen.toInt()
                val stepProbabilities = step.probabilities()
                val alternatives = (0..7).map { Situation1C(it, modifiableFields) }.toSet()
                val modernizedProbabilities = step1CModel.probabilities(alternatives, ParameterSet1C)
                val modernizedChoice = step1CModel.select(alternatives, ParameterSet1C, randomNumber)
                assertEquals(modernizedProbabilities.size, stepProbabilities.size)
                val zip = stepProbabilities.values.zip(modernizedProbabilities.values)
                zip.forEach { (a: Double, b: Double) ->
                    assertEquals(a, b, 0.00001)
                }
                assertEquals(stepChoice, modernizedChoice)
            }
        }
    }

    private fun DCDefaultModelStep.probabilities(): Map<Int, Double> {
        return alternatives.associate { it.name.toInt() to it.probability }
    }

    private fun DCDefaultModelStep.utilities(): Map<Int, Double> {
        return alternatives.associate { it.name.toInt() to it.utility }
    }
}
private fun <T> ModifiableDiscreteChoiceModel<Int, PersonSituation, T>.select(person: ActitoppPerson, parameters: T): Int {
    val alternatives =  (0..7).map { PersonSituation(it, person.toModifiable()) }.toSet()
    return select(alternatives, parameters)
}
fun testProbabilityMapEquality(
    expected: Map<Int, Double>,
    actual: Map<Int, Double>,
    delta: Double = 1e-8
): Boolean {
    require(expected.keys == actual.keys) {
        "Key mismatch: expected keys ${expected.keys}, got ${actual.keys}"
    }
    return expected.keys.all {
        val expectedValue = expected.getValue(it)
        val actualValue = actual.getValue(it)
        abs(expectedValue - actualValue) < delta
    }

}