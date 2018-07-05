package edu.kit.ifv.mobitopp.actitopp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 
 * @author Tim Hilgert
 *
 */
public class ActitoppPerson
{
	
	//zugehöriger Haushalt
	private ActiToppHousehold household;
	
	// Enthält alle Attribute, die nicht direkt über Variablen ausgelesen werden können
	private Map<String, Double> attributes;
	
	private HWeekPattern weekPattern;

	private int PersIndex;
	
	private int age;
	private int gender;
	private int employment;
	
	// Pendeldistanzen sind als Default = 0, das heißt nicht verfügbar oder Person pendelt nicht
	private double commutingdistance_work = 0.0;
	private double commutingdistance_education = 0.0;
	
	// Variablen für gemeinsame Aktivitäten

	// basiert auf Regressionsmodell und dient der Feestlegung der Modellierungsreihenfolge der Personen im HH
	private double probableshareofjointactions=-1;
	// Liste zu berücksichtigender gemeinsamer Wege/Aktivitäten der Person im HH
	private List<HActivity> jointActivitiesforConsideration;

	
	
	/**
	 * Konstruktor zum Erstellen einer Person
	 * 
	 * @param PersIndex
	 * @param children0_10
	 * @param children_u18
	 * @param age
	 * @param employment
	 * @param gender
	 * @param areatype
	 * @param numberofcarsinhousehold
	 */
	public ActitoppPerson(
			int PersIndex,
			int children0_10,
			int children_u18,
			int age,
			int employment,
			int gender,
			int areatype,
			int numberofcarsinhousehold
	) {
		
		/*
		 * Zur Abwärtskompatibilität können Personen nach wie vor ohne Haushaltsbezug erstellt
		 * werden. Es wird intern allerdings ein Haushaltsobjekt erzeugt, um den Variablenzugriff
		 * auf Haushaltsvariablen einheitlich nach außen einheitlich zu gestalten.
		 */
		this.household = new ActiToppHousehold(
				PersIndex, 
				children0_10, 
				children_u18, 
				areatype, 
				numberofcarsinhousehold
		);
				
		this.setPersIndex(PersIndex);
		this.setAge(age);
		this.setEmployment(employment);
		this.setGender(gender);  
		
		this.attributes = new HashMap<String, Double>();
		this.jointActivitiesforConsideration = new ArrayList<HActivity>();
		
		}
	
	
	/**
	 * Konstruktor zum Erstellen einer Person - mit Pendelentfernung
	 * 
	 * @param PersIndex
	 * @param children0_10
	 * @param children_u18
	 * @param age
	 * @param employment
	 * @param gender
	 * @param areatype
	 * @param numberofcarsinhousehold
	 * @param commutingdistance_work
	 * @param commutingdistance_education
	 */
	public ActitoppPerson(
			int PersIndex,
			int children0_10,
			int children_u18,
			int age,
			int employment,
			int gender,
			int areatype,
			int numberofcarsinhousehold,
			double commutingdistance_work,
			double commutingdistance_education
	) {
		
		this(PersIndex,children0_10,children_u18,age,employment,gender,areatype,numberofcarsinhousehold);
		
		this.setCommutingdistance_work(commutingdistance_work);
		this.setCommutingdistance_education(commutingdistance_education);
		}	
	
	/**
	 * Konstruktor zum Erstellen einer Person mit Haushaltskontext
	 * 
	 * @param household
	 * @param PersIndex
	 * @param age
	 * @param employment
	 * @param gender
	 */
	public ActitoppPerson(
			ActiToppHousehold household,
			int PersIndex,
			int age,
			int employment,
			int gender
	) {

		this.household = household;
		
		this.setPersIndex(PersIndex);
		this.setAge(age);
		this.setEmployment(employment);
		this.setGender(gender);
		
		this.attributes = new HashMap<String, Double>();
		this.jointActivitiesforConsideration = new ArrayList<HActivity>();
		
		}	

	
  /**
	 * Konstruktor zum Erstellen einer Person mit Haushaltskontext & Pendelentfernung
	 * 
	 * @param household
	 * @param PersIndex
	 * @param age
	 * @param employment
	 * @param gender
	 * @param commutingdistance_work
	 * @param commutingdistance_education
	 */
	public ActitoppPerson(
			ActiToppHousehold household,
			int PersIndex,
			int age,
			int employment,
			int gender,
			double commutingdistance_work,
			double commutingdistance_education
	) {
		
		this(household,PersIndex,age,employment,gender);
		
		this.setCommutingdistance_work(commutingdistance_work);
		this.setCommutingdistance_education(commutingdistance_education);
	
		}


	/**
	 * @return the weekPattern
	 */
	public HWeekPattern getWeekPattern() {
		return weekPattern;
	}


	/**
	 * @return the persIndex
	 */
	public int getPersIndex() {
		return PersIndex;
	}


	/**
	 * @param persIndex the persIndex to set
	 */
	public void setPersIndex(int persIndex) {
		PersIndex = persIndex;
	}


	/**
	 * @return the household
	 */
	public ActiToppHousehold getHousehold() {
		return household;
	}

	/**
	 * 
	 * @return the personnr in the household
	 */
	public int getPersNrinHousehold() {
		
		int result=-1;
		Map<Integer, ActitoppPerson> tmpmap = this.getHousehold().getHouseholdmembers();
		
		for (Map.Entry<Integer, ActitoppPerson> tmpmapentry: tmpmap.entrySet()) {
			if (tmpmapentry.getValue().equals(this)) result = tmpmapentry.getKey();
		}
		
		assert result!=-1: "Person does not exist in this household or has no PersNr in this household";
		return result;
	}
	
	
	/**
	 * @return the age
	 */
	public int getAge() {
		return age;
	}


	/**
	 * @param age the age to set
	 */
	public void setAge(int age) {
		this.age = age;
	}


	/**
	 * @return the gender
	 */
	public int getGender() {
		return gender;
	}


	/**
	 * @param gender the gender to set
	 */
	public void setGender(int gender) {
		this.gender = gender;
	}


	/**
	 * @return the employment
	 */
	public int getEmployment() {
		return employment;
	}


	/**
	 * @param employment the employment to set
	 */
	public void setEmployment(int employment) {
		this.employment = employment;
	}


	/**
	 * @return the children0_10
	 */
	public int getChildren0_10() {
		return getHousehold().getChildren0_10();
	}

	/**
	 * @return the children_u18
	 */
	public int getChildren_u18() {
		return getHousehold().getChildren_u18();
	}


	/**
	 * @return the areatype
	 */
	public int getAreatype() {
		return getHousehold().getAreatype();
	}


	/**
	 * @return the numberofcarsinhousehold
	 */
	public int getNumberofcarsinhousehold() {
		return getHousehold().getNumberofcarsinhousehold();
	}

	
	
	/**
	 * @return the commutingdistance_work
	 */
	public double getCommutingdistance_work() {
		return commutingdistance_work;
	}


	/**
	 * @param commutingdistance_work the commutingdistance_work to set
	 */
	public void setCommutingdistance_work(double commutingdistance_work) {
		this.commutingdistance_work = commutingdistance_work;
	}


	/**
	 * @return the commutingdistance_education
	 */
	public double getCommutingdistance_education() {
		return commutingdistance_education;
	}


	/**
	 * @param commutingdistance_education the commutingdistance_education to set
	 */
	public void setCommutingdistance_education(double commutingdistance_education) {
		this.commutingdistance_education = commutingdistance_education;
	}
	
	/**
	 * 
	 * @return the commutingduration_work [min]
	 */
	public int getCommutingDuration_work()
	{
		// mittlere Pendelgeschwindigkeit in km/h wird je gruppierter Pendelentfernung festgelegt
		// die mittleren Geschwindigkeiten wurden über alle Pendelwege des MOP 2004-2013 ermittelt
		double commutingspeed_work;
		if 			(commutingdistance_work>0  && commutingdistance_work <= 5)  commutingspeed_work = 16;
		else if (commutingdistance_work>5  && commutingdistance_work <= 10) commutingspeed_work = 29;
		else if (commutingdistance_work>10 && commutingdistance_work <= 20) commutingspeed_work = 38;
		else if (commutingdistance_work>20 && commutingdistance_work <= 50) commutingspeed_work = 51;
		else if (commutingdistance_work>50) 																commutingspeed_work = 67;
		else																																commutingspeed_work = 32;
		
		// Mindestdauer jedes Wegs: 1 Minute
		return (int) Math.max(1, Math.round((commutingdistance_work/commutingspeed_work)*60));
	}
	
	
	/**
	 * 
	 * @return the commutingduration_education [min]
	 */
	public int getCommutingDuration_education()
	{
		// mittlere Pendelgeschwindigkeit in km/h wird je gruppierter Pendelentfernung festgelegt
		// die mittleren Geschwindigkeiten wurden über alle Pendelwege des MOP 2004-2013 ermittelt
		double commutingspeed_education;
		if 			(commutingdistance_education>0  && commutingdistance_education <= 5)  commutingspeed_education = 12;
		else if (commutingdistance_education>5  && commutingdistance_education <= 10) commutingspeed_education = 21;
		else if (commutingdistance_education>10 && commutingdistance_education <= 20) commutingspeed_education = 28;
		else if (commutingdistance_education>20 && commutingdistance_education <= 50) commutingspeed_education = 40;
		else if (commutingdistance_education>50) 																			commutingspeed_education = 55;
		else																																					commutingspeed_education = 21;
		
		// Mindestdauer jedes Wegs: 1 Minute
		return (int) Math.max(1, Math.round((commutingdistance_education/commutingspeed_education)*60));
	}
	

	/**
	 * @param attributes spezifischesAttribut für Map
	 */
	public void addAttributetoMap(String name, Double value) {
		this.attributes.put(name, value);
	}
	
	/**
	 * 
	 * @param name spezifisches Attribut aus Map
	 * @return
	 */
	public double getAttributefromMap(String name) {
		return this.attributes.get(name);
	}


	/**
	 * @return the attributes
	 */
	public Map<String, Double> getAttributesMap() {
		return attributes;
	}
	
	/**
	 * Entfernt alle gespeicherten Attribute aus der Map
	 */
	public void clearAttributesMap() {
		attributes.clear();
	}
	
	/**
	 * Entfernt das gespeicherte WeekPattern von der Person
	 */
	public void clearWeekPattern() {
		weekPattern = null;
	}

	/**
	 * Entfernt alle gemeinsamen Aktivitäten anderer Haushaltsmitglieder aus der Liste  
	 */
	public void clearJointActivitiesforConsideration()
	{
		jointActivitiesforConsideration.clear();
	}

	/**
	 * @return the probableshareofjointactions
	 */
	public double getProbableshareofjointactions() {
		return probableshareofjointactions;
	}

	public void calculateProbableshareofjointactions(ModelFileBase fileBase) {
		
		// AttributeLookup erzeugen
		AttributeLookup lookup = new AttributeLookup(this);
		
		// Modellobjekt erzeugen
		DefaultLinearRegressionCalculation regression = new DefaultLinearRegressionCalculation("gemwegakt_anteil_reg_estimates", fileBase, lookup);
		
		// Initialisierung und Regressionskalkulation
		regression.initializeEstimates();				
		this.probableshareofjointactions = regression.calculateRegression();

	}


	@Override
	public String toString()	{
  	StringBuffer message = new StringBuffer();

  	message.append("\n Personeninformationen");
  	
		message.append("\n - PersIndex : ");
		message.append(PersIndex);
				
		message.append("\n - Alter : ");
		message.append(getAge());

		message.append("\n - Beruf : ");
		message.append(getEmployment());
		
		message.append("\n - Geschlecht : ");
		message.append(getGender());
		
		message.append("\n - Pendeldistanz Arbeiten : ");
		message.append(getCommutingdistance_work());		
		
		message.append("\n - Pendeldistanz Bildung : ");
		message.append(getCommutingdistance_education());		
		
		message.append("\n - Haushaltsebene ");
		message.append(getHousehold());
		
		return message.toString();
	}
	
	/**
   * Sortiert eine Liste mit Personen absteigend nach deren wahrscheinlichem Anteil an gemeinsamen Aktivitäten
   * 
   * @param personListe
   */
  public static void sortPersonListOnProbabilityofJointActions_DESC(List<ActitoppPerson> personList, ModelFileBase fileBase)
  {
  	assert personList != null : "Liste zum Sortieren ist leer";
  	
  	for (ActitoppPerson tmpperson : personList)
  	{
  		tmpperson.calculateProbableshareofjointactions(fileBase);
  	}
  	
      Collections.sort(personList, new Comparator<ActitoppPerson>()
      {
        @Override
        public int compare(ActitoppPerson person1, ActitoppPerson person2)
        {   
          if(person1.getProbableshareofjointactions() < person2.getProbableshareofjointactions())
          {
            return +1;
          }
          else if(person1.getProbableshareofjointactions() == person2.getProbableshareofjointactions())
          {
          	return 0;
          }
          else
          {
          	return -1;
          }
        }
      }
      );
  }
	
	/**
	 * Methode erzeugt Wochenaktivitätenplan für Person
	 * 
	 * @param modelbase
	 * @throws InvalidPersonPatternException
	 * @throws PrerequisiteNotMetException
	 */
	public void generateSchedule(ModelFileBase modelbase, RNGHelper randomgenerator)	throws InvalidPersonPatternException, InvalidHouseholdPatternException
	{		
		// Erzeuge ein leeres Default-Pattern
		weekPattern = new HWeekPattern(this);
		
		// Erzeuge einen Coordinator zum Modellablauf
		Coordinator modelCoordinator = new Coordinator(this, modelbase, randomgenerator);
	
		// Erzeuge den Schedule
		try 
		{
			modelCoordinator.executeModel();
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
  /**
   * 
   * @return
   */
  public List<HActivity> getAllJointActivitiesforConsideration()
  {
  	return jointActivitiesforConsideration;
  }
  
	/**
	 * Fügt die Aktivität der Liste gemeinsamer Aktivitäten hinzu falls kein Konflikt vorliegt
	 * 
	 * @param act
	 */
	public void addJointActivityforConsideration(HActivity act){
		// Prüfe, ob es bereits eine Aktivität in dem Zeitraum oder in der Tour gibt
		boolean activityconflict = false;
		for (HActivity tmpact : jointActivitiesforConsideration)
		{
			if(
					(	tmpact.getWeekDay()					==	act.getWeekDay() && 
						tmpact.getTour().getIndex() == 	act.getTour().getIndex() &&
						tmpact.getIndex()						== 	act.getIndex()
					) 
					||
					(
						act.checkOverlappingtoOtherActivity(tmpact)
					)
				)
			{
				activityconflict = true;
				System.err.println("Person " + getPersIndex() + ": Aktivität wurde wegen Konflikt mit bereits existierender Aktivität nicht als gemeinsame Aktivität aufgenommen!");
				System.err.println("aufzunehmende Akt: " + act);
				System.err.println("existierende Akt: " + tmpact);
				break;
			}
		}

		if (!activityconflict) 
		{
			jointActivitiesforConsideration.add(act);
		}
	}
	
	
	
	/**
	 * 
	 * Methode zum Überprüfen der Vorbedingung von Schritt 9
	 * 
	 * @return
	 */
	public boolean isPersonWorkerAndWorkMainToursAreScheduled()
	{   
		int employmenttype = this.getEmployment();
    if (employmenttype == 1 || employmenttype == 2 || employmenttype == 40 || employmenttype == 41 || employmenttype == 42 || employmenttype == 5)
    {
      for (HDay day : getWeekPattern().getDays())
      {
        for (HTour tour : day.getTours())
        {
          if (tour.getActivity(0).getType() == 'W' || tour.getActivity(0).getType() == 'E')
        	{
          	return true;
          }
        }
      }
    }
    else
    {
        return false;
    }
    return false;
	}



	
}
