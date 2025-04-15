package edu.kit.ifv.mobitopp.actitopp

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.text.NumberFormat
import java.text.ParseException
import java.util.Locale

/**
 * class to read parameter values for different dc model steps from file system
 *
 * @author Tim Hilgert
 */
class CSVDCParameterLoader {
    /**
     * method to load parameter values for a specified model step
     *
     * @param input
     * @param modelstep
     */
    fun loadParameterValues(input: InputStream, modelstep: DCModelSteplnformation) {
        /*
         * initialization of ModelAlternativeParameterValues objects
         * an object exists for each alternative of this modelstep
         */
        val alternativesParameters: MutableMap<String, DCModelAlternativeParameterValues> = HashMap()
        for (s in modelstep.alternativesList) {
            alternativesParameters[s] = DCModelAlternativeParameterValues()
        }

        try {
            BufferedReader(InputStreamReader(input)).use { inRead ->
                var header = true
                var line: String?
                while ((inRead.readLine().also { line = it }) != null) {
                    if (header) {
                        line = inRead.readLine()
                        header = false
                    }

                    val splitted = line!!.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                    val parameterName = splitted[0]
                    val alternativeName = splitted[1]
                    var parameterValue = 0.0
                    val nf = NumberFormat.getInstance(Locale.ENGLISH)
                    try {
                        parameterValue = nf.parse(splitted[2]).toDouble()
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }

                    if (parameterName != "0") {
                        alternativesParameters[alternativeName]!!.addParameterValue(parameterName, parameterValue)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        modelstep.alternativesParameters = alternativesParameters
    }
}
