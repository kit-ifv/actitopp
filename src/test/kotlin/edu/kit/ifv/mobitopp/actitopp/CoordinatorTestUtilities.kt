package edu.kit.ifv.mobitopp.actitopp

import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.generateHouseholds
import edu.kit.ifv.mobitopp.generatePersons
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

abstract class CoordinatorTestUtilities {


    protected val persons = generateHouseholds(200).flatMap { it.generatePersons(5) }
    protected val fileBase: ModelFileBase = ModelFileBase()
    protected val randomgenerator: RNGHelper = RNGHelper(1234)
    protected fun generateIntUtilities(id: String, person: ActitoppPerson): Map<Int, Double> {


        val step = DCDefaultModelStep(id, fileBase, AttributeLookup(person), randomgenerator)
        step.doStep()
        return step.utilities(String::toInt)
    }
    protected fun <T> generateUtilities(id: String, person: ActitoppPerson, day: HDay, converter: (String) -> T): Map<T, Double> {
        val step = DCDefaultModelStep(id, fileBase, AttributeLookup(person, day), randomgenerator)
        step.doStep()
        return step.utilities(converter)
    }
    protected fun <K> coerceMap(map: Map<K, Double>): String {
        return map.entries.joinToString(", ") { "${it.key}=${it.value.toString().take(5).padEnd(5, '0')}"  }
    }

    protected fun <K> Map<K, Double>.minus(other: Map<K, Double>): Map<K, Double> {
        return entries.associate { it.key to it.value - (other[it.key] ?: 0.0 )}
    }
    protected fun DCDefaultModelStep.probabilities() = probabilities {it.toInt()}
    protected fun <T> DCDefaultModelStep.probabilities(converter: (String) -> T): Map<T, Double> {
        return alternatives.associate { converter(it.name) to it.probability }
    }

    protected fun randomWeekRoutine(person: ActitoppPerson): WeekRoutine {
        val rng = Random(person.age * 10000 + person.persIndex)
        return WeekRoutine(
            amountOfWorkingDays = rng.nextInt(0, 7),
            amountOfEducationDays = rng.nextInt(0, 7),
            amountOfLeisureDays = rng.nextInt(0, 7),
            amountOfShoppingDays = rng.nextInt(0, 7),
            amountOfServiceDays = rng.nextInt(0, 7),
            amountOfImmobileDays = rng.nextInt(0, 7),
            averageAmountOfTours = rng.nextInt(0, 3),
            averageAmountOfActivities = rng.nextInt(0, 4),
        )
    }

    protected fun randomMainActivityTypes(person: ActitoppPerson, amount: Int = 7): List<ActivityType> {
        val rng = Random(person.persIndex)
        return (0..<amount).map { ActivityType.FULLSET.random(rng) }
    }

    protected fun randomPrecedingTours(person: ActitoppPerson, amount: Int = 7): List<Int> {
        val rng = Random(person.age)
        return (0..<amount).map { rng.nextInt(0, 5) }
    }

    protected fun HWeekPattern.loadActivities(acts: Collection<ActivityType>) {
        days.zip(acts).forEach { (day, acts) ->
            // Do not load a home activity into a pattern, because old actitopp does not generate a tour for staying home
            if(acts != ActivityType.HOME) {
                val tour = day.generateMainTour()
                tour.generateMainActivity(acts)
            }

        }
    }

    protected fun loadRandomPrecedingTours(person: ActitoppPerson, randomTargets: List<Int>) {
        person.weekPattern.days.zip(randomTargets).map { (day, acts) ->
            if(!day.isHomeDay) {
                repeat(acts) {
                    day.generatePrecedingTour()
                }
            }
        }
    }

    protected fun loadRandomFollowingTours(person: ActitoppPerson) {
        val rng = Random(person.age)
        person.weekPattern.days.forEach { day ->
            if(!day.isHomeDay) {
                repeat(rng.nextInt(0, 5)) {
                    day.generateFollowingTour()
                }
            }
        }
    }

    fun <T> assertUtilityEquals(first: UtilityDebug<T>, second: UtilityDebug<T>) {
        val message = "\n$first\n$second"
        assertEquals(
            first.randomNumber,
            second.randomNumber,
            message = "Random numbers don't match: ${first.randomNumber} ${second.randomNumber}"
        )
        assertContentEquals(
            first.options,
            second.options,
            message = "Available options don't match ${first.options} ${second.options}: $message"
        )
        assertTrue(
            testDoubleMapEquality(first.utilities, second.utilities),
            message = "Utilities don't match ${first.utilities.minus(second.utilities)}: $message"
        )


    }
    fun <K> testDoubleMapEquality(
        expected: Map<K, Double>,
        actual: Map<K, Double>,
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

}
fun <T> DCDefaultModelStep.utilities(converter: (String) ->T ): Map<T, Double> {
    return alternatives.associate { converter(it.name) to it.utility }
}