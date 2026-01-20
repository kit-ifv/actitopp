package edu.kit.ifv.mobitopp.actitoppNG.timebudgets.choicemodels

import edu.kit.ifv.mobitopp.actitoppNG.plandurations.Identifier
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.HistogramSelection
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.parameters.LeisureBudgets
import edu.kit.ifv.mobitopp.actitoppNG.utils.times
import edu.kit.ifv.mobitopp.discretechoice.structure.loadFromList


val leisureHistograms by lazy {
    HistogramSelection.createChoiceModelFromResource(
        identifier = Identifier.LEISURE_TIME_BUDGETS,
        parameter = LeisureBudgets,
        name = "Histogram selection for time budget for leisure"
    ) { histograms ->
        loadFromList(histograms - histograms[3]) {_, it ->
            base +
                    (it.amountOfWorkActivitiesInWeek()) * anzakt_woche_w +
                    (it.amountOfLeisureActivitiesInWeek()) * anzakt_woche_l +
                    (it.amountOfShoppingActivitiesInWeek()) * anzakt_woche_s +
                    (it.commuteIn0To5km()) * pendeln_0bis5km +
                    (it.isRetired()) * beruf_rentner +
                    (it.amountOfDaysWithLeisureActivityIs1()) * tagemit_lakt_1 +
                    (it.amountOfDaysWithLeisureActivityIs2()) * tagemit_lakt_2 +
                    (it.amountOfDaysWithLeisureActivityIs3()) * tagemit_lakt_3 +
                    (it.amountOfDaysWithLeisureActivityIs4()) * tagemit_lakt_4 +
                    (it.amountOfDaysWithLeisureActivityIs5()) * tagemit_lakt_5
        }
        option(histograms[3]) {
            0.0
        }


    }
}

