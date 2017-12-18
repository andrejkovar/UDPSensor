package rassus.lab2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Represents CO2 reader from txt file.
 */
public class CO2Reader{

    /**
     * Method reads line from txt file, parse it and returns CO2 value.
     *
     * @param readParameter read parameter
     * @return CO2 value
     */
    public static float read(long readParameter){

        int lineNumber = (int) (Math.abs(readParameter - System.currentTimeMillis()) % 100) + 1;
        try {
            String[] parameters = Files.readAllLines(Paths.get("mjerenja.txt")).get(lineNumber).split(",", -1);
            return Float.parseFloat(parameters[3]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
}