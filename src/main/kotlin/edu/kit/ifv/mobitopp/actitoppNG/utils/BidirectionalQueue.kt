package edu.kit.ifv.mobitopp.actitoppNG.utils

abstract class BidirectionalQueue<T : Any>(mainElement: T) : BidirectionalCollection<T> {
    private val queue: ArrayDeque<T> = ArrayDeque()
    private var offset = 0

    init {
        // In order to avoid inheritance issues, the queue is made private and the required main element is added here.
        queue.add(mainElement)
    }

    fun addPrecursor(element: T) {
        queue.addFirst(element)
        offset++
    }

    fun addSuccessor(element: T) {
        queue.addLast(element)
    }

    override fun amountOfElements() = queue.size
    override fun amountOfPrecursorElements() = offset
    override fun amountOfSuccessorElements() =
        queue.size - (offset + 1) // The main tour does not count towards the successosrs, thus + 1

    /**
     * Access the elements using a relative index centered around the main element.
     */
    override operator fun get(index: Int) = queue[index + offset]

    override fun indexedElements(): Collection<BidirectionalIndexedValue<T>> {
        return queue.withIndex().map { (index, value) -> BidirectionalIndexedValue(index, offset, value) }
    }

    override fun precursors(): Collection<T> {
        return queue.subList(0, offset)
    }

    override fun successors(): Collection<T> {
        return queue.subList(offset + 1, queue.size)
    }

    override fun elements(): Collection<T> {
        return queue
    }

    override val size: Int
        get() = queue.size

    override fun contains(element: T) = queue.contains(element)
    override fun iterator(): Iterator<T> {
        return queue.iterator()
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return queue.containsAll(elements)
    }

    override fun isEmpty(): Boolean {
        return queue.isEmpty()
    }

}