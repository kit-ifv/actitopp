package edu.kit.ifv.mobitopp.actitopp.demo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import edu.kit.ifv.mobitopp.actitopp.*;

/**
 * @author Tim Hilgert
 */
public class CSVHouseholdInputReader {

    BufferedReader inRead;

    /**
     * constructor
     *
     * @param filename
     */
    public CSVHouseholdInputReader(String filename) throws FileNotFoundException {
        inRead = new BufferedReader(new FileReader(filename));
    }

    /**
     * constructor
     *
     * @param input
     */
    public CSVHouseholdInputReader(InputStream input) {
        inRead = new BufferedReader(new InputStreamReader(input));
    }

    /**
     * method to read household information from file system
     * <p>
     * Expected input format:
     * hhindex;#childrenunder10;childrenunder18;areatype;#carsinhh
     *
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public HashMap<Integer, ActiToppHousehold> loadInput() throws FileNotFoundException, IOException {

        // Initialisierungen

        HashMap<Integer, ActiToppHousehold> householdmap = new HashMap<Integer, ActiToppHousehold>();


        String line = null;
        boolean header = true;

        int zeilenzaehler = 0;

        while ((line = inRead.readLine()) != null) {
            zeilenzaehler++;

            // skip header section
            if (header) {
                line = inRead.readLine();
                header = false;
            }
            String[] splitted = line.split(";");

            try {

                ActiToppHousehold tmphousehold = new ActiToppHousehold(
                        Integer.parseInt(splitted[0]),        // householdIndex
                        Integer.parseInt(splitted[1]),        // children_u10
                        Integer.parseInt(splitted[2]),        // children_u18
                        Integer.parseInt(splitted[3]),        // areatype
                        Integer.parseInt(splitted[4])            // numberofcarsinhousehold
                );

                householdmap.put(Integer.parseInt(splitted[0]), tmphousehold);

            } catch (NumberFormatException e) {
                // e.printStackTrace();
                System.err.println("invalid input data - NumberFormatException");
                System.err.println("row " + zeilenzaehler + " will be ignored!");
            }
        }
        return householdmap;
    }
}