package edu.kit.ifv.mobitopp.actitopp.modernization

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.steps.step1.assignWeekRoutine
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonWithRoutine
import edu.kit.ifv.mobitopp.randomPerson
import org.junit.jupiter.api.Assertions.*
import kotlin.jvm.Throws
import kotlin.test.Test

class PatternStructureTest {
    @Test
    fun exampleTest() {

        val actitoppPerson = ActitoppPerson.randomPerson()
        val weekRoutine = actitoppPerson.assignWeekRoutine()
        val personWithRoutine = PersonWithRoutine(actitoppPerson, weekRoutine)
        val patternStructure = PatternStructure(personWithRoutine)
        val output = patternStructure.determineNextMainActivity()

        println(output)
    }
}