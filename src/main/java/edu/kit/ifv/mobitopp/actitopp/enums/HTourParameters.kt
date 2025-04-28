package edu.kit.ifv.mobitopp.actitopp.enums

import edu.kit.ifv.mobitopp.actitopp.HTour

/**
 * @author Tim Hilgert
 */
enum class HTourParameters
{
    /*
        * Tourtyp
        */
    tourtyp_work{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.getActivity(0).activityType == ActivityType.WORK) 1.0 else 0.0)
        }
    },
    tourtyp_education{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.getActivity(0).activityType == ActivityType.EDUCATION) 1.0 else 0.0)
        }
    },
    tourtyp_leisure{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.getActivity(0).activityType == ActivityType.LEISURE) 1.0 else 0.0)
        }
    },
    tourtyp_shopping{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.getActivity(0).activityType == ActivityType.SHOPPING) 1.0 else 0.0)
        }
    },

    tourtyp_transport{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.getActivity(0).activityType == ActivityType.TRANSPORT) 1.0 else 0.0)
        }
    },


    /*
     * Dauer der Akt in Tour
     */
    dauer_akt_in_tour_0bis2std{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.actDuration >= 0 * 60 && tour.actDuration < 2 * 60) 1.0 else 0.0)
        }
    },
    dauer_akt_in_tour_2bis4std{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.actDuration >= 2 * 60 && tour.actDuration < 4 * 60) 1.0 else 0.0)
        }
    },
    dauer_akt_in_tour_4bis6std{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.actDuration >= 4 * 60 && tour.actDuration < 6 * 60) 1.0 else 0.0)
        }
    },
    dauer_akt_in_tour_6bis8std{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.actDuration >= 6 * 60 && tour.actDuration < 8 * 60) 1.0 else 0.0)
        }
    },
    dauer_akt_in_tour_8bis10std{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.actDuration >= 8 * 60 && tour.actDuration < 10 * 60) 1.0 else 0.0)
        }
    },
    dauer_akt_in_tour_10bis12std{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.actDuration >= 10 * 60 && tour.actDuration < 12 * 60) 1.0 else 0.0)
        }
    },
    dauer_akt_in_tour_12bis14std{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.actDuration >= 12 * 60 && tour.actDuration < 14 * 60) 1.0 else 0.0)
        }
    },
    dauer_akt_in_tour_ueber14std{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.actDuration >= 14 * 60) 1.0 else 0.0)
        }
    },


    /*
     * Ende Tour vorher
     */
    endetourvorher_Std_0{
        override fun getAttribute(tour: HTour): Double {
            val endetourvorher = tour.day.getTour(tour.index - 1).endTime
            return (if (endetourvorher >= 0 * 60 && endetourvorher < 1 * 60) 1.0 else 0.0)
        }
    },
    endetourvorher_Std_1{
        override fun getAttribute(tour: HTour): Double {
            val endetourvorher = tour.day.getTour(tour.index - 1).endTime
            return (if (endetourvorher >= 1 * 60 && endetourvorher < 2 * 60) 1.0 else 0.0)
        }
    },
    endetourvorher_Std_2{
        override fun getAttribute(tour: HTour): Double {
            val endetourvorher = tour.day.getTour(tour.index - 1).endTime
            return (if (endetourvorher >= 2 * 60 && endetourvorher < 3 * 60) 1.0 else 0.0)
        }
    },
    endetourvorher_Std_3{
        override fun getAttribute(tour: HTour): Double {
            val endetourvorher = tour.day.getTour(tour.index - 1).endTime
            return (if (endetourvorher >= 3 * 60 && endetourvorher < 4 * 60) 1.0 else 0.0)
        }
    },
    endetourvorher_Std_4{
        override fun getAttribute(tour: HTour): Double {
            val endetourvorher = tour.day.getTour(tour.index - 1).endTime
            return (if (endetourvorher >= 4 * 60 && endetourvorher < 5 * 60) 1.0 else 0.0)
        }
    },
    endetourvorher_Std_5{
        override fun getAttribute(tour: HTour): Double {
            val endetourvorher = tour.day.getTour(tour.index - 1).endTime
            return (if (endetourvorher >= 5 * 60 && endetourvorher < 6 * 60) 1.0 else 0.0)
        }
    },
    endetourvorher_Std_6{
        override fun getAttribute(tour: HTour): Double {
            val endetourvorher = tour.day.getTour(tour.index - 1).endTime
            return (if (endetourvorher >= 6 * 60 && endetourvorher < 7 * 60) 1.0 else 0.0)
        }
    },
    endetourvorher_Std_7{
        override fun getAttribute(tour: HTour): Double {
            val endetourvorher = tour.day.getTour(tour.index - 1).endTime
            return (if (endetourvorher >= 7 * 60 && endetourvorher < 8 * 60) 1.0 else 0.0)
        }
    },
    endetourvorher_Std_8{
        override fun getAttribute(tour: HTour): Double {
            val endetourvorher = tour.day.getTour(tour.index - 1).endTime
            return (if (endetourvorher >= 8 * 60 && endetourvorher < 9 * 60) 1.0 else 0.0)
        }
    },
    endetourvorher_Std_9{
        override fun getAttribute(tour: HTour): Double {
            val endetourvorher = tour.day.getTour(tour.index - 1).endTime
            return (if (endetourvorher >= 9 * 60 && endetourvorher < 10 * 60) 1.0 else 0.0)
        }
    },
    endetourvorher_Std_10{
        override fun getAttribute(tour: HTour): Double {
            val endetourvorher = tour.day.getTour(tour.index - 1).endTime
            return (if (endetourvorher >= 10 * 60 && endetourvorher < 11 * 60) 1.0 else 0.0)
        }
    },
    endetourvorher_Std_11{
        override fun getAttribute(tour: HTour): Double {
            val endetourvorher = tour.day.getTour(tour.index - 1).endTime
            return (if (endetourvorher >= 11 * 60 && endetourvorher < 12 * 60) 1.0 else 0.0)
        }
    },
    endetourvorher_Std_12{
        override fun getAttribute(tour: HTour): Double {
            val endetourvorher = tour.day.getTour(tour.index - 1).endTime
            return (if (endetourvorher >= 12 * 60 && endetourvorher < 13 * 60) 1.0 else 0.0)
        }
    },
    endetourvorher_Std_13{
        override fun getAttribute(tour: HTour): Double {
            val endetourvorher = tour.day.getTour(tour.index - 1).endTime
            return (if (endetourvorher >= 13 * 60 && endetourvorher < 14 * 60) 1.0 else 0.0)
        }
    },
    endetourvorher_Std_14{
        override fun getAttribute(tour: HTour): Double {
            val endetourvorher = tour.day.getTour(tour.index - 1).endTime
            return (if (endetourvorher >= 14 * 60 && endetourvorher < 15 * 60) 1.0 else 0.0)
        }
    },
    endetourvorher_Std_15{
        override fun getAttribute(tour: HTour): Double {
            val endetourvorher = tour.day.getTour(tour.index - 1).endTime
            return (if (endetourvorher >= 15 * 60 && endetourvorher < 16 * 60) 1.0 else 0.0)
        }
    },
    endetourvorher_Std_16{
        override fun getAttribute(tour: HTour): Double {
            val endetourvorher = tour.day.getTour(tour.index - 1).endTime
            return (if (endetourvorher >= 16 * 60 && endetourvorher < 17 * 60) 1.0 else 0.0)
        }
    },
    endetourvorher_Std_17{
        override fun getAttribute(tour: HTour): Double {
            val endetourvorher = tour.day.getTour(tour.index - 1).endTime
            return (if (endetourvorher >= 17 * 60 && endetourvorher < 18 * 60) 1.0 else 0.0)
        }
    },
    endetourvorher_Std_18{
        override fun getAttribute(tour: HTour): Double {
            val endetourvorher = tour.day.getTour(tour.index - 1).endTime
            return (if (endetourvorher >= 18 * 60 && endetourvorher < 19 * 60) 1.0 else 0.0)
        }
    },
    endetourvorher_Std_19{
        override fun getAttribute(tour: HTour): Double {
            val endetourvorher = tour.day.getTour(tour.index - 1).endTime
            return (if (endetourvorher >= 19 * 60 && endetourvorher < 20 * 60) 1.0 else 0.0)
        }
    },
    endetourvorher_Std_20{
        override fun getAttribute(tour: HTour): Double {
            val endetourvorher = tour.day.getTour(tour.index - 1).endTime
            return (if (endetourvorher >= 20 * 60 && endetourvorher < 21 * 60) 1.0 else 0.0)
        }
    },
    endetourvorher_Std_21{
        override fun getAttribute(tour: HTour): Double {
            val endetourvorher = tour.day.getTour(tour.index - 1).endTime
            return (if (endetourvorher >= 21 * 60 && endetourvorher < 22 * 60) 1.0 else 0.0)
        }
    },
    endetourvorher_Std_22{
        override fun getAttribute(tour: HTour): Double {
            val endetourvorher = tour.day.getTour(tour.index - 1).endTime
            return (if (endetourvorher >= 22 * 60 && endetourvorher < 23 * 60) 1.0 else 0.0)
        }
    },
    endetourvorher_Std_23{
        override fun getAttribute(tour: HTour): Double {
            val endetourvorher = tour.day.getTour(tour.index - 1).endTime
            return (if (endetourvorher >= 23 * 60 && endetourvorher < 24 * 60) 1.0 else 0.0)
        }
    },

    /*
     * Tour hat X Akt
     */
    tourhat1akt{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.amountOfActivities == 1) 1.0 else 0.0)
        }
    },
    tourhat2akt{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.amountOfActivities == 2) 1.0 else 0.0)
        }
    },
    tourhat3akt{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.amountOfActivities == 3) 1.0 else 0.0)
        }
    },
    tourhat4akt{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.amountOfActivities == 4) 1.0 else 0.0)
        }
    },

    /*
     * Anzahl Akt vor Hauptakt
     */
    anzaktvorhauptaktist1{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.lowestActivityIndex == -1) 1.0 else 0.0)
        }
    },
    anzaktvorhauptaktist2{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.lowestActivityIndex == -2) 1.0 else 0.0)
        }
    },
    anzaktvorhauptaktist3{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.lowestActivityIndex == -3) 1.0 else 0.0)
        }
    },

    /*
     * Tour Nr des Tages
     */
    tour1destages{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.day.lowestTourIndex == tour.index) 1.0 else 0.0)
        }
    },
    tour2destages{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.day.lowestTourIndex + 1 == tour.index) 1.0 else 0.0)
        }
    },
    tour3destages{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.day.lowestTourIndex + 2 == tour.index) 1.0 else 0.0)
        }
    },

    /*
     * Erste/Letzte Tour des Tages
     */
    erstetourdestages{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.day.lowestTourIndex == tour.index) 1.0 else 0.0)
        }
    },
    letztetourdestages{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.day.highestTourIndex == tour.index) 1.0 else 0.0)
        }
    },

    /*
     * Tour Vor/Nach Haupttour
     */
    tourliegtvorhaupttour{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.index < 0) 1.0 else 0.0)
        }
    },
    tourliegtnachhaupttour{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.index > 0) 1.0 else 0.0)
        }
    },
    touristhaupttour{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.index == 0) 1.0 else 0.0)
        }
    },


    /*
     * Startzeitraum Haupttour
     */
    startzeitraum_ht_1{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.attributesMap["tourStartCat_index"] == 1.0) 1.0 else 0.0)
        }
    },
    startzeitraum_ht_2{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.attributesMap["tourStartCat_index"] == 2.0) 1.0 else 0.0)
        }
    },
    startzeitraum_ht_3{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.attributesMap["tourStartCat_index"] == 3.0) 1.0 else 0.0)
        }
    },
    startzeitraum_ht_4{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.attributesMap["tourStartCat_index"] == 4.0) 1.0 else 0.0)
        }
    },
    startzeitraum_ht_5{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.attributesMap["tourStartCat_index"] == 5.0) 1.0 else 0.0)
        }
    },
    startzeitraum_ht_6{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.attributesMap["tourStartCat_index"] == 6.0) 1.0 else 0.0)
        }
    },
    startzeitraum_ht_7{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.attributesMap["tourStartCat_index"] == 7.0) 1.0 else 0.0)
        }
    },
    startzeitraum_ht_8{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.attributesMap["tourStartCat_index"] == 8.0) 1.0 else 0.0)
        }
    },
    startzeitraum_ht_9{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.attributesMap["tourStartCat_index"] == 9.0) 1.0 else 0.0)
        }
    },
    startzeitraum_ht_10{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.attributesMap["tourStartCat_index"] == 10.0) 1.0 else 0.0)
        }
    },
    startzeitraum_ht_11{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.attributesMap["tourStartCat_index"] == 11.0) 1.0 else 0.0)
        }
    },
    startzeitraum_ht_12{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.attributesMap["tourStartCat_index"] == 12.0) 1.0 else 0.0)
        }
    },
    startzeitraum_ht_13{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.attributesMap["tourStartCat_index"] == 13.0) 1.0 else 0.0)
        }
    },
    startzeitraum_ht_14{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.attributesMap["tourStartCat_index"] == 14.0) 1.0 else 0.0)
        }
    },
    startzeitraum_ht_15{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.attributesMap["tourStartCat_index"] == 15.0) 1.0 else 0.0)
        }
    },
    startzeitraum_ht_16{
        override fun getAttribute(tour: HTour): Double {
            return (if (tour.attributesMap["tourStartCat_index"] == 16.0) 1.0 else 0.0)
        }
    },

    vorherigeentscheidungyes{
        override fun getAttribute(tour: HTour): Double {
            var result = 0.0
            var tourgefunden = false
            var tmptour: HTour? = tour
            while (!tourgefunden)  {
                tmptour = tmptour!!.previousTourinPattern
                if (tmptour == null) break
                if (tmptour.isFirstTouroftheDay && (tmptour.getActivity(0).activityType == ActivityType.WORK || tmptour.getActivity(
                        0
                    ).activityType == ActivityType.EDUCATION)
                ) {
                    tourgefunden = true
                }
            }
            if (tourgefunden && tmptour!!.getAttributefromMap("default_start_cat_yes") == 1.0) result = 1.0
            return result
        }
    },
    ;


    abstract fun getAttribute(tour: HTour): Double


    companion object {
        fun getTourParameterFromString(name: String): HTourParameters {
            return entries.first { it.name == name }
        }
    }
}
