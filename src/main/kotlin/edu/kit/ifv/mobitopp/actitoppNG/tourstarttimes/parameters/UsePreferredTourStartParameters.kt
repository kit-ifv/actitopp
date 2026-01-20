package edu.kit.ifv.mobitopp.actitoppNG.tourstarttimes.parameters

val ParametersStep10A = ParameterStep10A(
    base = -0.2409,
    anztourenvorhaupttour = -0.3678,
    anztourennachhaupttour = -0.2897,
    tourhat1akt = -0.1876,
    alter_10bis17 = -0.8706,
    tourtyp_work = -0.3522,
    tag_sa = 1.4061,
    tag_so = 1.7576,
    dauer_akt_in_tour_2bis4std = 0.3190,
    dauer_akt_in_tour_4bis6std = -0.5283,
    dauer_akt_in_tour_6bis8std = -0.5120,
    dauer_akt_in_tour_8bis10std = -0.9989,
    dauer_akt_in_tour_10bis12std = -1.0021,
    dauer_akt_vorht_tag_1bis120 = 0.8132,
    std_start_T1_6_7_Uhr = -0.1871,
    std_start_T1_7_8_Uhr = -0.3214
)

data class ParameterStep10A(
    val base: Double,
    val anztourenvorhaupttour: Double,
    val anztourennachhaupttour: Double,
    val tourhat1akt: Double,
    val alter_10bis17: Double,
    val tourtyp_work: Double,
    val tag_sa: Double,
    val tag_so: Double,
    val dauer_akt_in_tour_2bis4std: Double,
    val dauer_akt_in_tour_4bis6std: Double,
    val dauer_akt_in_tour_6bis8std: Double,
    val dauer_akt_in_tour_8bis10std: Double,
    val dauer_akt_in_tour_10bis12std: Double,
    val dauer_akt_vorht_tag_1bis120: Double,
    val std_start_T1_6_7_Uhr: Double,
    val std_start_T1_7_8_Uhr: Double,

    )