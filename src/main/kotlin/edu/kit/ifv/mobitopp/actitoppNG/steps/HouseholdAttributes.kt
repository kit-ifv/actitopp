package edu.kit.ifv.mobitopp.actitoppNG.steps

import edu.kit.ifv.mobitopp.actitoppNG.Household
import edu.kit.ifv.mobitopp.actitoppNG.enums.AreaType

interface HouseholdAttributes {
    fun areaTypeRural(): Boolean
    fun areaTypeConurbation(): Boolean
    fun hasChildrenInHousehold(): Boolean
    fun amountOfChildrenInHousehold(): Int
    fun hasYouthsInHousehold(): Boolean
    fun amountOfYouthsInHousehold(): Int
    fun amountOfPKW(): Int
}

class HouseholdAttributesFromElement(val household: Household) : HouseholdAttributes {
    override fun areaTypeRural() = household.areaType == AreaType.RURAL
    override fun areaTypeConurbation() = household.areaType == AreaType.CONURBATION
    override fun hasChildrenInHousehold() = household.amountOfYoungMinors() > 0
    override fun amountOfChildrenInHousehold() = household.amountOfYoungMinors()
    override fun hasYouthsInHousehold() = household.amountOfAllMinors() > 0
    override fun amountOfYouthsInHousehold() = household.amountOfAllMinors()
    override fun amountOfPKW(): Int = household.numberOfCars
}