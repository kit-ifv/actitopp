package edu.kit.ifv.mobitopp.actitopp


/**
 * @author Tim Hilgert
 * This Exception is thrown if the weekly pattern of a person is invalid.
 * In that case, it is advised to redo the whole activity creation process but with a different seed.
 *
 *
 * Depending on the source of the error, only one person or the whole household needs to be remodeled
 */
class InvalidPatternException
/**
 * @return the errortype
 */(
    /**
     * @param errortype the errortype to set
     */
    // Household or Person
    override var errorType: String?,
    override var faultyPattern: HWeekPattern?, override var reason: String?,
    override var involvedActivities: Array<HActivity?>? = arrayOf()
) :
    AbsInvalidPatternException() {
    init {
        println("Creating")
    }
    companion object {
        private const val serialVersionUID = -3030772908826568766L
    }
}
