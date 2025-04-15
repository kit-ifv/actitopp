package edu.kit.ifv.mobitopp.actitopp

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


/**
 * @author Tim Hilgert
 */
class CSVWRDDistributionLoader {
    fun loadDistributionInformation(input: InputStream): WRDModelDistributionInformation {
        val minfo = WRDModelDistributionInformation()

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

                    val slot = splitted[0].toInt()
                    val amount = splitted[1].toInt()

                    minfo.addDistributionElement(slot, amount)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return minfo
    }
}
