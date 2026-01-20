package edu.kit.ifv.mobitopp.actitoppNG.utils

/**
 * A bidirectional indexed element has the original element, but also the position in the element queue as absolute position
 * (i.e. the first element has index 0, which is the first precursor element or the main element if no precursors exist)
 *
 * Also the element knows its position relative to the main element.
 */

class BidirectionalIndexedValue<out T : Any>(

    val absoluteIndex: Int,
    val offset: Int,
    val element: T,
) {
    val position = Position.fromRelativeIndex(absoluteIndex - offset)

    override fun toString(): String {
        return "($element, $position indices = [$absoluteIndex, $offset])"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is BidirectionalIndexedValue<*>) return false
        return (element == other.element)
    }

    override fun hashCode(): Int {
        return element.hashCode()
    }
}

