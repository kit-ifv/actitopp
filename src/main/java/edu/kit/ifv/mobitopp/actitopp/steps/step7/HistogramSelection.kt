package edu.kit.ifv.mobitopp.actitopp.steps.step7

interface HistogramSelection {

    fun select(randomNumber: Double, finalizedActivityPattern: FinalizedActivityPattern): ArrayHistogram
}