package edu.kit.ifv.mobitopp.actitopp.steps

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.ActitoppPersonModifierFields
import edu.kit.ifv.mobitopp.actitopp.enums.AreaType
import edu.kit.ifv.mobitopp.actitopp.enums.Employment
import edu.kit.ifv.mobitopp.actitopp.enums.Gender
import edu.kit.ifv.mobitopp.actitopp.enums.isEarning
import edu.kit.ifv.mobitopp.actitopp.enums.isNotEarning
import edu.kit.ifv.mobitopp.actitopp.enums.isParttime
import edu.kit.ifv.mobitopp.actitopp.enums.isStudent
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ChoiceSituation
import kotlin.math.max

open class PersonSituation(override val choice: Int, val personModifiers: ActitoppPersonModifierFields): ChoiceSituation<Int>() {
    val person = personModifiers.original
    fun householdHasChildrenBelow10() = person.children0_10 > 0
    fun isFulltimeEmployee() = person.employment == Employment.FULLTIME
    fun isParttimeEmployee() = person.employment.isParttime()
    fun isEarningMoney() = person.employment.isEarning()
    fun isNotEarningMoney() = person.employment.isNotEarning()
    fun isStudent() = person.employment.isStudent()
    fun isRetired() = person.employment == Employment.RETIRED
    fun isVocational() = person.employment == Employment.VOCATIONAL
    fun isAged10To17() = person.age in 10..17
    fun isAged18To25() = person.age in 18..25
    fun isAged26To35() = person.age in 26..35
    fun isAged36To50() = person.age in 36..50
    fun isAged51To60() = person.age in 51..60
    fun isAged61To70() = person.age in 61..70

    fun areaTypeRural() = person.areatype == AreaType.RURAL
    fun areaTypeConurbation() = person.areatype == AreaType.CONURBATION

    fun hasChildrenInHousehold() = person.children0_10 > 0
    fun amountOfChildrenInHousehold() = person.children0_10
    fun hasYouthsInHousehold() = person.children_u18 > 0
    fun amountOfYouthsInHousehold() = person.children_u18
    fun isMale() = person.gender == Gender.MALE
    fun amountOfWorkingDays() = personModifiers.amountOfWorkingDays
    fun amountOfLeisureDays() = personModifiers.amountOfLeisureDays
    fun amountOfEducationDays() = personModifiers.amountOfEducationDays
    fun amountOfShoppingDays() = personModifiers.amountOfShoppingDays
    fun amountOfServiceDays() = personModifiers.amountOfServiceDays

    fun commuteIn0To5km() = person.maxCommute in 0.0..5.0
    fun commuteIn5To10km() = person.maxCommute in 5.0..10.0
    fun commuteIn10To20km() = person.maxCommute in 10.0..20.0
    fun commuteIn20To50km() = person.maxCommute in 2.0..50.0
    fun commuteOver50km() = person.maxCommute > 50.0


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

val ActitoppPerson.maxCommute: Double get()  {
    return max(commutingdistance_work, commutingdistance_education)
}