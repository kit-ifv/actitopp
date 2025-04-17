package edu.kit.ifv.mobitopp.actitopp.enums

import edu.kit.ifv.mobitopp.actitopp.ActitoppPerson
import kotlin.math.max

/**
 * @author Tim Hilgert
 */

inline val Boolean.D get() = if(this) 1.0 else 0.0
enum class ActitoppPersonParameters{
    /*
        * Kinder 0-10
        */
    haushalthatkinderunter10{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.children0_10 > 0) 1.0 else 0.0)
        }
    },


    /*
     * Kinder unter 18
     */
    haushalthatkinderunter18{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.children_u18 > 0) 1.0 else 0.0)
        }
    },
    anzahlkinder_u18{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return actitoppPerson.children_u18.toDouble()
        }
    },

    /*
     * BERUF
     */
    beruf_vollzeit{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (actitoppPerson.employment == Employment.FULLTIME).D
        }
    },
    beruf_teilzeit{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return actitoppPerson.employment.isParttime().D
        }
    },
    beruf_ohneerwerb{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return actitoppPerson.employment.isNotEarning().D
        }
    },
    beruf_schueler{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return actitoppPerson.employment.isStudent().D
        }
    },
    beruf_azubi{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (actitoppPerson.employment == Employment.VOCATIONAL).D
        }
    },
    beruf_schueler_azubi{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return actitoppPerson.employment.isStudentOrAzubi().D
        }
    },
    beruf_erwerbstaetig{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return actitoppPerson.employment.isEarning().D
        }
    },
    beruf_rentner{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (actitoppPerson.employment == Employment.RETIRED).D
        }
    },

    /*
     *  HHGRO
     */
    persin2pershh{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.household.numberofPersonsinHousehold == 2) 1.0 else 0.0)
        }
    },
    persin3pershh{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.household.numberofPersonsinHousehold == 3) 1.0 else 0.0)
        }
    },
    rentnerin2pershh{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.household.numberofPersonsinHousehold == 2 && actitoppPerson.employment == Employment.RETIRED) 1.0 else 0.0)
        }
    },

    /*
     * ALTER
     */
    alter_10bis17{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.age >= 10 && actitoppPerson.age <= 17) 1.0 else 0.0)
        }
    },
    alter_18bis25{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.age >= 18 && actitoppPerson.age <= 25) 1.0 else 0.0)
        }
    },
    alter_26bis35{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.age >= 26 && actitoppPerson.age <= 35) 1.0 else 0.0)
        }
    },
    alter_36bis50{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.age >= 36 && actitoppPerson.age <= 50) 1.0 else 0.0)
        }
    },
    alter_51bis60{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.age >= 51 && actitoppPerson.age <= 60) 1.0 else 0.0)
        }
    },
    alter_61bis70{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.age >= 61 && actitoppPerson.age <= 70) 1.0 else 0.0)
        }
    },
    alter_ueber70{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.age >= 71) 1.0 else 0.0)
        }
    },
    alter_18bis35{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.age >= 18 && actitoppPerson.age <= 35) 1.0 else 0.0)
        }
    },

    /*
     * GESCHLECHT
     */
    male{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.gender == 1) 1.0 else 0.0)
        }
    },

    /*
     * RAUMTYP
     */
    Raumtyp_mobitopp_rural{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.areatype == 1) 1.0 else 0.0)
        }
    },
    Raumtyp_mobitopp_provincial{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.areatype == 2) 1.0 else 0.0)
        }
    },
    Raumtyp_mobitopp_cityoutskirt{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.areatype == 3) 1.0 else 0.0)
        }
    },
    Raumtyp_mobitopp_metropolitan{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.areatype == 4) 1.0 else 0.0)
        }
    },
    Raumtyp_mobitopp_conurbation{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.areatype == 5) 1.0 else 0.0)
        }
    },

    /*
     * PKWHH
     */
    PKWHH{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return actitoppPerson.numberofcarsinhousehold.toDouble()
        }
    },

    /*
     * Pendeln 0-5 Kilometer
     */
    pendeln_0bis5km{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            val commute_distance =
                max(actitoppPerson.commutingdistance_work, actitoppPerson.commutingdistance_education)
            return (if (commute_distance > 0 && commute_distance <= 5) 1.0 else 0.0)
        }
    },

    /*
     * Pendeln 5-10 Kilometer
     */
    pendeln_5bis10km{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            val commute_distance =
                max(actitoppPerson.commutingdistance_work, actitoppPerson.commutingdistance_education)
            return (if (commute_distance > 5 && commute_distance <= 10) 1.0 else 0.0)
        }
    },

    /*
     * Pendeln 10-20 Kilometer
     */
    pendeln_10bis20km{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            val commute_distance =
                max(actitoppPerson.commutingdistance_work, actitoppPerson.commutingdistance_education)
            return (if (commute_distance > 10 && commute_distance <= 20) 1.0 else 0.0)
        }
    },

    /*
     * Pendeln 20-50 Kilometer
     */
    pendeln_20bis50km{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            val commute_distance =
                max(actitoppPerson.commutingdistance_work, actitoppPerson.commutingdistance_education)
            return (if (commute_distance > 20 && commute_distance <= 50) 1.0 else 0.0)
        }
    },

    /*
     * Pendeln ueber 50 Kilometer
     */
    pendeln_ueber50km{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            val commute_distance =
                max(actitoppPerson.commutingdistance_work, actitoppPerson.commutingdistance_education)
            return (if (commute_distance > 50) 1.0 else 0.0)
        }
    },

    /*
     * Anzahl Arbeitstage (Stufe 1A)
     */
    anztage_w{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return actitoppPerson.attributesMap["anztage_w"]
        }
    },
    anzahl_arbeitstage0{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_w"] == 0.0) 1.0 else 0.0)
        }
    },
    anzahl_arbeitstage1{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_w"] == 1.0) 1.0 else 0.0)
        }
    },
    anzahl_arbeitstage2{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_w"] == 2.0) 1.0 else 0.0)
        }
    },
    anzahl_arbeitstage3{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_w"] == 3.0) 1.0 else 0.0)
        }
    },
    anzahl_arbeitstage4{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_w"] == 4.0) 1.0 else 0.0)
        }
    },
    anzahl_arbeitstage5{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_w"] == 5.0) 1.0 else 0.0)
        }
    },
    anzahl_arbeitstage6{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_w"] == 6.0) 1.0 else 0.0)
        }
    },
    anzahl_arbeitstage7{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_w"] == 7.0) 1.0 else 0.0)
        }
    },

    /*
     * Anzahl Bildungstage (Stufe 1B)
     */
    anztage_e{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return actitoppPerson.attributesMap["anztage_e"]
        }
    },
    anzahl_bildungstage0{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_e"] == 0.0) 1.0 else 0.0)
        }
    },
    anzahl_bildungstage1{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_e"] == 1.0) 1.0 else 0.0)
        }
    },
    anzahl_bildungstage2{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_e"] == 2.0) 1.0 else 0.0)
        }
    },
    anzahl_bildungstage3{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_e"] == 3.0) 1.0 else 0.0)
        }
    },
    anzahl_bildungstage4{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_e"] == 4.0) 1.0 else 0.0)
        }
    },
    anzahl_bildungstage5{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_e"] == 5.0) 1.0 else 0.0)
        }
    },
    anzahl_bildungstage6{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_e"] == 6.0) 1.0 else 0.0)
        }
    },
    anzahl_bildungstage7{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_e"] == 7.0) 1.0 else 0.0)
        }
    },

    /*
     * Anzahl Freizeittage (Stufe 1C)
     */
    anztage_l{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return actitoppPerson.attributesMap["anztage_l"] ?: 0.0
        }
    },
    anzahl_freizeittage0{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_l"] == 0.0) 1.0 else 0.0)
        }
    },
    anzahl_freizeittage1{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_l"] == 1.0) 1.0 else 0.0)
        }
    },
    anzahl_freizeittage2{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_l"] == 2.0) 1.0 else 0.0)
        }
    },
    anzahl_freizeittage3{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_l"] == 3.0) 1.0 else 0.0)
        }
    },
    anzahl_freizeittage4{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_l"] == 4.0) 1.0 else 0.0)
        }
    },
    anzahl_freizeittage5{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_l"] == 5.0) 1.0 else 0.0)
        }
    },
    anzahl_freizeittage6{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_l"] == 6.0) 1.0 else 0.0)
        }
    },
    anzahl_freizeittage7{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_l"] == 7.0) 1.0 else 0.0)
        }
    },

    /*
     * Anzahl Shoppingtage (Stufe 1D)
     */
    anztage_s{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return actitoppPerson.attributesMap["anztage_s"]
        }
    },
    anzahl_shoppingtage0{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_s"] == 0.0) 1.0 else 0.0)
        }
    },
    anzahl_shoppingtage1{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_s"] == 1.0) 1.0 else 0.0)
        }
    },
    anzahl_shoppingtage2{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_s"] == 2.0) 1.0 else 0.0)
        }
    },
    anzahl_shoppingtage3{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_s"] == 3.0) 1.0 else 0.0)
        }
    },
    anzahl_shoppingtage4{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_s"] == 4.0) 1.0 else 0.0)
        }
    },
    anzahl_shoppingtage5{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_s"] == 5.0) 1.0 else 0.0)
        }
    },
    anzahl_shoppingtage6{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_s"] == 6.0) 1.0 else 0.0)
        }
    },
    anzahl_shoppingtage7{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_s"] == 7.0) 1.0 else 0.0)
        }
    },

    /*
     * Anzahl Transporttage (Stufe 1E)
     */
    anztage_t{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return actitoppPerson.attributesMap["anztage_t"] ?:0.0
        }
    },
    anzahl_transporttage0{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_t"] == 0.0) 1.0 else 0.0)
        }
    },
    anzahl_transporttage1{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_t"] == 1.0) 1.0 else 0.0)
        }
    },
    anzahl_transporttage2{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_t"] == 2.0) 1.0 else 0.0)
        }
    },
    anzahl_transporttage3{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_t"] == 3.0) 1.0 else 0.0)
        }
    },
    anzahl_transporttage4{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_t"] == 4.0) 1.0 else 0.0)
        }
    },
    anzahl_transporttage5{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_t"] == 5.0) 1.0 else 0.0)
        }
    },
    anzahl_transporttage6{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_t"] == 6.0) 1.0 else 0.0)
        }
    },
    anzahl_transporttage7{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.attributesMap["anztage_t"] == 7.0) 1.0 else 0.0)
        }
    },

    /*
     * Anzahl immobile Tage (Stufe 1F)
     */
    anztage_immobil{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return actitoppPerson.attributesMap["anztage_immobil"]?: 0.0
        }
    },

    /*
     * Properties fuer Anzahl an Aktivitaeten in der Woche
     */
    wakt_prowoche_1bis3{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            val amountofactivities = actitoppPerson.countActivityTypes(ActivityType.WORK)
            return (if (amountofactivities >= 1 && amountofactivities <= 3) 1.0 else 0.0)
        }
    },
    wakt_prowoche_7bis10{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            val amountofactivities = actitoppPerson.countActivityTypes(ActivityType.WORK)
            return (if (amountofactivities >= 7 && amountofactivities <= 10) 1.0 else 0.0)
        }
    },
    eakt_prowoche_ueber0{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            val amountofactivities = actitoppPerson.countActivityTypes(ActivityType.EDUCATION)
            return (if (amountofactivities > 0) 1.0 else 0.0)
        }
    },
    eakt_prowoche_1bis3{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            val amountofactivities = actitoppPerson.countActivityTypes(ActivityType.EDUCATION)
            return (if (amountofactivities >= 1 && amountofactivities <= 3) 1.0 else 0.0)
        }
    },
    lakt_prowoche_1bis3{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            val amountofactivities = actitoppPerson.countActivityTypes(ActivityType.LEISURE)
            return (if (amountofactivities >= 1 && amountofactivities <= 3) 1.0 else 0.0)
        }
    },
    lakt_prowoche_4bis6{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            val amountofactivities = actitoppPerson.countActivityTypes(ActivityType.LEISURE)
            return (if (amountofactivities >= 4 && amountofactivities <= 6) 1.0 else 0.0)
        }
    },
    lakt_prowoche_7bis10{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            val amountofactivities = actitoppPerson.countActivityTypes(ActivityType.LEISURE)
            return (if (amountofactivities >= 7 && amountofactivities <= 10) 1.0 else 0.0)
        }
    },
    sakt_prowoche_1bis3{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            val amountofactivities = actitoppPerson.countActivityTypes(ActivityType.SHOPPING)
            return (if (amountofactivities >= 1 && amountofactivities <= 3) 1.0 else 0.0)
        }
    },
    sakt_prowoche_4bis6{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            val amountofactivities = actitoppPerson.countActivityTypes(ActivityType.SHOPPING)
            return (if (amountofactivities >= 4 && amountofactivities <= 6) 1.0 else 0.0)
        }
    },
    sakt_prowoche_7bis10{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            val amountofactivities = actitoppPerson.countActivityTypes(ActivityType.SHOPPING)
            return (if (amountofactivities >= 7 && amountofactivities <= 10) 1.0 else 0.0)
        }
    },
    takt_prowoche_1bis3{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            val amountofactivities = actitoppPerson.countActivityTypes(ActivityType.TRANSPORT)
            return (if (amountofactivities >= 1 && amountofactivities <= 3) 1.0 else 0.0)
        }
    },
    takt_prowoche_4bis6{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            val amountofactivities = actitoppPerson.countActivityTypes(ActivityType.TRANSPORT)
            return (if (amountofactivities >= 4 && amountofactivities <= 6) 1.0 else 0.0)
        }
    },
    takt_prowoche_7bis10{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            val amountofactivities = actitoppPerson.countActivityTypes(ActivityType.TRANSPORT)
            return (if (amountofactivities >= 7 && amountofactivities <= 10) 1.0 else 0.0)
        }
    },

    anzakt_woche_w{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return actitoppPerson.countActivityTypes(ActivityType.WORK).toDouble()
        }
    },
    anzakt_woche_e{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return actitoppPerson.countActivityTypes(ActivityType.EDUCATION).toDouble()
        }
    },
    anzakt_woche_l{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return actitoppPerson.countActivityTypes(ActivityType.LEISURE).toDouble()
        }
    },
    anzakt_woche_s{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return actitoppPerson.countActivityTypes(ActivityType.SHOPPING).toDouble()
        }
    },
    anzakt_woche_t{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return actitoppPerson.countActivityTypes(ActivityType.TRANSPORT).toDouble()
        }
    },

    /*
     * Anzahl Touren in der Woche
     */
    anztouren_woche_w{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return actitoppPerson.countTourTypes(ActivityType.WORK).toDouble()
        }
    },
    anztouren_woche_e{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return actitoppPerson.countTourTypes(ActivityType.EDUCATION).toDouble()
        }
    },
    anztouren_woche_l{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return actitoppPerson.countTourTypes(ActivityType.LEISURE).toDouble()
        }
    },
    anztouren_woche_s{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return actitoppPerson.countTourTypes(ActivityType.SHOPPING).toDouble()
        }
    },
    anztouren_woche_t{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return actitoppPerson.countTourTypes(ActivityType.TRANSPORT).toDouble()
        }
    },

    /*
     * Anzahl Tage mit spezifischen Aktivitaetentypen
     */
    tagemit_wakt_1{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.WORK) == 1) 1.0 else 0.0)
        }
    },
    tagemit_wakt_2{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.WORK) == 2) 1.0 else 0.0)
        }
    },
    tagemit_wakt_3{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.WORK) == 3) 1.0 else 0.0)
        }
    },
    tagemit_wakt_4{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.WORK) == 4) 1.0 else 0.0)
        }
    },
    tagemit_wakt_5{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.WORK) == 5) 1.0 else 0.0)
        }
    },
    tagemit_wakt_6{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.WORK) == 6) 1.0 else 0.0)
        }
    },
    tagemit_wakt_7{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.WORK) == 7) 1.0 else 0.0)
        }
    },
    tagemit_eakt_1{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.EDUCATION) == 1) 1.0 else 0.0)
        }
    },
    tagemit_eakt_2{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.EDUCATION) == 2) 1.0 else 0.0)
        }
    },
    tagemit_eakt_3{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.EDUCATION) == 3) 1.0 else 0.0)
        }
    },
    tagemit_eakt_4{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.EDUCATION) == 4) 1.0 else 0.0)
        }
    },
    tagemit_eakt_5{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.EDUCATION) == 5) 1.0 else 0.0)
        }
    },
    tagemit_eakt_6{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.EDUCATION) == 6) 1.0 else 0.0)
        }
    },
    tagemit_eakt_7{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.EDUCATION) == 7) 1.0 else 0.0)
        }
    },
    tagemit_lakt_1{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.LEISURE) == 1) 1.0 else 0.0)
        }
    },
    tagemit_lakt_2{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.LEISURE) == 2) 1.0 else 0.0)
        }
    },
    tagemit_lakt_3{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.LEISURE) == 3) 1.0 else 0.0)
        }
    },
    tagemit_lakt_4{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.LEISURE) == 4) 1.0 else 0.0)
        }
    },
    tagemit_lakt_5{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.LEISURE) == 5) 1.0 else 0.0)
        }
    },
    tagemit_lakt_6{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.LEISURE) == 6) 1.0 else 0.0)
        }
    },
    tagemit_lakt_7{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.LEISURE) == 7) 1.0 else 0.0)
        }
    },
    tagemit_sakt_1{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.SHOPPING) == 1) 1.0 else 0.0)
        }
    },
    tagemit_sakt_2{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.SHOPPING) == 2) 1.0 else 0.0)
        }
    },
    tagemit_sakt_3{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.SHOPPING) == 3) 1.0 else 0.0)
        }
    },
    tagemit_sakt_4{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.SHOPPING) == 4) 1.0 else 0.0)
        }
    },
    tagemit_sakt_5{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.SHOPPING) == 5) 1.0 else 0.0)
        }
    },
    tagemit_sakt_6{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.SHOPPING) == 6) 1.0 else 0.0)
        }
    },
    tagemit_sakt_7{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.SHOPPING) == 7) 1.0 else 0.0)
        }
    },
    tagemit_takt_1{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.TRANSPORT) == 1) 1.0 else 0.0)
        }
    },
    tagemit_takt_2{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.TRANSPORT) == 2) 1.0 else 0.0)
        }
    },
    tagemit_takt_3{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.TRANSPORT) == 3) 1.0 else 0.0)
        }
    },
    tagemit_takt_4{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.TRANSPORT) == 4) 1.0 else 0.0)
        }
    },
    tagemit_takt_5{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.TRANSPORT) == 5) 1.0 else 0.0)
        }
    },
    tagemit_takt_6{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.TRANSPORT) == 6) 1.0 else 0.0)
        }
    },
    tagemit_takt_7{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.countDaysWithSpecificActivity(ActivityType.TRANSPORT) == 7) 1.0 else 0.0)
        }
    },

    /*
     * Anzahl Tage mit Touren Vor/Nach-Haupttour
     */
    anztagemit_tourenvorht{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            var counter = 0
            for (day in actitoppPerson.days())  {
                if (!day.isHomeDay && day.lowestTourIndex < 0) counter++
            }
            return counter.toDouble()
        }
    },
    anztagemit_tourennachht{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            var counter = 0
            for (day in actitoppPerson.days())  {
                if (!day.isHomeDay && day.highestTourIndex > 0) counter++
            }
            return counter.toDouble()
        }
    },

    /*
     * Zeitbudget Work
     */
    zeitbudget_work_ueber_kat2{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.getAttributefromMap("Wbudget_category_alternative") > 2) 1.0 else 0.0)
        }
    },

    /*
     * Default Anzahl Touren am Tag = 1
     */
    mean_1tour{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.getAttributefromMap("anztourentag_mean") == 1.0) 1.0 else 0.0)
        }
    },

    /*
     * Default Anzahl Touren am Tag = 2
     */
    mean_2touren{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.getAttributefromMap("anztourentag_mean") == 2.0) 1.0 else 0.0)
        }
    },

    /*
     * Default Anzahl Akt am Tag = 1
     */
    mean_1akt{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.getAttributefromMap("anzakttag_mean") == 1.0) 1.0 else 0.0)
        }
    },

    /*
     * Default Anzahl Akt am Tag = 2
     */
    mean_2akt{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.getAttributefromMap("anzakttag_mean") == 2.0) 1.0 else 0.0)
        }
    },

    /*
     * Default Anzahl Akt am Tag = 3
     */
    mean_3akt{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.getAttributefromMap("anzakttag_mean") == 3.0) 1.0 else 0.0)
        }
    },

    /*
     * Standard-Startzeitraum fuer T1
     */
    std_start_T1_6_7_Uhr{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.getAttributefromMap("first_tour_default_start_cat") == 3.0) 1.0 else 0.0)
        }
    },
    std_start_T1_7_8_Uhr{
        override fun getAttribute(actitoppPerson: ActitoppPerson): Double {
            return (if (actitoppPerson.getAttributefromMap("first_tour_default_start_cat") == 4.0) 1.0 else 0.0)
        }
    },
    ;

    abstract fun getAttribute(actitoppPerson: ActitoppPerson): Double


    companion object {

        fun getPersonParameterFromString(name: String): ActitoppPersonParameters {
            return entries.first {it.name == name}
        }
    }
}
