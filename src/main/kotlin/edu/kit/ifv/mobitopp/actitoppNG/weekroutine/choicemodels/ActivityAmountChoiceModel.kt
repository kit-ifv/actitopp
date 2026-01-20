package edu.kit.ifv.mobitopp.actitoppNG.weekroutine.choicemodels


import edu.kit.ifv.discretechoice.extensions.optionsIndexed
import edu.kit.ifv.mobitopp.actitoppNG.steps.PersonAlternative
import edu.kit.ifv.mobitopp.actitoppNG.utils.times
import edu.kit.ifv.mobitopp.actitoppNG.weekroutine.parameters.ActivityAmountSet
import edu.kit.ifv.mobitopp.actitoppNG.weekroutine.parameters.DefaultActivityAmountParameters
import edu.kit.ifv.mobitopp.discretechoice.structure.DiscreteStructure
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.multinomialLogit

/**
 * The choice model for selecting the average amount of activities per day in the week routine. The default option of
 * 1 has associated utility 0.0. The other remaining options between 2 to 6 share a common utility function.
 */
val activityAmountChoiceModel = DiscreteStructure<Int, PersonAlternative, ActivityAmountSet> {
    option(1) {
        0.0
    }
    optionsIndexed(2..6) {_, it ->
        base +
                (it.isParttimeEmployee()) * beruf_teilzeit +
                (it.isStudent()) * beruf_schueler +
                (it.isVocational()) * beruf_azubi +
                (it.isAged26To35()) * alter_26bis35 +
                (it.isAged36To50()) * alter_36bis50 +
                (it.isAged51To60()) * alter_51bis60 +
                (it.isAged61To70()) * alter_61bis70 +
                (it.areaTypeRural()) * Raumtyp_mobitopp_rural +
                (it.commuteOver50km()) * pendeln_ueber50km +
                (it.commuteIn0To5km()) * pendeln_0bis5km +
                (it.hasChildrenInHousehold()) * haushalthatkinderunter10
    }
}.multinomialLogit("Amount of Activities in Week Routine").build(DefaultActivityAmountParameters)