package edu.kit.ifv.mobitopp.actitopp

import edu.kit.ifv.mobitopp.actitopp.steps.step1.ParameterSet1A
import edu.kit.ifv.mobitopp.actitopp.steps.step1.ParameterSet1B
import edu.kit.ifv.mobitopp.actitopp.steps.PersonSituation
import edu.kit.ifv.mobitopp.actitopp.steps.step1.Situation1A
import edu.kit.ifv.mobitopp.actitopp.steps.step1.step1AModel
import edu.kit.ifv.mobitopp.actitopp.steps.step1.step1BModel
import edu.kit.ifv.mobitopp.actitopp.steps.step1.step1BWithParams
import edu.kit.ifv.mobitopp.actitopp.steps.step1.step1CWithParams
import edu.kit.ifv.mobitopp.actitopp.steps.step1.step1DWithParams
import edu.kit.ifv.mobitopp.actitopp.steps.step1.step1EWithParams
import edu.kit.ifv.mobitopp.actitopp.steps.step1.step1FWithParams
import edu.kit.ifv.mobitopp.actitopp.steps.step1.step1KWithParams
import edu.kit.ifv.mobitopp.actitopp.steps.step1.step1LWithParams
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ModifiableDiscreteChoiceModel
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ParametrizedDiscreteChoiceModel
import edu.kit.ifv.mobitopp.generateHouseholds
import edu.kit.ifv.mobitopp.generatePersons
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertTrue


class CoordinatorTest: CoordinatorTestUtilities() {



    @Test
    fun failureWhenPreceedingStepsAreMissing() {
        assertThrows<IllegalStateException> {
            step1BModel.select(persons.first(), ParameterSet1B)
        }
    }

    /**
     * Keep this test separate from the logic of the other tests, and don't compare utilities, but rather results
     * as one test should also show that the probability calculation and selection are identical
     */
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

    @TestFactory
    fun generateDaysOfEducationActivities(): Collection<DynamicTest> {
        return buildModelTest("education Days", "1B", step1BWithParams) {
            it.generateWorkingDays()
        }

    }


    @TestFactory
    fun generateDaysOfLeisureActivities(): Collection<DynamicTest> {
        return buildModelTest("leisure Days", "1C", step1CWithParams) {
            it.generateWorkingDays()
        }
    }

    @Test
    fun assertStep1DThrows() {
        assertThrows<IllegalStateException> {
            val modifiableField = persons.first().toModifiable()
            // Should fail even if working days are present, because shopping accesses other fields.
            modifiableField.generateWorkingDays()
            generateModernUtilities(step1DWithParams, modifiableField)
        }
    }

    @TestFactory
    fun generateDaysOfShoppingActivities(): Collection<DynamicTest> {
        return buildModelTest("shopping Days", "1D", step1DWithParams) {
            it.generateWorkingDays()
            it.generateEducationDays()
            it.generateLeisureDays()
        }
    }
    @TestFactory
    fun generateDaysOfServiceActivities(): Collection<DynamicTest> {
        return buildModelTest("service Days", "1E", step1EWithParams) {}
    }

    @TestFactory
    fun generateDaysOfImmobility(): Collection<DynamicTest> {
        return buildModelTest("immobility Days", "1F", step1FWithParams) {
            it.generateWorkingDays()
            it.generateEducationDays()
            it.generateLeisureDays()
            it.generateShoppingDays()
            it.generateServiceDays()
        }
    }

    @TestFactory
    fun generateAverageAmountOfToursPerDay(): Collection<DynamicTest> {
        return buildModelTest("mean of Tours per day", "1K", step1KWithParams) {

        }
    }

    @TestFactory
    fun generateAverageAmountOfActivitiesPerDay(): Collection<DynamicTest> {
        return buildModelTest("mean of Activities per day", "1L", step1LWithParams) {

        }
    }

    private fun <P> buildModelTest(testName: String, stepId: String, model: ParametrizedDiscreteChoiceModel<Int, PersonSituation, P>, initializationBlock: (ActitoppPersonModifierFields) -> Unit): Collection<DynamicTest> {
        return persons.withIndex().map {(i, person) ->
            DynamicTest.dynamicTest("person $i $testName") {
                val modifierFields = ActitoppPersonModifierFields(person)
                modifierFields.apply(initializationBlock)
                val originalUtilities = generateIntUtilities(stepId, person)
                val newUtilities = generateModernUtilities(model, modifierFields)
                val equality = testDoubleMapEquality(originalUtilities, newUtilities)
                assertTrue(equality, message = "$\n${coerceMap(originalUtilities)}\n${coerceMap(newUtilities)}\n${originalUtilities.minus(newUtilities)}\n${PersonSituation(0, modifierFields).debug()}\n${person.attributesMap}\n$person")
            }
        }
    }



    private fun ActitoppPersonModifierFields.generateWorkingDays() {
        val workingDays = step1AModel.select((0..7).map { Situation1A(it, original) }.toSet(), ParameterSet1A, randomgenerator.randomValue)
        amountOfWorkingDays = workingDays
        original.addAttributetoMap("anztage_w", workingDays.toDouble())
    }

    private fun ActitoppPersonModifierFields.generateEducationDays() {
        this.amountOfEducationDays = step1BWithParams.select { PersonSituation(it, this) }
        original.addAttributetoMap("anztage_e", amountOfEducationDays.toDouble())
    }

    private fun ActitoppPersonModifierFields.generateLeisureDays() {
        amountOfLeisureDays = step1CWithParams.select { PersonSituation(it, this) }
        original.addAttributetoMap("anztage_l", amountOfLeisureDays.toDouble())
    }

    private fun ActitoppPersonModifierFields.generateShoppingDays() {
        amountOfShoppingDays = step1DWithParams.select { PersonSituation(it, this) }
        original.addAttributetoMap("anztage_s", amountOfShoppingDays.toDouble())
    }
    private fun ActitoppPersonModifierFields.generateServiceDays() {
        amountOfServiceDays = step1EWithParams.select { PersonSituation(it, this) }
        original.addAttributetoMap("anztage_t", amountOfServiceDays.toDouble())
    }


    private fun <P> generateModernUtilities(model: ParametrizedDiscreteChoiceModel<Int, PersonSituation, P>, person: ActitoppPerson): Map<Int, Double> {
        return model.utilities { PersonSituation(it, person.toModifiable()) }
    }

    private fun <P> generateModernUtilities(model: ParametrizedDiscreteChoiceModel<Int, PersonSituation, P>, modifiable: ActitoppPersonModifierFields): Map<Int, Double> {
        return model.utilities { PersonSituation(it, modifiable) }
    }


}
private fun <T> ModifiableDiscreteChoiceModel<Int, PersonSituation, T>.select(person: ActitoppPerson, parameters: T): Int {
    val alternatives =  (0..7).map { PersonSituation(it, person.toModifiable()) }.toSet()
    return select(alternatives, parameters)
}


