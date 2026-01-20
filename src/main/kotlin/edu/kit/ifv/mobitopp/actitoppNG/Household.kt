package edu.kit.ifv.mobitopp.actitoppNG

import edu.kit.ifv.mobitopp.actitoppNG.enums.AreaType

interface Household {
    val members: List<Person>
    val areaType: AreaType
    val numberOfCars: Int
    val id: Int

    /**
     * Returns the number of children in the household that are considered small children. Typically of age 0-10 (inclusive)
     * It is up to the implementation whether these children are added to the [members] field or not. But some choice
     * models require the number of children, which this method provides. The reason why the children may not be added
     * to the members list is that legacy actiTopp claimed to be unable to provide mobility plans for children of these
     * ages.
     */
    fun amountOfYoungMinors(): Int
    fun amountOfAllMinors(): Int
}

