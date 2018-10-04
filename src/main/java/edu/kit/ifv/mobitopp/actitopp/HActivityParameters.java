package edu.kit.ifv.mobitopp.actitopp;
/**
 * 
 * @author Tim Hilgert
 *
 */
public enum HActivityParameters {

	/*
	 * Aktivit�tenzweck
	 */
	aktzweck_work("aktzweck_work") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getActivityType() == ActivityType.WORK) ? 1.0 : 0.0);
		}
	},
	aktzweck_education("aktzweck_education") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getActivityType() == ActivityType.EDUCATION) ? 1.0 : 0.0);
		}
	},
	aktzweck_leisure("aktzweck_leisure") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getActivityType() == ActivityType.LEISURE) ? 1.0 : 0.0);
		}
	},
	aktzweck_shopping("aktzweck_shopping") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getActivityType() == ActivityType.SHOPPING) ? 1.0 : 0.0);
		}
	},	
	aktzweck_transport("aktzweck_transport") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getActivityType() == ActivityType.TRANSPORT) ? 1.0 : 0.0);
		}
	},
	

	/*
	 * Dauer Aktivit�t
	 */
	dauer_akt("dauer_akt") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return act.getDuration();
		}
	},	
	dauer_akt_1bis14("dauer_akt_1bis14") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getDuration() >= 1 && act.getDuration() <= 14) ? 1.0 : 0.0);
		}
	},
	dauer_akt_15bis29("dauer_akt_15bis29") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getDuration() >= 15 && act.getDuration() <= 29) ? 1.0 : 0.0);
		}
	},
	dauer_akt_30bis59("dauer_akt_30bis59") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getDuration() >= 30 && act.getDuration() <= 59) ? 1.0 : 0.0);
		}
	},
	dauer_akt_60bis119("dauer_akt_60bis119") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getDuration() >= 60 && act.getDuration() <= 119) ? 1.0 : 0.0);
		}
	},
	dauer_akt_120bis179("dauer_akt_120bis179") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getDuration() >= 120 && act.getDuration() <= 179) ? 1.0 : 0.0);
		}
	},
	dauer_akt_180bis239("dauer_akt_180bis239") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getDuration() >= 180 && act.getDuration() <= 239) ? 1.0 : 0.0);
		}
	},
	dauer_akt_240bis299("dauer_akt_240bis299") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getDuration() >= 240 && act.getDuration() <= 299) ? 1.0 : 0.0);
		}
	},
	dauer_akt_300bis359("dauer_akt_300bis359") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getDuration() >= 300 && act.getDuration() <= 359) ? 1.0 : 0.0);
		}
	},
	dauer_akt_360bis419("dauer_akt_360bis419") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getDuration() >= 360 && act.getDuration() <= 419) ? 1.0 : 0.0);
		}
	},
	dauer_akt_420bis479("dauer_akt_420bis479") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getDuration() >= 420 && act.getDuration() <= 479) ? 1.0 : 0.0);
		}
	},
	dauer_akt_480bis539("dauer_akt_480bis539") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getDuration() >= 480 && act.getDuration() <= 539) ? 1.0 : 0.0);
		}
	},
	dauer_akt_540bis599("dauer_akt_540bis599") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getDuration() >= 540 && act.getDuration() <= 599) ? 1.0 : 0.0);
		}
	},
	dauer_akt_600bis659("dauer_akt_600bis659") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getDuration() >= 600 && act.getDuration() <= 659) ? 1.0 : 0.0);
		}
	},
	dauer_akt_660bis719("dauer_akt_660bis719") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getDuration() >= 660 && act.getDuration() <= 719) ? 1.0 : 0.0);
		}
	},
	dauer_akt_720bis1440("dauer_akt_720bis1440") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getDuration() >= 720 && act.getDuration() <= 1440) ? 1.0 : 0.0);
		}
	},
	
	/*
	 * Erste Aktivit�t Tag/Tour
	 */
	ersteaktamtag("ersteaktamtag") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return (((act.getIndex() == act.getTour().getLowestActivityIndex()) && (act.getTour().getIndex() == act.getDay().getLowestTourIndex())) ? 1.0 : 0.0);
		}
	},
	ersteaktintour("ersteaktintour") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getIndex() == act.getTour().getLowestActivityIndex()) ? 1.0 : 0.0);
		}
	},
	
	
	/*
	 * mean Time Eigenschaft
	 */
	mittl_zeit_akt_1bis14min("mittl_zeit_akt_1bis14min") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.calculateMeanTime() >= 1 && act.calculateMeanTime() <= 14) ? 1.0 : 0.0);
		}
	},
	mittl_zeit_akt_15bis29min("mittl_zeit_akt_15bis29min") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.calculateMeanTime() >= 15 && act.calculateMeanTime() <= 29) ? 1.0 : 0.0);
		}
	},
	mittl_zeit_akt_30bis59min("mittl_zeit_akt_30bis59min") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.calculateMeanTime() >= 30 && act.calculateMeanTime() <= 59) ? 1.0 : 0.0);
		}
	},
	mittl_zeit_akt_60bis119min("mittl_zeit_akt_60bis119min") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.calculateMeanTime() >= 60 && act.calculateMeanTime() <= 119) ? 1.0 : 0.0);
		}
	},
	mittl_zeit_akt_120bis179min("mittl_zeit_akt_120bis179min") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.calculateMeanTime() >= 120 && act.calculateMeanTime() <= 179) ? 1.0 : 0.0);
		}
	},
	mittl_zeit_akt_180bis239min("mittl_zeit_akt_180bis239min") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.calculateMeanTime() >= 180 && act.calculateMeanTime() <= 239) ? 1.0 : 0.0);
		}
	},
	mittl_zeit_akt_240bis299min("mittl_zeit_akt_240bis299min") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.calculateMeanTime() >= 240 && act.calculateMeanTime() <= 299) ? 1.0 : 0.0);
		}
	},
	mittl_zeit_akt_300bis359min("mittl_zeit_akt_300bis359min") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.calculateMeanTime() >= 300 && act.calculateMeanTime() <= 359) ? 1.0 : 0.0);
		}
	},
	mittl_zeit_akt_360bis419min("mittl_zeit_akt_360bis419min") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.calculateMeanTime() >= 360 && act.calculateMeanTime() <= 419) ? 1.0 : 0.0);
		}
	},
	mittl_zeit_akt_420bis479min("mittl_zeit_akt_420bis479min") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.calculateMeanTime() >= 420 && act.calculateMeanTime() <= 479) ? 1.0 : 0.0);
		}
	},
	mittl_zeit_akt_480bis539min("mittl_zeit_akt_480bis539min") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.calculateMeanTime() >= 480 && act.calculateMeanTime() <= 539) ? 1.0 : 0.0);
		}
	},
	mittl_zeit_akt_540bis599min("mittl_zeit_akt_540bis599min") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.calculateMeanTime() >= 540 && act.calculateMeanTime() <= 599) ? 1.0 : 0.0);
		}
	},
	mittl_zeit_akt_600bis659min("mittl_zeit_akt_600bis659min") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.calculateMeanTime() >= 600 && act.calculateMeanTime() <= 659) ? 1.0 : 0.0);
		}
	},
	mittl_zeit_akt_660bis719min("mittl_zeit_akt_660bis719min") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.calculateMeanTime() >= 660 && act.calculateMeanTime() <= 719) ? 1.0 : 0.0);
		}
	},
	mittl_zeit_akt_720bis1440min("mittl_zeit_akt_720bis1440min") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.calculateMeanTime() >= 720 && act.calculateMeanTime() <= 1440) ? 1.0 : 0.0);
		}
	},
	
	/*
	 * Anzahl Aktivit�ten = Anzahl Tage mit diesem Zweck
	 */
	anzaktwieanztagemitzweck("anzaktwieanztagemitzweck") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return (act.getWeekPattern().countActivitiesPerWeek(act.getActivityType()) == act.getWeekPattern().countDaysWithSpecificActivity(act.getActivityType()) ? 1.0 : 0.0);
		}
	},
	
	/*
	 * Wochenzeitbudget mit Zweck
	 */
	wochenzbudget_zweck_kat1("wochenzbudget_zweck_kat1") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getPerson().getAttributefromMap(act.getActivityType()+"budget_category_alternative") == 1) ? 1.0 : 0.0);
		}
	},
	wochenzbudget_zweck_kat2("wochenzbudget_zweck_kat2") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getPerson().getAttributefromMap(act.getActivityType()+"budget_category_alternative") == 2) ? 1.0 : 0.0);
		}
	},
	wochenzbudget_zweck_kat3("wochenzbudget_zweck_kat3") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getPerson().getAttributefromMap(act.getActivityType()+"budget_category_alternative") == 3) ? 1.0 : 0.0);
		}
	},
	wochenzbudget_zweck_kat4("wochenzbudget_zweck_kat4") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getPerson().getAttributefromMap(act.getActivityType()+"budget_category_alternative") == 4) ? 1.0 : 0.0);
		}
	},
	wochenzbudget_zweck_kat5("wochenzbudget_zweck_kat5") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getPerson().getAttributefromMap(act.getActivityType()+"budget_category_alternative") == 5) ? 1.0 : 0.0);
		}
	},	
	
	/*
	 * Akt liegt Vor/Nach Hauptakt
	 */
	aktisthauptakt("aktisthauptakt") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getIndex() == 0) ? 1.0 : 0.0);
		}
	},
	aktliegtvorhauptakt("aktliegtvorhauptakt") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getIndex() < 0) ? 1.0 : 0.0);
		}
	},
	aktliegtnachhauptakt("aktliegtnachhauptakt") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			return ((act.getIndex() > 0) ? 1.0 : 0.0);
		}
	},
	
	/*
	 * Akt Startzeit
	 */
	start_stunde_akt_0_5("start_stunde_akt_0_5") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			int startzeit_stunde_akt = act.getStartTime()/60;
			return ((startzeit_stunde_akt>=0 && startzeit_stunde_akt<=5) ? 1.0 : 0.0);
		}
	},	
	start_stunde_akt_6_8("start_stunde_akt_6_8") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			int startzeit_stunde_akt = act.getStartTime()/60;
			return ((startzeit_stunde_akt>=6 && startzeit_stunde_akt<=8) ? 1.0 : 0.0);
		}
	},	
	start_stunde_akt_10_12("start_stunde_akt_10_12") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			int startzeit_stunde_akt = act.getStartTime()/60;
			return ((startzeit_stunde_akt>=10 && startzeit_stunde_akt<=12) ? 1.0 : 0.0);
		}
	},	
	start_stunde_akt_13_15("start_stunde_akt_13_15") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			int startzeit_stunde_akt = act.getStartTime()/60;
			return ((startzeit_stunde_akt>=13 && startzeit_stunde_akt<=15) ? 1.0 : 0.0);
		}
	},	
	start_stunde_akt_16_18("start_stunde_akt_16_18") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			int startzeit_stunde_akt = act.getStartTime()/60;
			return ((startzeit_stunde_akt>=16 && startzeit_stunde_akt<=18) ? 1.0 : 0.0);
		}
	},
	start_stunde_akt_19_21("start_stunde_akt_19_21") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			int startzeit_stunde_akt = act.getStartTime()/60;
			return ((startzeit_stunde_akt>=19 && startzeit_stunde_akt<=21) ? 1.0 : 0.0);
		}
	},	
	start_stunde_akt_22_23("start_stunde_akt_22_23") 
	{
		@Override
		public double getAttribute(HActivity act) 
		{
			int startzeit_stunde_akt = act.getStartTime()/60;
			return ((startzeit_stunde_akt>=22 && startzeit_stunde_akt<=23) ? 1.0 : 0.0);
		}
	},	
	
	;
		
	private final String name;

	/**
	 * privater Konstruktor
	 * 
	 * @param name
	 */
	private HActivityParameters(String name)
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
	public static HActivityParameters getEnumValue(String name)
	{
		// Eindeutigkeitspr�fung
		checkUniqueness(name);
		
		// R�ckgabe des passenden Enums
		for (HActivityParameters parameter : values())
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
			for (HActivityParameters parameter : values())
			{
				if (parameter.name.equals(name))
				{
					counter++;
					if (counter>1) throw new IllegalArgumentException(name + " identifier is not unique - wrong enum specification");
				}
			}
	}

	public abstract double getAttribute(HActivity act);
	
	
}
