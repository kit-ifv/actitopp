package edu.kit.ifv.mobitopp.actitopp

import java.io.FileWriter
import java.io.IOException

/**
 * @author Tim Hilgert
 *
 *
 * debugloggers store modeling decision in maps.
 * debug loggers may be used to explictly monitor specific decision during modeling that may not be directly
 * visible when only analysing overall modeling result (i.e. activity schedules).
 */
class DebugLoggers() {
    /*
        * Overall HashMap including all generated loggers
        */
    var debugloggers: HashMap<String, LinkedHashMap<Any, String>>? = null


    /**
     * constructor to create debug loggers object
     */
    init {
        debugloggers = HashMap()
    }


    /**
     * constructor to create a subordinate logger (storing information for one household only)
     * create a logger object and created empty loggers for all elements that are already in the superordinate logger object
     *
     * @param overallLogger
     */
    constructor(overallLogger: DebugLoggers) : this() {
        for (s in overallLogger.debugloggers!!.keys) {
            this.addDebugLogger(s)
        }
    }


    /**
     * add a new logger element
     *
     * @param key
     */
    fun addDebugLogger(key: String) {
        debugloggers!![key] = LinkedHashMap()
    }


    /**
     * @param key
     * @return
     */
    fun existsLogger(key: String): Boolean {
        var result = false
        if (debugloggers!!.containsKey(key)) result = true
        return result
    }

    /**
     * @param key
     * @return
     */
    fun getLogger(key: String): LinkedHashMap<Any, String> {
        return debugloggers!![key]!!
    }

    /**
     * add information to a logger
     *
     * @param key
     * @param referenceobject
     * @param decision
     */
    fun addDebugInfo(key: String, referenceobject: Any, decision: String) {
        debugloggers!![key]!![referenceobject] = decision
    }


    /**
     * add all information of a subordinate logger to the superordinate logger
     *
     * @param householdlogger
     */
    fun addHouseholdDebugInfotoOverallLogger(householdlogger: DebugLoggers) {
        for ((loggerid, logger) in householdlogger.debugloggers!!) {
            getLogger(loggerid).putAll(logger)
        }
    }


    /**
     * export of all logger elements to the file system
     *
     * @param basepath
     */
    fun exportallLoggerInfos(basepath: String) {
        for (key in debugloggers!!.keys) {
            exportLoggerInfos(key, "$basepath/Logger_$key.csv")
        }
    }


    /**
     * export of a single logger element to the file system
     *
     * @param key
     * @param filename
     */
    fun exportLoggerInfos(key: String, filename: String) {
        try {
            val writer = FileWriter(filename)

            var wroteheader = false

            /*
             * Loop through all entries of the logger element
             */
            val relevantmap = debugloggers!![key]!!

            for (referenceobject in relevantmap.keys) {
                var rowcontent: String? = ""

                // HouseholdLogger
                if (referenceobject is ActiToppHousehold) {
                    if (!wroteheader) {
                        // Header
                        writer.append("HHIndex;Decision")
                        writer.append('\n')
                        writer.flush()
                        wroteheader = true
                    }

                    // HHIndex
                    rowcontent += referenceobject.householdIndex.toString() + ";"
                    // Decision
                    rowcontent += relevantmap[referenceobject]
                }

                // PersonLogger
                if (referenceobject is ActitoppPerson) {
                    val actperson = referenceobject

                    if (!wroteheader) {
                        // Header
                        writer.append("HHIndex;PersIndex;Decision")
                        writer.append('\n')
                        writer.flush()
                        wroteheader = true
                    }

                    // HHIndex
                    rowcontent += actperson.household.householdIndex.toString() + ";"
                    // PersIndex
                    rowcontent += actperson.persIndex.toString() + ";"
                    // Decision
                    rowcontent += relevantmap[referenceobject]
                }

                // Daylogger
                if (referenceobject is HDay) {
                    val actday = referenceobject

                    if (!wroteheader) {
                        // Header
                        writer.append("HHIndex;PersIndex;WOTAG;Decision")
                        writer.append('\n')
                        writer.flush()
                        wroteheader = true
                    }

                    // HHIndex
                    rowcontent += actday.person.household.householdIndex.toString() + ";"
                    // PersIndex
                    rowcontent += actday.person.persIndex.toString() + ";"
                    // WOTAG - WeekDay
                    rowcontent += actday.weekday.toString() + ";"
                    // Decision
                    rowcontent += relevantmap[referenceobject]
                }

                // Tourlogger
                if (referenceobject is HTour) {
                    val acttour = referenceobject

                    if (!wroteheader) {
                        // Header
                        writer.append("HHIndex;PersIndex;WOTAG;TourIndex;Decision")
                        writer.append('\n')
                        writer.flush()
                        wroteheader = true
                    }

                    // HHIndex
                    rowcontent += acttour.person.household.householdIndex.toString() + ";"
                    // PersIndex
                    rowcontent += acttour.person.persIndex.toString() + ";"
                    // WOTAG
                    rowcontent += acttour.day.weekday.toString() + ";"
                    // TourIndex
                    rowcontent += acttour.index.toString() + ";"
                    // Decision
                    rowcontent += relevantmap[referenceobject]
                }

                // Activitylogger
                if (referenceobject is HActivity) {
                    val actact = referenceobject

                    if (!wroteheader) {
                        // Header
                        writer.append("HHIndex;PersIndex;WOTAG;TourIndex;AktIndex;Decision")
                        writer.append('\n')
                        writer.flush()
                        wroteheader = true
                    }

                    // HHIndex
                    rowcontent += actact.person.household.householdIndex.toString() + ";"
                    // PersIndex
                    rowcontent += actact.person.persIndex.toString() + ";"
                    // WOTAG
                    rowcontent += actact.day.weekday.toString() + ";"
                    // TourIndex
                    rowcontent += actact.tourIndex.toString() + ";"
                    // AktIndex
                    rowcontent += actact.index.toString() + ";"
                    // Decision
                    rowcontent += relevantmap[referenceobject]
                }

                // write row
                rowcontent += "\n"

                writer.append(rowcontent)
                writer.flush()
            }

            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    /**
     * delete all information of a single person from the debug logger object
     * e.g. when a person needs to be modeled again
     *
     * @param tmpperson
     */
    fun deleteInformationforPerson(tmpperson: ActitoppPerson) {
        val persindex = tmpperson.persIndex
        for (key in debugloggers!!.keys) {
            /*
             * Loop all debug logger elements
             */
            val relevantmap = debugloggers!![key]!!

            val it = relevantmap.keys.iterator()
            while (it.hasNext()) {
                val referenceobject = it.next()

                // PersonLogger
                if (referenceobject is ActitoppPerson) {
                    if (referenceobject.persIndex == persindex) it.remove()
                }

                // Daylogger
                if (referenceobject is HDay) {
                    if (referenceobject.person.persIndex == persindex) it.remove()
                }

                // Tourlogger
                if (referenceobject is HTour) {
                    if (referenceobject.person.persIndex == persindex) it.remove()
                }

                // Activitylogger
                if (referenceobject is HActivity) {
                    if (referenceobject.person.persIndex == persindex) it.remove()
                }
            }
        }
    }
}
