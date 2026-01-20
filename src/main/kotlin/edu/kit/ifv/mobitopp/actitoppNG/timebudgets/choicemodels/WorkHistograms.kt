package edu.kit.ifv.mobitopp.actitoppNG.timebudgets.choicemodels

import edu.kit.ifv.mobitopp.actitoppNG.plandurations.Identifier
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.HistogramSelection
import edu.kit.ifv.mobitopp.actitoppNG.timebudgets.parameters.WorkBudgets
import edu.kit.ifv.mobitopp.actitoppNG.utils.times
import edu.kit.ifv.mobitopp.discretechoice.structure.loadFromList

val workHistograms by lazy {
    HistogramSelection.createChoiceModelFromResource(
        identifier = Identifier.WORK_TIME_BUDGETS,
        parameter = WorkBudgets,
        name = "Selection of histogram for work activities."
    ) { histograms ->
        loadFromList(histograms - histograms[6]) { _,it ->
            base +
                    (it.amountOfWorkActivitiesInWeek()) * anzakt_woche_w +
                    (it.amountOfEducationActivitiesInWeek()) * anzakt_woche_e +
                    (it.isFulltimeEmployee()) * beruf_vollzeit +
                    (it.isParttimeEmployee()) * beruf_teilzeit +
                    (it.isStudent()) * beruf_schueler +
                    (it.isVocational()) * beruf_azubi +
                    (it.isMale()) * male +
                    (it.isAged18To25()) * alter_18bis25 +
                    (it.isAged26To35()) * alter_26bis35 +
                    (it.isAged36To50()) * alter_36bis50 +
                    (it.isAged51To60()) * alter_51bis60 +
                    (it.amountOfDaysWithWorkActivityIs1()) * tagemit_wakt_1 +
                    (it.amountOfDaysWithWorkActivityIs2()) * tagemit_wakt_2 +
                    (it.amountOfDaysWithWorkActivityIs3()) * tagemit_wakt_3 +
                    (it.amountOfDaysWithWorkActivityIs4()) * tagemit_wakt_4 +
                    (it.amountOfDaysWithWorkActivityIs5()) * tagemit_wakt_5 +
                    (it.amountOfDaysWithWorkActivityIs6()) * tagemit_wakt_6
        }
        option(histograms[6]) {
            0.0
        }
    }
}



