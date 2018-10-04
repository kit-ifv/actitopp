package edu.kit.ifv.mobitopp.actitopp;

/**
 * 
 * @author Tim Hilgert
 *
 */
public enum HTourParameters {

	/*
	 * Tourtyp
	 */
	tourtyp_work("tourtyp_work") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getActivity(0).getActivityType() == ActivityType.WORK) ? 1.0 : 0.0);
		}
	},
	tourtyp_education("tourtyp_education") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getActivity(0).getActivityType() == ActivityType.EDUCATION) ? 1.0 : 0.0);
		}
	},
	tourtyp_leisure("tourtyp_leisure") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getActivity(0).getActivityType() == ActivityType.LEISURE) ? 1.0 : 0.0);
		}
	},
	tourtyp_shopping("tourtyp_shopping") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getActivity(0).getActivityType() == ActivityType.SHOPPING) ? 1.0 : 0.0);
		}
	},
	
	tourtyp_transport("tourtyp_transport") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getActivity(0).getActivityType() == ActivityType.TRANSPORT) ? 1.0 : 0.0);
		}
	},
	
	
	
	
	/*
	 * Dauer der Akt in Tour
	 */
	dauer_akt_in_tour_0bis2std("dauer_akt_in_tour_0bis2std") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getActDuration() >= 0*60 && tour.getActDuration() < 2*60) ? 1.0 : 0.0);
		}
	},
	dauer_akt_in_tour_2bis4std("dauer_akt_in_tour_2bis4std") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getActDuration() >= 2*60 && tour.getActDuration() < 4*60) ? 1.0 : 0.0);
		}
	},
	dauer_akt_in_tour_4bis6std("dauer_akt_in_tour_4bis6std") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getActDuration() >= 4*60 && tour.getActDuration() < 6*60) ? 1.0 : 0.0);
		}
	},
	dauer_akt_in_tour_6bis8std("dauer_akt_in_tour_6bis8std") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getActDuration() >= 6*60 && tour.getActDuration() < 8*60) ? 1.0 : 0.0);
		}
	},
	dauer_akt_in_tour_8bis10std("dauer_akt_in_tour_8bis10std") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getActDuration() >= 8*60 && tour.getActDuration() < 10*60) ? 1.0 : 0.0);
		}
	},
	dauer_akt_in_tour_10bis12std("dauer_akt_in_tour_10bis12std") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getActDuration() >= 10*60 && tour.getActDuration() < 12*60) ? 1.0 : 0.0);
		}
	},
	dauer_akt_in_tour_12bis14std("dauer_akt_in_tour_12bis14std") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getActDuration() >= 12*60 && tour.getActDuration() < 14*60) ? 1.0 : 0.0);
		}
	},
	dauer_akt_in_tour_ueber14std("dauer_akt_in_tour_ueber14std") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getActDuration() >= 14*60) ? 1.0 : 0.0);
		}
	},
	
	
	
	/*
	 * Ende Tour vorher
	 */
	endetourvorher_Std_0("endetourvorher_Std_0") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			int endetourvorher = tour.getDay().getTour(tour.getIndex()-1).getEndTime();
			return ((endetourvorher >= 0*60 && endetourvorher < 1*60) ? 1.0 : 0.0);
		}
	},
	endetourvorher_Std_1("endetourvorher_Std_1") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			int endetourvorher = tour.getDay().getTour(tour.getIndex()-1).getEndTime();
			return ((endetourvorher >= 1*60 && endetourvorher < 2*60) ? 1.0 : 0.0);
		}
	},
	endetourvorher_Std_2("endetourvorher_Std_2") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			int endetourvorher = tour.getDay().getTour(tour.getIndex()-1).getEndTime();
			return ((endetourvorher >= 2*60 && endetourvorher < 3*60) ? 1.0 : 0.0);
		}
	},	
	endetourvorher_Std_3("endetourvorher_Std_3") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			int endetourvorher = tour.getDay().getTour(tour.getIndex()-1).getEndTime();
			return ((endetourvorher >= 3*60 && endetourvorher < 4*60) ? 1.0 : 0.0);
		}
	},
	endetourvorher_Std_4("endetourvorher_Std_4") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			int endetourvorher = tour.getDay().getTour(tour.getIndex()-1).getEndTime();
			return ((endetourvorher >= 4*60 && endetourvorher < 5*60) ? 1.0 : 0.0);
		}
	},
	endetourvorher_Std_5("endetourvorher_Std_5") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			int endetourvorher = tour.getDay().getTour(tour.getIndex()-1).getEndTime();
			return ((endetourvorher >= 5*60 && endetourvorher < 6*60) ? 1.0 : 0.0);
		}
	},
	endetourvorher_Std_6("endetourvorher_Std_6") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			int endetourvorher = tour.getDay().getTour(tour.getIndex()-1).getEndTime();
			return ((endetourvorher >= 6*60 && endetourvorher < 7*60) ? 1.0 : 0.0);
		}
	},
	endetourvorher_Std_7("endetourvorher_Std_7") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			int endetourvorher = tour.getDay().getTour(tour.getIndex()-1).getEndTime();
			return ((endetourvorher >= 7*60 && endetourvorher < 8*60) ? 1.0 : 0.0);
		}
	},
	endetourvorher_Std_8("endetourvorher_Std_8") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			int endetourvorher = tour.getDay().getTour(tour.getIndex()-1).getEndTime();
			return ((endetourvorher >= 8*60 && endetourvorher < 9*60) ? 1.0 : 0.0);
		}
	},
	endetourvorher_Std_9("endetourvorher_Std_9") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			int endetourvorher = tour.getDay().getTour(tour.getIndex()-1).getEndTime();
			return ((endetourvorher >= 9*60 && endetourvorher < 10*60) ? 1.0 : 0.0);
		}
	},
	endetourvorher_Std_10("endetourvorher_Std_10") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			int endetourvorher = tour.getDay().getTour(tour.getIndex()-1).getEndTime();
			return ((endetourvorher >= 10*60 && endetourvorher < 11*60) ? 1.0 : 0.0);
		}
	},
	endetourvorher_Std_11("endetourvorher_Std_11") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			int endetourvorher = tour.getDay().getTour(tour.getIndex()-1).getEndTime();
			return ((endetourvorher >= 11*60 && endetourvorher < 12*60) ? 1.0 : 0.0);
		}
	},
	endetourvorher_Std_12("endetourvorher_Std_12") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			int endetourvorher = tour.getDay().getTour(tour.getIndex()-1).getEndTime();
			return ((endetourvorher >= 12*60 && endetourvorher < 13*60) ? 1.0 : 0.0);
		}
	},
	endetourvorher_Std_13("endetourvorher_Std_13") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			int endetourvorher = tour.getDay().getTour(tour.getIndex()-1).getEndTime();
			return ((endetourvorher >= 13*60 && endetourvorher < 14*60) ? 1.0 : 0.0);
		}
	},
	endetourvorher_Std_14("endetourvorher_Std_14") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			int endetourvorher = tour.getDay().getTour(tour.getIndex()-1).getEndTime();
			return ((endetourvorher >= 14*60 && endetourvorher < 15*60) ? 1.0 : 0.0);
		}
	},
	endetourvorher_Std_15("endetourvorher_Std_15") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			int endetourvorher = tour.getDay().getTour(tour.getIndex()-1).getEndTime();
			return ((endetourvorher >= 15*60 && endetourvorher < 16*60) ? 1.0 : 0.0);
		}
	},	
	endetourvorher_Std_16("endetourvorher_Std_16") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			int endetourvorher = tour.getDay().getTour(tour.getIndex()-1).getEndTime();
			return ((endetourvorher >= 16*60 && endetourvorher < 17*60) ? 1.0 : 0.0);
		}
	},	
	endetourvorher_Std_17("endetourvorher_Std_17") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			int endetourvorher = tour.getDay().getTour(tour.getIndex()-1).getEndTime();
			return ((endetourvorher >= 17*60 && endetourvorher < 18*60) ? 1.0 : 0.0);
		}
	},	
	endetourvorher_Std_18("endetourvorher_Std_18") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			int endetourvorher = tour.getDay().getTour(tour.getIndex()-1).getEndTime();
			return ((endetourvorher >= 18*60 && endetourvorher < 19*60) ? 1.0 : 0.0);
		}
	},	
	endetourvorher_Std_19("endetourvorher_Std_19") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			int endetourvorher = tour.getDay().getTour(tour.getIndex()-1).getEndTime();
			return ((endetourvorher >= 19*60 && endetourvorher < 20*60) ? 1.0 : 0.0);
		}
	},	
	endetourvorher_Std_20("endetourvorher_Std_20") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			int endetourvorher = tour.getDay().getTour(tour.getIndex()-1).getEndTime();
			return ((endetourvorher >= 20*60 && endetourvorher < 21*60) ? 1.0 : 0.0);
		}
	},	
	endetourvorher_Std_21("endetourvorher_Std_21") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			int endetourvorher = tour.getDay().getTour(tour.getIndex()-1).getEndTime();
			return ((endetourvorher >= 21*60 && endetourvorher < 22*60) ? 1.0 : 0.0);
		}
	},	
	endetourvorher_Std_22("endetourvorher_Std_22") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			int endetourvorher = tour.getDay().getTour(tour.getIndex()-1).getEndTime();
			return ((endetourvorher >= 22*60 && endetourvorher < 23*60) ? 1.0 : 0.0);
		}
	},	
	endetourvorher_Std_23("endetourvorher_Std_23") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			int endetourvorher = tour.getDay().getTour(tour.getIndex()-1).getEndTime();
			return ((endetourvorher >= 23*60 && endetourvorher < 24*60) ? 1.0 : 0.0);
		}
	},	
	
	/*
	 * Tour hat X Akt
	 */
	tourhat1akt("tourhat1akt") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getAmountOfActivities() == 1) ? 1.0 : 0.0);
		}
	},	
	tourhat2akt("tourhat2akt") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getAmountOfActivities() == 2) ? 1.0 : 0.0);
		}
	},	
	tourhat3akt("tourhat3akt") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getAmountOfActivities() == 3) ? 1.0 : 0.0);
		}
	},	
	tourhat4akt("tourhat4akt") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getAmountOfActivities() == 4) ? 1.0 : 0.0);
		}
	},	
	
	/*
	 * Anzahl Akt vor Hauptakt
	 */
	anzaktvorhauptaktist1("anzaktvorhauptaktist1") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getLowestActivityIndex() == -1) ? 1.0 : 0.0);
		}
	},
	anzaktvorhauptaktist2("anzaktvorhauptaktist2") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getLowestActivityIndex() == -2) ? 1.0 : 0.0);
		}
	},
	anzaktvorhauptaktist3("anzaktvorhauptaktist3") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getLowestActivityIndex() == -3) ? 1.0 : 0.0);
		}
	},
	
	/*
	 * Tour Nr des Tages
	 */
	tour1destages("tour1destages") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getDay().getLowestTourIndex() == tour.getIndex()) ? 1.0 : 0.0);
		}
	},	
	tour2destages("tour2destages") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getDay().getLowestTourIndex() + 1 == tour.getIndex()) ? 1.0 : 0.0);
		}
	},	
	tour3destages("tour3destages") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getDay().getLowestTourIndex() + 2 == tour.getIndex()) ? 1.0 : 0.0);
		}
	},	
	
	/*
	 * Erste/Letzte Tour des Tages
	 */
	erstetourdestages("erstetourdestages") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getDay().getLowestTourIndex() == tour.getIndex()) ? 1.0 : 0.0);
		}
	},
	letztetourdestages("letztetourdestages") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getDay().getHighestTourIndex() == tour.getIndex()) ? 1.0 : 0.0);
		}
	},	
	
	/*
	 * Tour Vor/Nach Haupttour
	 */
	tourliegtvorhaupttour("tourliegtvorhaupttour") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getIndex() < 0) ? 1.0 : 0.0);
		}
	},		
	tourliegtnachhaupttour("tourliegtnachhaupttour") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getIndex() > 0) ? 1.0 : 0.0);
		}
	},	
	touristhaupttour("touristhaupttour") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getIndex() == 0) ? 1.0 : 0.0);
		}
	},		
	
	
	/*
	 * Startzeitraum Haupttour
	 */
	startzeitraum_ht_1("startzeitraum_ht_1") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getAttributesMap().get("tourStartCat_index") == 1) ? 1.0 : 0.0);
		}
	},		
	startzeitraum_ht_2("startzeitraum_ht_2") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getAttributesMap().get("tourStartCat_index") == 2) ? 1.0 : 0.0);
		}
	},	
	startzeitraum_ht_3("startzeitraum_ht_3") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getAttributesMap().get("tourStartCat_index") == 3) ? 1.0 : 0.0);
		}
	},	
	startzeitraum_ht_4("startzeitraum_ht_4") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getAttributesMap().get("tourStartCat_index") == 4) ? 1.0 : 0.0);
		}
	},	
	startzeitraum_ht_5("startzeitraum_ht_5") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getAttributesMap().get("tourStartCat_index") == 5) ? 1.0 : 0.0);
		}
	},	
	startzeitraum_ht_6("startzeitraum_ht_6") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getAttributesMap().get("tourStartCat_index") == 6) ? 1.0 : 0.0);
		}
	},	
	startzeitraum_ht_7("startzeitraum_ht_7") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getAttributesMap().get("tourStartCat_index") == 7) ? 1.0 : 0.0);
		}
	},	
	startzeitraum_ht_8("startzeitraum_ht_8") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getAttributesMap().get("tourStartCat_index") == 8) ? 1.0 : 0.0);
		}
	},	
	startzeitraum_ht_9("startzeitraum_ht_9") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getAttributesMap().get("tourStartCat_index") == 9) ? 1.0 : 0.0);
		}
	},	
	startzeitraum_ht_10("startzeitraum_ht_10") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getAttributesMap().get("tourStartCat_index") == 10) ? 1.0 : 0.0);
		}
	},
	startzeitraum_ht_11("startzeitraum_ht_11") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getAttributesMap().get("tourStartCat_index") == 11) ? 1.0 : 0.0);
		}
	},
	startzeitraum_ht_12("startzeitraum_ht_12") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getAttributesMap().get("tourStartCat_index") == 12) ? 1.0 : 0.0);
		}
	},
	startzeitraum_ht_13("startzeitraum_ht_13") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getAttributesMap().get("tourStartCat_index") == 13) ? 1.0 : 0.0);
		}
	},
	startzeitraum_ht_14("startzeitraum_ht_14") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getAttributesMap().get("tourStartCat_index") == 14) ? 1.0 : 0.0);
		}
	},
	startzeitraum_ht_15("startzeitraum_ht_15") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getAttributesMap().get("tourStartCat_index") == 15) ? 1.0 : 0.0);
		}
	},
	startzeitraum_ht_16("startzeitraum_ht_16") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			return ((tour.getAttributesMap().get("tourStartCat_index") == 16) ? 1.0 : 0.0);
		}
	},
	
	vorherigeentscheidungyes("vorherigeentscheidungyes") 
	{
		@Override
		public double getAttribute(HTour tour) 
		{
			double result=0;
			boolean tourgefunden=false;
			HTour tmptour=tour;
			while (!tourgefunden)
			{
				 tmptour = tmptour.getPreviousTourinPattern();
				 if (tmptour==null)	break;
				 if (tmptour.isFirstTouroftheDay() && (tmptour.getActivity(0).getActivityType()==ActivityType.WORK || tmptour.getActivity(0).getActivityType()==ActivityType.EDUCATION))
				 {
					 tourgefunden = true;
				 }
			}
			if (tourgefunden && tmptour.getAttributefromMap("default_start_cat_yes")==1.0) result=1;			
			return result;
		}
	},
	
	
	;
		
	private final String name;

	/**
	 * privater Konstruktor
	 * 
	 * @param name
	 */
	private HTourParameters(String name)
	{
		this.name = name;
	}
	
	/**
	 * 
	 * Methode zur R�ckgabe des EnumValues f�r einen gegebenen String
	 * 
	 * @param name
	 * @return
	 */
	public static HTourParameters getEnumValue(String name)
	{
		// Eindeutigkeitspr�fung
		checkUniqueness(name);
		
		// R�ckgabe des passenden Enums
		for (HTourParameters parameter : values())
		{
			if (parameter.name.equals(name)) return parameter;
		}
		throw new IllegalArgumentException(name + " not found");
	}
	
	/**
	 * 
	 * Methode zur Pr�fung der Eindeutigkeit der Enum-Namensvariable
	 * 
	 * @param name
	 */
	private static void checkUniqueness(String name)
	{
			int counter=0;
			for (HTourParameters parameter : values())
			{
				if (parameter.name.equals(name))
				{
					counter++;
					if (counter>1) throw new IllegalArgumentException(name + " identifier is not unique - wrong enum specification");
				}
			}
	}

	public abstract double getAttribute(HTour tour);
	
	
}
