package edu.kit.ifv.mobitopp.actitopp

import java.util.Collections
import java.util.NavigableMap
import java.util.SortedMap
import java.util.TreeMap
import kotlin.math.max
import kotlin.math.min

class MutableDistributionEntry(var int: Int) {

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

    private fun getsumofalldistributionelements(lowerbound: Int, upperbound: Int): Int {
        return histogram.filterKeys { it in lowerbound..upperbound }.values.sum()
    }


    /**
     * method to modify an element of the distribution
     *
     * @param slot
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
        val sumofvalidelements = getsumofalldistributionelements(usedLowerBound, usedUpperBound)
        val validelements = TreeMap<Int, Double>()
        var runningshare = 0.0

        var firstslot = -1
        var lastslot = -1

        // if all element values are equal to zero, choose one of them randomly
        if (sumofvalidelements == 0) {
            firstslot = usedLowerBound
            lastslot = usedUpperBound
        } else {
            for ((slot, amount) in histogram) {
                if (slot >= usedLowerBound && slot <= usedUpperBound) {
                    //check if the rand value lies between the runninshare of the last slot and the actual slot

                    if (rand >= runningshare) firstslot = slot

                    //update runningsahre / accumulated share for the distribution element
                    val share = amount.toDouble() / sumofvalidelements.toDouble()
                    runningshare += share
                    validelements[slot] = runningshare

                    //check if the slot ist the last value where rand is smaller than the runningshare
                    if (lastslot == -1 && rand <= runningshare) lastslot = slot
                }
            }
            assert(Math.round(runningshare).toDouble() == 1.0) { "sum of valid element share is not equal to 1!" }
        }

        assert(firstslot != -1) { "could not determine firstslot for randomPick" }
        assert(lastslot != -1) { "could not determine lastslot for randomPick" }

        //choose one of the possible slots
        return randomgenerator.getRandomValueBetween(firstslot, lastslot, 1)
    }
}
