package edu.kit.ifv.mobitopp.actitopp.steps.scrapPath

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.HDay
import edu.kit.ifv.mobitopp.actitopp.PersonWeekRoutine
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.enums.Employment
import edu.kit.ifv.mobitopp.actitopp.steps.PersonAttributes
import edu.kit.ifv.mobitopp.actitopp.steps.PersonAttributesFromElement
import edu.kit.ifv.mobitopp.actitopp.steps.step1.times
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.AllocatedLogit
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ChoiceSituation
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ModifiableDiscreteChoiceModel
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.initializeWithParameters
import java.time.DayOfWeek

data class PersonWithRoutine(
    val person: ActitoppPerson,
    val routine: PersonWeekRoutine,
) {
    fun amountOfWorkingDays() = routine.amountOfWorkingDays
    fun amountOfLeisureDays() = routine.amountOfLeisureDays
    fun amountOfEducationDays() = routine.amountOfEducationDays
    fun amountOfShoppingDays() = routine.amountOfShoppingDays
    fun amountOfServiceDays() = routine.amountOfServiceDays
    fun amountOfImmobileDays() = routine.amountOfImmobileDays
    fun has5WorkDays() = routine.amountOfWorkingDays == 5
    fun has5EducationDays() = routine.amountOfEducationDays == 5
}

interface DayAttributes {
    fun isMonday(): Boolean
    fun isTuesday(): Boolean
    fun isWednesday(): Boolean
    fun isThursday(): Boolean
    fun isFriday(): Boolean
    fun isSaturday(): Boolean
    fun isSunday(): Boolean

    fun amountOfBeforeTours(): Int

    fun mainActivityIsWork(): Boolean
    fun mainActivityIsEducation(): Boolean
    fun mainActivityIsShopping(): Boolean
}

class DayAttributesFromElement(private val element: HDay) : DayAttributes {
    override fun isMonday() = element.weekday == DayOfWeek.MONDAY
    override fun isTuesday() = element.weekday == DayOfWeek.TUESDAY
    override fun isWednesday() = element.weekday == DayOfWeek.WEDNESDAY
    override fun isThursday() = element.weekday == DayOfWeek.THURSDAY
    override fun isFriday() = element.weekday == DayOfWeek.FRIDAY
    override fun isSaturday() = element.weekday == DayOfWeek.SATURDAY
    override fun isSunday() = element.weekday == DayOfWeek.SUNDAY

    override fun amountOfBeforeTours(): Int = -1 * element.lowestTourIndex

    override fun mainActivityIsWork(): Boolean = element.mainTourType == ActivityType.WORK
    override fun mainActivityIsEducation(): Boolean = element.mainTourType == ActivityType.EDUCATION
    override fun mainActivityIsShopping(): Boolean = element.mainTourType == ActivityType.SHOPPING
}


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

}

class RoutineAttributesFromElement(val element: PersonWeekRoutine) : RoutineAttributes {
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
}

interface PersonAndRoutineAttributes: PersonAttributes, RoutineAttributes
data class PersonAndRoutineFrom(
    val element: PersonWithRoutine,
    private val routine: RoutineAttributes = RoutineAttributesFromElement(element.routine),
    private val person: PersonAttributes = PersonAttributesFromElement(element.person)
):PersonAndRoutineAttributes,  RoutineAttributes by routine, PersonAttributes by person

class DaySituation(
    override val choice: ActivityType,
    private val personRoutine: PersonWithRoutine,
    val day: HDay,
    private val personAttributesFromElement: PersonAndRoutineFrom = PersonAndRoutineFrom(
        personRoutine
    ),
    private val dayAttributesFromElement: DayAttributesFromElement = DayAttributesFromElement(day),
) :
    ChoiceSituation<ActivityType>(), PersonAttributes by personAttributesFromElement, RoutineAttributes by personAttributesFromElement,
    DayAttributes by dayAttributesFromElement {


}

val ParameterSet2A = ParameterCollectionStep2A(
    work = ParametersStep2A(
        base = -0.5374,
        employmentFullTime = 0.6741,
        employmentPartTime = 0.7520,
        employmentNotEarning = 0.1923,
        employmentStudent = 0.3334,
        employmentVocational = 0.6364,
        amountOfYouths = 0.0157,
        has5WorkingDays = -0.3172,
        has5EducationDays = 0.5653,
        tuesday = 0.2455,
        wednesday = 0.2724,
        thursday = 0.3519,
        friday = -0.0849,
        saturday = -4.2897,
        sunday = -6.0544,
        amountOfWorkdays = 1.1204,
        amountOfEducationDays = 0.1160,
        amountOfLeisureDays = 0.0258,
        amountOfShoppingDays = -0.00111,
        amountOfTransportDays = 0.0311,
        amountOfImmobileDays = -1.0048,
    ),
    education = ParametersStep2A(
        base = -2.0857,
        employmentFullTime = -0.1152,
        employmentPartTime = -0.2732,
        employmentNotEarning = 0.2275,
        employmentStudent = 0.5730,
        employmentVocational = 1.0611,
        amountOfYouths = -0.00777,
        has5WorkingDays = -0.3606,
        has5EducationDays = -0.2815,
        tuesday = 0.2964,
        wednesday = 0.1432,
        thursday = 0.1816,
        friday = -0.1789,
        saturday = -6.5275,
        sunday = -8.4008,
        amountOfWorkdays = 0.3398,
        amountOfEducationDays = 1.5550,
        amountOfLeisureDays = 0.0524,
        amountOfShoppingDays = -0.00581,
        amountOfTransportDays = -0.00406,
        amountOfImmobileDays = -0.8921,
    ),
    leisure = ParametersStep2A(
        base = 1.9943,
        employmentFullTime = -0.0312,
        employmentPartTime = -0.0150,
        employmentNotEarning = -0.0105,
        employmentStudent = 0.0280,
        employmentVocational = 0.1044,
        amountOfYouths = 0.0319,
        has5WorkingDays = 0.00941,
        has5EducationDays = -0.0548,
        tuesday = 0.1002,
        wednesday = 0.2222,
        thursday = 0.3170,
        friday = 0.3957,
        saturday = 0.1398,
        sunday = -0.3538,
        amountOfWorkdays = -0.1783,
        amountOfEducationDays = -0.2711,
        amountOfLeisureDays = 0.3393,
        amountOfShoppingDays = -0.0681,
        amountOfTransportDays = -0.0126,
        amountOfImmobileDays = -1.1550,
    ),
    shopping = ParametersStep2A(
        base = 3.3805,
        employmentFullTime = 0.000393,
        employmentPartTime = 0.0385,
        employmentNotEarning = -0.00451,
        employmentStudent = -0.1266,
        employmentVocational = -0.0696,
        amountOfYouths = 0.0364,
        has5WorkingDays = 0.0477,
        has5EducationDays = 0.0109,
        tuesday = -0.0684,
        wednesday = -0.0216,
        thursday = 0.1976,
        friday = 0.2751,
        saturday = -0.4959,
        sunday = -3.4272,
        amountOfWorkdays = -0.2604,
        amountOfEducationDays = -0.3485,
        amountOfLeisureDays = -0.2464,
        amountOfShoppingDays = 0.2289,
        amountOfTransportDays = -0.0602,
        amountOfImmobileDays = -1.2861,
    ),
    transport = ParametersStep2A(
        base = 2.0037,
        employmentFullTime = 0.0729,
        employmentPartTime = -0.0390,
        employmentNotEarning = 0.0550,
        employmentStudent = 0.1582,
        employmentVocational = 0.2911,
        amountOfYouths = -0.1479,
        has5WorkingDays = -0.1548,
        has5EducationDays = -0.3484,
        tuesday = 0.0244,
        wednesday = 0.1071,
        thursday = 0.2112,
        friday = 0.2118,
        saturday = -1.0866,
        sunday = -1.8189,
        amountOfWorkdays = -0.2783,
        amountOfEducationDays = -0.2864,
        amountOfLeisureDays = -0.2315,
        amountOfShoppingDays = -0.2012,
        amountOfTransportDays = 0.5990,
        amountOfImmobileDays = -1.4004,
    ),
)

data class ParameterCollectionStep2A(
    val work: ParametersStep2A,
    val education: ParametersStep2A,
    val leisure: ParametersStep2A,
    val shopping: ParametersStep2A,
    val transport: ParametersStep2A,

    )

data class ParametersStep2A(
    val base: Double,
    val employmentFullTime: Double,
    val employmentPartTime: Double,
    val employmentNotEarning: Double,
    val employmentStudent: Double,
    val employmentVocational: Double,
    val amountOfYouths: Double,
    val has5WorkingDays: Double,
    val has5EducationDays: Double,
    val tuesday: Double,
    val wednesday: Double,
    val thursday: Double,
    val friday: Double,
    val saturday: Double,
    val sunday: Double,
    val amountOfWorkdays: Double,
    val amountOfEducationDays: Double,
    val amountOfLeisureDays: Double,
    val amountOfShoppingDays: Double,
    val amountOfTransportDays: Double,
    val amountOfImmobileDays: Double,
)

val step2AModel =
    ModifiableDiscreteChoiceModel<ActivityType, DaySituation, ParameterCollectionStep2A>(AllocatedLogit.create {
        option(ActivityType.EDUCATION, parameters = { education }, { standardUtilityFunction(this, it) })
        option(ActivityType.HOME) {
            0.0
        }
        option(ActivityType.LEISURE, parameters = { leisure }, { standardUtilityFunction(this, it) })
        option(ActivityType.SHOPPING, parameters = { shopping }, { standardUtilityFunction(this, it) })
        option(ActivityType.TRANSPORT, parameters = { transport }, { standardUtilityFunction(this, it) })
        option(ActivityType.WORK, parameters = { work }, {
            standardUtilityFunction(this, it)
        }
        )


    })


val coordinatedStep2AModel =
    ModifiableDiscreteChoiceModel<ActivityType, DaySituation, ParameterCollectionStep2A>(AllocatedLogit.create {
        option(ActivityType.EDUCATION, parameters = { education }, {
            (if (it.isStudentOrAzubi() && it.day.isStandardWorkingDay()) 1.3 else 1.0) * standardUtilityFunction(
                this,
                it
            )
        })
        option(ActivityType.HOME) {
            0.0
        }
        option(ActivityType.LEISURE, parameters = { leisure }, { standardUtilityFunction(this, it) })
        option(ActivityType.SHOPPING, parameters = { shopping }, { standardUtilityFunction(this, it) })
        option(ActivityType.TRANSPORT, parameters = { transport }, { standardUtilityFunction(this, it) })
        option(ActivityType.WORK, parameters = { work }, {
            (if (it.isEmployedAnywhere() && it.day.isStandardWorkingDay()) 1.3 else 1.0) * standardUtilityFunction(
                this,
                it
            )
        }
        )


    })

val coordinatedStep2AWithParams = coordinatedStep2AModel.initializeWithParameters(ParameterSet2A)
val step2AWithParams = step2AModel.initializeWithParameters(ParameterSet2A)
private val standardUtilityFunction: ParametersStep2A.(DaySituation) -> Double = {
    base +
            (it.isFulltimeEmployee()) * employmentFullTime +
            (it.isParttimeEmployee()) * employmentPartTime +
            (it.isNotEarningMoney()) * employmentNotEarning +
            (it.isStudent()) * employmentStudent +
            (it.isVocational()) * employmentVocational +
            (it.amountOfYouthsInHousehold()) * amountOfYouths +
            (it.has5WorkDays()) * has5WorkingDays +
            (it.has5EducationDays()) * has5EducationDays +
            (it.isTuesday()) * tuesday +
            (it.isWednesday()) * wednesday +
            (it.isThursday()) * thursday +
            (it.isFriday()) * friday +
            (it.isSaturday()) * saturday +
            (it.isSunday()) * sunday +
            (it.amountOfWorkingDays()) * amountOfWorkdays +
            (it.amountOfEducationDays()) * amountOfEducationDays +
            (it.amountOfLeisureDays()) * amountOfLeisureDays +
            (it.amountOfShoppingDays()) * amountOfShoppingDays +
            (it.amountOfServiceDays()) * amountOfTransportDays +
            (it.amountOfImmobileDays()) * amountOfImmobileDays
}
