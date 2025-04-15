package edu.kit.ifv.mobitopp.actitopp


abstract class AbsInvalidPatternException : Exception() {
    abstract var reason: String?

    abstract var errorType: String?

    abstract var faultyPattern: HWeekPattern?

    abstract var involvedActivities: Array<HActivity?>?

    companion object {
        private const val serialVersionUID = -3495343767758041143L
    }
}
