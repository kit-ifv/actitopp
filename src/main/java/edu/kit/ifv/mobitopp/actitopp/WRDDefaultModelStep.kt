package edu.kit.ifv.mobitopp.actitopp

/**
 * @author Tim Hilgert
 *
 *
 * object for a wrd (weighted random draw) model step
 */
class WRDDefaultModelStep(
    id: String, // category to get a random draw
    private val category: String, activityType: ActivityType, // surrounding modelCoordinator for this step
    private val modelCoordinator: Coordinator
) :
    AbsHModelStep(id) {
    // activitytype for personal, activity type specific distributions
    private val activityType: ActivityType

    // Distribution element that is used to pick a random number
    private var wrddist: WRDDiscreteDistribution?

    // decision if the distribution should be adapted after drawing
    private var modifydistribution = false

    // element that is finally chosen based on weighted random draw
    private var chosenDistributionElement = 0

    //limits the range in which a number should be picked randomly.
    private var lowerBoundLimiter = -1
    private var upperBoundLimiter = -1

    /**
     * @param id
     * @param category
     * @param activityType
     * @param modelCoordinator
     */
    init {
        this.activityType = activityType

        // check if a personalized distribution for this id, category and activity type already exists. If not, create one.
        this.wrddist = modelCoordinator.getpersonalWRDdistribution(id, category, activityType)
        if (wrddist == null) {
            val modelstep = modelCoordinator.fileBase.getModelInformationforWRDStep(id)
            val distributioninformation = modelstep!!.getWRDDistribution(category)
            wrddist = WRDDiscreteDistribution(distributioninformation!!)

            modelCoordinator.addpersonalWRDdistribution(id, category, activityType, wrddist!!)
        }
    }

    /**
     * creates wrd model step element without a given activity type
     * may be used when wrddist should not be dependent from activity type
     *
     * @param id
     * @param category
     * @param modelCoordinator
     */
    constructor(id: String, category: String, modelCoordinator: Coordinator) : this(
        id,
        category,
        ActivityType.UNKNOWN,
        modelCoordinator
    )


    public override fun doStep(): Int {
        // pick a random number within the given boundaries

        chosenDistributionElement = wrddist!!.getRandomPickFromDistribution(
            this.lowerBoundLimiter,
            this.upperBoundLimiter, modelCoordinator.randomGenerator
        )

        if (modifydistribution) {
            wrddist!!.modifydistributionelement(chosenDistributionElement)
        }

        return getchosenDistributionElement()
    }


    fun printDecisionProcess() {
        println("--------------- MC-Simulation @ " + this.id + this.category + this.activityType + " ---------------")
        println("From " + this.lowerBoundLimiter)
        println("To " + this.upperBoundLimiter)
        println("Random Value: " + modelCoordinator.randomGenerator.lastRandomValue)
        println("Chosen: $chosenDistributionElement")
        println("")
    }

    /**
     * @param lowerbound
     * @param upperbound
     */
    fun setRangeBounds(lowerbound: Int, upperbound: Int) {
        this.lowerBoundLimiter = lowerbound
        this.upperBoundLimiter = upperbound
    }

    fun getchosenDistributionElement(): Int {
        return chosenDistributionElement
    }

    fun setModifydistribution(modifydistribution: Boolean) {
        this.modifydistribution = modifydistribution
    }
}
