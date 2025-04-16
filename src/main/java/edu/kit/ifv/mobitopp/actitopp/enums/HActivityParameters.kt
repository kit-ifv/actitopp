package edu.kit.ifv.mobitopp.actitopp.enums

import edu.kit.ifv.mobitopp.actitopp.HActivity

/**
 * @author Tim Hilgert
 */
enum class HActivityParameters
/**
 * privater Konstruktor
 *
 * @param description
 */(val description: String) {
    /*
        * Aktivitaetenzweck
        */
    aktzweck_work("aktzweck_work") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.activityType == ActivityType.WORK) 1.0 else 0.0)
        }
    },
    aktzweck_education("aktzweck_education") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.activityType == ActivityType.EDUCATION) 1.0 else 0.0)
        }
    },
    aktzweck_leisure("aktzweck_leisure") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.activityType == ActivityType.LEISURE) 1.0 else 0.0)
        }
    },
    aktzweck_shopping("aktzweck_shopping") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.activityType == ActivityType.SHOPPING) 1.0 else 0.0)
        }
    },
    aktzweck_transport("aktzweck_transport") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.activityType == ActivityType.TRANSPORT) 1.0 else 0.0)
        }
    },


    /*
     * Dauer Aktivitaet
     */
    dauer_akt("dauer_akt") {
        override fun getAttribute(act: HActivity): Double {
            return act.duration.toDouble()
        }
    },
    dauer_akt_1bis14("dauer_akt_1bis14") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.duration >= 1 && act.duration <= 14) 1.0 else 0.0)
        }
    },
    dauer_akt_15bis29("dauer_akt_15bis29") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.duration >= 15 && act.duration <= 29) 1.0 else 0.0)
        }
    },
    dauer_akt_30bis59("dauer_akt_30bis59") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.duration >= 30 && act.duration <= 59) 1.0 else 0.0)
        }
    },
    dauer_akt_60bis119("dauer_akt_60bis119") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.duration >= 60 && act.duration <= 119) 1.0 else 0.0)
        }
    },
    dauer_akt_120bis179("dauer_akt_120bis179") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.duration >= 120 && act.duration <= 179) 1.0 else 0.0)
        }
    },
    dauer_akt_180bis239("dauer_akt_180bis239") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.duration >= 180 && act.duration <= 239) 1.0 else 0.0)
        }
    },
    dauer_akt_240bis299("dauer_akt_240bis299") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.duration >= 240 && act.duration <= 299) 1.0 else 0.0)
        }
    },
    dauer_akt_300bis359("dauer_akt_300bis359") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.duration >= 300 && act.duration <= 359) 1.0 else 0.0)
        }
    },
    dauer_akt_360bis419("dauer_akt_360bis419") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.duration >= 360 && act.duration <= 419) 1.0 else 0.0)
        }
    },
    dauer_akt_420bis479("dauer_akt_420bis479") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.duration >= 420 && act.duration <= 479) 1.0 else 0.0)
        }
    },
    dauer_akt_480bis539("dauer_akt_480bis539") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.duration >= 480 && act.duration <= 539) 1.0 else 0.0)
        }
    },
    dauer_akt_540bis599("dauer_akt_540bis599") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.duration >= 540 && act.duration <= 599) 1.0 else 0.0)
        }
    },
    dauer_akt_600bis659("dauer_akt_600bis659") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.duration >= 600 && act.duration <= 659) 1.0 else 0.0)
        }
    },
    dauer_akt_660bis719("dauer_akt_660bis719") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.duration >= 660 && act.duration <= 719) 1.0 else 0.0)
        }
    },
    dauer_akt_720bis1440("dauer_akt_720bis1440") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.duration >= 720 && act.duration <= 1440) 1.0 else 0.0)
        }
    },

    /*
     * Erste Aktivitaet Tag/Tour
     */
    ersteaktamtag("ersteaktamtag") {
        override fun getAttribute(act: HActivity): Double {
            return (if ((act.index == act.tour.lowestActivityIndex) && (act.tour.index == act.day.lowestTourIndex)) 1.0 else 0.0)
        }
    },
    ersteaktintour("ersteaktintour") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.index == act.tour.lowestActivityIndex) 1.0 else 0.0)
        }
    },


    /*
     * mean Time Eigenschaft
     */
    mittl_zeit_akt_1bis14min("mittl_zeit_akt_1bis14min") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.calculateMeanTime() >= 1 && act.calculateMeanTime() <= 14) 1.0 else 0.0)
        }
    },
    mittl_zeit_akt_15bis29min("mittl_zeit_akt_15bis29min") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.calculateMeanTime() >= 15 && act.calculateMeanTime() <= 29) 1.0 else 0.0)
        }
    },
    mittl_zeit_akt_30bis59min("mittl_zeit_akt_30bis59min") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.calculateMeanTime() >= 30 && act.calculateMeanTime() <= 59) 1.0 else 0.0)
        }
    },
    mittl_zeit_akt_60bis119min("mittl_zeit_akt_60bis119min") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.calculateMeanTime() >= 60 && act.calculateMeanTime() <= 119) 1.0 else 0.0)
        }
    },
    mittl_zeit_akt_120bis179min("mittl_zeit_akt_120bis179min") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.calculateMeanTime() >= 120 && act.calculateMeanTime() <= 179) 1.0 else 0.0)
        }
    },
    mittl_zeit_akt_180bis239min("mittl_zeit_akt_180bis239min") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.calculateMeanTime() >= 180 && act.calculateMeanTime() <= 239) 1.0 else 0.0)
        }
    },
    mittl_zeit_akt_240bis299min("mittl_zeit_akt_240bis299min") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.calculateMeanTime() >= 240 && act.calculateMeanTime() <= 299) 1.0 else 0.0)
        }
    },
    mittl_zeit_akt_300bis359min("mittl_zeit_akt_300bis359min") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.calculateMeanTime() >= 300 && act.calculateMeanTime() <= 359) 1.0 else 0.0)
        }
    },
    mittl_zeit_akt_360bis419min("mittl_zeit_akt_360bis419min") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.calculateMeanTime() >= 360 && act.calculateMeanTime() <= 419) 1.0 else 0.0)
        }
    },
    mittl_zeit_akt_420bis479min("mittl_zeit_akt_420bis479min") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.calculateMeanTime() >= 420 && act.calculateMeanTime() <= 479) 1.0 else 0.0)
        }
    },
    mittl_zeit_akt_480bis539min("mittl_zeit_akt_480bis539min") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.calculateMeanTime() >= 480 && act.calculateMeanTime() <= 539) 1.0 else 0.0)
        }
    },
    mittl_zeit_akt_540bis599min("mittl_zeit_akt_540bis599min") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.calculateMeanTime() >= 540 && act.calculateMeanTime() <= 599) 1.0 else 0.0)
        }
    },
    mittl_zeit_akt_600bis659min("mittl_zeit_akt_600bis659min") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.calculateMeanTime() >= 600 && act.calculateMeanTime() <= 659) 1.0 else 0.0)
        }
    },
    mittl_zeit_akt_660bis719min("mittl_zeit_akt_660bis719min") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.calculateMeanTime() >= 660 && act.calculateMeanTime() <= 719) 1.0 else 0.0)
        }
    },
    mittl_zeit_akt_720bis1440min("mittl_zeit_akt_720bis1440min") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.calculateMeanTime() >= 720 && act.calculateMeanTime() <= 1440) 1.0 else 0.0)
        }
    },

    /*
     * Anzahl Aktivitaeten = Anzahl Tage mit diesem Zweck
     */
    anzaktwieanztagemitzweck("anzaktwieanztagemitzweck") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.weekPattern.countActivitiesPerWeek(act.activityType) == act.weekPattern.countDaysWithSpecificActivity(
                    act.activityType
                )
            ) 1.0 else 0.0)
        }
    },

    /*
     * Wochenzeitbudget mit Zweck
     */
    wochenzbudget_zweck_kat1("wochenzbudget_zweck_kat1") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.person.getAttributefromMap(act.activityType.toString() + "budget_category_alternative") == 1.0) 1.0 else 0.0)
        }
    },
    wochenzbudget_zweck_kat2("wochenzbudget_zweck_kat2") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.person.getAttributefromMap(act.activityType.toString() + "budget_category_alternative") == 2.0) 1.0 else 0.0)
        }
    },
    wochenzbudget_zweck_kat3("wochenzbudget_zweck_kat3") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.person.getAttributefromMap(act.activityType.toString() + "budget_category_alternative") == 3.0) 1.0 else 0.0)
        }
    },
    wochenzbudget_zweck_kat4("wochenzbudget_zweck_kat4") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.person.getAttributefromMap(act.activityType.toString() + "budget_category_alternative") == 4.0) 1.0 else 0.0)
        }
    },
    wochenzbudget_zweck_kat5("wochenzbudget_zweck_kat5") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.person.getAttributefromMap(act.activityType.toString() + "budget_category_alternative") == 5.0) 1.0 else 0.0)
        }
    },

    /*
     * Akt liegt Vor/Nach Hauptakt
     */
    aktisthauptakt("aktisthauptakt") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.index == 0) 1.0 else 0.0)
        }
    },
    aktliegtvorhauptakt("aktliegtvorhauptakt") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.index < 0) 1.0 else 0.0)
        }
    },
    aktliegtnachhauptakt("aktliegtnachhauptakt") {
        override fun getAttribute(act: HActivity): Double {
            return (if (act.index > 0) 1.0 else 0.0)
        }
    },

    /*
     * Akt Startzeit
     */
    start_stunde_akt_0_5("start_stunde_akt_0_5") {
        override fun getAttribute(act: HActivity): Double {
            val startzeit_stunde_akt = act.startTime / 60
            return (if (startzeit_stunde_akt >= 0 && startzeit_stunde_akt <= 5) 1.0 else 0.0)
        }
    },
    start_stunde_akt_6_8("start_stunde_akt_6_8") {
        override fun getAttribute(act: HActivity): Double {
            val startzeit_stunde_akt = act.startTime / 60
            return (if (startzeit_stunde_akt >= 6 && startzeit_stunde_akt <= 8) 1.0 else 0.0)
        }
    },
    start_stunde_akt_10_12("start_stunde_akt_10_12") {
        override fun getAttribute(act: HActivity): Double {
            val startzeit_stunde_akt = act.startTime / 60
            return (if (startzeit_stunde_akt >= 10 && startzeit_stunde_akt <= 12) 1.0 else 0.0)
        }
    },
    start_stunde_akt_13_15("start_stunde_akt_13_15") {
        override fun getAttribute(act: HActivity): Double {
            val startzeit_stunde_akt = act.startTime / 60
            return (if (startzeit_stunde_akt >= 13 && startzeit_stunde_akt <= 15) 1.0 else 0.0)
        }
    },
    start_stunde_akt_16_18("start_stunde_akt_16_18") {
        override fun getAttribute(act: HActivity): Double {
            val startzeit_stunde_akt = act.startTime / 60
            return (if (startzeit_stunde_akt >= 16 && startzeit_stunde_akt <= 18) 1.0 else 0.0)
        }
    },
    start_stunde_akt_19_21("start_stunde_akt_19_21") {
        override fun getAttribute(act: HActivity): Double {
            val startzeit_stunde_akt = act.startTime / 60
            return (if (startzeit_stunde_akt >= 19 && startzeit_stunde_akt <= 21) 1.0 else 0.0)
        }
    },
    start_stunde_akt_22_23("start_stunde_akt_22_23") {
        override fun getAttribute(act: HActivity): Double {
            val startzeit_stunde_akt = act.startTime / 60
            return (if (startzeit_stunde_akt >= 22 && startzeit_stunde_akt <= 23) 1.0 else 0.0)
        }
    },
    ;

    abstract fun getAttribute(act: HActivity): Double


    companion object {
        /**
         * Methode zur Rueckgabe des EnumValues fuer einen gegebenen String
         *
         * @param name
         * @return
         */
        fun getActivityParameterFromString(name: String): HActivityParameters {
            return entries.first { it.name == name }
        }
    }
}
