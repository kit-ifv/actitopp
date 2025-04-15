package edu.kit.ifv.mobitopp.actitopp

import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.useLines


fun loadDistributionInformationFromFile(string: String) =loadDistributionInformationFromFile(Path(string))
fun loadDistributionInformationFromFile(path: Path): WRDModelDistributionInformation {
    require(path.exists()) {
        "The path $path does not exist."
    }
    val distributionInformation = WRDModelDistributionInformation()
    path.useLines { lines ->
        lines.drop(1).forEach { line ->
            val split = line.split(";")
            val slot = split[0].toInt()
            val amount = split[1].toInt()
            distributionInformation.addDistributionElement(slot, amount)
        }

    }

    return distributionInformation
}