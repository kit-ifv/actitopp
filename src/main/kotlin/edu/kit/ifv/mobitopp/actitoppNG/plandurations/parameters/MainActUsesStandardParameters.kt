package edu.kit.ifv.mobitopp.actitoppNG.plandurations.parameters

val ParametersStep8A = ParameterCollectionStep8A(
    no = ParameterStep8A(
        base = 0.3709,
        aktzweck_work = -0.5610,
        aktzweck_education = -0.7771,
        anzaktwieanztagemitzweck = -0.8515,
        beruf_vollzeit = 0.0500,
        beruf_teilzeit = -0.1152,
        taghat1akt = -0.3783,
        taghat2akt = -0.2701,
        taghat3akt = -0.1396,
        taghat1tour = 0.3030,
        taghat2touren = 0.0732,
        tag_sa = 0.3699,
        tag_so = 0.3027,
        mittl_zeit_akt_1bis14min = -1.5198,
        mittl_zeit_akt_15bis29min = -1.3527,
        mittl_zeit_akt_30bis59min = -0.4782,
        mittl_zeit_akt_60bis119min = -0.3813,
        mittl_zeit_akt_120bis179min = 0.0175,
        mittl_zeit_akt_180bis239min = 0.6227,
        mittl_zeit_akt_240bis299min = 0.5606,
        mittl_zeit_akt_300bis359min = 0.6856,
        mittl_zeit_akt_360bis419min = 0.7753,
        mittl_zeit_akt_420bis479min = 0.7963,
        tourhat1akt = -0.7774,
        tourhat2akt = -0.5889,
        tourhat3akt = -0.3665,
    )
)

data class ParameterCollectionStep8A(
    val no: ParameterStep8A,
)

data class ParameterStep8A(
    val base: Double,
    val aktzweck_work: Double,
    val aktzweck_education: Double,
    val anzaktwieanztagemitzweck: Double,
    val beruf_vollzeit: Double,
    val beruf_teilzeit: Double,
    val taghat1akt: Double,
    val taghat2akt: Double,
    val taghat3akt: Double,
    val taghat1tour: Double,
    val taghat2touren: Double,
    val tag_sa: Double,
    val tag_so: Double,
    val mittl_zeit_akt_1bis14min: Double,
    val mittl_zeit_akt_15bis29min: Double,
    val mittl_zeit_akt_30bis59min: Double,
    val mittl_zeit_akt_60bis119min: Double,
    val mittl_zeit_akt_120bis179min: Double,
    val mittl_zeit_akt_180bis239min: Double,
    val mittl_zeit_akt_240bis299min: Double,
    val mittl_zeit_akt_300bis359min: Double,
    val mittl_zeit_akt_360bis419min: Double,
    val mittl_zeit_akt_420bis479min: Double,
    val tourhat1akt: Double,
    val tourhat2akt: Double,
    val tourhat3akt: Double,

    )