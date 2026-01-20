package edu.kit.ifv.mobitopp.actitoppNG

import edu.kit.ifv.mobitopp.actitoppNG.modernization.DefaultPlanGeneration
import edu.kit.ifv.mobitopp.actitoppNG.modernization.ModernizedActivity
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking


suspend fun Collection<ActitoppPerson>.generateSchedules(): List<List<ModernizedActivity>> = coroutineScope {


    this@generateSchedules.withIndex().map { (index, person) ->
        async(Default) {
            val householdPlan = DefaultPlanGeneration()
            val schedule = householdPlan.generate(person).finish()
            if (index % 100 == 0) println("Working on person $index done")
            schedule
        }
    }.awaitAll()
}

fun main() {
    val targets = randomHouseholds(10000).flatMap { it.generatePersons(5) }

    runBlocking {
        targets.generateSchedules()
    }

}