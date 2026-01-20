package edu.kit.ifv.mobitopp.actitoppNG.utils

/**
 * A bidirectional collection extends bidirectionally from a main element, where precursor elements are added to the
 * front and successor elements to the back of the ordering of the structure.
 */
interface BidirectionalCollection<T : Any> : Collection<T> {
    fun amountOfElements(): Int
    fun amountOfPrecursorElements(): Int
    fun amountOfSuccessorElements(): Int

    //    val size: Int
    operator fun get(index: Int = 0): T
    fun mainElement() = get(0)
    fun indexedElements(): Collection<BidirectionalIndexedValue<T>>

    fun elements(): Collection<T>

    fun precursors(): Collection<T>
    fun successors(): Collection<T>

}