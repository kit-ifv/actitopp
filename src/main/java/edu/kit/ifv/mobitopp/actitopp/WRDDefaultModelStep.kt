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
    private val activityType: ActivityType, // surrounding modelCoordinator for this step
    private val modelCoordinator: Coordinator
) :
    AbsHModelStep(id) {

    // Distribution element that is used to pick a random number

    // check if a personalized distribution for this id, category and activity type already exists. If not, create one.
    private val weightedDistribution: WRDDiscreteDistribution =
        modelCoordinator.getOrCreateWeightedDistribution(id, category, activityType)

    // decision if the distribution should be adapted after drawing
    private var modifydistribution = false

    // element that is finally chosen based on weighted random draw
    private var chosenDistributionElement = 0


    /*Use lateinit to avoid having to initialize with some invalid default value like -1 */
    private lateinit var bounds: IntRange

    /**
     * creates wrd model step element without a given activity type
     * may be used when wrddist should not be dependent from activity type
     *
     * @param id
     * @param category
     * @param modelCoordinator
     */
    constructor(id: String, category: Category, modelCoordinator: Coordinator) : this(
        id,
        category,
        ActivityType.UNKNOWN,
        modelCoordinator
    )


    public override fun doStep(): Int {
        // pick a random number within the given boundaries

        chosenDistributionElement = weightedDistribution.getRandomPickFromDistribution(
            this.bounds, modelCoordinator.randomGenerator
        )

        if (modifydistribution) {
            weightedDistribution.modifydistributionelement(chosenDistributionElement)
        }

        return getchosenDistributionElement()
    }


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

    fun getchosenDistributionElement(): Int {
        return chosenDistributionElement
    }

    fun setModifydistribution(modifydistribution: Boolean) {
        this.modifydistribution = modifydistribution
    }
}
