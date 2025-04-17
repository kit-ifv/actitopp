package edu.kit.ifv.mobitopp.actitopp

import java.io.File
import java.io.FileWriter
import java.io.IOException

class CSVExportLogger(var basepath: File) {
    var activitywriter: FileWriter? = null
    var tripwriter: FileWriter? = null
    var personwriter: FileWriter? = null

    /**
     * constructor with basepath
     *
     * @param basepath
     * @throws IOException
     */
    init {
        openLogging(false)

        writeActivityData_header()
        writeTripData_header()
        writePersonData_header()

        closeLogging()
    }

    @Throws(IOException::class)
    fun writeLogging(maptoexport: HashMap<Int?, *>) {
        openLogging(true)
        for (referenceobject in maptoexport.values) {
            // Householdmap
            if (referenceobject is ActiToppHousehold) {
                for (actperson in referenceobject.householdmembersasList) {
                    exportsinglePerson(actperson)
                }
            }
            // Personmap
            if (referenceobject is ActitoppPerson) {
                exportsinglePerson(referenceobject)
            }
        }
        closeLogging()
    }


    @Throws(IOException::class)
    private fun openLogging(appendToExistingFile: Boolean) {
        activitywriter = FileWriter(File(basepath, "actitopp_activities.csv"), appendToExistingFile)
        tripwriter = FileWriter(File(basepath, "actitopp_trips.csv"), appendToExistingFile)
        personwriter = FileWriter(File(basepath, "actitopp_persons.csv"), appendToExistingFile)
    }

    @Throws(IOException::class)
    private fun closeLogging() {
        activitywriter!!.close()
        tripwriter!!.close()
        personwriter!!.close()
    }


    @Throws(IOException::class)
    private fun writeActivityData_header() {
        // Header
        activitywriter!!.append("HHIndex;PersNr;PersIndex;WOTAG;TourIndex;AktIndex;startzeit;startzeit_woche;endzeit;endzeit_woche;Dauer;zweck;jointStatus")
        activitywriter!!.append('\n')
        activitywriter!!.flush()
    }


    @Throws(IOException::class)
    private fun writeTripData_header() {
        // Header
        tripwriter!!.append("HHIndex;PersNr;PersIndex;WOTAG;anzeit;anzeit_woche;abzeit;abzeit_woche;Dauer;zweck_text;jointStatus")
        tripwriter!!.append('\n')
        tripwriter!!.flush()
    }


    @Throws(IOException::class)
    private fun writePersonData_header() {
        // Header
        personwriter!!.append("HHIndex;numberofhhmembers;children010;childrenunder18;areatype;numberofcarsinhh;PersNr;PersIndex;Age;Employment;Gender;isAllowedToWork;CommutingdistanceWork;CommutingdistanceEducation")
        personwriter!!.append('\n')
        personwriter!!.flush()
    }

    @Throws(IOException::class)
    private fun exportsinglePerson(person: ActitoppPerson) {
        // Export activity data
        for (act in person.weekPattern!!.allActivities) {
            if (act.isScheduled) {
                activitywriter!!.append(writeActivity(act))
                activitywriter!!.flush()
            }
        }
        // Export trip data
        for (trip in person.weekPattern!!.allTrips) {
            if (trip.duration > 0) {
                tripwriter!!.append(writeTrip(trip))
                tripwriter!!.flush()
            }
        }
        // Export person information
        personwriter!!.append(writePersonInformation(person))
        personwriter!!.flush()
    }


    private fun writePersonInformation(person: ActitoppPerson): String {
        var returnstring = ""


        // HHIndex
        returnstring += person.household.householdIndex.toString() + ";"
        // Number of HH members
        returnstring += person.household.numberofPersonsinHousehold.toString() + ";"
        // Children 0-10
        returnstring += person.household.children0_10.toString() + ";"
        // Children under 18
        returnstring += person.household.children_u18.toString() + ";"
        // AreaType
        returnstring += person.household.areatype.toString() + ";"
        // Number of cars in HH
        returnstring += person.household.numberofcarsinhousehold.toString() + ";"

        // PersNr
        returnstring += person.persNrinHousehold.toString() + ";"
        // PersIndex
        returnstring += person.persIndex.toString() + ";"
        // Age
        returnstring += person.age.toString() + ";"
        // Employment
        returnstring += person.employment.toString() + ";"
        // Gender
        returnstring += person.gender.toString() + ";"
        // isAllowedToWork
        returnstring += person.isAllowedToWork.toString() + ";"
        // CommutingDistance Work
        returnstring += person.commutingdistance_work.toString() + ";"
        // CommutingDistance Education
        returnstring += person.commutingdistance_education.toString() + ""

        returnstring += "\n"
        return returnstring
    }


    /**
     * create output activity information
     *
     * @param act
     * @return
     */
    private fun writeActivity(act: HActivity): String {
        assert(act.isScheduled) { "Activity is not fully scheduled" }
        var returnstring = ""

        /*
         * estimate tourindex and activityindex for home activities
         * if day is home-day only, return 0/0, otherwise -99/-99
         */
        var tmptourindex = 0
        var tmpaktindex = 0
        if (act.day.amountOfTours > 0) {
            tmptourindex = -99
            tmpaktindex = -99
        }

        // HHIndex
        returnstring += act.person.household.householdIndex.toString() + ";"
        // PersNr
        returnstring += act.person.persNrinHousehold.toString() + ";"
        // PersIndex
        returnstring += act.person.persIndex.toString() + ";"
        // WOTAG
        returnstring += act.weekDay.toString() + ";"
        // TourIndex
        returnstring += (if (act.isHomeActivity) tmptourindex else act.tourIndex).toString() + ";"
        // AktIndex
        returnstring += (if (act.isHomeActivity) tmpaktindex else act.index).toString() + ";"
        // starttime
        returnstring += act.startTime.toString() + ";"
        // starttime_week
        returnstring += act.startTimeWeekContext.toString() + ";"
        // endtime
        returnstring += act.endTime.toString() + ";"
        // endtime_week
        returnstring += act.endTimeWeekContext.toString() + ";"
        // duration
        returnstring += act.duration.toString() + ";"
        // purpose
        returnstring += act.activityType.typeasChar.toString() + ";"
        // joint Status
        returnstring += (if (Configuration.modelJointActions) act.jointStatus else "-99").toString() + ""

        returnstring += "\n"
        return returnstring
    }


    /**
     * create output trip information
     *
     * @param trip
     * @return
     */
    private fun writeTrip(trip: HTrip): String {
        assert(trip.isScheduled) { "Trip is not scheduled!" }
        var returnstring = ""

        // HHIndex
        returnstring += trip.activity.person.household.householdIndex.toString() + ";"
        // PersNr
        returnstring += trip.activity.person.persNrinHousehold.toString() + ";"
        // PersIndex
        returnstring += trip.activity.person.persIndex.toString() + ";"
        // Weekday
        returnstring += trip.activity.weekDay.toString() + ";"
        // endtime
        returnstring += trip.endTime.toString() + ";"
        // endtime_week
        returnstring += trip.endTimeWeekContext.toString() + ";"
        // starttime
        returnstring += trip.startTime.toString() + ";"
        // starttime_week
        returnstring += trip.startTimeWeekContext.toString() + ";"
        // duration
        returnstring += trip.duration.toString() + ";"
        // type of trip
        returnstring += trip.type.typeasChar.toString() + ";"
        // jointStatus
        returnstring += (if (Configuration.modelJointActions) trip.jointStatus else "-99").toString() + ""

        returnstring += "\n"
        return returnstring
    }
}

