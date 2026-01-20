package edu.kit.ifv.mobitopp.actitoppNG.timebudgets.parameters

val TransportBudgets = TransportBudgetSet.create(
    category2 = TransportBudgetParameters(
        base = -1.2365,
        anzakt_woche_w = -0.0387,
        anzakt_woche_s = -0.0486,
        anzakt_woche_t = 0.2417,
        beruf_vollzeit = -0.2497,
        beruf_teilzeit = -0.1264,
        beruf_schueler = -0.7904,
        tagemit_takt_1 = -0.4481,
    ),
    category3 = TransportBudgetParameters(
        base = -1.7208,
        anzakt_woche_w = -0.0611,
        anzakt_woche_s = -0.0505,
        anzakt_woche_t = 0.3177,
        beruf_vollzeit = -0.1934,
        beruf_teilzeit = -0.0707,
        beruf_schueler = -0.5535,
        tagemit_takt_1 = -0.3096,
    ),
    category4 = TransportBudgetParameters(
        base = -1.6474,
        anzakt_woche_w = -0.0570,
        anzakt_woche_s = -0.1095,
        anzakt_woche_t = 0.3642,
        beruf_vollzeit = -0.6205,
        beruf_teilzeit = -0.5268,
        beruf_schueler = -0.5862,
        tagemit_takt_1 = -0.8365,
    )
)

data class TransportBudgetSet(
    val parameters: List<TransportBudgetParameters>,


    ) : List<TransportBudgetParameters> by parameters {
    companion object {
        fun create(
            category2: TransportBudgetParameters,
            category3: TransportBudgetParameters,
            category4: TransportBudgetParameters,
        ): TransportBudgetSet {
            return TransportBudgetSet(
                listOf(category2, category3, category4)
            )
        }
    }
}

data class TransportBudgetParameters(
    val base: Double,
    val anzakt_woche_w: Double,
    val anzakt_woche_s: Double,
    val anzakt_woche_t: Double,
    val beruf_vollzeit: Double,
    val beruf_teilzeit: Double,
    val beruf_schueler: Double,
    val tagemit_takt_1: Double,


    )