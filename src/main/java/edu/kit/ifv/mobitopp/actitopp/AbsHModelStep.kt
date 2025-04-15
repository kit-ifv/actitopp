package edu.kit.ifv.mobitopp.actitopp

abstract class AbsHModelStep(protected var id: String) {
    protected abstract fun doStep(): Int
}
