package edu.kit.ifv.mobitopp.actitoppNG.steps

import edu.kit.ifv.mobitopp.actitoppNG.Person
import edu.kit.ifv.mobitopp.actitoppNG.enums.Employment
import edu.kit.ifv.mobitopp.actitoppNG.enums.Gender
import edu.kit.ifv.mobitopp.actitoppNG.enums.isEarning
import edu.kit.ifv.mobitopp.actitoppNG.enums.isEmployedAnywhere
import edu.kit.ifv.mobitopp.actitoppNG.enums.isNotEarning
import edu.kit.ifv.mobitopp.actitoppNG.enums.isParttime
import edu.kit.ifv.mobitopp.actitoppNG.enums.isStudent
import edu.kit.ifv.mobitopp.actitoppNG.enums.isStudentOrAzubi
import edu.kit.ifv.mobitopp.actitoppNG.weekroutine.WeekRoutine

/**
 * This interface is a rewrite of the actitoppPersonParameters Enum, and exists only to be able to cross reference the
 * code. Once the rewrite is complete most of these conditions could be inlined in the utility functions.
 */
interface PersonAttributes {
    fun isFulltimeEmployee(): Boolean
    fun isParttimeEmployee(): Boolean
    fun isEarningMoney(): Boolean
    fun isNotEarningMoney(): Boolean
    fun isEmployedAnywhere(): Boolean
    fun isStudentOrAzubi(): Boolean
    fun isStudent(): Boolean
    fun isRetired(): Boolean
    fun isVocational(): Boolean
    fun isAged10To17(): Boolean
    fun isAged18To25(): Boolean
    fun isAged26To35(): Boolean
    fun isAged36To50(): Boolean
    fun isAged51To60(): Boolean
    fun isAged61To70(): Boolean
    fun isAged18To35(): Boolean
    fun isMale(): Boolean
    fun commuteIn0To5km(): Boolean
    fun commuteIn5To10km(): Boolean
    fun commuteIn10To20km(): Boolean
    fun commuteIn20To50km(): Boolean
    fun commuteOver50km(): Boolean
}

class PersonAttributesFromElement(val person: Person) : PersonAttributes {

    override fun isFulltimeEmployee() = person.employment == Employment.FULLTIME
    override fun isParttimeEmployee() = person.employment.isParttime()
    override fun isEarningMoney() = person.employment.isEarning()
    override fun isNotEarningMoney() = person.employment.isNotEarning()
    override fun isStudent() = person.employment.isStudent()
    override fun isRetired() = person.employment == Employment.RETIRED
    override fun isVocational() = person.employment == Employment.VOCATIONAL
    override fun isAged10To17() = person.age in 10..17
    override fun isAged18To25() = person.age in 18..25
    override fun isAged26To35() = person.age in 26..35
    override fun isAged36To50() = person.age in 36..50
    override fun isAged51To60() = person.age in 51..60
    override fun isAged61To70() = person.age in 61..70
    override fun isAged18To35(): Boolean = person.age in 18..35
    override fun isMale() = person.gender == Gender.MALE
    override fun commuteIn0To5km() = person.maxCommute in 0.0..5.0
    override fun commuteIn5To10km() = person.maxCommute in 5.0..10.0
    override fun commuteIn10To20km() = person.maxCommute in 10.0..20.0
    override fun commuteIn20To50km() = person.maxCommute in 2.0..50.0
    override fun commuteOver50km() = person.maxCommute > 50.0


    override fun isEmployedAnywhere(): Boolean = person.employment.isEmployedAnywhere()
    override fun isStudentOrAzubi(): Boolean {
        return person.employment.isStudentOrAzubi()
    }
}


class PersonAlternative private constructor(

    private val weekRoutine: WeekRoutine,
    val person: Person,
    attributes: PersonAttributesFromElement,
    householdAttributes: HouseholdAttributes,
) : PersonAttributes by attributes, HouseholdAttributes by householdAttributes {


    constructor(weekRoutine: WeekRoutine, person: Person) : this(
        weekRoutine,
        person,
        PersonAttributesFromElement(person),
        HouseholdAttributesFromElement(person.household)
    )


    fun amountOfWorkingDays() = weekRoutine.amountOfWorkingDays
    fun amountOfLeisureDays() = weekRoutine.amountOfLeisureDays
    fun amountOfEducationDays() = weekRoutine.amountOfEducationDays
    fun amountOfShoppingDays() = weekRoutine.amountOfShoppingDays

}

