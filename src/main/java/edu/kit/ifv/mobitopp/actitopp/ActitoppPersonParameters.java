package edu.kit.ifv.mobitopp.actitopp;

/**
 * 
 * @author Tim Hilgert
 *
 */
public enum ActitoppPersonParameters {

	/*
	 * Kinder 0-10
	 */
	haushalthatkinderunter10("haushalthatkinderunter10") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getChildren0_10() > 0) ? 1.0 : 0.0);
		}
	},	
	
	
	/*
	 * Kinder unter 18
	 */
	haushalthatkinderunter18("haushalthatkinderunter18") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getChildren_u18() > 0) ? 1.0 : 0.0);
		}
	},	
	anzahlkinder_u18("anzahlkinder_u18") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return actitoppPerson.getChildren_u18();
		}
	},		
	
	/*
	 * BERUF
	 */
	beruf_vollzeit("beruf_vollzeit") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getEmployment() == 1) ? 1.0 : 0.0);
		}
	},
	beruf_teilzeit("beruf_teilzeit") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			int employmentType = actitoppPerson.getEmployment();
	    double returnvalue = 0.0;
	    if (employmentType == 2 || employmentType == 21 || employmentType == 22) returnvalue = 1.0;
	    return returnvalue;
		}
	},
	beruf_ohneerwerb("beruf_ohneerwerb") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getEmployment() == 3) ? 1.0 : 0.0);
		}
	},
	beruf_schueler("beruf_schueler") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			int employmentType = actitoppPerson.getEmployment();
	    double returnvalue = 0.0;
	    if (employmentType == 4 || employmentType == 40 || employmentType == 41 || employmentType == 42) returnvalue = 1.0;
	    return returnvalue;
		}
	},
	beruf_azubi("beruf_azubi") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getEmployment() == 5) ? 1.0 : 0.0);
		}
	},
	beruf_schueler_azubi("beruf_schueler_azubi") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			int employmentType = actitoppPerson.getEmployment();
			double returnvalue = 0.0;
		  if (employmentType == 4 || employmentType == 40 || employmentType == 41 || employmentType == 42 || employmentType == 5) returnvalue = 1.0;
		  return returnvalue;
		}
	},
	beruf_erwerbstaetig("beruf_erwerbstaetig") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			int employmentType = actitoppPerson.getEmployment();
	    double returnvalue = 0.0;
	    if (employmentType == 1 || employmentType == 2 || employmentType == 21 || employmentType == 22) returnvalue = 1.0;
	    return returnvalue;
		}
	},
	beruf_rentner("beruf_rentner") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getEmployment() == 7) ? 1.0 : 0.0);
		}
	},
	
	/*
	 *  HHGRO
	 */
	persin2pershh("persin2pershh") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getHousehold().getNumberofPersonsinHousehold() == 2) ? 1.0 : 0.0);
		}
	},	
	persin3pershh("persin3pershh") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getHousehold().getNumberofPersonsinHousehold() == 3) ? 1.0 : 0.0);
		}
	},
	rentnerin2pershh("rentnerin2pershh") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getHousehold().getNumberofPersonsinHousehold() == 2 && actitoppPerson.getEmployment() == 7) ? 1.0 : 0.0);
		}
	},
	
	/*
	 * ALTER
	 */
	alter_10bis17("alter_10bis17") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAge() >= 10 && actitoppPerson.getAge() <= 17) ? 1.0 : 0.0);
		}
	},
	alter_18bis25("alter_18bis25") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAge() >= 18 && actitoppPerson.getAge() <= 25) ? 1.0 : 0.0);
		}
	},
	alter_26bis35("alter_26bis35") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAge() >= 26 && actitoppPerson.getAge() <= 35) ? 1.0 : 0.0);
		}
	},
	alter_36bis50("alter_36bis50") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAge() >= 36 && actitoppPerson.getAge() <= 50) ? 1.0 : 0.0);
		}
	},
	alter_51bis60("alter_51bis60") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAge() >= 51 && actitoppPerson.getAge() <= 60) ? 1.0 : 0.0);
		}
	},
	alter_61bis70("alter_61bis70") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAge() >= 61 && actitoppPerson.getAge() <= 70) ? 1.0 : 0.0);
		}
	},
	alter_ueber70("alter_ueber70") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAge() >= 71) ? 1.0 : 0.0);
		}
	},
	alter_18bis35("alter_18bis35") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAge() >= 18 && actitoppPerson.getAge() <= 35) ? 1.0 : 0.0);
		}
	},
	
	/*
	 * GESCHLECHT
	 */	
	male("male") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getGender() == 1) ? 1.0 : 0.0);
		}
	},
 
	/*
	 * RAUMTYP
	 */	
	Raumtyp_mobitopp_rural("Raumtyp_mobitopp_rural") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAreatype() == 1) ? 1.0 : 0.0);
		}
	},	
	Raumtyp_mobitopp_provincial("Raumtyp_mobitopp_provincial") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAreatype() == 2) ? 1.0 : 0.0);
		}
	},	
	Raumtyp_mobitopp_cityoutskirt("Raumtyp_mobitopp_cityoutskirt") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAreatype() == 3) ? 1.0 : 0.0);
		}
	},	
	Raumtyp_mobitopp_metropolitan("Raumtyp_mobitopp_metropolitan") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAreatype() == 4) ? 1.0 : 0.0);
		}
	},	
	Raumtyp_mobitopp_conurbation("Raumtyp_mobitopp_conurbation") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAreatype() == 5) ? 1.0 : 0.0);
		}
	},	
	
	/*
	 * PKWHH
	 */	
	PKWHH("PKWHH") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return actitoppPerson.getNumberofcarsinhousehold();
		}
	},	
	
	/*
	 * Pendeln 0-5 Kilometer
	 */	
	pendeln_0bis5km("pendeln_0bis5km") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			double commute_distance = Math.max(actitoppPerson.getCommutingdistance_work(), actitoppPerson.getCommutingdistance_education());
			return ((commute_distance > 0 && commute_distance <= 5) ? 1.0 : 0.0);
		}
	},	
	
	/*
	 * Pendeln 5-10 Kilometer
	 */	
	pendeln_5bis10km("pendeln_5bis10km") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			double commute_distance = Math.max(actitoppPerson.getCommutingdistance_work(), actitoppPerson.getCommutingdistance_education());
			return ((commute_distance > 5 && commute_distance <= 10) ? 1.0 : 0.0);
		}
	},	
	
	/*
	 * Pendeln 10-20 Kilometer
	 */	
	pendeln_10bis20km("pendeln_10bis20km") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			double commute_distance = Math.max(actitoppPerson.getCommutingdistance_work(), actitoppPerson.getCommutingdistance_education());
			return ((commute_distance > 10 && commute_distance <= 20) ? 1.0 : 0.0);
		}
	},	
	
	/*
	 * Pendeln 20-50 Kilometer
	 */	
	pendeln_20bis50km("pendeln_20bis50km") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			double commute_distance = Math.max(actitoppPerson.getCommutingdistance_work(), actitoppPerson.getCommutingdistance_education());
			return ((commute_distance > 20 && commute_distance <= 50) ? 1.0 : 0.0);
		}
	},	
	
	/*
	 * Pendeln über 50 Kilometer
	 */	
	pendeln_ueber50km("pendeln_ueber50km") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			double commute_distance = Math.max(actitoppPerson.getCommutingdistance_work(), actitoppPerson.getCommutingdistance_education());
			return ((commute_distance > 50) ? 1.0 : 0.0);
		}
	},	
		
	/*
	 * Anzahl Arbeitstage (Stufe 1A)
	 */	
	anztage_w("anztage_w") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return actitoppPerson.getAttributesMap().get("anztage_w").doubleValue();
		}
	},	
	anzahl_arbeitstage0("anzahl_arbeitstage0") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_w").doubleValue() == 0) ? 1.0 : 0.0);
		}
	},	
	anzahl_arbeitstage1("anzahl_arbeitstage1") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_w").doubleValue() == 1) ? 1.0 : 0.0);
		}
	},	
	anzahl_arbeitstage2("anzahl_arbeitstage2") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_w").doubleValue() == 2) ? 1.0 : 0.0);
		}
	},	
	anzahl_arbeitstage3("anzahl_arbeitstage3") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_w").doubleValue() == 3) ? 1.0 : 0.0);
		}
	},	
	anzahl_arbeitstage4("anzahl_arbeitstage4") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_w").doubleValue() == 4) ? 1.0 : 0.0);
		}
	},	
	anzahl_arbeitstage5("anzahl_arbeitstage5") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_w").doubleValue() == 5) ? 1.0 : 0.0);
		}
	},	
	anzahl_arbeitstage6("anzahl_arbeitstage6") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_w").doubleValue() == 6) ? 1.0 : 0.0);
		}
	},	
	anzahl_arbeitstage7("anzahl_arbeitstage7") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_w").doubleValue() == 7) ? 1.0 : 0.0);
		}
	},	

	/*
	 * Anzahl Bildungstage (Stufe 1B)
	 */	
	anztage_e("anztage_e") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return actitoppPerson.getAttributesMap().get("anztage_e").doubleValue();
		}
	},	
	anzahl_bildungstage0("anzahl_bildungstage0") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_e").doubleValue() == 0) ? 1.0 : 0.0);
		}
	},	
	anzahl_bildungstage1("anzahl_bildungstage1") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_e").doubleValue() == 1) ? 1.0 : 0.0);
		}
	},	
	anzahl_bildungstage2("anzahl_bildungstage2") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_e").doubleValue() == 2) ? 1.0 : 0.0);
		}
	},	
	anzahl_bildungstage3("anzahl_bildungstage3") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_e").doubleValue() == 3) ? 1.0 : 0.0);
		}
	},	
	anzahl_bildungstage4("anzahl_bildungstage4") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_e").doubleValue() == 4) ? 1.0 : 0.0);
		}
	},	
	anzahl_bildungstage5("anzahl_bildungstage5") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_e").doubleValue() == 5) ? 1.0 : 0.0);
		}
	},	
	anzahl_bildungstage6("anzahl_bildungstage6") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_e").doubleValue() == 6) ? 1.0 : 0.0);
		}
	},	
	anzahl_bildungstage7("anzahl_bildungstage7") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_e").doubleValue() == 7) ? 1.0 : 0.0);
		}
	},	
	
	/*
	 * Anzahl Freizeittage (Stufe 1C)
	 */	
	anztage_l("anztage_l") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return actitoppPerson.getAttributesMap().get("anztage_l").doubleValue();
		}
	},	
	anzahl_freizeittage0("anzahl_freizeittage0") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_l").doubleValue() == 0) ? 1.0 : 0.0);
		}
	},	
	anzahl_freizeittage1("anzahl_freizeittage1") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_l").doubleValue() == 1) ? 1.0 : 0.0);
		}
	},	
	anzahl_freizeittage2("anzahl_freizeittage2") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_l").doubleValue() == 2) ? 1.0 : 0.0);
		}
	},	
	anzahl_freizeittage3("anzahl_freizeittage3") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_l").doubleValue() == 3) ? 1.0 : 0.0);
		}
	},	
	anzahl_freizeittage4("anzahl_freizeittage4") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_l").doubleValue() == 4) ? 1.0 : 0.0);
		}
	},	
	anzahl_freizeittage5("anzahl_freizeittage5") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_l").doubleValue() == 5) ? 1.0 : 0.0);
		}
	},	
	anzahl_freizeittage6("anzahl_freizeittage6") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_l").doubleValue() == 6) ? 1.0 : 0.0);
		}
	},	
	anzahl_freizeittage7("anzahl_freizeittage7") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_l").doubleValue() == 7) ? 1.0 : 0.0);
		}
	},	
	
	/*
	 * Anzahl Shoppingtage (Stufe 1D)
	 */	
	anztage_s("anztage_s") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return actitoppPerson.getAttributesMap().get("anztage_s").doubleValue();
		}
	},	
	anzahl_shoppingtage0("anzahl_shoppingtage0") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_s").doubleValue() == 0) ? 1.0 : 0.0);
		}
	},	
	anzahl_shoppingtage1("anzahl_shoppingtage1") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_s").doubleValue() == 1) ? 1.0 : 0.0);
		}
	},	
	anzahl_shoppingtage2("anzahl_shoppingtage2") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_s").doubleValue() == 2) ? 1.0 : 0.0);
		}
	},	
	anzahl_shoppingtage3("anzahl_shoppingtage3") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_s").doubleValue() == 3) ? 1.0 : 0.0);
		}
	},	
	anzahl_shoppingtage4("anzahl_shoppingtage4") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_s").doubleValue() == 4) ? 1.0 : 0.0);
		}
	},	
	anzahl_shoppingtage5("anzahl_shoppingtage5") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_s").doubleValue() == 5) ? 1.0 : 0.0);
		}
	},	
	anzahl_shoppingtage6("anzahl_shoppingtage6") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_s").doubleValue() == 6) ? 1.0 : 0.0);
		}
	},	
	anzahl_shoppingtage7("anzahl_shoppingtage7") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_s").doubleValue() == 7) ? 1.0 : 0.0);
		}
	},	
	
	/*
	 * Anzahl Transporttage (Stufe 1E)
	 */	
	anztage_t("anztage_t") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return actitoppPerson.getAttributesMap().get("anztage_t").doubleValue();
		}
	},	
	anzahl_transporttage0("anzahl_transporttage0") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_t").doubleValue() == 0) ? 1.0 : 0.0);
		}
	},	
	anzahl_transporttage1("anzahl_transporttage1") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_t").doubleValue() == 1) ? 1.0 : 0.0);
		}
	},	
	anzahl_transporttage2("anzahl_transporttage2") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_t").doubleValue() == 2) ? 1.0 : 0.0);
		}
	},	
	anzahl_transporttage3("anzahl_transporttage3") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_t").doubleValue() == 3) ? 1.0 : 0.0);
		}
	},	
	anzahl_transporttage4("anzahl_transporttage4") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_t").doubleValue() == 4) ? 1.0 : 0.0);
		}
	},	
	anzahl_transporttage5("anzahl_transporttage5") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_t").doubleValue() == 5) ? 1.0 : 0.0);
		}
	},	
	anzahl_transporttage6("anzahl_transporttage6") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_t").doubleValue() == 6) ? 1.0 : 0.0);
		}
	},	
	anzahl_transporttage7("anzahl_transporttage7") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributesMap().get("anztage_t").doubleValue() == 7) ? 1.0 : 0.0);
		}
	},	
	
	/*
	 * Anzahl immobile Tage (Stufe 1F)
	 */	
	anztage_immobil("anztage_immobil") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return actitoppPerson.getAttributesMap().get("anztage_immobil").doubleValue();
		}
	},	
	
	/*
	 * Properties für Anzahl an Aktivitäten in der Woche
	 */
	wakt_prowoche_1bis3("wakt_prowoche_1bis3") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			int amountofactivities = actitoppPerson.getWeekPattern().countActivitiesPerWeek('W');
			return ((amountofactivities >= 1 && amountofactivities <= 3) ? 1.0 : 0.0);
		}
	},	
	wakt_prowoche_7bis10("wakt_prowoche_7bis10") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			int amountofactivities = actitoppPerson.getWeekPattern().countActivitiesPerWeek('W');
			return ((amountofactivities >= 7 && amountofactivities <= 10) ? 1.0 : 0.0);
		}
	},		
	eakt_prowoche_ueber0("eakt_prowoche_ueber0") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			int amountofactivities = actitoppPerson.getWeekPattern().countActivitiesPerWeek('E');
			return ((amountofactivities > 0) ? 1.0 : 0.0);
		}
	},	
	eakt_prowoche_1bis3("eakt_prowoche_1bis3") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			int amountofactivities = actitoppPerson.getWeekPattern().countActivitiesPerWeek('E');
			return ((amountofactivities >= 1 && amountofactivities <= 3) ? 1.0 : 0.0);
		}
	},	
	lakt_prowoche_1bis3("lakt_prowoche_1bis3") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			int amountofactivities = actitoppPerson.getWeekPattern().countActivitiesPerWeek('L');
			return ((amountofactivities >= 1 && amountofactivities <= 3) ? 1.0 : 0.0);
		}
	},	
	lakt_prowoche_4bis6("lakt_prowoche_4bis6") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			int amountofactivities = actitoppPerson.getWeekPattern().countActivitiesPerWeek('L');
			return ((amountofactivities >= 4 && amountofactivities <= 6) ? 1.0 : 0.0);
		}
	},	
	lakt_prowoche_7bis10("lakt_prowoche_7bis10") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			int amountofactivities = actitoppPerson.getWeekPattern().countActivitiesPerWeek('L');
			return ((amountofactivities >= 7 && amountofactivities <= 10) ? 1.0 : 0.0);
		}
	},	
	sakt_prowoche_1bis3("sakt_prowoche_1bis3") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			int amountofactivities = actitoppPerson.getWeekPattern().countActivitiesPerWeek('S');
			return ((amountofactivities >= 1 && amountofactivities <= 3) ? 1.0 : 0.0);
		}
	},	
	sakt_prowoche_4bis6("sakt_prowoche_4bis6") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			int amountofactivities = actitoppPerson.getWeekPattern().countActivitiesPerWeek('S');
			return ((amountofactivities >= 4 && amountofactivities <= 6) ? 1.0 : 0.0);
		}
	},	
	sakt_prowoche_7bis10("sakt_prowoche_7bis10") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			int amountofactivities = actitoppPerson.getWeekPattern().countActivitiesPerWeek('S');
			return ((amountofactivities >= 7 && amountofactivities <= 10) ? 1.0 : 0.0);
		}
	},	
	takt_prowoche_1bis3("takt_prowoche_1bis3") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			int amountofactivities = actitoppPerson.getWeekPattern().countActivitiesPerWeek('T');
			return ((amountofactivities >= 1 && amountofactivities <= 3) ? 1.0 : 0.0);
		}
	},	
	takt_prowoche_4bis6("takt_prowoche_4bis6") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			int amountofactivities = actitoppPerson.getWeekPattern().countActivitiesPerWeek('T');
			return ((amountofactivities >= 4 && amountofactivities <= 6) ? 1.0 : 0.0);
		}
	},	
	takt_prowoche_7bis10("takt_prowoche_7bis10") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			int amountofactivities = actitoppPerson.getWeekPattern().countActivitiesPerWeek('T');
			return ((amountofactivities >= 7 && amountofactivities <= 10) ? 1.0 : 0.0);
		}
	},	
	
	anzakt_woche_w("anzakt_woche_w") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return actitoppPerson.getWeekPattern().countActivitiesPerWeek('W');
		}
	},	
	anzakt_woche_e("anzakt_woche_e") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return actitoppPerson.getWeekPattern().countActivitiesPerWeek('E');
		}
	},
	anzakt_woche_l("anzakt_woche_l") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return actitoppPerson.getWeekPattern().countActivitiesPerWeek('L');
		}
	},
	anzakt_woche_s("anzakt_woche_s") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return actitoppPerson.getWeekPattern().countActivitiesPerWeek('S');
		}
	},
	anzakt_woche_t("anzakt_woche_t") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return actitoppPerson.getWeekPattern().countActivitiesPerWeek('T');
		}
	},
	
	/*
	 * Anzahl Touren in der Woche
	 */
	anztouren_woche_w("anztouren_woche_w") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return actitoppPerson.getWeekPattern().countToursPerWeek('W');
		}
	},	
	anztouren_woche_e("anztouren_woche_e") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return actitoppPerson.getWeekPattern().countToursPerWeek('E');
		}
	},	
	anztouren_woche_l("anztouren_woche_l") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return actitoppPerson.getWeekPattern().countToursPerWeek('L');
		}
	},	
	anztouren_woche_s("anztouren_woche_s") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return actitoppPerson.getWeekPattern().countToursPerWeek('S');
		}
	},	
	anztouren_woche_t("anztouren_woche_t") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return actitoppPerson.getWeekPattern().countToursPerWeek('T');
		}
	},	
	
	/*
	 * Anzahl Tage mit spezifischen Aktivitätentypen
	 */
	tagemit_wakt_1("tagemit_wakt_1") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('W') == 1 ? 1.0 : 0.0);
		}
	},		
	tagemit_wakt_2("tagemit_wakt_2") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('W') == 2 ? 1.0 : 0.0);
		}
	},	
	tagemit_wakt_3("tagemit_wakt_3") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('W') == 3 ? 1.0 : 0.0);
		}
	},	
	tagemit_wakt_4("tagemit_wakt_4") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('W') == 4 ? 1.0 : 0.0);
		}
	},		
	tagemit_wakt_5("tagemit_wakt_5") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('W') == 5 ? 1.0 : 0.0);
		}
	},	
	tagemit_wakt_6("tagemit_wakt_6") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('W') == 6 ? 1.0 : 0.0);
		}
	},	
	tagemit_wakt_7("tagemit_wakt_7") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('W') == 7 ? 1.0 : 0.0);
		}
	},	
	tagemit_eakt_1("tagemit_eakt_1") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('E') == 1 ? 1.0 : 0.0);
		}
	},		
	tagemit_eakt_2("tagemit_eakt_2") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('E') == 2 ? 1.0 : 0.0);
		}
	},	
	tagemit_eakt_3("tagemit_eakt_3") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('E') == 3 ? 1.0 : 0.0);
		}
	},	
	tagemit_eakt_4("tagemit_eakt_4") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('E') == 4 ? 1.0 : 0.0);
		}
	},		
	tagemit_eakt_5("tagemit_eakt_5") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('E') == 5 ? 1.0 : 0.0);
		}
	},	
	tagemit_eakt_6("tagemit_eakt_6") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('E') == 6 ? 1.0 : 0.0);
		}
	},	
	tagemit_eakt_7("tagemit_eakt_7") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('E') == 7 ? 1.0 : 0.0);
		}
	},	
	tagemit_lakt_1("tagemit_lakt_1") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('L') == 1 ? 1.0 : 0.0);
		}
	},		
	tagemit_lakt_2("tagemit_lakt_2") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('L') == 2 ? 1.0 : 0.0);
		}
	},	
	tagemit_lakt_3("tagemit_lakt_3") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('L') == 3 ? 1.0 : 0.0);
		}
	},	
	tagemit_lakt_4("tagemit_lakt_4") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('L') == 4 ? 1.0 : 0.0);
		}
	},		
	tagemit_lakt_5("tagemit_lakt_5") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('L') == 5 ? 1.0 : 0.0);
		}
	},	
	tagemit_lakt_6("tagemit_lakt_6") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('L') == 6 ? 1.0 : 0.0);
		}
	},	
	tagemit_lakt_7("tagemit_lakt_7") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('L') == 7 ? 1.0 : 0.0);
		}
	},	
	tagemit_sakt_1("tagemit_sakt_1") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('S') == 1 ? 1.0 : 0.0);
		}
	},		
	tagemit_sakt_2("tagemit_sakt_2") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('S') == 2 ? 1.0 : 0.0);
		}
	},	
	tagemit_sakt_3("tagemit_sakt_3") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('S') == 3 ? 1.0 : 0.0);
		}
	},	
	tagemit_sakt_4("tagemit_sakt_4") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('S') == 4 ? 1.0 : 0.0);
		}
	},		
	tagemit_sakt_5("tagemit_sakt_5") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('S') == 5 ? 1.0 : 0.0);
		}
	},	
	tagemit_sakt_6("tagemit_sakt_6") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('S') == 6 ? 1.0 : 0.0);
		}
	},	
	tagemit_sakt_7("tagemit_sakt_7") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('S') == 7 ? 1.0 : 0.0);
		}
	},	
	tagemit_takt_1("tagemit_takt_1") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('T') == 1 ? 1.0 : 0.0);
		}
	},		
	tagemit_takt_2("tagemit_takt_2") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('T') == 2 ? 1.0 : 0.0);
		}
	},	
	tagemit_takt_3("tagemit_takt_3") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('T') == 3 ? 1.0 : 0.0);
		}
	},	
	tagemit_takt_4("tagemit_takt_4") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('T') == 4 ? 1.0 : 0.0);
		}
	},		
	tagemit_takt_5("tagemit_takt_5") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('T') == 5 ? 1.0 : 0.0);
		}
	},	
	tagemit_takt_6("tagemit_takt_6") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('T') == 6 ? 1.0 : 0.0);
		}
	},	
	tagemit_takt_7("tagemit_takt_7") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return (actitoppPerson.getWeekPattern().countDaysWithSpecificActivity('T') == 7 ? 1.0 : 0.0);
		}
	},	
	
	/*
	 * Anzahl Tage mit Touren Vor/Nach-Haupttour
	 */
	anztagemit_tourenvorht("anztagemit_tourenvorht") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			int counter=0;
			for (HDay day : actitoppPerson.getWeekPattern().getDays())
			{
				if (!day.isHomeDay() && day.getLowestTourIndex()<0) counter++;
			}
			return counter;
		}
	},
	anztagemit_tourennachht("anztagemit_tourennachht") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			int counter=0;
			for (HDay day : actitoppPerson.getWeekPattern().getDays())
			{
				if (!day.isHomeDay() && day.getHighestTourIndex()>0) counter++;
			}
			return counter;
		}
	},
	
	/*
	 * Zeitbudget Work
	 */
	zeitbudget_work_ueber_kat2("zeitbudget_work_ueber_kat2") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributefromMap("Wbudget_category_alternative") > 2) ? 1.0 : 0.0);
		}
	},
	
	/*
	 * Default Anzahl Touren am Tag = 1
	 */
	def_1tour("mean_1tour") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributefromMap("anztourentag_mean") == 1) ? 1.0 : 0.0);
		}
	},
	/*
	 * Default Anzahl Touren am Tag = 2
	 */
	def_2touren("mean_2touren") 
	{
		@Override
		public double getAttribute(ActitoppPerson actitoppPerson) 
		{
			return ((actitoppPerson.getAttributefromMap("anztourentag_mean") == 2) ? 1.0 : 0.0);
		}
	},
	
	;
		
	private final String name;

	/**
	 * privater Konstruktor
	 * 
	 * @param name
	 */
	private ActitoppPersonParameters(String name)
	{
		this.name = name;
	}
	
	/**
	 * 
	 * Methode zur Rückgabe des EnumValues für einen gegebenen String
	 * 
	 * @param name
	 * @return
	 */
	public static ActitoppPersonParameters getEnumValue(String name)
	{
		// Eindeutigkeitsprüfung
		checkUniqueness(name);
		
		// Rückgabe des passenden Enums
		for (ActitoppPersonParameters parameter : values())
		{
			if (parameter.name.equals(name)) return parameter;
		}
		throw new IllegalArgumentException(name + " not found");
	}
	

	/**
	 * 
	 * Methode zur Prüfung der Eindeutigkeit der Enum-Namensvariable
	 * 
	 * @param name
	 */
	private static void checkUniqueness(String name)
	{
			int counter=0;
			for (ActitoppPersonParameters parameter : values())
			{
				if (parameter.name.equals(name))
				{
					counter++;
					if (counter>1) throw new IllegalArgumentException(name + " identifier is not unique - wrong enum specification");
				}
			}
	}

	public abstract double getAttribute(ActitoppPerson actitoppPerson);
	
	
}
