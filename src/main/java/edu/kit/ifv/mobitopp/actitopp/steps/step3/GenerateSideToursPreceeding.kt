package edu.kit.ifv.mobitopp.actitopp.steps.step3

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.HDay
import edu.kit.ifv.mobitopp.actitopp.HTour
import edu.kit.ifv.mobitopp.actitopp.WeekRoutine
import edu.kit.ifv.mobitopp.actitopp.RNGHelper
import edu.kit.ifv.mobitopp.actitopp.steps.scrapPath.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ParametrizedDiscreteChoiceModel


class GenerateSideToursPreceeding(rngHelper: RNGHelper,
                                  choiceModel: ParametrizedDiscreteChoiceModel<Int, PreviousDaySituation, ParameterCollectionStep3A> = step3AWithParams,
) : DefaultSideTourDeterminer<ParameterCollectionStep3A>(rngHelper, choiceModel) {
    override fun createChoiceSituation(
        choice: Int,
        day: DayWithBounds,
        previousResult: Int?,
        personWithRoutine: PersonWithRoutine,
    ): PreviousDaySituation {
        return PreviousDaySituation(
            choice,
            day.day,
            previousResult,
            null,
            personWithRoutine.person,
            personWithRoutine.routine
        )
    }
    // FOr the preceding tours we can define this utility function to help create the inputs.
    fun generate(person: ActitoppPerson, routine: WeekRoutine, intArray: Collection<Int>, days: List<HDay>): List<Int> =
        generate(PrecedingInput(PersonWithRoutine(person, routine), intArray, days))

    override fun determineMinimumAmountOfTours(remainingNumberOfTours: Int): Int {
        return remainingNumberOfTours / 2
    }

    override fun spawnTour(day: HDay): HTour {
        return day.generatePrecedingTour()
    }
}