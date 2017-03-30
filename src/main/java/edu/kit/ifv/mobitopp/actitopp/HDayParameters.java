package edu.kit.ifv.mobitopp.actitopp;

/**
 * 
 * @author Tim Hilgert
 *
 */
public enum HDayParameters {

	
	/*
	 * Wochentag
	 */	
	tag_mo("tag_mo") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getWeekday() == 1) ? 1.0 : 0.0);
		}
	},	
	tag_di("tag_di") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getWeekday() == 2) ? 1.0 : 0.0);
		}
	},
	tag_mi("tag_mi") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getWeekday() == 3) ? 1.0 : 0.0);
		}
	},
	tag_do("tag_do") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getWeekday() == 4) ? 1.0 : 0.0);
		}
	},
	tag_fr("tag_fr") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getWeekday() == 5) ? 1.0 : 0.0);
		}
	},
	tag_sa("tag_sa") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getWeekday() == 6) ? 1.0 : 0.0);
		}
	},
	tag_so("tag_so") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getWeekday() == 7) ? 1.0 : 0.0);
		}
	},
	
	/*
	 * Tag hat X Touren
	 */
	taghat1tour("taghat1tour") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getAmountOfTours() == 1) ? 1.0 : 0.0);
		}
	},
	taghat2touren("taghat2touren") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getAmountOfTours() == 2) ? 1.0 : 0.0);
		}
	},
	taghat3touren("taghat3touren") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getAmountOfTours() == 3) ? 1.0 : 0.0);
		}
	},
	
	
	/*
	 * Tag hat X Aktivitäten
	 */
	taghat1akt("taghat1akt") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getTotalAmountOfActivitites() == 1) ? 1.0 : 0.0);
		}
	},
	taghat2akt("taghat2akt") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getTotalAmountOfActivitites() == 2) ? 1.0 : 0.0);
		}
	},
	taghat3akt("taghat3akt") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getTotalAmountOfActivitites() == 3) ? 1.0 : 0.0);
		}
	},
	taghat4akt("taghat4akt") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getTotalAmountOfActivitites() == 4) ? 1.0 : 0.0);
		}
	},
	taghat5akt("taghat5akt") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getTotalAmountOfActivitites() == 5) ? 1.0 : 0.0);
		}
	},
	taghat6akt("taghat6akt") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getTotalAmountOfActivitites() == 6) ? 1.0 : 0.0);
		}
	},	

	/*
	 * Tag hat Aktivitäten eines Typs
	 */	
	taghattakt("taghattakt") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getTotalAmountOfActivitites('T') > 0) ? 1.0 : 0.0);
		}
	},	
	
	
	/*
	 * Haupttour des Tages
	 */
	haupttour_work("haupttour_work") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getMainTourType() == 'W') ? 1.0 : 0.0);
		}
	},		
	haupttour_education("haupttour_education") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getMainTourType() == 'E') ? 1.0 : 0.0);
		}
	},	
	haupttour_leisure("haupttour_leisure") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getMainTourType() == 'L') ? 1.0 : 0.0);
		}
	},	
	haupttour_shopping("haupttour_shopping") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getMainTourType() == 'S') ? 1.0 : 0.0);
		}
	},	
	haupttour_transport("haupttour_transport") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getMainTourType() == 'T') ? 1.0 : 0.0);
		}
	},		
	
	
	/*
	 * Dauer Hauptakt Tag
	 */
	dauer_hauptakt_tag_0bis2std("dauer_hauptakt_tag_0bis2std") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.calculatedurationofmainactivitiesonday() >= 0*60 && day.calculatedurationofmainactivitiesonday() < 2*60) ? 1.0 : 0.0);
		}
	},
	dauer_hauptakt_tag_2bis4std("dauer_hauptakt_tag_2bis4std") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.calculatedurationofmainactivitiesonday() >= 2*60 && day.calculatedurationofmainactivitiesonday() < 4*60) ? 1.0 : 0.0);
		}
	},
	dauer_hauptakt_tag_4bis6std("dauer_hauptakt_tag_4bis6std") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.calculatedurationofmainactivitiesonday() >= 4*60 && day.calculatedurationofmainactivitiesonday() < 6*60) ? 1.0 : 0.0);
		}
	},
	dauer_hauptakt_tag_6bis8std("dauer_hauptakt_tag_6bis8std") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.calculatedurationofmainactivitiesonday() >= 6*60 && day.calculatedurationofmainactivitiesonday() < 8*60) ? 1.0 : 0.0);
		}
	},
	dauer_hauptakt_tag_8bis10std("dauer_hauptakt_tag_8bis10std") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.calculatedurationofmainactivitiesonday() >= 8*60 && day.calculatedurationofmainactivitiesonday() < 10*60) ? 1.0 : 0.0);
		}
	},
	dauer_hauptakt_tag_10bis12std("dauer_hauptakt_tag_10bis12std") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.calculatedurationofmainactivitiesonday() >= 10*60 && day.calculatedurationofmainactivitiesonday() < 12*60) ? 1.0 : 0.0);
		}
	},
	dauer_hauptakt_tag_12bis14std("dauer_hauptakt_tag_12bis14std") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.calculatedurationofmainactivitiesonday() >= 12*60 && day.calculatedurationofmainactivitiesonday() < 14*60) ? 1.0 : 0.0);
		}
	},
	dauer_hauptakt_tag_ueber14std("dauer_hauptakt_tag_ueber14std") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.calculatedurationofmainactivitiesonday() >= 14*60) ? 1.0 : 0.0);
		}
	},
	
	
	
	/*
	 * Dauer alle Akt Tag
	 */
	dauer_akt_tag_4bis6std("dauer_akt_tag_4bis6std") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getTotalAmountOfActivityTime() >= 4*60 && day.getTotalAmountOfActivityTime() < 6*60) ? 1.0 : 0.0);
		}
	},
	dauer_akt_tag_6bis8std("dauer_akt_tag_6bis8std") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getTotalAmountOfActivityTime() >= 6*60 && day.getTotalAmountOfActivityTime() < 8*60) ? 1.0 : 0.0);
		}
	},
	dauer_akt_tag_8bis10std("dauer_akt_tag_8bis10std") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getTotalAmountOfActivityTime() >= 8*60 && day.getTotalAmountOfActivityTime() < 10*60) ? 1.0 : 0.0);
		}
	},
	dauer_akt_tag_10bis12std("dauer_akt_tag_10bis12std") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getTotalAmountOfActivityTime() >= 10*60 && day.getTotalAmountOfActivityTime() < 12*60) ? 1.0 : 0.0);
		}
	},
	dauer_akt_tag_12bis14std("dauer_akt_tag_12bis14std") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getTotalAmountOfActivityTime() >= 12*60 && day.getTotalAmountOfActivityTime() < 14*60) ? 1.0 : 0.0);
		}
	},	
	
	/*
	 * Dauer Akt vor Haupttour
	 */
	dauer_akt_vorht_tag_1bis120("dauer_akt_vorht_tag_1bis120") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getTotalAmountOfActivityTimeUntilMainTour(day.getFirstTourOfDay()) >= 1 && day.getTotalAmountOfActivityTimeUntilMainTour(day.getFirstTourOfDay()) < 120) ? 1.0 : 0.0);
		}
	},		
	dauer_akt_vorht_tag_121bis240("dauer_akt_vorht_tag_121bis240") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((day.getTotalAmountOfActivityTimeUntilMainTour(day.getFirstTourOfDay()) >= 121 && day.getTotalAmountOfActivityTimeUntilMainTour(day.getFirstTourOfDay()) < 240) ? 1.0 : 0.0);
		}
	},		
	
	/*
	 * Dauer Akt ab Haupttour
	 */
	dauer_akt_abht_tag_600bis659("dauer_akt_abht_tag_600bis659") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((((day.getTotalAmountOfRemainingActivityTime(day.getTour(0))) >= 600 && day.getTotalAmountOfRemainingActivityTime(day.getTour(0)) < 659)) ? 1.0 : 0.0);
		}
	},		
	dauer_akt_abht_tag_660bis779("dauer_akt_abht_tag_660bis779") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((((day.getTotalAmountOfRemainingActivityTime(day.getTour(0))) >= 660 && day.getTotalAmountOfRemainingActivityTime(day.getTour(0)) < 679)) ? 1.0 : 0.0);
		}
	},		
	dauer_akt_abht_tag_780bis899("dauer_akt_abht_tag_780bis899") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return ((((day.getTotalAmountOfRemainingActivityTime(day.getTour(0))) >= 780 && day.getTotalAmountOfRemainingActivityTime(day.getTour(0)) < 899)) ? 1.0 : 0.0);
		}
	},		
	
	/*
	 * AnzTouren Vor/Nach Haupttour
	 */
	anztourenvorhaupttour("anztourenvorhaupttour") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return (-1) * day.getLowestTourIndex();
		}
	},			
	anztourennachhaupttour("anztourennachhaupttour") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return (+1) * day.getHighestTourIndex();
		}
	},	

	/*
	 * AnzahlTouren am Tag
	 */
	anztourenamtag("anztourenamtag") 
	{
		@Override
		public double getAttribute(HDay day) 
		{
			return day.getAmountOfTours();
		}
	},	
	
	
	;
		
	private final String name;

	/**
	 * privater Konstruktor
	 * 
	 * @param name
	 */
	private HDayParameters(String name)
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
	public static HDayParameters getEnumValue(String name)
	{
		// Eindeutigkeitsprüfung
		checkUniqueness(name);
		
		// Rückgabe des passenden Enums
		for (HDayParameters parameter : values())
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
		for (HDayParameters parameter : values())
		{
			if (parameter.name.equals(name))
			{
				counter++;
				if (counter>1) throw new IllegalArgumentException(name + " identifier is not unique - wrong enum specification");
			}
		}
	}

	public abstract double getAttribute(HDay day);
	
	
}
