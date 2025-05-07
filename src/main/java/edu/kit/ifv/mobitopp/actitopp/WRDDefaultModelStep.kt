package edu.kit.ifv.mobitopp.actitopp

import edu.kit.ifv.mobitopp.actitopp.changes.Category
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType

/**
 * @author Tim Hilgert
 *
 *
 * object for a wrd (weighted random draw) model step
 */
class WRDDefaultModelStep(
    id: String, // category to get a random draw
    private val category: Category, // activitytype for personal, activity type specific distributions
    private val activityType: ActivityType = ActivityType.UNKNOWN, // surrounding modelCoordinator for this step
    private val modelCoordinator: Coordinator
) :
    AbsHModelStep(id) {

    // Distribution element that is used to pick a random number

    // check if a personalized distribution for this id, category and activity type already exists. If not, create one.
    private val weightedDistribution: WRDDiscreteDistribution =
        modelCoordinator.getOrCreateWeightedDistribution(id, category, activityType)

    // decision if the distribution should be adapted after drawing
    private var modifydistribution = false


    private var bounds: IntRange = Int.MIN_VALUE..Int.MAX_VALUE



    public override fun doStep(): Int {
        // pick a random number within the given boundaries

        val selection = weightedDistribution.getRandomPickFromDistribution(
            this.bounds, modelCoordinator.randomGenerator
        )

        if (modifydistribution) {
            weightedDistribution.modifydistributionelement(selection)
            weightedDistribution.theModifiee = modelCoordinator.person
        }

        return selection.also { chosenDistributionElement = selection }
    }

    @Deprecated("Only used in the print method below")
    private var chosenDistributionElement = 0

    fun printDecisionProcess() {
        println("--------------- MC-Simulation @ " + this.id + this.category + this.activityType + " ---------------")
        println("From " + this.bounds.first)
        println("To " + this.bounds.last)
        println("Random Value: " + modelCoordinator.randomGenerator.lastRandomValue)
        println("Chosen: $chosenDistributionElement")
        println("")
    }

    /**
     * @param lowerbound
     * @param upperbound
     */
    fun setRangeBounds(lowerbound: Int, upperbound: Int) {
        require(lowerbound <= upperbound) {
            "Cannot set a range to $lowerbound $upperbound somehow the upperbound is smaller than the lower bound"
        }
        this.bounds = lowerbound..upperbound
    }

    fun setModifydistribution(modifydistribution: Boolean) {
        this.modifydistribution = modifydistribution
    }
}
