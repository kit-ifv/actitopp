package edu.kit.ifv.mobitopp.actitoppNG.timebudgets.parameters

val EducationBudget = EducationBudgetSet.create(
    category1 = EducationBudgetParameters(
        base = 7.3308,
        anzakt_woche_w = 0.2824,
        anzakt_woche_e = -1.1222,
        alter_10bis17 = -3.0926,
        beruf_azubi = -1.1582,
        tagemit_eakt_2 = 6.7131,
        tagemit_eakt_3 = -0.1972,
        tagemit_eakt_4 = -4.7670,
        tagemit_eakt_5 = -5.3585,
    ),
    category2 = EducationBudgetParameters(
        base = 1.2732,
        anzakt_woche_w = 0.2963,
        anzakt_woche_e = -0.2518,
        alter_10bis17 = -1.2461,
        beruf_azubi = -0.0487,
        tagemit_eakt_2 = 10.0972,
        tagemit_eakt_3 = 3.9357,
        tagemit_eakt_4 = 0.2901,
        tagemit_eakt_5 = -2.6143,
    ),
    category3 = EducationBudgetParameters(
        base = -0.6735,
        anzakt_woche_w = 0.1956,
        anzakt_woche_e = -0.0807,
        alter_10bis17 = -0.4415,
        beruf_azubi = -0.0811,
        tagemit_eakt_2 = -0.9664,
        tagemit_eakt_3 = 3.9599,
        tagemit_eakt_4 = 1.7891,
        tagemit_eakt_5 = -0.1693,
    ),
    category5 = EducationBudgetParameters(
        base = -0.1449,
        anzakt_woche_w = 0.00873,
        anzakt_woche_e = 0.1256,
        alter_10bis17 = -0.0963,
        beruf_azubi = 1.2008,
        tagemit_eakt_2 = -1.5017,
        tagemit_eakt_3 = -1.1565,
        tagemit_eakt_4 = -1.6420,
        tagemit_eakt_5 = -0.7661,
    ),
    category6 = EducationBudgetParameters(
        base = 1.7090,
        anzakt_woche_w = -0.4791,
        anzakt_woche_e = 0.0202,
        alter_10bis17 = -0.7520,
        beruf_azubi = 2.7480,
        tagemit_eakt_2 = 4.4675,
        tagemit_eakt_3 = -14.3348,
        tagemit_eakt_4 = -3.9791,
        tagemit_eakt_5 = -2.2662,
    )
)

data class EducationBudgetSet(
    val parameters: List<EducationBudgetParameters>,
) : List<EducationBudgetParameters> by parameters {
    companion object {
        fun create(
            category1: EducationBudgetParameters,
            category2: EducationBudgetParameters,
            category3: EducationBudgetParameters,
            category5: EducationBudgetParameters,
            category6: EducationBudgetParameters,
        ): EducationBudgetSet {
            return EducationBudgetSet(
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

data class EducationBudgetParameters(
    val base: Double,
    val anzakt_woche_w: Double,
    val anzakt_woche_e: Double,
    val alter_10bis17: Double,
    val beruf_azubi: Double,
    val tagemit_eakt_2: Double,
    val tagemit_eakt_3: Double,
    val tagemit_eakt_4: Double,
    val tagemit_eakt_5: Double,

    )