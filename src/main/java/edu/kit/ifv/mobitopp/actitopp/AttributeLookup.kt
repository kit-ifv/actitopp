package edu.kit.ifv.mobitopp.actitopp

import edu.kit.ifv.mobitopp.actitopp.enums.ActitoppPersonParameters
import edu.kit.ifv.mobitopp.actitopp.enums.HActivityParameters
import edu.kit.ifv.mobitopp.actitopp.enums.HDayParameters
import edu.kit.ifv.mobitopp.actitopp.enums.HTourParameters

/**
 * @author Tim Hilgert
 */
class AttributeLookup(var currentPerson: ActitoppPerson) {
    lateinit var currentDay: HDay
    lateinit var currentTour: HTour
    lateinit var currentActivity: HActivity

    /**
     * constructor
     *
     * @param currentPerson
     * @param currentDay
     */
    constructor(currentPerson: ActitoppPerson, currentDay: HDay) : this(currentPerson) {
        this.currentDay = currentDay
    }

    /**
     * constructor
     *
     * @param currentPerson
     * @param currentDay
     * @param currentTour
     */
    constructor(currentPerson: ActitoppPerson, currentDay: HDay, currentTour: HTour) : this(
        currentPerson,
        currentDay
    ) {
        this.currentTour = currentTour
    }

    /**
     * constructor
     *
     * @param currentPerson
     * @param currentDay
     * @param currentTour
     * @param currentActivity
     */
    constructor(
        currentPerson: ActitoppPerson,
        currentDay: HDay,
        currentTour: HTour,
        currentActivity: HActivity
    ) : this(currentPerson, currentDay, currentTour) {
        this.currentActivity = currentActivity
    }
    private val cache: MutableMap<Pair<String, String>, Double> = mutableMapOf()
    /**
     * Get AttributeValue for specific reference
     *
     * @param reference
     * @param attributeName
     * @return
     */
    fun getAttributeValue(reference: String, attributeName: String): Double {
        if(cache.containsKey(reference to attributeName)) { return cache.getValue(reference to attributeName)}
        var attributeValue = 999999.0
        assert(reference == "default" || reference == "person" || reference == "day" || reference == "tour" || reference == "activity") { "Unknown reference Value - $reference" }

        when (reference) {
            "default", "person" -> {
                attributeValue = ActitoppPersonParameters.getPersonParameterFromString(attributeName).getAttribute(
                    currentPerson
                )
            }

            "day" -> {
                attributeValue = HDayParameters.getDayParameterFromString(attributeName).getAttribute(currentDay)
            }

            "tour" -> {
                attributeValue = HTourParameters.getTourParameterFromString(attributeName).getAttribute(currentTour)
            }

            "activity" -> {
                attributeValue = HActivityParameters.getActivityParameterFromString(attributeName).getAttribute(currentActivity)
            }
        }

        assert(attributeValue != 999999.0) { "AttributeValue couldn't be read! - Reference: $reference - Attribute: $attributeName" }
        cache[reference to attributeName] = attributeValue
        return attributeValue
    }

    override fun toString(): String {
        var personindex = "n.a."
        var daynumber = "n.a."
        var tournr = "n.a."
        var aktnr = "n.a."

        personindex = "" + currentPerson.persIndex
        daynumber = "" + currentDay.weekday
        tournr = "" + currentTour.index
        aktnr = "" + currentActivity.index

        return " Personindex: $personindex // Weekday: $daynumber // Tourindex: $tournr // Actindex: $aktnr"
    }
}
