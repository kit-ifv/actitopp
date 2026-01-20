package edu.kit.ifv.mobitopp.actitoppNG

import edu.kit.ifv.mobitopp.actitoppNG.modernization.DefaultPlanGeneration
import edu.kit.ifv.mobitopp.actitoppNG.modernization.MobilityPlanGeneration
import edu.kit.ifv.mobitopp.actitoppNG.modernization.plan.MobilityPlan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

fun interface HouseholdPlanGeneration {

    fun generateSchedules(household: Household): Map<Person, MobilityPlan>
}

class StandardHouseholdPlanGeneration(private val planGeneration: MobilityPlanGeneration = DefaultPlanGeneration()) :
    HouseholdPlanGeneration {

    override fun generateSchedules(household: Household): Map<Person, MobilityPlan> {
        return household.members.associateWith { member ->
            context(member.spawnRandomGenerator()) {
                planGeneration.generate(member)
            }

        }
    }
}



class ParallelHouseholdPlanGeneration(private val planGeneration: MobilityPlanGeneration = DefaultPlanGeneration()) :
    HouseholdPlanGeneration {

    override fun generateSchedules(household: Household): Map<Person, MobilityPlan> {
        return runBlocking {
            household.members
                .map { member ->
                    async(Dispatchers.Default) {
                        val plan = context(member.spawnRandomGenerator()) {
                            planGeneration.generate(member)
                        }
                        member to plan
                    }
                }
                .awaitAll()
                .toMap()
        }
    }
}
