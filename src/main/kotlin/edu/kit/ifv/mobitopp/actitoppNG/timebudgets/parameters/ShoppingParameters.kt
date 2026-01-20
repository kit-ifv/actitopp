package edu.kit.ifv.mobitopp.actitoppNG.timebudgets.parameters

val ShoppingBudgets = ShoppingBudgetSet.create(
    category1 = ShoppingBudgetParameters(
        base = 0.0826,
        anzakt_woche_w = 0.0619,
        anzakt_woche_l = 0.0566,
        anzakt_woche_s = -0.5868,
        beruf_vollzeit = 0.2800,
        beruf_teilzeit = -0.0544,
        beruf_schueler = 0.5929,
        tagemit_sakt_1 = 1.5346,
        tagemit_sakt_2 = 0.5645,
        tagemit_sakt_3 = -0.0589,
        tagemit_sakt_4 = -0.1618,
    ),
    category2 = ShoppingBudgetParameters(
        base = -0.3110,
        anzakt_woche_w = 0.0342,
        anzakt_woche_l = 0.0127,
        anzakt_woche_s = -0.1860,
        beruf_vollzeit = 0.1946,
        beruf_teilzeit = 0.1101,
        beruf_schueler = 0.2652,
        tagemit_sakt_1 = 0.9219,
        tagemit_sakt_2 = 0.6691,
        tagemit_sakt_3 = 0.2181,
        tagemit_sakt_4 = 0.1537,
    ),
    category4 = ShoppingBudgetParameters(
        base = -0.5612,
        anzakt_woche_w = -0.0342,
        anzakt_woche_l = -0.0107,
        anzakt_woche_s = 0.0769,
        beruf_vollzeit = -0.2313,
        beruf_teilzeit = -0.1265,
        beruf_schueler = -0.3019,
        tagemit_sakt_1 = -0.8605,
        tagemit_sakt_2 = -0.4907,
        tagemit_sakt_3 = -0.4661,
        tagemit_sakt_4 = -0.1199,
    ),
    category5 = ShoppingBudgetParameters(
        base = -0.3159,
        anzakt_woche_w = -0.0597,
        anzakt_woche_l = -0.0474,
        anzakt_woche_s = 0.1253,
        beruf_vollzeit = -0.4267,
        beruf_teilzeit = -0.3545,
        beruf_schueler = -0.7126,
        tagemit_sakt_1 = -1.2950,
        tagemit_sakt_2 = -1.2527,
        tagemit_sakt_3 = -0.9871,
        tagemit_sakt_4 = -0.5124,
    )
)

data class ShoppingBudgetSet(
    val parameters: List<ShoppingBudgetParameters>,


    ) : List<ShoppingBudgetParameters> by parameters {
    companion object {
        fun create(
            category1: ShoppingBudgetParameters,
            category2: ShoppingBudgetParameters,
            category4: ShoppingBudgetParameters,
            category5: ShoppingBudgetParameters,
        ): ShoppingBudgetSet {
            return ShoppingBudgetSet(
                listOf(
                    category1,
                    category2,
                    category4,
                    category5,

                    )
            )
        }
    }
}

data class ShoppingBudgetParameters(
    val base: Double,
    val anzakt_woche_w: Double,
    val anzakt_woche_l: Double,
    val anzakt_woche_s: Double,
    val beruf_vollzeit: Double,
    val beruf_teilzeit: Double,
    val beruf_schueler: Double,
    val tagemit_sakt_1: Double,
    val tagemit_sakt_2: Double,
    val tagemit_sakt_3: Double,
    val tagemit_sakt_4: Double,

    )