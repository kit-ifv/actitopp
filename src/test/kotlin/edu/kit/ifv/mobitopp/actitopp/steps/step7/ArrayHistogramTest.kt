package edu.kit.ifv.mobitopp.actitopp.steps.step7

import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class ArrayHistogramTest {
    @Test
    fun boundedSelectionTakesProperElements() {
        val histogram = ArrayHistogram(offset = 0, listOf(1, 1, 1, 1, 1))
        assertEquals(histogram[0], 0.2)
        assertEquals(histogram[2], 0.2)

        assertEquals(histogram.select(0.5), 2)

        assertEquals(histogram.select(0.45, lowerBoundInclusive = 0, upperBoundInclusive = 1), 0)
    }
}