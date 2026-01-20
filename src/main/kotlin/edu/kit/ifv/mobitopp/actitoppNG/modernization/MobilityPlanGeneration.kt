package edu.kit.ifv.mobitopp.actitoppNG.modernization


import edu.kit.ifv.mobitopp.actitoppNG.Person
import edu.kit.ifv.mobitopp.actitoppNG.RNGHelper
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.strats.LegacySpawnSideTours
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.strats.SpawnSideTours
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.strats.SpawnWeek
import edu.kit.ifv.mobitopp.actitoppNG.mobilitystructure.strats.StandardImplementation
import edu.kit.ifv.mobitopp.actitoppNG.modernization.plan.MobilityPlan
import edu.kit.ifv.mobitopp.actitoppNG.modernization.plan.StandardCommuteDurations
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.AssignMinorActivityDuration
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.StickySelector
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.assignFirstMainActivities
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.assignMinorActivities
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.assignSecondaryMainActivities
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.choicemodels.LEAD
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.choicemodels.MAJOR
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.FinalizedActivityPattern
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.HistogramPerActivity
import edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes.PreferredStartViaChoiceModel
import edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes.StandardPreferredTourStart
import edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes.TourStartByHistogramsRelative
import edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes.TourStartWithPreference
import edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes.UsePreferredTourStart
import edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes.assignFirstTourStarts
import edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes.assignPreferredTourStart
import edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes.assignRemainingTourStarts
import edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes.assignSecondTourStarts
import edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes.choicemodels.FIRST_TOUR_HISTOGRAM
import edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes.choicemodels.SECOND_TOUR_HISTOGRAM
import edu.kit.ifv.mobitopp.actitoppNG.weekroutine.generateWeekRoutine
import kotlin.random.Random


fun interface MobilityPlanGeneration {
    context(rng: Random)
    fun generate(person: Person, amountOfDays: Int): MobilityPlan
    context(rng: Random)
    fun generate(person: Person) = generate(person, 7)

    fun generate(person: Person, amountOfDays : Int = 7, randomOffset: Long = 0L) = context(person.spawnRandomGenerator(randomOffset)) {generate(person, amountOfDays)}
}

class DefaultPlanGeneration : MobilityPlanGeneration {
    context(rng: Random)
    override fun generate(person: Person, amountOfDays: Int): MobilityPlan {
        val rng = person.spawnRandomGenerator()
        val structureGenerator = StandardStructureGeneration()
        val durationGenerator = StandardDurationAssignment()
        val startTimeGenerator = StandardStartTimeAssignment()
        val mobilityPlan = structureGenerator.generate(person, amountOfDays)
        mobilityPlan.apply {
            durationGenerator.assignDurations(this)
            startTimeGenerator.assignStartTimes(this)
        }
        mobilityPlan.extrudeHomeActivities()
        return mobilityPlan
    }
}

class StandardStructureGeneration(
    private val histograms: HistogramPerActivity = HistogramPerActivity.DEFAULT,
) : MobilityPlanGeneration {
    private val sideActivityStrategy = StandardImplementation()
    private val spawnMainActivities = SpawnWeek()
    private val spawnSideTours: SpawnSideTours = LegacySpawnSideTours()
    context(rng: Random)
    override fun generate(person: Person, amountOfDays: Int): MobilityPlan {

        val weekRoutine = person.generateWeekRoutine()
        val mobilityStructure = MobilityStructure(person, weekRoutine)
        // Generate the main activities of each day
        spawnMainActivities.spawnMainActivities(mobilityStructure)
        // Determine the amount of tours that precede & succeed the main tour.
        spawnSideTours.spawnSideTours(mobilityStructure)
        sideActivityStrategy.spawnSideActivities(mobilityStructure)
        // Determine the amount of precursor and successor tours for each tour of the day

        val budget = histograms.determineTimeBudgets(
            person,
            FinalizedActivityPattern.fromModernPattern(mobilityStructure)
        )
        return mobilityStructure.toPlan(StandardCommuteDurations.STANDARD_ASSIGNMENT, budget)
    }
}

fun interface MobilityPlanDurationAssignment {
    context(rng: Random)
    fun assignDurations(mobilityPlan: MobilityPlan)
}

class StandardDurationAssignment() : MobilityPlanDurationAssignment {
    context(rng: Random)
    override fun assignDurations(mobilityPlan: MobilityPlan) {
        mobilityPlan.assignFirstMainActivities(StickySelector( LEAD))
        mobilityPlan.assignSecondaryMainActivities(StickySelector( MAJOR))
        mobilityPlan.assignMinorActivities(AssignMinorActivityDuration())

    }
}

fun interface MobilityPlanStartTimeAssignment {
    context(rng: Random)
    fun assignStartTimes(mobilityPlan: MobilityPlan)
}

class StandardStartTimeAssignment() : MobilityPlanStartTimeAssignment {
    context(rng: Random)
    override fun assignStartTimes(mobilityPlan: MobilityPlan) {
        val preferredHistogram = mobilityPlan.assignPreferredTourStart(StandardPreferredTourStart())

        val firstStrategy = TourStartWithPreference(
            startTimeHistograms = FIRST_TOUR_HISTOGRAM,
            preferredTourStart = preferredHistogram,
            strategy = PreferredStartViaChoiceModel(),
        )

        val secondStrategy = TourStartWithPreference(
            startTimeHistograms = SECOND_TOUR_HISTOGRAM,
            preferredTourStart = preferredHistogram,
            strategy = UsePreferredTourStart.DISABLED
        )
        mobilityPlan.assignFirstTourStarts(firstStrategy)
        mobilityPlan.assignSecondTourStarts(secondStrategy)
        mobilityPlan.assignRemainingTourStarts(TourStartByHistogramsRelative.standard())
    }

}
