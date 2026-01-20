package edu.kit.ifv.mobitopp.actitoppNG.timebudgets.parameters

val LeisureBudgets = LeisureBudgetSet.create(
    category1 = LeisureBudgetParameters(
        base = 1.0576,
        anzakt_woche_w = 0.0143,
        anzakt_woche_l = -0.7111,
        anzakt_woche_s = 0.1308,
        pendeln_0bis5km = -0.4133,
        beruf_rentner = 0.1009,
        tagemit_lakt_1 = 3.2007,
        tagemit_lakt_2 = 1.0523,
        tagemit_lakt_3 = -0.2179,
        tagemit_lakt_4 = -0.9268,
        tagemit_lakt_5 = -1.0579,
    ),
    category2 = LeisureBudgetParameters(
        base = 0.1193,
        anzakt_woche_w = 0.0122,
        anzakt_woche_l = -0.3675,
        anzakt_woche_s = 0.0821,
        pendeln_0bis5km = -0.2179,
        beruf_rentner = 0.0133,
        tagemit_lakt_1 = 3.0153,
        tagemit_lakt_2 = 1.9184,
        tagemit_lakt_3 = 1.0253,
        tagemit_lakt_4 = 0.2947,
        tagemit_lakt_5 = -0.2229,
    ),
    category3 = LeisureBudgetParameters(
        base = -0.1239,
        anzakt_woche_w = 0.0129,
        anzakt_woche_l = -0.1611,
        anzakt_woche_s = 0.0409,
        pendeln_0bis5km = -0.2390,
        beruf_rentner = 0.0311,
        tagemit_lakt_1 = 1.7189,
        tagemit_lakt_2 = 1.3350,
        tagemit_lakt_3 = 0.8949,
        tagemit_lakt_4 = 0.5184,
        tagemit_lakt_5 = 0.0611,
    ),
    category5 = LeisureBudgetParameters(
        base = -1.5743,
        anzakt_woche_w = -0.0263,
        anzakt_woche_l = 0.1213,
        anzakt_woche_s = -0.0629,
        pendeln_0bis5km = -0.0422,
        beruf_rentner = 0.0500,
        tagemit_lakt_1 = 0.6724,
        tagemit_lakt_2 = -1.0757,
        tagemit_lakt_3 = -1.0992,
        tagemit_lakt_4 = -0.7921,
        tagemit_lakt_5 = -0.2934,
    ),
    category6 = LeisureBudgetParameters(
        base = -3.1398,
        anzakt_woche_w = -0.1197,
        anzakt_woche_l = 0.2145,
        anzakt_woche_s = -0.0863,
        pendeln_0bis5km = -0.2943,
        beruf_rentner = -0.3829,
        tagemit_lakt_1 = 2.9839,
        tagemit_lakt_2 = 0.7981,
        tagemit_lakt_3 = -0.2040,
        tagemit_lakt_4 = -0.3433,
        tagemit_lakt_5 = -0.5545,
    )
)

data class LeisureBudgetSet(
    val parameters: List<LeisureBudgetParameters>,
) : List<LeisureBudgetParameters> by parameters {
    companion object {
        fun create(
            category1: LeisureBudgetParameters,
            category2: LeisureBudgetParameters,
            category3: LeisureBudgetParameters,
            category5: LeisureBudgetParameters,
            category6: LeisureBudgetParameters,
        ): LeisureBudgetSet {
            return LeisureBudgetSet(
                listOf(
                    category1,
                    category2,
                    category3,
                    category5,
                    category6,
                )
            )
        }
    }
}


data class LeisureBudgetParameters(
    val base: Double,
    val anzakt_woche_w: Double,
    val anzakt_woche_l: Double,
    val anzakt_woche_s: Double,
    val pendeln_0bis5km: Double,
    val beruf_rentner: Double,
    val tagemit_lakt_1: Double,
    val tagemit_lakt_2: Double,
    val tagemit_lakt_3: Double,
    val tagemit_lakt_4: Double,
    val tagemit_lakt_5: Double,

    )