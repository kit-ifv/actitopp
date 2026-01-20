package edu.kit.ifv.mobitopp.actitoppNG

import edu.kit.ifv.mobitopp.actitoppNG.enums.AreaType

/**
 * The legacy ActiTopp household holds two fields for the number of children(0-10) and youths(0..<18) as they are
 * not designed to be explicitly modelled as [Person] for some obscure reason. Even though actiTopp claims that it
 * is only unable to produce plans for children and is able to produce plans for youths.
 */
class ActiToppHousehold(

    private val numMinorsUpTo10: Int,
    private val numMinorsBelow18: Int,
    override val areaType: AreaType,
    override val numberOfCars: Int = 0,
) : Household {


    override val id: Int = ID
    override fun amountOfYoungMinors(): Int = numMinorsUpTo10
    override fun amountOfAllMinors() = numMinorsBelow18

    override val members: MutableList<Person> = mutableListOf()
    fun add(person: Person) = members.add(person)


    companion object {
        var ID = 0
            get() = field++
            private set
    }
}
