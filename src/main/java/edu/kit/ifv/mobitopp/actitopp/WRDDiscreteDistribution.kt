package edu.kit.ifv.mobitopp.actitopp

import java.util.Collections
import java.util.NavigableMap
import java.util.SortedMap
import java.util.TreeMap

class MutableDistributionEntry(var int: Int) {

}

/**
 * ROBIN: It appears that this distribution takes a histogram as input. This knowledge is rather helpful.
 * We can assume that the map is sorted along the keys.
 */
class WRDDiscreteDistribution(private val histogram: NavigableMap<Int, Int>) {

    constructor(distributioninformation: WRDModelDistributionInformation): this(TreeMap(distributioninformation))

    /**
     * No reason not to remember the lowest and highest key, the key structure of this class is never changing, only the values
     */
    private val lowestKey: Int = histogram.keys.min()

    private val highestKey: Int = histogram.keys.max()
    /**
     * return the sum of all distributionselements
     *
     * @return
     */
    private fun getsumofalldistributionelements(): Int {
        var sum = 0
        for ((_, value) in histogram) {
            sum += value
        }
        assert(sum != 0) { "sum is zero : no entries in distribution?" }
        return sum
    }

    /**
     * returns the sum of all distribtionselements within the given boundaries
     *
     * @param lowerbound
     * @param upperbound
     * @return
     */
    private fun getsumofalldistributionelements(lowerbound: Int, upperbound: Int): Int {
        var sum = 0
        for ((key, value) in histogram) {
            if (key >= lowerbound && key <= upperbound) sum += value
        }
        return sum
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
     * @param lowerbound
     * @param upperbound
     * @param randomgenerator
     * @return
     */
    fun getRandomPickFromDistribution(lowerbound: Int, upperbound: Int, randomgenerator: RNGHelper): Int {
        //Phase1: check and apply bounds

        var usedLowerBound = lowestKey
        var usedUpperBound = highestKey

        if (lowerbound != -1 && upperbound != -1) {
            // make sure that boundaries determined by preconditions fit the boundaries of the wrd distribution
            assert(lowerbound <= usedUpperBound) { "inconsistent boundaries! lowerBound from preconditions: $lowerbound does not match wrd distributions boundaries: $usedLowerBound - $usedUpperBound" }
            assert(upperbound >= usedLowerBound) { "inconsistent boundaries! upperBound from preconditions: $upperbound does not match wrd distributions boundaries: $usedLowerBound - $usedUpperBound" }
            assert(lowerbound <= upperbound) { "inconsistent boundaries! upperbound < lowerbound!" }

            if (lowerbound >= usedLowerBound) usedLowerBound = lowerbound
            if (upperbound <= usedUpperBound) usedUpperBound = upperbound
        }


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
