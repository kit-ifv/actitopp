package edu.kit.ifv.mobitopp.actitoppNG.weekroutine.parameters

/**
 * Collects parameters of [ActivityAmountParameters]. The individual options will access the parameters by the order in
 * which they appear.
 */
data class ActivityAmountSet(
    val parameters: List<ActivityAmountParameters>,


    ) : List<ActivityAmountParameters> by parameters {


    companion object {
        operator fun invoke(
            option2: ActivityAmountParameters,
            option3: ActivityAmountParameters,
            option4: ActivityAmountParameters,
            option5: ActivityAmountParameters,
            option6: ActivityAmountParameters,
        ): ActivityAmountSet {
            return ActivityAmountSet(
                listOf(option2, option3, option4, option5, option6)
            )
        }
    }

}

/**
 * This class contains the parameters for the utility function to determine the average activity amount.
 * @param base The default parameter.
 * @param beruf_teilzeit Parameter that should be applied when the person is part-time employed.
 * @param beruf_schueler Parameter that should be applied when the person is a student.
 * @param beruf_azubi Parameter that should be applied when the person is in a vocational program.
 * @param alter_26bis35 Parameter that should be applied when the person is aged between 26 and 35 (inclusive)
 * @param alter_36bis50 Parameter that should be applied when the person is aged between 36 and 50 (inclusive)
 * @param alter_51bis60 Parameter that should be applied when the person is aged between 51 and 60 (inclusive)
 * @param alter_61bis70 Parameter that should be applied when the person is aged between 61 and 70 (inclusive)
 * @param Raumtyp_mobitopp_rural Parameter that should be applied when the home location of the household of the person
 *   is classified as RURAL
 * @param pendeln_ueber50km Parameter that should be applied when the commute distance of the person is over 50 km
 * @param pendeln_0bis5km Parameter that should be applied when the commute distance is within 0 to 5 km
 * @param haushalthatkinderunter10 Parameter that should be applied when the person household has children
 */
data class ActivityAmountParameters(
    val base: Double,
    val beruf_teilzeit: Double,
    val beruf_schueler: Double,
    val beruf_azubi: Double,
    val alter_26bis35: Double,
    val alter_36bis50: Double,
    val alter_51bis60: Double,
    val alter_61bis70: Double,
    val Raumtyp_mobitopp_rural: Double,
    val pendeln_ueber50km: Double,
    val pendeln_0bis5km: Double,
    val haushalthatkinderunter10: Double,

    )

/**
 * The original parameter set for the activity amount, taken from mop14_withpkwhh. Originally called 1LParams.
 */
val DefaultActivityAmountParameters = ActivityAmountSet(
    option2 = ActivityAmountParameters(
        base = 0.3956,
        beruf_teilzeit = 0.4052,
        beruf_schueler = 0.3082,
        beruf_azubi = 0.2121,
        alter_26bis35 = 0.4286,
        alter_36bis50 = 0.5350,
        alter_51bis60 = 0.3188,
        alter_61bis70 = 0.3318,
        Raumtyp_mobitopp_rural = -0.5865,
        pendeln_ueber50km = -0.1666,
        pendeln_0bis5km = 0.2628,
        haushalthatkinderunter10 = 0.1804,
    ),
    option3 = ActivityAmountParameters(
        base = -0.9043,
        beruf_teilzeit = 0.6790,
        beruf_schueler = 0.2746,
        beruf_azubi = 0.1893,
        alter_26bis35 = 1.1473,
        alter_36bis50 = 1.1567,
        alter_51bis60 = 0.6968,
        alter_61bis70 = 0.6427,
        Raumtyp_mobitopp_rural = -0.6093,
        pendeln_ueber50km = -0.4316,
        pendeln_0bis5km = 0.3914,
        haushalthatkinderunter10 = 0.5053,
    ),
    option4 = ActivityAmountParameters(
        base = -2.5651,
        beruf_teilzeit = 0.8759,
        beruf_schueler = 0.2732,
        beruf_azubi = 0.9468,
        alter_26bis35 = 1.6813,
        alter_36bis50 = 1.7647,
        alter_51bis60 = 1.1197,
        alter_61bis70 = 0.8423,
        Raumtyp_mobitopp_rural = -1.0050,
        pendeln_ueber50km = -0.4089,
        pendeln_0bis5km = 0.4109,
        haushalthatkinderunter10 = 0.6865,
    ),
    option5 = ActivityAmountParameters(
        base = -3.7358,
        beruf_teilzeit = 0.9369,
        beruf_schueler = 0.3443,
        beruf_azubi = 0.5735,
        alter_26bis35 = 1.8968,
        alter_36bis50 = 1.7846,
        alter_51bis60 = 1.0495,
        alter_61bis70 = 0.5129,
        Raumtyp_mobitopp_rural = -0.3769,
        pendeln_ueber50km = -0.3099,
        pendeln_0bis5km = 0.2829,
        haushalthatkinderunter10 = 0.6870,
    ),
    option6 = ActivityAmountParameters(
        base = -4.6359,
        beruf_teilzeit = 0.9776,
        beruf_schueler = 0.5584,
        beruf_azubi = 0.3049,
        alter_26bis35 = 2.0686,
        alter_36bis50 = 1.8213,
        alter_51bis60 = 1.4204,
        alter_61bis70 = 0.4929,
        Raumtyp_mobitopp_rural = -0.0415,
        pendeln_ueber50km = -0.1946,
        pendeln_0bis5km = 0.2037,
        haushalthatkinderunter10 = 0.7003,
    ),
)