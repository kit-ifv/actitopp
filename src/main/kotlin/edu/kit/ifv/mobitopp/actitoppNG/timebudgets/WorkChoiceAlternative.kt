package edu.kit.ifv.mobitopp.actitoppNG.timebudgets

import edu.kit.ifv.mobitopp.actitoppNG.Person
import edu.kit.ifv.mobitopp.actitoppNG.steps.PersonAttributes
import edu.kit.ifv.mobitopp.actitoppNG.steps.PersonAttributesFromElement

class WorkChoiceAlternative private constructor(
    val personAttributes: PersonAttributes,
    val patternAttributes: FinalizedPatternAttributes,
) :
    PersonAttributes by personAttributes,
    FinalizedPatternAttributes by patternAttributes {
    constructor(finalizedPattern: FinalizedActivityPattern, person: Person) : this(
        PersonAttributesFromElement(person),
        PatternAttributesByElement(finalizedPattern)
    )

}