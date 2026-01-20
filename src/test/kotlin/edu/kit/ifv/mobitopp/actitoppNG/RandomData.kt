package edu.kit.ifv.mobitopp.actitoppNG

import edu.kit.ifv.mobitopp.actitoppNG.enums.Category
import edu.kit.ifv.mobitopp.actitoppNG.enums.ActivityType
import edu.kit.ifv.mobitopp.actitoppNG.enums.AreaType
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitoppNG.modernization.DayStructure
import edu.kit.ifv.mobitopp.actitoppNG.modernization.DurationDay
import edu.kit.ifv.mobitopp.actitoppNG.modernization.LinkedActivity
import edu.kit.ifv.mobitopp.actitoppNG.modernization.ModernizedActivity
import edu.kit.ifv.mobitopp.actitoppNG.modernization.ModernizedTrip
import edu.kit.ifv.mobitopp.actitoppNG.modernization.ModifiableDayStructure
import edu.kit.ifv.mobitopp.actitoppNG.modernization.MutableTourStructure
import edu.kit.ifv.mobitopp.actitoppNG.modernization.PlannedTourAmounts
import edu.kit.ifv.mobitopp.actitoppNG.modernization.durations.MobilityPlanInputs
import edu.kit.ifv.mobitopp.actitoppNG.modernization.plan.ActualMobilityPlan
import edu.kit.ifv.mobitopp.actitoppNG.modernization.plan.MobilityPlan
import edu.kit.ifv.mobitopp.actitoppNG.modernization.plan.MovingDayPlanInput
import edu.kit.ifv.mobitopp.actitoppNG.modernization.plan.StandardCommuteDurations
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.FinalizedActivityPattern
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.FinalizedActivityPatternImpl
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.TimeBudgets
import edu.kit.ifv.mobitopp.actitoppNG.utils.Position
import edu.kit.ifv.mobitopp.actitoppNG.weekroutine.ModifiableWeekRoutine
import edu.kit.ifv.mobitopp.actitoppNG.weekroutine.WeekRoutine
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

private val default = Random(1)
fun randomHousehold(random: Random = default): ActiToppHousehold {
    return ActiToppHousehold(
        random.nextInt(0, 10),
        random.nextInt(0, 10),
        AreaType.fromCode(random.nextInt(0, 10)),
        random.nextInt(0, 10)
    )
}

fun randomHouseholds(amount: Int, random: Random = default): List<ActiToppHousehold> {
    return (0..<amount).map { randomHousehold(random) }
}

fun ActiToppHousehold.generatePersons(amount: Int, random: Random = default): List<ActitoppPerson> {
    return (0..<amount).map {
        this.randomPerson(random)
    }
}

fun ActiToppHousehold.randomPerson(random: Random = default): ActitoppPerson {
    return ActitoppPerson(
        household = this,
        attributes = PersonAttributes.random(random),
    )
}


fun randomWeekRoutine(random: Random = default): WeekRoutine {
    return ModifiableWeekRoutine().apply {

        amountOfWorkingDays = random.nextInt(0, 10)
        amountOfEducationDays = random.nextInt(0, 10)
        amountOfLeisureDays = random.nextInt(0, 10)
        amountOfShoppingDays = random.nextInt(0, 10)
        amountOfServiceDays = random.nextInt(0, 10)
        amountOfImmobileDays = random.nextInt(0, 10)
        averageAmountOfTours = random.nextInt(0, 10)
        averageAmountOfActivities = random.nextInt(0, 10)
    }
}





fun randomDayStructure(random: Random = default): DayStructure {
    return ModifiableDayStructure(randomDurationDay(random), randomTourStructure(random)).apply {
        repeat(random.nextInt(0, 5)) {
            addPrecursor(randomTourStructure(random))
        }
        repeat(random.nextInt(0, 5)) {
            addSuccessor(randomTourStructure(random))
        }
    }
}

fun randomTourStructure(random: Random = default): MutableTourStructure {
    return MutableTourStructure(activityType(random)).apply {
        repeat(random.nextInt(0, 5)) {
            addPrecursor(activityType(random))
        }
        repeat(random.nextInt(0, 5)) {
            addSuccessor(activityType(random))
        }
    }
}

private fun activityType(random: Random) = ActivityType.OUTOFHOMEACTIVITY.random(random)
fun randomDurationDay(random: Random = default): DurationDay {
    return DurationDay(random.nextInt(0, 6))
}

fun randomPlannedTourAmounts(random: Random = default): PlannedTourAmounts {
    return PlannedTourAmounts.invoke(random.nextInt(0, 5), random.nextInt(0, 5))
}

fun randomMobilityPlan(random: Random = default): MobilityPlan {
    val person = randomPersonWithRoutine(random)
    val timeBudgets = randomTimeBudgets(random)
    val tripDuration = StandardCommuteDurations()
    val movingDayPlanInput = MovingDayPlanInput(person, tripDuration, timeBudgets, randomDurationDay(random))
    val dayPlans = (0..random.nextInt(0, 5)).map { randomDayStructure(random).toDayPlan(movingDayPlanInput) }
    val acts = dayPlans.flatMap { day -> day.map { act -> act.apply {
        startTime = randomTime(random)
        duration = randomDuration(random)
    } } }
    val actualMobilityPlan = ActualMobilityPlan(
        dayPlans = dayPlans,
        homePlans = emptyList(),
        activities = acts,
        timeBudgets = timeBudgets,
        person = person,
        tripDuration = tripDuration
    )
    return actualMobilityPlan
}

fun randomMobilityPlanInput(random: Random = default): MobilityPlanInputs {
    val mobilityPlan = randomMobilityPlan(random)
    val dayPlan = mobilityPlan.dayPlans.random(random)
    val tourPlan = dayPlan.tourPlans.random(random)
    return MobilityPlanInputs(mobilityPlan, mobilityPlan.person, dayPlan, tourPlan, tourPlan.random(random))
}

fun randomTimeBudgets(random: Random = default): TimeBudgets {
    return TimeBudgets(
        workBudget = randomTime(random),
        educationBudget = randomTime(random),
        leisureBudget = randomTime(random),
        shoppingBudget = randomTime(random),
        transportBudget = randomTime(random),
        workCategory = Category(random.nextInt(1, 15)),
        educationCategory = Category(random.nextInt(1, 15)),
        leisureCategory = Category(random.nextInt(1, 15)),
        shoppingCategory = Category(random.nextInt(1, 15)),
        transportCategory = Category(random.nextInt(1, 15))
    )
}
fun randomLinkedActivity(random: Random = default): LinkedActivity {

    val previous = randomActivity(random)
    val next = randomActivity(random)
    val linkedPrevious = LinkedActivity(
        previous
    )
    val linkedNext = LinkedActivity(
        next
    )
    val current = LinkedActivity(
        original = randomActivity(random),
    )
    val prevTrip = ModernizedTrip(
        duration = randomDuration(random),
        previousActivity = linkedPrevious,
        nextActivity = current
    )
    val nTrip = ModernizedTrip(
        duration = randomDuration(random),
        previousActivity = current,
        nextActivity = linkedNext
    )
    current.apply {
        previousTrip = prevTrip
        nextTrip = nTrip
    }
    return current
}

fun randomActivity(random: Random = default): ModernizedActivity {
    return ModernizedActivity(
        activityType = ActivityType.FULLSET.random(random),

        startTime = randomTime(random),
        duration = randomDuration(random),
        position = Position.entries.random(random)
    )
}

fun randomTime(random: Random = default): Duration {
    return 7.days * random.nextDouble()
}

fun randomDuration(random: Random = default): Duration {
    return 1.5.hours * random.nextDouble()
}

object RandomHHKeeper {
    val households: List<ActiToppHousehold>
    init {
        households = randomHouseholds(1000)
        households.forEach { household ->
            household.generatePersons(5)
        }
    }
    fun randomPerson(random: Random): Person {
        return households.random(random).members.random(random)
    }
}

fun randomPersonWithRoutine(random: Random = default): PersonWithRoutine {
    return PersonWithRoutine(RandomHHKeeper.randomPerson(random), randomWeekRoutine(random))
}


fun randomFinalizedPattern(random: Random): FinalizedActivityPattern {
    return FinalizedActivityPatternImpl(
        workDays = random.nextInt(0, 7),
        educationDays = random.nextInt(0, 7),
        leisureDays = random.nextInt(0, 7),
        shoppingDays = random.nextInt(0, 7),
        transportDays = random.nextInt(0, 7),
        workActivities = random.nextInt(0, 14),
        educationActivities = random.nextInt(0, 14),
        leisureActivities = random.nextInt(0, 14),
        shoppingActivities = random.nextInt(0, 14),
        transportActivities = random.nextInt(0, 14),
    )
}