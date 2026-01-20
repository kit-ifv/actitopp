package edu.kit.ifv.mobitopp.actitoppNG.timebudgets.choicemodels

import edu.kit.ifv.discretechoice.extensions.optionsIndexed
import edu.kit.ifv.mobitopp.actitoppNG.plandurations.Identifier
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.HistogramSelection
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.parameters.ShoppingBudgets
import edu.kit.ifv.mobitopp.actitoppNG.utils.times


val shoppingHistograms by lazy {
    HistogramSelection.createChoiceModelFromResource(
        identifier = Identifier.SHOPPING_TIME_BUDGETS,
        parameter = ShoppingBudgets,
        name = "Histogram selection for time budget for shopping"
    ) { l ->
        optionsIndexed(l[0], l[1], l[3], l[4]) { _, it ->
            base +
                    (it.amountOfWorkActivitiesInWeek()) * anzakt_woche_w +
                    (it.amountOfLeisureActivitiesInWeek()) * anzakt_woche_l +
                    (it.amountOfShoppingActivitiesInWeek()) * anzakt_woche_s +
                    (it.isFulltimeEmployee()) * beruf_vollzeit +
                    (it.isParttimeEmployee()) * beruf_teilzeit +
                    (it.isStudent()) * beruf_schueler +
                    (it.amountOfDaysWithShoppingActivityIs1()) * tagemit_sakt_1 +
                    (it.amountOfDaysWithShoppingActivityIs2()) * tagemit_sakt_2 +
                    (it.amountOfDaysWithShoppingActivityIs3()) * tagemit_sakt_3 +
                    (it.amountOfDaysWithShoppingActivityIs4()) * tagemit_sakt_4
        }
        option(l[2]) { 0.0 }
    }
}




