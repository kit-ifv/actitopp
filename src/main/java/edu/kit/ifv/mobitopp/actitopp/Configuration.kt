package edu.kit.ifv.mobitopp.actitopp

/**
 * @author Tim Hilgert
 */
object Configuration {
    /*
     * coordination of input parameter set
     *
     * possible configurations:
     *
     * mopv10					=	use parameter estimation of MOP 2004-2013 (actiTopp version 1.0)
     * mopv11					= use parameter estimation of MOP 2004-2013 (actiTopp version 1.1 & 1.2)
     * mopv13					= use parameter estimation of MOP 2004-2013 (actiTopp version 1.3 incl. joint activities)
     * mopv14					= use parameter estimation of MOP 2004-2013 (actiTopp version 1.4+ incl. joint activities)
     * stuttgart			=	use parameter estimation of MOP 2004-2013 calibrated for stuttgart area (only actitopp version 1.0)
     */

    var parameterset: String = "mopv14_withpkwhh"
    fun setParameters(text: String) {
        parameterset = text
    }
    /*
     * when modeling a whole week, we need to consider stability aspects in behavior (e.g. leaving the house at the same time every morning).
     * To demonstrate the significance of these aspects, the coordination of the modeling can enabled and disabled.
     *
     * i.e: when disabled, different steps (e.g. 8A) will be skipped
     *
     */
    
    var coordinated_modelling: Boolean = true

    /*
     * modeling joint actions within household
     */
    //TODO try to get joint actions back to work
    var modelJointActions: Boolean = false



    /*
     * steps that use discrete choice modeling
     */

    var dcsteps: HashSet<String> = HashSet()

    init {
        dcsteps.add("1A")
        dcsteps.add("1B")
        dcsteps.add("1C")
        dcsteps.add("1D")
        dcsteps.add("1E")
        dcsteps.add("1F")
        dcsteps.add("1K")
        dcsteps.add("1L")

        /* step 1K,1L is available since actiTopp version 1.4 */
        dcsteps.add("2A")

        dcsteps.add("3A")
        dcsteps.add("3B")

        dcsteps.add("4A")

        dcsteps.add("5A")
        dcsteps.add("5B")

        dcsteps.add("6A")

        dcsteps.add("7A")
        dcsteps.add("7C")
        dcsteps.add("7E")
        dcsteps.add("7G")
        dcsteps.add("7I")

        dcsteps.add("8A")
        dcsteps.add("8B")
        dcsteps.add("8D")
        dcsteps.add("8J")

        dcsteps.add("9A")

        dcsteps.add("10A")
        dcsteps.add("10M")
        dcsteps.add("10O")
        dcsteps.add("10S")

        /*"10B", "10D", "10G", "10J",*/ /*Stufe 10B-J are no longer included since actiTopp version 1.4 */
        dcsteps.add("11")
        /* step 11 is available since actiTopp version 1.3 */
    }

    /*
     * steps that use weighted random draw (wrd) modeling
     */
    
    var wrdsteps: HashMap<String, Int> = HashMap()

    init {
        wrdsteps["7B"] = 8
        wrdsteps["7D"] = 5
        wrdsteps["7F"] = 5
        wrdsteps["7H"] = 4
        wrdsteps["7J"] = 3

        wrdsteps["8C"] = 14
        wrdsteps["8E"] = 14
        wrdsteps["8K"] = 14

        wrdsteps["10N"] = 15
        wrdsteps["10P"] = 10
        wrdsteps["10T"] = 9
    }

    /*
     * steps that use linear regression modeling
     */
    
    var linregsteps_filenames = HashSet<String>()

    init {

        linregsteps_filenames.add("97estimates")
    }


    /*
     * activity durations - categories
     */
    // activity durations - categories - Lower Bounds
    // TODO these bounds are completely equal to the histogram data from all 8X tables, thus they should be
    //   an attribute of these classe s instead of being coded into the configuration :eyeroll:
    @Deprecated("This should never be referenced, but rather the histograms should hold this data.")
    val ACT_TIME_TIMECLASSES_LB: IntArray =
        intArrayOf(1, 15, 30, 60, 120, 180, 240, 300, 360, 420, 480, 540, 600, 660, 720)

    // activity durations - categories - Upper Bounds
    @Deprecated("This should never be referenced, but rather the histograms should hold this data.")
    val ACT_TIME_TIMECLASSES_UB: IntArray =
        intArrayOf(14, 29, 59, 119, 179, 239, 299, 359, 419, 479, 539, 599, 659, 719, 1440)

    // activity durations - number of categories
    @Deprecated("This should never be referenced, but rather the histograms should hold this data.")
    val NUMBER_OF_ACT_DURATION_CLASSES: Int = ACT_TIME_TIMECLASSES_LB.size


    /*
     * activity durations for home activity - categories
     */
    // activity durations for home activity - categories - Lower Bounds
    
    val HOME_TIME_TIMECLASSES_LB: IntArray = intArrayOf(0, 15, 30, 60, 120, 180, 240, 300, 360, 420)

    // activity durations for home activity - categories - Upper Bounds
    
    val HOME_TIME_TIMECLASSES_UB: IntArray = intArrayOf(14, 29, 59, 119, 179, 239, 299, 359, 419, 1440)

    // activity durations for home activity - number of categories
    
    val NUMBER_OF_HOME_DURATION_CLASSES: Int = HOME_TIME_TIMECLASSES_LB.size


    /*
     * start time for first tour of the day - categories
     */
    // start time for first tour of the day  - Lower Bounds
    
    val FIRST_TOUR_START_TIMECLASSES_LB: IntArray =
        intArrayOf(0, 120, 240, 360, 420, 480, 540, 600, 660, 780, 900, 960, 1020, 1080, 1200, 1320)

    // start time for first tour of the day  - Upper Bounds
    
    val FIRST_TOUR_START_TIMECLASSES_UB: IntArray =
        intArrayOf(119, 239, 359, 419, 479, 539, 599, 659, 779, 899, 959, 1019, 1079, 1199, 1319, 1439)

    // start time for first tour of the day - number of categories
    
    val NUMBER_OF_FIRST_START_TIME_CLASSES: Int = FIRST_TOUR_START_TIMECLASSES_LB.size


    /*
     * start time for second and third tour of the day
     */
    // start time for second and third tour of the day - Lower Bounds
    
    val SECTHR_TOUR_START_TIMECLASSES_LB: IntArray = intArrayOf(0, 540, 660, 780, 840, 900, 960, 1020, 1080, 1140, 1200)

    // start time for second and third tour of the day - Upper Bounds
    
    val SECTHR_TOUR_START_TIMECLASSES_UB: IntArray =
        intArrayOf(539, 659, 779, 839, 899, 959, 1019, 1079, 1139, 1199, 1439)

    // start time for second and third tour of the day - number of categories
    
    val NUMBER_OF_SECTHR_START_TIME_CLASSES: Int = SECTHR_TOUR_START_TIMECLASSES_LB.size


    /*
     * trip time constant - default time occupation of a trip
     */
    const val FIXED_TRIP_TIME_ESTIMATOR: Int = 15


    /*
     * ONLY FOR DEBUG ON CONSOLE
     */
    
    var debugenabled: Boolean = false
}