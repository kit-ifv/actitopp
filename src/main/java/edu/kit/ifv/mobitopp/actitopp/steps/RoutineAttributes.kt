package edu.kit.ifv.mobitopp.actitopp.steps

import edu.kit.ifv.mobitopp.actitopp.WeekRoutine

interface RoutineAttributes {

    fun amountOfWorkingDays(): Int
    fun amountOfLeisureDays(): Int
    fun amountOfEducationDays(): Int
    fun amountOfShoppingDays(): Int
    fun amountOfServiceDays(): Int
    fun amountOfImmobileDays(): Int
    fun has5WorkDays(): Boolean
    fun has5EducationDays(): Boolean

    fun averageAmountOfToursIs1(): Boolean
    fun averageAmountOfToursIs2(): Boolean
    fun amountOfWorkingDaysIs0(): Boolean
    fun amountOfLeisureDaysIs0(): Boolean
    fun amountOfEducationDaysIs0(): Boolean
    fun amountOfShoppingDaysIs0(): Boolean
    fun amountOfServiceDaysIs0(): Boolean

    fun amountOfWorkingDaysIs1(): Boolean
    fun amountOfLeisureDaysIs1(): Boolean
    fun amountOfEducationDaysIs1(): Boolean
    fun amountOfShoppingDaysIs1(): Boolean
    fun amountOfServiceDaysIs1(): Boolean

    fun averageAmountOfActivitiesIs1(): Boolean
    fun averageAmountOfActivitiesIs2(): Boolean
    fun averageAmountOfActivitiesIs3(): Boolean


}

class RoutineAttributesFromElement(val element: WeekRoutine) : RoutineAttributes {
    override fun amountOfWorkingDays() = element.amountOfWorkingDays
    override fun amountOfLeisureDays() = element.amountOfLeisureDays
    override fun amountOfEducationDays() = element.amountOfEducationDays
    override fun amountOfShoppingDays() = element.amountOfShoppingDays
    override fun amountOfServiceDays() = element.amountOfServiceDays
    override fun amountOfImmobileDays() = element.amountOfImmobileDays
    override fun has5WorkDays() = element.amountOfWorkingDays == 5
    override fun has5EducationDays() = element.amountOfEducationDays == 5
    override fun averageAmountOfToursIs1(): Boolean = element.averageAmountOfTours == 1

    override fun averageAmountOfToursIs2(): Boolean = element.averageAmountOfTours == 2

    override fun amountOfWorkingDaysIs0(): Boolean = element.amountOfWorkingDays == 0
    override fun amountOfLeisureDaysIs0(): Boolean = element.amountOfLeisureDays == 0
    override fun amountOfEducationDaysIs0(): Boolean = element.amountOfEducationDays == 0
    override fun amountOfShoppingDaysIs0(): Boolean = element.amountOfShoppingDays == 0
    override fun amountOfServiceDaysIs0(): Boolean = element.amountOfServiceDays == 0
    override fun amountOfWorkingDaysIs1(): Boolean = element.amountOfWorkingDays == 1
    override fun amountOfLeisureDaysIs1(): Boolean = element.amountOfLeisureDays == 1
    override fun amountOfEducationDaysIs1(): Boolean = element.amountOfEducationDays == 1
    override fun amountOfShoppingDaysIs1(): Boolean = element.amountOfShoppingDays == 1
    override fun amountOfServiceDaysIs1(): Boolean = element.amountOfServiceDays == 1

    override fun averageAmountOfActivitiesIs1(): Boolean = element.averageAmountOfActivities == 1
    override fun averageAmountOfActivitiesIs2(): Boolean = element.averageAmountOfActivities == 2
    override fun averageAmountOfActivitiesIs3(): Boolean = element.averageAmountOfActivities == 3
}