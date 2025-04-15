package edu.kit.ifv.mobitopp.actitopp

import java.text.NumberFormat


/**
 * @author Tim Hilgert
 *
 *
 * object for a discrete choice model step
 */
class DCDefaultModelStep(
    id: String,
    modelFileBase: ModelFileBase,
    private val attributeLookup: AttributeLookup,
    private val randomgenerator: RNGHelper
) :
    AbsHModelStep(id) {
    var decision: Int = -1
        private set
    lateinit var alternativeChosen: String
        private set
    private val choiceFunction: ChoiceFunction = LogitFunction()


    /*
* load model information from file base
*/
    private val modelinfo: DCModelSteplnformation = modelFileBase.getModelInformationforDCStep(id)

    private val alternatives = ArrayList<DCAlternative>()

    //restrict alternatives to a specific range
    var lowerBound: Int = 0
        private set
    var upperBound: Int = -1
        private set


    init {
        /*
         *  create an object for each step alternative
         */
        for (s in modelinfo.alternativesList) {
            alternatives.add(DCAlternative(s))
        }
    }


    /**
     * method to do a dc model step
     */
    public override fun doStep(): Int {
        /*
                 * set rangeLimiter (UpperBound) if not yet determinded
                 */

        upperBound = if (upperBound >= 0) upperBound else alternatives.size

        assert(upperBound >= lowerBound) { "fromRangeLimiter > toRangeLimiter!" }

        /*
         * disable alternatives out of Lower-UpperBound range!
         */
        for (i in alternatives.indices) {
            if (i < lowerBound || i > upperBound) alternatives[i].isEnabled = false
        }

        /*
         * check that there is at least one alternative still enabled
         */
        var alternativeavailable = false
        for (mAlt in alternatives) {
            if (mAlt.isEnabled) alternativeavailable = true
        }
        assert(alternativeavailable) { "no alternative available!" }

        /*
         * initialize utility function for each alternative
         */
        for (mAlt in alternatives) {
            if (mAlt.isEnabled) {
                val uf = mAlt.utilityFunction

                // Loop through all parameters of this alternative
                modelinfo.getParameterValuesforAlternativeDepre(mAlt.name).entries.forEach { (parameterName, parameterValue) ->
                    if (parameterName == "Grundnutzen" || parameterName == "Intercept") {
                        uf.setBaseWeight(parameterValue)
                    } else {
                        val parameterContext = modelinfo.getContextforParameterDepre(parameterName)
                        val attributeValue = attributeLookup.getAttributeValue(
                            parameterContext!!,
                            parameterName
                        )
                        uf.addParameterAttributeCombination(
                            UtilityParameterAttributeCombination(
                                parameterName,
                                parameterValue, attributeValue
                            )
                        )
                    }
                }
            }
        }

        /*
         * determine probabilities of enabled alternatives
         */
        choiceFunction.calculateProbabilities(alternatives)

        /*
         * decide for one alternative
         */
        val randomvalue = randomgenerator.randomValue
        decision = choiceFunction.chooseAlternative(alternatives, randomvalue)
        alternativeChosen = alternatives[decision].name

        // DEBUG USE ONLY
        if (Configuration.debugenabled) {
            printDecisionProcess()
        }

        assert(decision != -1) { "could not make a decision!" }
        return decision
    }

    /**
     * Limits the DC-process to a certain alternative range. this method must be called before doStep() if necessary
     *
     * @param from
     * @param to
     */
    fun limitUpperandLowerBound(from: Int, to: Int) {
        lowerBound = from
        upperBound = to
    }

    /**
     * Limits the DC-process to a certain alternative upperBound. this method must be called before doStep() if necessary
     *
     * @param to
     */
    fun limitUpperBoundOnly(to: Int) {
        upperBound = to
    }

    /**
     * Limits the DC-process to a certain alternative lowerBound. this method must be called before doStep() if necessary
     *
     * @param from
     */
    fun limitLowerBoundOnly(from: Int) {
        lowerBound = from
    }

    /**
     * methode disables an alternative of the set of alternatives.
     * this alternative will not be considered when choosing an alternative.
     *
     * @param name
     */
    fun disableAlternative(name: String) {
        for (ma in alternatives) {
            if (ma.name == name) ma.isEnabled = false
        }
    }

    /**
     * changes the utilityfactor of an alternative based on an alternativename.
     * utilityfactir is used to influence the utility/significance of alternatives
     *
     * @param alternativename
     * @param utilityfactor
     */
    fun adaptUtilityFactor(alternativename: String, utilityfactor: Double) {
        for (ma in alternatives) {
            if (ma.name == alternativename) ma.setUtilityfactor(utilityfactor)
        }
    }


    /**
     * changes the utilityfactor of an alternative based on an alternativeindex.
     * utilityfactir is used to influence the utility/significance of alternatives
     *
     * @param alternativeindex
     * @param utilityfactor
     */
    fun adaptUtilityFactor(alternativeindex: Int, utilityfactor: Double) {
        alternatives[alternativeindex].setUtilityfactor(utilityfactor)
    }

    /**
     * prints the decision process for debug reasons
     */
    fun printDecisionProcess() {
        println("-------- DECISIONS FOR STEP " + this.id + " ---------------")

        for (mAlt in alternatives) {
            if (mAlt.isEnabled) {
                println(
                    "alternative: " + mAlt.name + " prob: " + NumberFormat.getPercentInstance()
                        .format(mAlt.probability) + " - utility: " + mAlt.utility
                )
            }
        }

        println("Chosen alternative: $alternativeChosen")
        println("Random Value: " + randomgenerator.lastRandomValue)
        println("SAVED for: $attributeLookup")
        println()
    }

    /**
     * check if alternative is enabled or not
     *
     * @param name
     * @return
     */
    fun alternativeisEnabled(name: String): Boolean {
        var result = false
        val it: Iterator<DCAlternative> = alternatives.iterator()
        while (it.hasNext()) {
            val ma = it.next()
            if (ma.name == name && ma.isEnabled) result = true
        }
        return result
    }
}
