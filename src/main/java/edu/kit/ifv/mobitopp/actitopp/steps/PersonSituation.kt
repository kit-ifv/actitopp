package edu.kit.ifv.mobitopp.actitopp.steps

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.ActitoppPersonModifierFields
import edu.kit.ifv.mobitopp.actitopp.enums.AreaType
import edu.kit.ifv.mobitopp.actitopp.enums.Employment
import edu.kit.ifv.mobitopp.actitopp.enums.Gender
import edu.kit.ifv.mobitopp.actitopp.enums.isEarning
import edu.kit.ifv.mobitopp.actitopp.enums.isEmployedAnywhere
import edu.kit.ifv.mobitopp.actitopp.enums.isNotEarning
import edu.kit.ifv.mobitopp.actitopp.enums.isParttime
import edu.kit.ifv.mobitopp.actitopp.enums.isStudent
import edu.kit.ifv.mobitopp.actitopp.enums.isStudentOrAzubi
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ChoiceSituation
import kotlin.math.max

/**
 * This interface is a rewrite of the actitoppPersonParameters Enum, and exists only to be able to cross reference the
 * code. Once the rewrite is complete most of these conditions could be inlined in the utility functions.
 */
interface PersonAttributes {
    fun householdHasChildrenBelow10(): Boolean
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
    fun areaTypeRural(): Boolean
    fun areaTypeConurbation(): Boolean
    fun hasChildrenInHousehold(): Boolean
    fun amountOfChildrenInHousehold(): Int
    fun hasYouthsInHousehold(): Boolean
    fun amountOfYouthsInHousehold(): Int
    fun isMale(): Boolean
    fun commuteIn0To5km(): Boolean
    fun commuteIn5To10km(): Boolean
    fun commuteIn10To20km(): Boolean
    fun commuteIn20To50km(): Boolean
    fun commuteOver50km(): Boolean
}

class PersonAttributesFromElement(val person: ActitoppPerson) : PersonAttributes {
    override fun householdHasChildrenBelow10() = person.children0_10 > 0
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
    override fun areaTypeRural() = person.areatype == AreaType.RURAL
    override fun areaTypeConurbation() = person.areatype == AreaType.CONURBATION
    override fun hasChildrenInHousehold() = person.children0_10 > 0
    override fun amountOfChildrenInHousehold() = person.children0_10
    override fun hasYouthsInHousehold() = person.children_u18 > 0
    override fun amountOfYouthsInHousehold() = person.children_u18
    override fun isMale() = person.gender == Gender.MALE
    override fun commuteIn0To5km() = person.maxCommute in 0.0..5.0
    override fun commuteIn5To10km() = person.maxCommute in 5.0..10.0
    override fun commuteIn10To20km() = person.maxCommute in 10.0..20.0
    override fun commuteIn20To50km() = person.maxCommute in 2.0..50.0
    override fun commuteOver50km() = person.maxCommute > 50.0

    override     fun isEmployedAnywhere(): Boolean = person.employment.isEmployedAnywhere()
    override fun isStudentOrAzubi(): Boolean {
        return person.employment.isStudentOrAzubi()
    }
}

open class PersonSituation(
    override val choice: Int,
    val personModifiers: ActitoppPersonModifierFields,
    attributes: PersonAttributesFromElement = PersonAttributesFromElement(personModifiers.original),
) : ChoiceSituation<Int>(), PersonAttributes by attributes {
    val person = personModifiers.original

    fun amountOfWorkingDays() = personModifiers.amountOfWorkingDays
    fun amountOfLeisureDays() = personModifiers.amountOfLeisureDays
    fun amountOfEducationDays() = personModifiers.amountOfEducationDays
    fun amountOfShoppingDays() = personModifiers.amountOfShoppingDays
    fun amountOfServiceDays() = personModifiers.amountOfServiceDays


    fun debug(): String {
        return mapOf(
            "householdHasChildrenBelow10" to householdHasChildrenBelow10(),
            "isFulltimeEmployee" to isFulltimeEmployee(),
            "isParttimeEmployee" to isParttimeEmployee(),
            "isEarningMoney" to isEarningMoney(),
            "isNotEarningMoney" to isNotEarningMoney(),
            "isStudent" to isStudent(),
            "isRetired" to isRetired(),
            "isVocational" to isVocational(),
            "isAged10To17" to isAged10To17(),
            "isAged18To25" to isAged18To25(),
            "isAged26To35" to isAged26To35(),
            "isAged36To50" to isAged36To50(),
            "isAged51To60" to isAged51To60(),
            "isAged61To70" to isAged61To70(),
            "areaTypeRural" to areaTypeRural(),
            "areaTypeConurbation" to areaTypeConurbation(),
            "hasChildrenInHousehold" to hasChildrenInHousehold(),
            "amountOfChildrenInHousehold" to amountOfChildrenInHousehold(),
            "hasYouthsInHousehold" to hasYouthsInHousehold(),
            "amountOfYouthsInHousehold" to amountOfYouthsInHousehold(),
            "isMale" to isMale(),
//        "amountOfWorkingDays" to amountOfWorkingDays(),
//        "amountOfLeisureDays" to amountOfLeisureDays(),
//        "amountOfEducationDays" to amountOfEducationDays(),
//        "amountOfServiceDays" to amountOfServiceDays(),
        ).entries.joinToString(separator = "\n") { "${it.key}: ${it.value}" }
    }
}

val ActitoppPerson.maxCommute: Double
    get() {
        return max(commutingdistance_work, commutingdistance_education)
    }