package edu.kit.ifv.mobitopp.actitopp.steps.step6

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.HActivity
import edu.kit.ifv.mobitopp.actitopp.HDay
import edu.kit.ifv.mobitopp.actitopp.HTour
import edu.kit.ifv.mobitopp.actitopp.WeekRoutine
import edu.kit.ifv.mobitopp.actitopp.enums.ActivityType
import edu.kit.ifv.mobitopp.actitopp.modernization.BidirectionalIndexedValue
import edu.kit.ifv.mobitopp.actitopp.modernization.DayStructure
import edu.kit.ifv.mobitopp.actitopp.modernization.PlannedTourAmounts
import edu.kit.ifv.mobitopp.actitopp.modernization.Position
import edu.kit.ifv.mobitopp.actitopp.modernization.TourStructure
import edu.kit.ifv.mobitopp.actitopp.steps.ActivityAttributes
import edu.kit.ifv.mobitopp.actitopp.steps.ActivityAttributesByElement
import edu.kit.ifv.mobitopp.actitopp.steps.DayAttributesFromElement
import edu.kit.ifv.mobitopp.actitopp.steps.DayAttributesFromStructure
import edu.kit.ifv.mobitopp.actitopp.steps.FullyQualifiedDayStructureAttributes
import edu.kit.ifv.mobitopp.actitopp.steps.PersonAttributes
import edu.kit.ifv.mobitopp.actitopp.steps.RoutineAttributes
import edu.kit.ifv.mobitopp.actitopp.steps.TourAttributes
import edu.kit.ifv.mobitopp.actitopp.steps.TourAttributesByElement
import edu.kit.ifv.mobitopp.actitopp.steps.TourAttributesByIndexedStructure
import edu.kit.ifv.mobitopp.actitopp.steps.TourAttributesByStructAndNumbers
import edu.kit.ifv.mobitopp.actitopp.steps.step1.times
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonAndRoutineAttributes
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonAndRoutineFrom
import edu.kit.ifv.mobitopp.actitopp.steps.step2.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.AllocatedLogit
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ChoiceSituation
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ModifiableDiscreteChoiceModel
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.initializeWithParameters

val ParameterSet6 = ParameterCollectionStep6(
    work = ParameterStep6(
        base = -1.6982,
        tourliegtvorhaupttour = 0.2652,
        tourliegtnachhaupttour = -0.0328,
        aktliegtvorhauptakt = 0.8281,
        tourtyp_work = 2.9160,
        tourtyp_education = 1.4195,
        tourtyp_shopping = 1.0241,
        tourtyp_transport = 0.8938,
        tag_fr = -0.2467,
        tag_sa = -1.4383,
        tag_so = -1.7615,
        haupttour_work = -0.5643,
        haupttour_education = -0.2211,
        haupttour_shopping = 0.2582,
        haupttour_transport = 0.2261,
        tourhat2akt = -1.1914,
        tourhat3akt = -0.3814,
        tourhat4akt = -0.4260,
        anzahl_arbeitstage0 = -16.2867,
        anzahl_bildungstage0 = 0.2050,
        anzahl_shoppingtage0 = 0.5511,
        anzahl_transporttage0 = 0.2282,
        anzahl_arbeitstage1 = -0.7809,
        anzahl_bildungstage1 = 0.5050,
        anzahl_shoppingtage1 = 0.4023,
        anzahl_transporttage1 = 0.1984,
    ),
    education = ParameterStep6(
        base = -1.2102,
        tourliegtvorhaupttour = -0.1735,
        tourliegtnachhaupttour = -1.2508,
        aktliegtvorhauptakt = 0.9232,
        tourtyp_work = 1.1085,
        tourtyp_education = 1.3120,
        tourtyp_shopping = 0.8789,
        tourtyp_transport = -0.1551,
        tag_fr = -0.5384,
        tag_sa = -3.0813,
        tag_so = -2.7102,
        haupttour_work = -0.1757,
        haupttour_education = -0.8118,
        haupttour_shopping = -0.2551,
        haupttour_transport = -0.3626,
        tourhat2akt = -0.0548,
        tourhat3akt = 0.4142,
        tourhat4akt = 0.0536,
        anzahl_arbeitstage0 = -0.1357,
        anzahl_bildungstage0 = -20.7038,
        anzahl_shoppingtage0 = -0.1372,
        anzahl_transporttage0 = 0.2432,
        anzahl_arbeitstage1 = -0.1555,
        anzahl_bildungstage1 = -1.3224,
        anzahl_shoppingtage1 = -0.0549,
        anzahl_transporttage1 = 0.3539,
    ),
    shopping = ParameterStep6(
        base = -0.2896,
        tourliegtvorhaupttour = 0.4685,
        tourliegtnachhaupttour = -0.1368,
        aktliegtvorhauptakt = -0.1613,
        tourtyp_work = 0.9678,
        tourtyp_education = 0.6501,
        tourtyp_shopping = 1.7814,
        tourtyp_transport = 1.1717,
        tag_fr = -0.00215,
        tag_sa = -0.3740,
        tag_so = -2.0753,
        haupttour_work = -0.0343,
        haupttour_education = -0.1284,
        haupttour_shopping = -0.1403,
        haupttour_transport = -0.2957,
        tourhat2akt = 0.4346,
        tourhat3akt = 0.1806,
        tourhat4akt = 0.1775,
        anzahl_arbeitstage0 = -0.0357,
        anzahl_bildungstage0 = 0.1476,
        anzahl_shoppingtage0 = -16.8443,
        anzahl_transporttage0 = -0.1487,
        anzahl_arbeitstage1 = -0.0262,
        anzahl_bildungstage1 = 0.1196,
        anzahl_shoppingtage1 = -1.2287,
        anzahl_transporttage1 = -0.0937,
    ),
    transport = ParameterStep6(
        base = -0.5102,
        tourliegtvorhaupttour = 0.3259,
        tourliegtnachhaupttour = 0.3171,
        aktliegtvorhauptakt = 0.5881,
        tourtyp_work = 1.0137,
        tourtyp_education = 0.5672,
        tourtyp_shopping = 1.1928,
        tourtyp_transport = 1.3146,
        tag_fr = -0.0264,
        tag_sa = -0.6922,
        tag_so = -0.9313,
        haupttour_work = 0.0241,
        haupttour_education = -0.2241,
        haupttour_shopping = -0.0995,
        haupttour_transport = -0.0195,
        tourhat2akt = 0.3532,
        tourhat3akt = 0.4179,
        tourhat4akt = 0.1415,
        anzahl_arbeitstage0 = -0.0887,
        anzahl_bildungstage0 = 0.0895,
        anzahl_shoppingtage0 = 0.2009,
        anzahl_transporttage0 = -17.9573,
        anzahl_arbeitstage1 = -0.1132,
        anzahl_bildungstage1 = -0.1142,
        anzahl_shoppingtage1 = 0.1095,
        anzahl_transporttage1 = -1.3423,
    )
)


data class ParameterCollectionStep6(

    val work: ParameterStep6,
    val education: ParameterStep6,
    val shopping: ParameterStep6,
    val transport: ParameterStep6,
)

data class ParameterStep6(
    val base: Double,
    val tourliegtvorhaupttour: Double,
    val tourliegtnachhaupttour: Double,
    val aktliegtvorhauptakt: Double,
    val tourtyp_work: Double,
    val tourtyp_education: Double,
    val tourtyp_shopping: Double,
    val tourtyp_transport: Double,
    val tag_fr: Double,
    val tag_sa: Double,
    val tag_so: Double,
    val haupttour_work: Double,
    val haupttour_education: Double,
    val haupttour_shopping: Double,
    val haupttour_transport: Double,
    val tourhat2akt: Double,
    val tourhat3akt: Double,
    val tourhat4akt: Double,
    val anzahl_arbeitstage0: Double,
    val anzahl_bildungstage0: Double,
    val anzahl_shoppingtage0: Double,
    val anzahl_transporttage0: Double,
    val anzahl_arbeitstage1: Double,
    val anzahl_bildungstage1: Double,
    val anzahl_shoppingtage1: Double,
    val anzahl_transporttage1: Double,


    )

class ActivitySituation private constructor(
    override val choice: ActivityType,
    personAndRoutineAttributes: PersonAndRoutineAttributes,
    dayAttributes: FullyQualifiedDayStructureAttributes,
    tourAttributes: TourAttributes,
    activityAttributes: ActivityAttributes,
) :
    ChoiceSituation<ActivityType>(), TourAttributes by tourAttributes, PersonAttributes by personAndRoutineAttributes,
    RoutineAttributes by personAndRoutineAttributes, FullyQualifiedDayStructureAttributes by dayAttributes,
    ActivityAttributes by activityAttributes {

    constructor(
        choice: ActivityType,
        person: ActitoppPerson,
        routine: WeekRoutine,
        day: HDay,
        tour: HTour,
        activity: HActivity,
    ) : this(
        choice,
        PersonAndRoutineFrom(PersonWithRoutine(person, routine)),
        DayAttributesFromElement(day),
        TourAttributesByElement(tour),
        ActivityAttributesByElement(activity)
    )

    constructor(
        choice: ActivityType,
        personWithRoutine: PersonWithRoutine,
        dayStructure: DayStructure,
        tourStructure: BidirectionalIndexedValue<TourStructure>,
        position: Position,
        plannedTourAmounts: PlannedTourAmounts,
    ) : this(
        choice,
        PersonAndRoutineFrom(personWithRoutine),
        DayAttributesFromStructure(dayStructure),
        TourAttributesByStructAndNumbers(tourStructure, plannedTourAmounts.precursorAmount, plannedTourAmounts.successorAmount),
        ActivityAttributes { position == Position.BEFORE }
    )

}

val step6Model =
    ModifiableDiscreteChoiceModel<ActivityType, ActivitySituation, ParameterCollectionStep6>(AllocatedLogit.create {
        option(ActivityType.WORK, parameters = { work }) { standardUtilityFunction(this, it) }
        option(ActivityType.EDUCATION, parameters = { education }) { standardUtilityFunction(this, it) }
        option(ActivityType.LEISURE) { 0.0 }
        option(ActivityType.SHOPPING, parameters = { shopping }) { standardUtilityFunction(this, it) }
        option(ActivityType.TRANSPORT, parameters = { transport }) { standardUtilityFunction(this, it) }

    })

val step6WithParams = step6Model.initializeWithParameters(ParameterSet6)
private val standardUtilityFunction: ParameterStep6.(ActivitySituation) -> Double = {
    base +
            (it.isBeforeMainTour()) * tourliegtvorhaupttour +
            (it.isAfterMainTour()) * tourliegtnachhaupttour +
            (it.isBeforeMainActivity()) * aktliegtvorhauptakt +
            (it.tourMainActivityIsWork()) * tourtyp_work +
            (it.tourMainActivityIsEducation()) * tourtyp_education +
            (it.tourMainActivityIsShopping()) * tourtyp_shopping +
            (it.tourMainActivityIsTransport()) * tourtyp_transport +
            (it.isFriday()) * tag_fr +
            (it.isSaturday()) * tag_sa +
            (it.isSunday()) * tag_so +
            (it.dayMainActivityIsWork()) * haupttour_work +
            (it.dayMainActivityIsEducation()) * haupttour_education +
            (it.dayMainActivityIsShopping()) * haupttour_shopping +
            (it.dayMainActivityIsTransport()) * haupttour_transport +
            (it.tourHas2Activities()) * tourhat2akt +
            (it.tourHas3Activities()) * tourhat3akt +
            (it.tourHas4Activities()) * tourhat4akt +
            (it.amountOfWorkingDaysIs0()) * anzahl_arbeitstage0 +
            (it.amountOfEducationDaysIs0()) * anzahl_bildungstage0 +
            (it.amountOfShoppingDaysIs0()) * anzahl_shoppingtage0 +
            (it.amountOfServiceDaysIs0()) * anzahl_transporttage0 +
            (it.amountOfWorkingDaysIs1()) * anzahl_arbeitstage1 +
            (it.amountOfEducationDaysIs1()) * anzahl_bildungstage1 +
            (it.amountOfShoppingDaysIs1()) * anzahl_shoppingtage1 +
            (it.amountOfServiceDaysIs1()) * anzahl_transporttage1


}
