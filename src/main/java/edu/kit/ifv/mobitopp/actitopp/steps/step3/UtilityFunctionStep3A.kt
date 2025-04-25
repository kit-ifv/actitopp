package edu.kit.ifv.mobitopp.actitopp.steps.step3

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.HDay
import edu.kit.ifv.mobitopp.actitopp.PersonWeekRoutine
import edu.kit.ifv.mobitopp.actitopp.amountOfLaterTours
import edu.kit.ifv.mobitopp.actitopp.amountOfPreviousTours
import edu.kit.ifv.mobitopp.actitopp.hasNoLaterTours
import edu.kit.ifv.mobitopp.actitopp.hasNoPreviousTours
import edu.kit.ifv.mobitopp.actitopp.steps.scrapPath.DayAttributes
import edu.kit.ifv.mobitopp.actitopp.steps.scrapPath.DayAttributesFromElement
import edu.kit.ifv.mobitopp.actitopp.steps.scrapPath.PersonAndRoutineAttributes
import edu.kit.ifv.mobitopp.actitopp.steps.scrapPath.PersonAndRoutineFrom
import edu.kit.ifv.mobitopp.actitopp.steps.scrapPath.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitopp.steps.step1.times
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.AllocatedLogit
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ChoiceSituation
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ModifiableDiscreteChoiceModel
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.initializeWithParameters


interface PreviousDayAttributes : DayAttributes {
    fun previousDayHasNoBeforeTour(): Boolean
    fun previousDayHasOneBeforeTour(): Boolean
    fun previousDayHasNoAfterTour(): Boolean
    fun previousDayHasOneAfterTour(): Boolean
}

class PreviousDayAttributesFromElement(
    val day: HDay,
    private val previousDay: HDay?,
    private val dayAttributesFromElement: DayAttributesFromElement = DayAttributesFromElement(day),
) : PreviousDayAttributes, DayAttributes by dayAttributesFromElement {

    override fun previousDayHasNoBeforeTour(): Boolean = (previousDay?.hasNoPreviousTours()) ?: false

    override fun previousDayHasOneBeforeTour(): Boolean = (previousDay?.amountOfPreviousTours() == 1)

    override fun previousDayHasNoAfterTour(): Boolean = previousDay?.hasNoLaterTours() ?: false

    override fun previousDayHasOneAfterTour(): Boolean = previousDay?.amountOfLaterTours() == 1
}

class PreviousDayAttributesNumeric(
    val day: HDay,
    val previousDayBeforeTours: Int?,
    val previousDayAfterTours: Int?,
    private val dayAttributesFromElement: DayAttributesFromElement = DayAttributesFromElement(
        day
    ),
) : PreviousDayAttributes, DayAttributes by dayAttributesFromElement {
    override fun previousDayHasNoBeforeTour(): Boolean = previousDayBeforeTours == 0

    override fun previousDayHasOneBeforeTour(): Boolean = previousDayBeforeTours == 1

    override fun previousDayHasNoAfterTour(): Boolean = previousDayAfterTours == 0

    override fun previousDayHasOneAfterTour(): Boolean = previousDayAfterTours == 1

}

data class ParameterCollectionStep3A(
    val one: ParameterStep3A,
    val two: ParameterStep3A,
    val three: ParameterStep3A,
    val four: ParameterStep3A,
    val five: ParameterStep3A,
)

data class ParameterStep3A(
    val base: Double,
    val employmentFullTime: Double,
    val mainActivityIsWork: Double,
    val mainActivityIsEducation: Double,
    val mainActivityIsShopping: Double,
    val saturday: Double,
    val sunday: Double,
    val male: Double,
    val age10To17: Double,
    val aged26To35: Double,
    val aged36To50: Double,
    val aged51To60: Double,
    val commuteIn0To5km: Double,
    val averageAmountOfToursIs1: Double,
    val averageAmountOfToursIs2: Double,
    val previousDayHas0TourBeforeMainAct: Double,
    val previousDayHas1TourBeforeMainAct: Double,
)

class PreviousDaySituation private constructor(
    override val choice: Int,
    val previousDayAttributes: PreviousDayAttributes,
    val pAttr: PersonAndRoutineAttributes,
) : ChoiceSituation<Int>(), PreviousDayAttributes by previousDayAttributes, PersonAndRoutineAttributes by pAttr {
    constructor(
        choice: Int,
        day: HDay,
        previousDayBeforeTours: Int?,
        previousDayAfterTours: Int?,
        person: ActitoppPerson,
        weekRoutine: PersonWeekRoutine,
    ) : this(
        choice, PreviousDayAttributesNumeric(day, previousDayBeforeTours, previousDayAfterTours), PersonAndRoutineFrom(
            PersonWithRoutine(person, weekRoutine)
        )
    )
}

val ParameterSet3A = ParameterCollectionStep3A(
    one = ParameterStep3A(
        base = 0.7630,
        employmentFullTime = -0.1760,
        mainActivityIsWork = -1.6877,
        mainActivityIsEducation = -2.5719,
        mainActivityIsShopping = -0.9099,
        saturday = 0.1418,
        sunday = -0.9393,
        male = 0.1119,
        age10To17 = -0.9380,
        aged26To35 = 0.00470,
        aged36To50 = -0.0107,
        aged51To60 = 0.0557,
        commuteIn0To5km = 0.0946,
        averageAmountOfToursIs1 = -2.1886,
        averageAmountOfToursIs2 = -0.8743,
        previousDayHas0TourBeforeMainAct = -0.3108,
        previousDayHas1TourBeforeMainAct = 0.1726,
    ),
    two = ParameterStep3A(
        base = 0.4677,
        employmentFullTime = -0.1081,
        mainActivityIsWork = -2.4572,
        mainActivityIsEducation = -3.3469,
        mainActivityIsShopping = -1.4774,
        saturday = -0.0442,
        sunday = -1.8903,
        male = 0.2344,
        age10To17 = -1.2095,
        aged26To35 = 0.2807,
        aged36To50 = 0.1175,
        aged51To60 = 0.1013,
        commuteIn0To5km = 0.0773,
        averageAmountOfToursIs1 = -4.7503,
        averageAmountOfToursIs2 = -1.7349,
        previousDayHas0TourBeforeMainAct = -0.3532,
        previousDayHas1TourBeforeMainAct = -0.0819,
    ),
    three = ParameterStep3A(
        base = -0.4980,
        employmentFullTime = -0.0125,
        mainActivityIsWork = -2.9908,
        mainActivityIsEducation = -4.6668,
        mainActivityIsShopping = -1.9111,
        saturday = -0.2036,
        sunday = -2.5550,
        male = 0.3343,
        age10To17 = -0.8478,
        aged26To35 = 0.6538,
        aged36To50 = 0.5881,
        aged51To60 = 0.3034,
        commuteIn0To5km = 0.1524,
        averageAmountOfToursIs1 = -7.5700,
        averageAmountOfToursIs2 = -2.7737,
        previousDayHas0TourBeforeMainAct = -0.4455,
        previousDayHas1TourBeforeMainAct = -0.2092,
    ),
    four = ParameterStep3A(
        base = -2.3133,
        employmentFullTime = -0.1058,
        mainActivityIsWork = -3.2268,
        mainActivityIsEducation = -2.8818,
        mainActivityIsShopping = -1.8705,
        saturday = -0.1871,
        sunday = -3.3593,
        male = 0.4994,
        age10To17 = -1.6217,
        aged26To35 = 0.8452,
        aged36To50 = 0.9128,
        aged51To60 = 0.4848,
        commuteIn0To5km = 0.5226,
        averageAmountOfToursIs1 = -16.4474,
        averageAmountOfToursIs2 = -3.4787,
        previousDayHas0TourBeforeMainAct = -0.1075,
        previousDayHas1TourBeforeMainAct = -0.2115,
    ),
    five = ParameterStep3A(
        base = -3.3742,
        employmentFullTime = 0.1436,
        mainActivityIsWork = -3.4376,
        mainActivityIsEducation = -11.8487,
        mainActivityIsShopping = -2.5762,
        saturday = -0.1417,
        sunday = -2.8644,
        male = 0.3232,
        age10To17 = -8.9737,
        aged26To35 = 1.7751,
        aged36To50 = 1.4184,
        aged51To60 = 1.3157,
        commuteIn0To5km = 0.0508,
        averageAmountOfToursIs1 = -16.1372,
        averageAmountOfToursIs2 = -4.3569,
        previousDayHas0TourBeforeMainAct = -0.8014,
        previousDayHas1TourBeforeMainAct = -0.8141,
    ),

    )

val step3AModel =
    ModifiableDiscreteChoiceModel<Int, PreviousDaySituation, ParameterCollectionStep3A>(AllocatedLogit.create {
        option(0) { 0.0 }
        option(1, parameters = { one }, {
            val util = standardUtilityFunction(this, it)
            util
        })
        option(2, parameters = { two }, { standardUtilityFunction(this, it) })
        option(3, parameters = { three }, { standardUtilityFunction(this, it) })
        option(4, parameters = { four }, { standardUtilityFunction(this, it) })
        option(5, parameters = { five }, { standardUtilityFunction(this, it) })
    })

val step3AWithParams = step3AModel.initializeWithParameters(ParameterSet3A)
private val standardUtilityFunction: ParameterStep3A.(PreviousDaySituation) -> Double = {
    base +
            (it.isFulltimeEmployee()) * employmentFullTime +
            (it.mainActivityIsWork()) * mainActivityIsWork +
            (it.mainActivityIsEducation()) * mainActivityIsEducation +
            (it.mainActivityIsShopping()) * mainActivityIsShopping +
            (it.isSaturday()) * saturday +
            (it.isSunday()) * sunday +
            (it.isMale()) * male +
            (it.isAged10To17()) * age10To17 +
            (it.isAged26To35()) * aged26To35 +
            (it.isAged36To50()) * aged36To50 +
            (it.isAged51To60()) * aged51To60 +
            (it.commuteIn0To5km()) * commuteIn0To5km +
            (it.averageAmountOfToursIs1()) * averageAmountOfToursIs1 +
            (it.averageAmountOfToursIs2()) * averageAmountOfToursIs2 +
            (it.previousDayHasNoBeforeTour()) * previousDayHas0TourBeforeMainAct +
            (it.previousDayHasOneBeforeTour()) * previousDayHas1TourBeforeMainAct
}
