package edu.kit.ifv.mobitopp.actitoppNG.timebudgets.choicemodels

import edu.kit.ifv.mobitopp.actitoppNG.plandurations.Identifier
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.HistogramSelection
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.parameters.TransportBudgets
import edu.kit.ifv.mobitopp.actitoppNG.utils.times
import edu.kit.ifv.mobitopp.discretechoice.structure.loadFromList


val transportHistograms = HistogramSelection.createChoiceModelFromResource(
    identifier = Identifier.TRANSPORT_TIME_BUDGETS,
    parameter = TransportBudgets,
    name = "Histogram selection for time budgets for transport"
) { histograms ->
    option(histograms[0]) {
        0.0
    }
    loadFromList(histograms - histograms[0]) { _, it->
        base +
                (it.amountOfWorkActivitiesInWeek()) * anzakt_woche_w +
                (it.amountOfShoppingActivitiesInWeek()) * anzakt_woche_s +
                (it.amountOfTransportActivitiesInWeek()) * anzakt_woche_t +
                (it.isFulltimeEmployee()) * beruf_vollzeit +
                (it.isParttimeEmployee()) * beruf_teilzeit +
                (it.isStudent()) * beruf_schueler +
                (it.amountOfDaysWithTransportActivityIs1()) * tagemit_takt_1
    }

}

