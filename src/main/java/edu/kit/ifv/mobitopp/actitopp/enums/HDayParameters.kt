package edu.kit.ifv.mobitopp.actitopp.enums

import edu.kit.ifv.mobitopp.actitopp.HDay

/**
 * @author Tim Hilgert
 */
enum class HDayParameters
/**
 * privater Konstruktor
 *
 * @param description
 */{
    /*
        * Wochentag
        */
    tag_mo{
        override fun getAttribute(day: HDay): Double {
            return (if (day.weekday == 1) 1.0 else 0.0)
        }
    },
    tag_di{
        override fun getAttribute(day: HDay): Double {
            return (if (day.weekday == 2) 1.0 else 0.0)
        }
    },
    tag_mi{
        override fun getAttribute(day: HDay): Double {
            return (if (day.weekday == 3) 1.0 else 0.0)
        }
    },
    tag_do{
        override fun getAttribute(day: HDay): Double {
            return (if (day.weekday == 4) 1.0 else 0.0)
        }
    },
    tag_fr{
        override fun getAttribute(day: HDay): Double {
            return (if (day.weekday == 5) 1.0 else 0.0)
        }
    },
    tag_sa{
        override fun getAttribute(day: HDay): Double {
            return (if (day.weekday == 6) 1.0 else 0.0)
        }
    },
    tag_so{
        override fun getAttribute(day: HDay): Double {
            return (if (day.weekday == 7) 1.0 else 0.0)
        }
    },

    /*
     * Tag hat X Touren
     */
    taghat1tour{
        override fun getAttribute(day: HDay): Double {
            return (if (day.amountOfTours == 1) 1.0 else 0.0)
        }
    },
    taghat2touren{
        override fun getAttribute(day: HDay): Double {
            return (if (day.amountOfTours == 2) 1.0 else 0.0)
        }
    },
    taghat3touren{
        override fun getAttribute(day: HDay): Double {
            return (if (day.amountOfTours == 3) 1.0 else 0.0)
        }
    },


    /*
     * Tag hat X Aktivitaeten
     */
    taghat1akt{
        override fun getAttribute(day: HDay): Double {
            return (if (day.totalAmountOfActivitites == 1) 1.0 else 0.0)
        }
    },
    taghat2akt{
        override fun getAttribute(day: HDay): Double {
            return (if (day.totalAmountOfActivitites == 2) 1.0 else 0.0)
        }
    },
    taghat3akt{
        override fun getAttribute(day: HDay): Double {
            return (if (day.totalAmountOfActivitites == 3) 1.0 else 0.0)
        }
    },
    taghat4akt{
        override fun getAttribute(day: HDay): Double {
            return (if (day.totalAmountOfActivitites == 4) 1.0 else 0.0)
        }
    },
    taghat5akt{
        override fun getAttribute(day: HDay): Double {
            return (if (day.totalAmountOfActivitites == 5) 1.0 else 0.0)
        }
    },
    taghat6akt{
        override fun getAttribute(day: HDay): Double {
            return (if (day.totalAmountOfActivitites == 6) 1.0 else 0.0)
        }
    },

    /*
     * Tag hat Aktivitaeten eines Typs
     */
    taghattakt{
        override fun getAttribute(day: HDay): Double {
            return (if (day.getTotalNumberOfActivitites(ActivityType.TRANSPORT) > 0) 1.0 else 0.0)
        }
    },


    /*
     * Haupttour des Tages
     */
    haupttour_work{
        override fun getAttribute(day: HDay): Double {
            return (if (day.mainTourType == ActivityType.WORK) 1.0 else 0.0)
        }
    },
    haupttour_education{
        override fun getAttribute(day: HDay): Double {
            return (if (day.mainTourType == ActivityType.EDUCATION) 1.0 else 0.0)
        }
    },
    haupttour_leisure{
        override fun getAttribute(day: HDay): Double {
            return (if (day.mainTourType == ActivityType.LEISURE) 1.0 else 0.0)
        }
    },
    haupttour_shopping{
        override fun getAttribute(day: HDay): Double {
            return (if (day.mainTourType == ActivityType.SHOPPING) 1.0 else 0.0)
        }
    },
    haupttour_transport{
        override fun getAttribute(day: HDay): Double {
            return (if (day.mainTourType == ActivityType.TRANSPORT) 1.0 else 0.0)
        }
    },


    /*
     * Dauer Hauptakt Tag
     */
    dauer_hauptakt_tag_0bis2std{
        override fun getAttribute(day: HDay): Double {
            return (if (day.calculatedurationofmainactivitiesonday() >= 0 * 60 && day.calculatedurationofmainactivitiesonday() < 2 * 60) 1.0 else 0.0)
        }
    },
    dauer_hauptakt_tag_2bis4std{
        override fun getAttribute(day: HDay): Double {
            return (if (day.calculatedurationofmainactivitiesonday() >= 2 * 60 && day.calculatedurationofmainactivitiesonday() < 4 * 60) 1.0 else 0.0)
        }
    },
    dauer_hauptakt_tag_4bis6std{
        override fun getAttribute(day: HDay): Double {
            return (if (day.calculatedurationofmainactivitiesonday() >= 4 * 60 && day.calculatedurationofmainactivitiesonday() < 6 * 60) 1.0 else 0.0)
        }
    },
    dauer_hauptakt_tag_6bis8std{
        override fun getAttribute(day: HDay): Double {
            return (if (day.calculatedurationofmainactivitiesonday() >= 6 * 60 && day.calculatedurationofmainactivitiesonday() < 8 * 60) 1.0 else 0.0)
        }
    },
    dauer_hauptakt_tag_8bis10std{
        override fun getAttribute(day: HDay): Double {
            return (if (day.calculatedurationofmainactivitiesonday() >= 8 * 60 && day.calculatedurationofmainactivitiesonday() < 10 * 60) 1.0 else 0.0)
        }
    },
    dauer_hauptakt_tag_10bis12std{
        override fun getAttribute(day: HDay): Double {
            return (if (day.calculatedurationofmainactivitiesonday() >= 10 * 60 && day.calculatedurationofmainactivitiesonday() < 12 * 60) 1.0 else 0.0)
        }
    },
    dauer_hauptakt_tag_12bis14std{
        override fun getAttribute(day: HDay): Double {
            return (if (day.calculatedurationofmainactivitiesonday() >= 12 * 60 && day.calculatedurationofmainactivitiesonday() < 14 * 60) 1.0 else 0.0)
        }
    },
    dauer_hauptakt_tag_ueber14std{
        override fun getAttribute(day: HDay): Double {
            return (if (day.calculatedurationofmainactivitiesonday() >= 14 * 60) 1.0 else 0.0)
        }
    },


    /*
     * Dauer alle Akt Tag
     */
    dauer_akt_tag_4bis6std{
        override fun getAttribute(day: HDay): Double {
            return (if (day.totalAmountOfActivityTime >= 4 * 60 && day.totalAmountOfActivityTime < 6 * 60) 1.0 else 0.0)
        }
    },
    dauer_akt_tag_6bis8std{
        override fun getAttribute(day: HDay): Double {
            return (if (day.totalAmountOfActivityTime >= 6 * 60 && day.totalAmountOfActivityTime < 8 * 60) 1.0 else 0.0)
        }
    },
    dauer_akt_tag_8bis10std{
        override fun getAttribute(day: HDay): Double {
            return (if (day.totalAmountOfActivityTime >= 8 * 60 && day.totalAmountOfActivityTime < 10 * 60) 1.0 else 0.0)
        }
    },
    dauer_akt_tag_10bis12std{
        override fun getAttribute(day: HDay): Double {
            return (if (day.totalAmountOfActivityTime >= 10 * 60 && day.totalAmountOfActivityTime < 12 * 60) 1.0 else 0.0)
        }
    },
    dauer_akt_tag_12bis14std{
        override fun getAttribute(day: HDay): Double {
            return (if (day.totalAmountOfActivityTime >= 12 * 60 && day.totalAmountOfActivityTime < 14 * 60) 1.0 else 0.0)
        }
    },

    /*
     * Dauer Akt vor Haupttour
     */
    dauer_akt_vorht_tag_1bis120{
        override fun getAttribute(day: HDay): Double {
            return (if (day.getTotalAmountOfActivityTimeUntilMainTour(day.firstTourOfDay) >= 1 && day.getTotalAmountOfActivityTimeUntilMainTour(
                    day.firstTourOfDay
                ) < 120
            ) 1.0 else 0.0)
        }
    },
    dauer_akt_vorht_tag_121bis240{
        override fun getAttribute(day: HDay): Double {
            return (if (day.getTotalAmountOfActivityTimeUntilMainTour(day.firstTourOfDay) >= 121 && day.getTotalAmountOfActivityTimeUntilMainTour(
                    day.firstTourOfDay
                ) < 240
            ) 1.0 else 0.0)
        }
    },

    /*
     * Dauer Akt ab Haupttour
     */
    dauer_akt_abht_tag_600bis659{
        override fun getAttribute(day: HDay): Double {
            return (if (((day.getTotalAmountOfRemainingActivityTime(day.getTour(0))) >= 600 && day.getTotalAmountOfRemainingActivityTime(
                    day.getTour(0)
                ) < 659)
            ) 1.0 else 0.0)
        }
    },
    dauer_akt_abht_tag_660bis779{
        override fun getAttribute(day: HDay): Double {
            return (if (((day.getTotalAmountOfRemainingActivityTime(day.getTour(0))) >= 660 && day.getTotalAmountOfRemainingActivityTime(
                    day.getTour(0)
                ) < 679)
            ) 1.0 else 0.0)
        }
    },
    dauer_akt_abht_tag_780bis899{
        override fun getAttribute(day: HDay): Double {
            return (if (((day.getTotalAmountOfRemainingActivityTime(day.getTour(0))) >= 780 && day.getTotalAmountOfRemainingActivityTime(
                    day.getTour(0)
                ) < 899)
            ) 1.0 else 0.0)
        }
    },

    /*
     * AnzTouren Vor/Nach Haupttour
     */
    anztourenvorhaupttour{
        override fun getAttribute(day: HDay): Double {
            return ((-1) * day.lowestTourIndex).toDouble()
        }
    },
    anztourennachhaupttour{
        override fun getAttribute(day: HDay): Double {
            return ((+1) * day.highestTourIndex).toDouble()
        }
    },

    tagvorher_0vortouren{
        override fun getAttribute(day: HDay): Double {
            val previousday = day.previousDay
            var tagvorhervortouren = 999
            if (previousday != null)  {
                tagvorhervortouren = if (previousday.isHomeDay)  {
                    0
                } else {
                    -1 * previousday.lowestTourIndex
                }
            }
            return (if (tagvorhervortouren == 0) 1.0 else 0.0)
        }
    },
    tagvorher_1vortour{
        override fun getAttribute(day: HDay): Double {
            val previousday = day.previousDay
            var tagvorhervortouren = 999
            if (previousday != null)  {
                tagvorhervortouren = if (previousday.isHomeDay)  {
                    0
                } else {
                    -1 * previousday.lowestTourIndex
                }
            }
            return (if (tagvorhervortouren == 1) 1.0 else 0.0)
        }
    },
    tagvorher_0nachtouren{
        override fun getAttribute(day: HDay): Double {
            val previousday = day.previousDay
            var tagvorhernachtouren = 999
            if (previousday != null)  {
                tagvorhernachtouren = if (previousday.isHomeDay)  {
                    0
                } else {
                    +1 * previousday.highestTourIndex
                }
            }
            return (if (tagvorhernachtouren == 0) 1.0 else 0.0)
        }
    },
    tagvorher_1nachtour{
        override fun getAttribute(day: HDay): Double {
            val previousday = day.previousDay
            var tagvorhernachtouren = 999
            if (previousday != null)  {
                tagvorhernachtouren = if (previousday.isHomeDay)  {
                    0
                } else {
                    +1 * previousday.highestTourIndex
                }
            }
            return (if (tagvorhernachtouren == 1) 1.0 else 0.0)
        }
    },


    /*
     * AnzahlTouren am Tag
     */
    anztourenamtag{
        override fun getAttribute(day: HDay): Double {
            return day.amountOfTours.toDouble()
        }
    },
    ;


    abstract fun getAttribute(day: HDay): Double


    companion object {
        /**
         * Methode zur Rueckgabe des EnumValues fuer einen gegebenen String
         *
         * @param name
         * @return
         */
        fun getDayParameterFromString(name: String): HDayParameters {
            return entries.first {it.name == name}
        }
    }
}
