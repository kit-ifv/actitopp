package edu.kit.ifv.mobitopp.actitopp.steps.step3

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.HDay
import edu.kit.ifv.mobitopp.actitopp.HTour
import edu.kit.ifv.mobitopp.actitopp.WeekRoutine
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.modernization.DurationDay
import edu.kit.ifv.mobitopp.actitopp.steps.PersonAttributes
import edu.kit.ifv.mobitopp.actitopp.steps.DayAttributes
import edu.kit.ifv.mobitopp.actitopp.steps.DayAttributesFromElement
import edu.kit.ifv.mobitopp.actitopp.steps.DayAttributesFromWeekday
import edu.kit.ifv.mobitopp.actitopp.steps.DayStructureAttributes
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonAndRoutineAttributes
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonAndRoutineFrom
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitopp.steps.RoutineAttributes
import edu.kit.ifv.mobitopp.actitopp.steps.step1.times
import edu.kit.ifv.mobitopp.actitopp.steps.TourAttributes
import edu.kit.ifv.mobitopp.actitopp.steps.TourAttributesByElement
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.AllocatedLogit
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ChoiceSituation
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ModifiableDiscreteChoiceModel
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.initializeWithParameters

val ParameterSet4 = ParameterCollectionStep4(
    work = ParameterStep4(
        base = -1.7501,
        employmentFullTime = -0.9697,
        employmentPartTime = -1.3653,
        employmentNotEarning = -0.1735,
        employmentStudent = -0.3361,
        employmentVocational = -0.7533,
        mainTourWork = 1.1157,
        mainTourEducation = 0.7885,
        mainTourShopping = 0.1578,
        mainTourTransport = -0.1555,
        friday = -0.5263,
        saturday = -1.3667,
        sunday = -1.9997,
        firstTourOfDay = 0.8812,
        secondTourOfDay = 0.4393,
        thirdTourOfDay = 0.1969,
        beforeMainTour = 0.9663,
        noWorkDays = -18.3170,
        noEducationDays = -0.4234,
        noShoppingDays = 0.2598,
        noTransportDays = -0.0111,
        oneWorkDay = -0.9385,
        oneEducationDay = -0.3050,
        oneShoppingDay = 0.1728,
        oneTransportDay = 0.0593,
    ),
    education = ParameterStep4(
        base = -2.5271,
        employmentFullTime = 0.5000,
        employmentPartTime = 0.4529,
        employmentNotEarning = -0.1959,
        employmentStudent = -0.6008,
        employmentVocational = -0.7283,
        mainTourWork = 0.7486,
        mainTourEducation = 0.4766,
        mainTourShopping = 0.3754,
        mainTourTransport = 0.1499,
        friday = -0.7106,
        saturday = -3.0991,
        sunday = -3.6256,
        firstTourOfDay = 1.9136,
        secondTourOfDay = 0.6323,
        thirdTourOfDay = 0.1368,
        beforeMainTour = 0.2895,
        noWorkDays = -0.0728,
        noEducationDays = -18.0594,
        noShoppingDays = -0.3400,
        noTransportDays = 0.1226,
        oneWorkDay = -0.2758,
        oneEducationDay = -0.7765,
        oneShoppingDay = -0.1335,
        oneTransportDay = 0.1842,
    ),
    shopping = ParameterStep4(
        base = -0.5772,
        employmentFullTime = 0.0428,
        employmentPartTime = -0.00410,
        employmentNotEarning = 0.0147,
        employmentStudent = -0.2357,
        employmentVocational = -0.2806,
        mainTourWork = -0.4049,
        mainTourEducation = -0.3622,
        mainTourShopping = 0.1575,
        mainTourTransport = 0.2327,
        friday = 0.0797,
        saturday = -0.0465,
        sunday = -2.0657,
        firstTourOfDay = 1.1478,
        secondTourOfDay = 0.8921,
        thirdTourOfDay = 0.2871,
        beforeMainTour = 0.6183,
        noWorkDays = -0.2003,
        noEducationDays = -0.0225,
        noShoppingDays = -18.7611,
        noTransportDays = -0.0929,
        oneWorkDay = -0.1150,
        oneEducationDay = -0.1458,
        oneShoppingDay = -1.1941,
        oneTransportDay = -0.0387,
    ),
    transport = ParameterStep4(
        base = -0.3648,
        employmentFullTime = 0.4146,
        employmentPartTime = 0.5462,
        employmentNotEarning = 0.5708,
        employmentStudent = 0.0648,
        employmentVocational = 0.0453,
        mainTourWork = -0.2881,
        mainTourEducation = -0.7766,
        mainTourShopping = 0.6828,
        mainTourTransport = 0.7680,
        friday = -0.0518,
        saturday = -0.9960,
        sunday = -1.2149,
        firstTourOfDay = 0.1461,
        secondTourOfDay = 0.1155,
        thirdTourOfDay = 0.0808,
        beforeMainTour = 0.6063,
        noWorkDays = 0.0342,
        noEducationDays = -0.0521,
        noShoppingDays = 0.3509,
        noTransportDays = -16.9907,
        oneWorkDay = 0.0996,
        oneEducationDay = -0.2836,
        oneShoppingDay = 0.1898,
        oneTransportDay = -1.3796,
    )
)


data class ParameterCollectionStep4(

    val work: ParameterStep4,
    val education: ParameterStep4,
    val shopping: ParameterStep4,
    val transport: ParameterStep4,

    )

data class ParameterStep4(
    val base: Double,
    val employmentFullTime: Double,
    val employmentPartTime: Double,
    val employmentNotEarning: Double,
    val employmentStudent: Double,
    val employmentVocational: Double,
    val mainTourWork: Double,
    val mainTourEducation: Double,
    val mainTourShopping: Double,
    val mainTourTransport: Double,
    val friday: Double,
    val saturday: Double,
    val sunday: Double,
    val firstTourOfDay: Double,
    val secondTourOfDay: Double,
    val thirdTourOfDay: Double,
    val beforeMainTour: Double,
    val noWorkDays: Double,
    val noEducationDays: Double,
    val noShoppingDays: Double,
    val noTransportDays: Double,
    val oneWorkDay: Double,
    val oneEducationDay: Double,
    val oneShoppingDay: Double,
    val oneTransportDay: Double,
)

class TourSituation private constructor(
    override val choice: ActivityType, personAndRoutineAttributes: PersonAndRoutineAttributes,
    dayAttributes: DayAttributes, tourAttributes: TourAttributes,
) :
    ChoiceSituation<ActivityType>(), TourAttributes by tourAttributes, PersonAttributes by personAndRoutineAttributes,
    RoutineAttributes by personAndRoutineAttributes, DayAttributes by dayAttributes {

        constructor(choice: ActivityType, person: ActitoppPerson, routine: WeekRoutine, day: HDay, tour: HTour): this(
            choice,
            PersonAndRoutineFrom(PersonWithRoutine(person, routine)),
            DayAttributesFromElement(day),
            TourAttributesByElement(tour)
        )

    constructor(choice: ActivityType, person: ActitoppPerson, routine: WeekRoutine, day: DurationDay, tour: HTour): this(
        choice,
        PersonAndRoutineFrom(PersonWithRoutine(person, routine)),
        DayAttributesFromWeekday(day),
        TourAttributesByElement(tour)
    )

}
class TourSituationInt private constructor(
    override val choice: Int, personAndRoutineAttributes: PersonAndRoutineAttributes,
    dayAttributes: DayStructureAttributes, tourAttributes: TourAttributes,

    ) :
    ChoiceSituation<Int>(), TourAttributes by tourAttributes, PersonAttributes by personAndRoutineAttributes,
    RoutineAttributes by personAndRoutineAttributes, DayStructureAttributes by dayAttributes {

    constructor(choice: Int, person: ActitoppPerson, routine: WeekRoutine, day: HDay, tour: HTour): this(
        choice,
        PersonAndRoutineFrom(PersonWithRoutine(person, routine)),
        DayAttributesFromElement(day),
        TourAttributesByElement(tour)
    )

}

val step4Model =
    ModifiableDiscreteChoiceModel<ActivityType, TourSituation, ParameterCollectionStep4>(AllocatedLogit.create {
        option(ActivityType.EDUCATION, parameters = { education }, { standardUtilityFunction(this, it) })
        option(ActivityType.LEISURE) { 0.0 }
        option(ActivityType.SHOPPING, parameters = { shopping }, { standardUtilityFunction(this, it) })
        option(ActivityType.TRANSPORT, parameters = { transport }, { standardUtilityFunction(this, it) })
        option(ActivityType.WORK, parameters = { work }, { standardUtilityFunction(this, it) })

    })

val step4WithParams = step4Model.initializeWithParameters(ParameterSet4)
private val standardUtilityFunction: ParameterStep4.(TourSituation) -> Double = {
    base +
            (it.isFulltimeEmployee()) * employmentFullTime +
            (it.isParttimeEmployee()) * employmentPartTime +
            (it.isNotEarningMoney()) * employmentNotEarning +
            (it.isStudent()) * employmentStudent +
            (it.isVocational()) * employmentVocational +
            (it.mainActivityIsWork()) * mainTourWork +
            (it.mainActivityIsEducation()) * mainTourEducation +
            (it.mainActivityIsShopping()) * mainTourShopping +
            (it.mainActivityIsTransport()) * mainTourTransport +
            (it.isFriday()) * friday +
            (it.isSaturday()) * saturday +
            (it.isSunday()) * sunday +
            (it.isFirstTourOfDay()) * firstTourOfDay +
            (it.isSecondTourOfDay()) * secondTourOfDay +
            (it.isThirdTourOfDay()) * thirdTourOfDay +
            (it.isBeforeMainTour()) * beforeMainTour +
            (it.amountOfWorkingDaysIs0()) * noWorkDays +
            (it.amountOfEducationDaysIs0()) * noEducationDays +
            (it.amountOfShoppingDaysIs0()) * noShoppingDays +
            (it.amountOfServiceDaysIs0()) * noTransportDays +
            (it.amountOfWorkingDaysIs1()) * oneWorkDay +
            (it.amountOfEducationDaysIs1()) * oneEducationDay +
            (it.amountOfShoppingDaysIs1()) * oneShoppingDay +
            (it.amountOfServiceDaysIs1()) * oneTransportDay
}
