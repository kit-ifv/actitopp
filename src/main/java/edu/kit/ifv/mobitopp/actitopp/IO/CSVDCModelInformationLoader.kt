package edu.kit.ifv.mobitopp.actitopp.IO

import edu.kit.ifv.mobitopp.actitopp.DCModelSteplnformation
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader


/**
 * @author Tim Hilgert
 */
class CSVDCModelInformationLoader {

    fun loadModelFlowData(input: InputStream, modelstep: DCModelSteplnformation) {
        // Map for parameter Names and Context

        val paramNamesContexts = HashMap<String, String>()
        // List for alternative names
        val alternativesList = ArrayList<String>()

        var line: String? = null
        var header = true

        BufferedReader(InputStreamReader(input)).use { inRead ->
            while ((inRead.readLine().also { line = it }) != null) {
                var paramName = ""
                var paramvalue = ""
                var `in` = false
                var alt = false

                // skip header section
                if (header) {
                    line = inRead.readLine()
                    header = false
                }
                val splitted = line!!.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                paramName = splitted[0]

                if (splitted[1] == "yes") `in` = true
                if (splitted[2] == "yes") alt = true

                if (`in`) {
                    paramvalue = splitted[3]
                    assert(paramvalue == "default" || paramvalue == "person" || paramvalue == "day" || paramvalue == "tour" || paramvalue == "activity") { "wrong Reference Value for InputParamMap - $paramName - $paramvalue - SourceLocation: $input" }
                    paramNamesContexts[paramName] = paramvalue
                }

                if (alt) alternativesList.add(paramName)
            }
        }
        // Add the resulting information from file to the model information
        modelstep.setParameterNamesContextsDepre(paramNamesContexts)
        modelstep.setAlternativesListDepre(alternativesList)
    }
}
