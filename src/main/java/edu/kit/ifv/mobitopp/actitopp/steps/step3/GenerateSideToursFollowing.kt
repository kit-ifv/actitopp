package edu.kit.ifv.mobitopp.actitopp.steps.step3

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import edu.kit.ifv.mobitopp.actitopp.HDay
import edu.kit.ifv.mobitopp.actitopp.HTour
import edu.kit.ifv.mobitopp.actitopp.WeekRoutine
import edu.kit.ifv.mobitopp.actitopp.RNGHelper
import edu.kit.ifv.mobitopp.actitopp.steps.scrapPath.PersonWithRoutine
import edu.kit.ifv.mobitopp.actitopp.utilityFunctions.ParametrizedDiscreteChoiceModel

class GenerateSideToursFollowing(
    rngHelper: RNGHelper,
    choiceModel: ParametrizedDiscreteChoiceModel<Int, PreviousDaySituation, ParameterCollectionStep3B> = step3BWithParams,
) : DefaultSideTourDeterminer<ParameterCollectionStep3B>(rngHelper, choiceModel) {
    override fun createChoiceSituation(
        choice: Int,
        day: DayWithBounds,
        previousResult: Int?,
        personWithRoutine: PersonWithRoutine,
    ): PreviousDaySituation {
        return PreviousDaySituation(
            choice,
            day.day,
            day.amountOfTours - 1,
            previousResult,
            personWithRoutine.person,
            personWithRoutine.routine
        )
    }

    fun generate(person: ActitoppPerson, routine: WeekRoutine, days: List<DayWithBounds>): List<Int> =
        generate(PrecedingInput(PersonWithRoutine(person, routine),days ))

    override fun determineMinimumAmountOfTours(remainingNumberOfTours: Int): Int {
        return remainingNumberOfTours
    }

    override fun spawnTour(day: HDay): HTour {
        return day.generateFollowingTour()
    }
}