package edu.kit.ifv.mobitopp.actitopp.demo;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import edu.kit.ifv.mobitopp.actitopp.*;


public class CSVExportWriter {

    FileWriter writer;

    /**
     * Konstruktor
     *
     * @param filename
     * @throws IOException
     */
    public CSVExportWriter(String filename) throws IOException {
        writer = new FileWriter(filename);
    }


    /**
     * export trip data of all persons
     * map can consists of persons or households
     *
     * @param maptoexport
     * @throws IOException
     */
    public void exportTripData(HashMap<Integer, ?> maptoexport) throws IOException {
        exportTripData_header();

        for (Object referenceobject : maptoexport.values()) {

            // Householdmap
            if (referenceobject instanceof ActiToppHousehold) {
                ActiToppHousehold acthousehold = ((ActiToppHousehold) referenceobject);
                for (ActitoppPerson actperson : acthousehold.getHouseholdmembersasList()) {
                    exportTripData_singlePerson(actperson);
                }
            }

            // Personmap
            if (referenceobject instanceof ActitoppPerson) {
                ActitoppPerson actperson = ((ActitoppPerson) referenceobject);
                exportTripData_singlePerson(actperson);
            }
        }
        writer.close();
    }


    private void exportTripData_header() throws IOException {
        // Header
        writer.append("HHIndex;PersNr;PersIndex;WOTAG;anzeit;anzeit_woche;abzeit;abzeit_woche;Dauer;zweck_text;jointStatus");
        writer.append('\n');
        writer.flush();
    }


    private void exportTripData_singlePerson(ActitoppPerson actperson) throws IOException {
        // read trip information from activity schedule
        for (HTrip trip : actperson.getWeekPattern().getAllTrips()) {
            if (trip.getDuration() > 0) {
                writer.append(writeTrip(trip));
                writer.flush();
            }

        }
    }

    /**
     * create output trip information
     *
     * @param act
     * @return
     */
    private String writeTrip(HTrip trip) {
        assert trip.isScheduled() : "Trip is not scheduled!";
        String rueckgabe = "";

        // HHIndex
        rueckgabe += trip.activity.getPerson().getHousehold().getHouseholdIndex() + ";";
        // PersNr
        rueckgabe += trip.activity.getPerson().getPersNrinHousehold() + ";";
        // PersIndex
        rueckgabe += trip.activity.getPerson().getPersIndex() + ";";
        // Weekday
        rueckgabe += trip.activity.getWeekDay() + ";";
        // anzeit (endtime)
        rueckgabe += trip.getEndTime() + ";";
        // anzeit_woche (endtime_week)
        rueckgabe += trip.getEndTimeWeekContext() + ";";
        // abzeit (starttime)
        rueckgabe += trip.getStartTime() + ";";
        // abzeit_woche (starttime_week)
        rueckgabe += trip.getStartTimeWeekContext() + ";";
        // duration
        rueckgabe += trip.getDuration() + ";";
        // type of trip
        rueckgabe += trip.getType().getTypeasChar() + ";";
        // jointStatus
        rueckgabe += (Configuration.INSTANCE.getModel_joint_actions() ? trip.getJointStatus() : "-99") + "";

        rueckgabe += "\n";
        return rueckgabe;
    }


    /**
     * export activity data of all persons
     * map can consists of persons or households
     *
     * @param maptoexport
     * @throws IOException
     */
    public void exportActivityData(HashMap<Integer, ?> maptoexport) throws IOException {

        exportActivityData_header();

        for (Object referenceobject : maptoexport.values()) {

            // Householdmap
            if (referenceobject instanceof ActiToppHousehold) {
                ActiToppHousehold acthousehold = ((ActiToppHousehold) referenceobject);
                for (ActitoppPerson actperson : acthousehold.getHouseholdmembersasList()) {
                    exportActivityData_singlePerson(actperson);
                }
            }

            // Personmap
            if (referenceobject instanceof ActitoppPerson) {
                ActitoppPerson actperson = ((ActitoppPerson) referenceobject);
                exportActivityData_singlePerson(actperson);
            }
        }
        writer.close();
    }


    private void exportActivityData_header() throws IOException {
        // Header
        writer.append("HHIndex;PersNr;PersIndex;WOTAG;TourIndex;AktIndex;startzeit;startzeit_woche;endzeit;endzeit_woche;Dauer;zweck;jointStatus");
        writer.append('\n');
        writer.flush();
    }


    private void exportActivityData_singlePerson(ActitoppPerson actperson) throws IOException {
        // Fuege alle Aktivitaeten hinzu
        for (HActivity act : actperson.getWeekPattern().getAllActivities()) {
            if (act.isScheduled()) {
                writer.append(writeActivity(act));
                writer.flush();
            }
        }
    }


    /**
     * Schreibe Zeile mit Aktivitaeteninfos
     *
     * @param act
     * @return
     */
    private String writeActivity(HActivity act) {
        assert act.isScheduled() : "Activity is not fully scheduled";
        String rueckgabe = "";

        /*
         * TourINdex und Aktindex fuer Heimaktivitaeten bestimmen
         * Falls Tag ein kompletter Heimtag ist, dann wird 0/0 zurueckgegeben, ansonsten -99/-99
         */
        int tmptourindex = 0;
        int tmpaktindex = 0;
        if (act.getDay().getAmountOfTours() > 0) {
            tmptourindex = -99;
            tmpaktindex = -99;
        }

        // HHIndex
        rueckgabe += act.getPerson().getHousehold().getHouseholdIndex() + ";";
        // PersNr
        rueckgabe += act.getPerson().getPersNrinHousehold() + ";";
        // PersIndex
        rueckgabe += act.getPerson().getPersIndex() + ";";
        // WOTAG
        rueckgabe += act.getWeekDay() + ";";
        // TourIndex
        rueckgabe += (act.isHomeActivity() ? tmptourindex : act.getTourIndex()) + ";";
        // AktIndex
        rueckgabe += (act.isHomeActivity() ? tmpaktindex : act.getIndex()) + ";";
        // Startzeit
        rueckgabe += act.getStartTime() + ";";
        // Startzeit_woche
        rueckgabe += act.getStartTimeWeekContext() + ";";
        // Endzeit
        rueckgabe += act.getEndTime() + ";";
        // Endzeit_woche
        rueckgabe += act.getEndTimeWeekContext() + ";";
        // Dauer
        rueckgabe += act.getDuration() + ";";
        // Zweck
        rueckgabe += act.getActivityType().getTypeasChar() + ";";
        // joint Status
        rueckgabe += (Configuration.INSTANCE.getModel_joint_actions() ? act.getJointStatus() : "-99") + "";

        rueckgabe += "\n";
        return rueckgabe;
    }

}
