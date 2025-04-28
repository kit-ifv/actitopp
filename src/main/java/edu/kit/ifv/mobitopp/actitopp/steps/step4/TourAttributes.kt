package edu.kit.ifv.mobitopp.actitopp.steps.step4

import edu.kit.ifv.mobitopp.actitopp.HTour

interface TourAttributes {
    fun isFirstTourOfDay(): Boolean
    fun isSecondTourOfDay(): Boolean
    fun isThirdTourOfDay(): Boolean
    fun isBeforeMainTour(): Boolean
}

class TourAttributesByElement(val element: HTour) : TourAttributes {
    override fun isFirstTourOfDay(): Boolean = element.index == element.day.lowestTourIndex
    override fun isSecondTourOfDay(): Boolean = element.index == element.day.lowestTourIndex + 1
    override fun isThirdTourOfDay(): Boolean = element.index == element.day.lowestTourIndex + 2
    override fun isBeforeMainTour(): Boolean = element.index < 0
}