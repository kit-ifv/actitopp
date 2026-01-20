package edu.kit.ifv.mobitopp.actitoppNG.timebudgets.choicemodels

import edu.kit.ifv.mobitopp.actitoppNG.plandurations.Identifier
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.HistogramSelection
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.parameters.EducationBudget
import edu.kit.ifv.mobitopp.actitoppNG.utils.times
import edu.kit.ifv.mobitopp.discretechoice.structure.loadFromList


val educationHistograms by lazy {
    HistogramSelection.createChoiceModelFromResource(
        identifier = Identifier.EDUCATION_TIME_BUDGETS,
        parameter = EducationBudget,
        name = "Histogram selection for education duration"
    ) { histograms ->
        loadFromList(histograms - histograms[3]) {_, it ->


            base +
                    (it.amountOfWorkActivitiesInWeek()) * anzakt_woche_w +
                    (it.amountOfEducationActivitiesInWeek()) * anzakt_woche_e +
                    (it.isAged10To17()) * alter_10bis17 +
                    (it.isVocational()) * beruf_azubi +
                    (it.amountOfDaysWithEducationActivityIs2()) * tagemit_eakt_2 +
                    (it.amountOfDaysWithEducationActivityIs3()) * tagemit_eakt_3 +
                    (it.amountOfDaysWithEducationActivityIs4()) * tagemit_eakt_4 +
                    (it.amountOfDaysWithEducationActivityIs5()) * tagemit_eakt_5
        }
        option(histograms[3]) {
            0.0
        }
    }
}