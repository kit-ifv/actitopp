package edu.kit.ifv.mobitopp.actitopp

/**
 * object from this class represent modeling elements for linear regression models consisting of:
 *
 *
 * - name : name of the parameter, e.g. alter10bis17
 * - contextIdentifier: corresponding context, e.g. person, day, tour, ...
 * - estimateValue : value of the estimate for regression calculation (readed as input from file base)
 * - attributevalue : value of the attribute (determined at runtime for the corresponding object, e.g. person)
 *
 *
 * attributevalue is initialized with -99999. During the modeling execution it will be overwritten with the runtime value
 *
 * @author Tim Hilgert
 */
class LinRegEstimate(
    /**
     * @param name the name to set
     */
    var name: String,
    value: Double,
    /**
     * @param contextIdentifier the contextIdentifier to set
     */
    var contextIdentifier: String
) {
    /**
     * @return the name
     */
    /**
     * @return the contextIdentifier
     */
    val estimateValue = value
    var attributeValue = -99999.0


    override fun toString(): String {
        return "$name ($contextIdentifier) estimate value:$estimateValue attribute value: $attributeValue"
    }
}
