package edu.kit.ifv.mobitopp.actitopp.demo;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import edu.kit.ifv.mobitopp.actitopp.*;

public class ExampleActiTopp {

    private static ModelFileBase fileBase = new ModelFileBase();
    private static RNGHelper randomgenerator = new RNGHelper(1234);
    private static DebugLoggers debugloggers = new DebugLoggers();

    /**
     * @param args
     */
    public static void main(String[] args) {

        createAndModelOnePerson_Example1();

        createAndModelOnePerson_Example2();

        createAndModelOnePerson_Example3();

        createAndModelMultiplePersons_Example1();

        createAndModelMultiplePersons_Example2();

        /*Demonstration of using a Debug-Loggers for decision step 2A*/
        debugloggers.addDebugLogger("2A");

        createAndModelMultiplePersons_Example2();

        debugloggers.exportLoggerInfos("2A", "src/main/resources/DemoLogger2A.csv");

        Configuration.INSTANCE.setParameterset("mopv14_nopkwhh");
        createAndModelOnePerson_Example1_nocarinfo();

    }


    /**
     * Create a person including activity schedule - example 1
     */
    public static void createAndModelOnePerson_Example1() {
        ActitoppPerson testperson = new ActitoppPerson(
                10,    // PersIndex
                0,        // number of children 0-10
                1,        // number of children below 18
                55,    // age
                1,        // employment status
                1,        // gender
                2,        // area of living
                2            // number of cars in household
        );
        System.out.println(testperson);

        /*
         * create schedules for a person until there is a schedule without any mistake
         *
         * there might be overlapping of activities when having a unfavorable stream of random numbers.
         * if this occurs, the generation of activity schedules will be repeated with another
         * stream of random numbers.
         */
        boolean scheduleOK = false;
        while (!scheduleOK) {
            try {
                // create week activity schedule
                testperson.generateSchedule(fileBase, randomgenerator);
                scheduleOK = true;
            } catch (InvalidPatternException e) {
                System.err.println(e.getReason());
                System.err.println("person involved: " + testperson.getPersIndex());
            }
        }
        //testperson.getweekPattern().printOutofHomeActivitiesList();
        testperson.getWeekPattern().printAllActivitiesList();
    }

    public static void createAndModelOnePerson_Example1_nocarinfo() {
        ActitoppPerson testperson = new ActitoppPerson(
                10,    // PersIndex
                0,        // number of children 0-10
                1,        // number of children below 18
                55,    // age
                1,        // employment status
                1,        // gender
                2        // area of living
        );
        System.out.println(testperson);

        /*
         * create schedules for a person until there is a schedule without any mistake
         *
         * there might be overlapping of activities when having a unfavorable stream of random numbers.
         * if this occurs, the generation of activity schedules will be repeated with another
         * stream of random numbers.
         */
        boolean scheduleOK = false;
        while (!scheduleOK) {
            try {
                // create week activity schedule
                testperson.generateSchedule(fileBase, randomgenerator);
                scheduleOK = true;
            } catch (InvalidPatternException e) {
                System.err.println(e.getReason());
                System.err.println("person involved: " + testperson.getPersIndex());
            }
        }
        //testperson.getweekPattern().printOutofHomeActivitiesList();
        testperson.getWeekPattern().printAllActivitiesList();
    }


    /**
     * Create a person including activity schedule - example 2
     */
    public static void createAndModelOnePerson_Example2() {
        ActitoppPerson testperson = new ActitoppPerson(
                20,    // PersIndex
                0,        // number of children 0-10
                1,        // number of children below 18
                55,    // age
                1,        // employment status
                1,        // gender
                2,        // area of living
                2,        // number of cars in household
                3.0,    // commuting distance work (kilometers - 0 if no commuting)
                0.0        // commuting distance education (kilometers - 0 if no commuting)
        );
        System.out.println(testperson);

        /*
         * create schedules for a person until there is a schedule without any mistake
         *
         * there might be overlapping of activities when having a unfavorable stream of random numbers.
         * if this occurs, the generation of activity schedules will be repeated with another
         * stream of random numbers.
         */
        boolean scheduleOK = false;
        while (!scheduleOK) {
            try {
                // create week activity schedule
                testperson.generateSchedule(fileBase, randomgenerator);
                scheduleOK = true;
            } catch (InvalidPatternException e) {
                System.err.println(e.getReason());
                System.err.println("person involved: " + testperson.getPersIndex());
            }
        }
        //testperson.getweekPattern().printOutofHomeActivitiesList();
        testperson.getWeekPattern().printAllActivitiesList();
    }


    /**
     * Create a person including household context and activity schedule - example 3
     */
    public static void createAndModelOnePerson_Example3() {

        ActiToppHousehold testhousehold = new ActiToppHousehold(
                1,        // household index
                0,        // number of children 0-10
                1,        // number of children bwlow 18
                2,        // are of living
                2            // number of cars in household
        );

        ActitoppPerson testperson = new ActitoppPerson(
                testhousehold,  // household
                1,                            // person number in household
                10,                        // PersIndex
                55,                        // age
                1,                            // employment status
                1                            // gender
        );

        // add person to household
        testhousehold.addHouseholdmember(testperson, testperson.getPersIndex());

        System.out.println(testperson);

        /*
         * create schedules for a person until there is a schedule without any mistake
         *
         * there might be overlapping of activities when having a unfavorable stream of random numbers.
         * if this occurs, the generation of activity schedules will be repeated with another
         * stream of random numbers.
         */
        boolean scheduleOK = false;
        while (!scheduleOK) {
            try {
                // create week activity schedule
                testperson.generateSchedule(fileBase, randomgenerator);
                scheduleOK = true;
            } catch (InvalidPatternException e) {
                System.err.println(e.getReason());
                System.err.println("person involved: " + testperson.getPersIndex());
            }
        }
        //testperson.getweekPattern().printOutofHomeActivitiesList();
        testperson.getWeekPattern().printAllActivitiesList();
    }


    /**
     * Create multiple persons including activity schedules - example 1
     */
    public static void createAndModelMultiplePersons_Example1() {
        try {
            CSVPersonInputReader loader = new CSVPersonInputReader(ModelFileBase.class.getResourceAsStream("demo/Demopersonen.csv"));
            HashMap<Integer, ActitoppPerson> personmap = loader.loadInput_withouthouseholdcontexts();

            for (Integer key : personmap.keySet()) {
                ActitoppPerson actperson = personmap.get(key);
                System.out.println(actperson);
                // System.out.println(actperson.getPersIndex());

                /*
                 * create schedules for a person until there is a schedule without any mistake
                 *
                 * there might be overlapping of activities when having a unfavorable stream of random numbers.
                 * if this occurs, the generation of activity schedules will be repeated with another
                 * stream of random numbers.
                 */
                boolean scheduleOK = false;
                while (!scheduleOK) {
                    try {
                        // create week activity schedule
                        actperson.generateSchedule(fileBase, randomgenerator);
                        scheduleOK = true;
                    } catch (InvalidPatternException e) {
                        System.err.println(e.getReason());
                    }
                }
                actperson.getWeekPattern().printAllActivitiesList();
            }

            // Output as csv file
            CSVExportWriter tripwriter = new CSVExportWriter("src/main/resources/DemoTripList.csv");
            tripwriter.exportTripData(personmap);

            CSVExportWriter activitywriter = new CSVExportWriter("src/main/resources/DemoActivityList.csv");
            activitywriter.exportActivityData(personmap);

            System.out.println("all persons processed!");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Create multiple persons including household contexts and activity schedules - example 2
     */
    public static void createAndModelMultiplePersons_Example2() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy kk:mm:ss");

        LocalDateTime start = LocalDateTime.now();
        long timestart = System.currentTimeMillis();
        System.out.println(start.format(df));

        /*
         *
         * read input information from file system
         *
         */
        HashMap<Integer, ActiToppHousehold> householdmap = null;

        try {
            CSVHouseholdInputReader hhloader = new CSVHouseholdInputReader(ModelFileBase.class.getResourceAsStream("demo/Demo_HHInfo.csv"));
            CSVPersonInputReader personloader = new CSVPersonInputReader(ModelFileBase.class.getResourceAsStream("demo/Demo_Personen_mitHHIndex.csv"));
            householdmap = hhloader.loadInput();
            personloader.loadInput(householdmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert householdmap != null : "could not create householdmap.";

        // Model Household using parallel streams
        householdmap.values().stream().forEach(ExampleActiTopp::runHousehold);

        try {
            // Output information as csv files
            CSVExportLogger logger = new CSVExportLogger(new File("output"));
            logger.writeLogging(householdmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("all persons processed!");

        LocalDateTime end = LocalDateTime.now();
        long timeende = System.currentTimeMillis();
        System.out.println("End: " + end.format(df));

        long dauer_msec = (timeende - timestart);
        System.out.println("Duration total: " + dauer_msec + " milli sec");
        double dauer_msec_perhh = dauer_msec / householdmap.size();
        System.out.println("Duration per HH: " + dauer_msec_perhh + " milli sec");

    }


    private static void runHousehold(ActiToppHousehold acthousehold) {
        System.out.println("HH: " + acthousehold.getHouseholdIndex() + " HHGRO: " + acthousehold.getNumberofPersonsinHousehold());


        /*
         * create schedules for a person until there is a schedule without any mistake
         *
         * order of modeling is based in possible share of joint activities
         *
         * there might be overlapping of activities when having a unfavorable stream of random numbers.
         * if this occurs, the generation of activity schedules will be repeated with another
         * stream of random numbers.
         */
        boolean householdscheduleOK = false;
        while (!householdscheduleOK) {
            //create DebugLogger
            DebugLoggers hhlogger = new DebugLoggers(debugloggers);

            //create activity schedules for the whole household
            var why = true;
            while(why) {
                try {
                    acthousehold.generateSchedules(fileBase, randomgenerator, hhlogger);
                    randomgenerator.getRandomValue();
                } catch (Exception e) {

                }
            }



            //System.out.println("HHdone: " + key);
            householdscheduleOK = true;

            //add debug information to the overall logger
            debugloggers.addHouseholdDebugInfotoOverallLogger(hhlogger);
        }
    }

}
