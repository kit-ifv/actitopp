package edu.kit.ifv.mobitopp

import edu.kit.ifv.mobitopp.actitopp.ActiToppHousehold
import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.DebugLoggers
import edu.kit.ifv.mobitopp.actitopp.ModelFileBase
import edu.kit.ifv.mobitopp.actitopp.RNGHelper
import kotlin.random.Random

class LargeRun {


    constructor(
        householdIndex: Int,
        children0_10: Int,
        children_u18: Int,
        areatype: Int,
        numberofcarsinhousehold: Int,
    )

}

class Limits {

}
private val fileBase = ModelFileBase()
private val randomgenerator = RNGHelper(1234)
private val debugloggers = DebugLoggers()
private val random = Random(1)
private fun generateHousehold(): ActiToppHousehold {
    return ActiToppHousehold(
        random.nextInt(0, 10),
        random.nextInt(0, 10),
        random.nextInt(0, 10),
        random.nextInt(0, 10),
        random.nextInt(0, 10)
    )
}
private fun generateHouseholds(amount: Int): List<ActiToppHousehold> {
    return (0..amount).map { generateHousehold() }
}
fun ActiToppHousehold.generatePersons(amount: Int): List<ActitoppPerson> {
    return (0..amount).map {
        this.generatePerson(it)
    }
}

fun ActiToppHousehold.generatePerson(number: Int): ActitoppPerson {
    return         ActitoppPerson(this, number, number, random.nextInt(0,100), random.nextInt(0, 100), random.nextInt(), random.nextDouble(), random.nextDouble()).also{this.addHouseholdmember(it, it.persIndex)}
}
fun Collection<ActitoppPerson>.generateSchedules() {
    forEach { it.generateSchedule(fileBase, randomgenerator, debugloggers) }
}
fun main() {
    val targets = generateHouseholds(10000).flatMap { it.generatePersons(5) }
    targets.generateSchedules()
}