package edu.kit.ifv.mobitopp.actitopp.steps

import edu.kit.ifv.mobitopp.actitopp.HActivity

interface ActivityAttributes {
    fun isBeforeMainActivity(): Boolean
}

class ActivityAttributesByElement(val element: HActivity): ActivityAttributes {
    override fun isBeforeMainActivity(): Boolean = element.index < 0
}