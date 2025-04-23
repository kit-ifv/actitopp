package edu.kit.ifv.mobitopp.actitopp.IO

import edu.kit.ifv.mobitopp.actitopp.LinRegEstimate
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.text.NumberFormat
import java.text.ParseException
import java.util.Locale

/**
 * @author Tim Hilbert
 */
class CSVLinRegEstimatesLoader {
    /**
     * read estimate values from file system and store them in a hash map
     *
     * @param input
     * @return
     */
    fun getEstimates(input: InputStream): HashMap<String, LinRegEstimate> {
        val estimatesMap = HashMap<String, LinRegEstimate>()


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
                    val variable = splitted[0]
                    val contextIdentifier = splitted[2]

                    var estimatevalue = 0.0
                    val nf = NumberFormat.getInstance(Locale.ENGLISH)

                    try {
                        estimatevalue = nf.parse(splitted[1]).toDouble()
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }

                    assert(contextIdentifier == "default" || contextIdentifier == "person" || contextIdentifier == "day" || contextIdentifier == "tour" || contextIdentifier == "activity") { "wrong Reference Value for InputParamMap - $variable - $contextIdentifier - SourceLocation: $input" }
                    estimatesMap[variable] = LinRegEstimate(variable, estimatevalue, contextIdentifier)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return estimatesMap
    }
}
