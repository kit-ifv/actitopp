package edu.kit.ifv.mobitopp.actitopp.changes

import edu.kit.ifv.mobitopp.actitopp.ActitoppPersonModifierFields
import edu.kit.ifv.mobitopp.actitopp.enums.AreaType
import edu.kit.ifv.mobitopp.actitopp.enums.Employment
import edu.kit.ifv.mobitopp.actitopp.enums.Gender
import edu.kit.ifv.mobitopp.actitopp.enums.isEarning
import edu.kit.ifv.mobitopp.actitopp.enums.isNotEarning
import edu.kit.ifv.mobitopp.actitopp.enums.isParttime
import edu.kit.ifv.mobitopp.actitopp.enums.isStudent
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ChoiceSituation

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
    fun amountOfServiceDays() = personModifiers.amountOfServiceDays
}