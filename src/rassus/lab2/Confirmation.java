package rassus.lab2;

import com.google.gson.Gson;

/**
 * Represents measurement packet confirmation.
 */
public class Confirmation {

    private int measurementIdConfirmation;

    /**
     * Confirmation constructor.
     *
     * @param measurementIdConfirmation
     */
    public Confirmation(int measurementIdConfirmation){
        this.measurementIdConfirmation = measurementIdConfirmation;
    }

    /**
     * Measurement confirmation id getter.
     *
     * @return measurement confirmation id
     */
    public int getMeasurementIdConfirmation(){
        return measurementIdConfirmation;
    }

    /**
     * Converts Confirmation object to JSON string format.
     *
     * @return confirmation object in JSON string format.
     */
    @Override
    public String toString(){
        return new Gson().toJson(this);
    }

    /**
     * Creates Confirmation object from given Confirmation object in JSON string format.
     *
     * @param confirmation confirmation object in JSON string format.
     * @return Confirmation object
     */
    public static Confirmation createConfirmation(String confirmation){
        return new Gson().fromJson(confirmation, Confirmation.class);
    }
}
