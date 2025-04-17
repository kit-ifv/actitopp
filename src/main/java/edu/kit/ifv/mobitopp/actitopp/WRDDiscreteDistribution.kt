package edu.kit.ifv.mobitopp.actitopp

import java.util.NavigableMap
import java.util.TreeMap
import kotlin.math.max
import kotlin.math.min

class HistogramDistribution(initialMap: Map<Int, Int>) {

    private var distribution : Map<Int, Double>
    init {
        val sum = initialMap.values.sum()
        distribution = initialMap.mapValues { it.value.toDouble() / sum }
    }

    /**
     * I figured out the original code: Roughly translated it says -> Take the probability of the selected element, shift
     * it by a third of the probability of not selecting that element, and update all other elements accordingly
     */
    fun increaseProbabilityFor(element: Int) {
        require(element in distribution) {
            "How did you manage to update an element that you didn't select from the distribution in the first place"
        }
        val original = distribution.getValue(element)
        val nonSelectionProbability = 1 - original
        val updated = original + (1.0/3) * nonSelectionProbability
        val remaining = 1 - updated
        val scalingFactor = remaining / nonSelectionProbability
        val updatedDistribution = distribution.toMutableMap()

        updatedDistribution.forEach { (key, value) ->
            updatedDistribution[key] = value * scalingFactor
        }
        updatedDistribution[element] = updated
        distribution = updatedDistribution

    }
}

/**
 * ROBIN: It appears that this distribution takes a histogram as input. This knowledge is rather helpful.
 * We can assume that the map is sorted along the keys.
 */
class WRDDiscreteDistribution(private val histogram: NavigableMap<Int, Int>) {

    constructor(distributioninformation: WRDModelDistributionInformation) : this(TreeMap(distributioninformation))

    /**
     * No reason not to remember the lowest and highest key, the key structure of this class is never changing, only the values
     */
    private val lowestKey: Int = histogram.keys.min()

    private val highestKey: Int = histogram.keys.max()

    private fun getsumofalldistributionelements(): Int {
        return histogram.values.sum()
    }

    /**
     * method to modify an element of the distribution
     *
     * TODO this method appears to shift the selection probability towards the selected element "IF" the element has
     *   been selected and the modification is allowed. From the first appearance it seems to boost the selection
     *   probability of the selected element by 50% relative selection probability. This means that the map could
     *   internally be represented by the selection probabilities, instead of the amount of occurences.
     *
     *   UPDATE IT IS 33.3% not 50%. Try to guess that from the code below
     */

    fun modifydistributionelement(slot: Int) {
        val oldvalue = histogram[slot]!!
        val newvalue = oldvalue + (0.5 * getsumofalldistributionelements()).toInt()
        histogram[slot] = newvalue
    }

    /**
     * returns an element from the distribution based on a random number
     * WRD = weighted random draw - the selection of the element is dependent on their share within the distribution
     *
     */
    fun getRandomPickFromDistribution(bounds: IntRange, randomgenerator: RNGHelper): Int {
        //Phase1: check and apply bounds
        /*Issue 1) The assignment to usedLowerBound /upperbound can be solved via max/min, instead of assignment -> if
          Issue 2) Assert statements have two effects: Disabled -> Useless, Enabled -> Error. Why can we pass lowerbound
          and upper bound as parameters, if the software always (wih assertions) collapses if the input parameters would
          actually limit the method. In that case we could simply skip this process of using upper and lower bounds.
          Issue 3) upperbound and lowerbound are integers, and upperbound could be smaller than lowerbound. This can be
          avoided altogether if the input passed to this method is not two naked Ints, but a range.
           */
        // In order to honor the legacy code, let's throw an exception if the range is somehow empty
        require(!bounds.isEmpty()) {
            "Cannot operate on an invalid bounds $bounds"
        }
        val usedLowerBound = max(lowestKey, bounds.first)
        val usedUpperBound = min(highestKey, bounds.last)


        //Phase2: get random value
        val rand = randomgenerator.randomValue

        //Phase 3: create a map with valid elements (within the boundaries) and their accumulated share (according to all valid elements)
        /* Issue 4) The sum of valid distribution elements, and the check if the slot from the histogram lies within the
           bounds of the range later in the code could be trivially simplified by just filtering the histogram.
        * */
        val relevantElements = histogram.filterKeys { it in usedLowerBound..usedUpperBound }
        val sumofvalidelements = relevantElements.values.sum()

        // if all element values are equal to zero, choose one of them randomly
        if (sumofvalidelements == 0) {
            return randomgenerator.getRandomValueBetween(usedLowerBound, usedUpperBound, 1)
        }
        var acc = 0.0
        val normalizedValues = relevantElements.mapValues { acc += it.value.toDouble() / sumofvalidelements; acc }
        /* Issue 5) A detailed analysis of the original code showed that the selection of a firstslot / lastslot would
        ALWAYS result in the same element being used for both slots, this is most likely not what the author intended
        BUT, it is what the author has written. This behaviour can be easily be reproduced by just returning the first
        element with a cumulative sum larger than the random value.

         */
        val selectedElement = normalizedValues.entries.firstOrNull{it.value > rand}?.key ?: normalizedValues.keys.last()
        return selectedElement

    }
}
