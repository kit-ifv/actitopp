package edu.kit.ifv.mobitopp.actitopp

import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
private const val RESOURCE_PATH = "src/main/resources/edu/kit/ifv/mobitopp/actitopp/"
/**
 * @author Tim Hilgert
 *
 *
 * Used to load ALL property files before model execution.
 * This includes property files, model flow files, time distribution files and others
 */
class ModelFileBase private constructor(
    private val inputType: Input,
    dcsteps: HashSet<String>?,
    wrdsteps: HashMap<String, Int>?,
    linregsteps_filenames: HashSet<String>?
) {
    private interface Input {
        @Throws(IOException::class)
        fun newInputStream(name: String): InputStream?
    }

    private class FileInput(private val basePath: File) : Input {
        @Throws(IOException::class)
        override fun newInputStream(name: String): InputStream {
            println("loading file: " + name + " from parameter set " + basePath.absolutePath)
            return Files.newInputStream(File(this.basePath, name).toPath())
        }
    }

    private class JarInput(private val parameterset: String) : Input {
        override fun newInputStream(name: String): InputStream? {
            println("loading file from JAR: $name from parameter set $parameterset")
            return ModelFileBase::class.java.getResourceAsStream(this.parameterset + "/" + name)
        }
    }

    private val modelInformationDCsteps = HashMap<String, DCModelSteplnformation>()
    private val modelInformationWRDsteps = WeightedDistributionRegistry()
    private val linearregressionestimatesmap = HashMap<String, HashMap<String, LinRegEstimate>>()


    /**
     * Constructor with custom parameter set (using Configuration values for step information)
     */
    /**
     * Constructor with standard parameters (using Configuration values)
     */
    @JvmOverloads
    constructor(parameterset: String = Configuration.parameterset) : this(
        JarInput(parameterset),
        Configuration.dcsteps,
        Configuration.wrdsteps,
        Configuration.linregsteps_filenames
    )

    /**
     * Constructor with custom parametersets and step information
     *
     * @param basePath
     * @param dcsteps
     * @param wrdsteps
     * @param linregsteps_filenames
     */
    constructor(
        basePath: File,
        dcsteps: HashSet<String>,
        wrdsteps: HashMap<String, Int>,
        linregsteps_filenames: HashSet<String>
    ) : this(
        FileInput(basePath), dcsteps, wrdsteps, linregsteps_filenames
    )

    init {
        try {
            // Initializations
            if (dcsteps != null) {
                initDCStepInformation(dcsteps)
                initDCStepParameters(dcsteps)
            }
            if (wrdsteps != null) {
                initWRDSteps(wrdsteps)
            }
            if (linregsteps_filenames != null) {
                initLinearRegressionEstimates(linregsteps_filenames)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException()
        }
    }


    /**
     * returns [DCModelSteplnformation] object for specific id
     *
     * @param modelstepid
     * @return
     */
    fun getModelInformationforDCStep(modelstepid: String): DCModelSteplnformation {
        return modelInformationDCsteps.getValue(modelstepid)
    }


    /**
     * Robin: We can replace the original getModelInformationforWRDStep() because it was only an intermediate
     * call chain where afterwards .getDistribution() is called upon.
     */

    fun getDistributionFor(stepID: String, category: String): WRDModelDistributionInformation {
        return modelInformationWRDsteps[stepID, category]
    }



    /**
     * return linear regression estimated map for specified regressionname
     *
     * @param regressionname
     * @return
     */
    fun getLinearRegressionEstimates(regressionname: String): HashMap<String, LinRegEstimate> {
        return linearregressionestimatesmap.getValue(regressionname)
    }

    /**
     * read all relevant model flow information from files for dc steps
     *
     * @param dcsteps
     * @throws FileNotFoundException
     * @throws IOException
     */
    @Throws(FileNotFoundException::class, IOException::class)
    private fun initDCStepInformation(dcsteps: HashSet<String>) {
        for (s in dcsteps) {
            newInputStream(s + "model_flow").use { input ->
                val loader = CSVDCModelInformationLoader()
                // Creates ModelInformationOject
                val modelStep = DCModelSteplnformation()
                // Load ParameterNames, Contexts and Alternatives
                loader.loadModelFlowData(input!!, modelStep)
                // Adds the modelinformation to the map
                modelInformationDCsteps.put(s, modelStep)
            }
        }
    }

    /**
     * read all relevant parameter from files for dc steps
     *
     * @param dcsteps
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun initDCStepParameters(dcsteps: HashSet<String>) {
        // parameters need to be available for all DC model steps
        for (keyString in dcsteps) {
            val modelstep = modelInformationDCsteps[keyString]

            newInputStream(keyString + "Params").use { input ->
                val parameterLoader = CSVDCParameterLoader()
                parameterLoader.loadParameterValues(input!!, modelstep!!)
            }
        }
    }

    /**
     * initialize weighted random draw steps
     *
     * @param wrdsteps
     * @throws FileNotFoundException
     * @throws IOException
     */
    private fun initWRDSteps(wrdsteps: HashMap<String, Int>) {
        wrdsteps.forEach { (stepID, maxIndex) ->
            (0..maxIndex).forEach {
                modelInformationWRDsteps[stepID, it.toString()] = loadDistributionInformationFromFile("$RESOURCE_PATH${Configuration.parameterset}/${stepID}_KAT_$it.csv")
            }
        }
    }

    /**
     * initialize estimated for linear regression steps
     *
     * @param linregsteps_filenames
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun initLinearRegressionEstimates(linregsteps_filenames: HashSet<String>) {
        for (name in linregsteps_filenames) {
            newInputStream(name).use { input ->
                val loader = CSVLinRegEstimatesLoader()
                val tmpmap = loader.getEstimates(input!!)
                linearregressionestimatesmap.put(name, tmpmap)
            }
        }
    }

    /**
     * @param name
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun newInputStream(name: String): InputStream? {
        return inputType.newInputStream("$name.csv")
    }
}

