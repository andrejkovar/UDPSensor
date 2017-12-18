package rassus.lab2;

import com.google.gson.Gson;
import java.util.Comparator;

/**
 * Represents CO2 measurement with time parameters.
 */
public class Measurement {

    private int id;
    private float CO2;
    private long scalarTime;
    private TimeVector vectorTime;

    /**
     * Measurement constructor. Id is generated from hashCode() method.
     *
     * @param CO2 CO2 value
     * @param scalarTime scalar time
     * @param vectorTime vector time
     */
    public Measurement(float CO2, long scalarTime, TimeVector vectorTime){

        this.CO2 = CO2;
        this.scalarTime = scalarTime;
        this.vectorTime = new TimeVector(vectorTime);
        this.id = hashCode();
    }

    /**
     * Update scalar time if temp time is lower than given scalar time.
     *
     * @param sensorClockTime scalar time
     */
    public void updateScalarTime(long sensorClockTime) {

        if(scalarTime <= sensorClockTime){
            scalarTime = sensorClockTime + 1;
        }
    }

    /**
     * Update vector time on given vector time.
     *
     * @param timeVector vector time
     */
    public void updateVectorTime(TimeVector timeVector) {

        setVectorTime(timeVector);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Measurement that = (Measurement) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        int result = (CO2 != +0.0f ? Float.floatToIntBits(CO2) : 0);
        result = 31 * result + (int) (scalarTime ^ (scalarTime >>> 32));
        result = 31 * result + vectorTime.hashCode();
        return result;
    }

    /**
     * Converts Measurement object to JSON string format.
     *
     * @return Measurement object in JSON string format
     */
    @Override
    public String toString(){
        return new Gson().toJson(this);
    }

    /**
     * Creates Measurement object from given Measurement in JSON string format.
     *
     * @param measurement Measurement in JSON string format.
     * @return Measurement object
     */
    public static Measurement createMeasurement(String measurement){
        return new Gson().fromJson(measurement, Measurement.class);
    }

    /**
     * Creates Measurement scalar Comparator. It compares measurement scalar times for ascending order.
     */
    public static Comparator<Measurement> ScalarComparator = (o1 , o2) -> (int) (o1.getScalarTime() - o2.getScalarTime());

    /**
     * Creates Measurement vector Comparator. It compares measurement vector times for ascending order.
     */
    public static Comparator<Measurement> VectorComparator = (o1, o2) -> {

        if(o1.getVectorTime().getVector().equals(o2.getVectorTime().getVector())){
            return 0;
        }

        for(int tempPort : UDPSensor.SENSOR_PORTS){
            if(o1.getVectorTime().getVector().get(tempPort) < o2.getVectorTime().getVector().get(tempPort)){
                for(int secondPort : UDPSensor.SENSOR_PORTS){
                    if(tempPort != secondPort){
                        if(o1.getVectorTime().getVector().get(tempPort) > o2.getVectorTime().getVector().get(tempPort)){
                            return 1;
                        }
                    }
                }
                return -1;
            }
        }
        return 1;
    };

    public int getId(){
        return id;
    }

    public float getCO2Value(){
        return CO2;
    }

    public long getScalarTime() {
        return scalarTime;
    }

    public void setScalarTime(long scalarTime) {
        this.scalarTime = scalarTime;
    }

    public TimeVector getVectorTime() {
        return new TimeVector(vectorTime);
    }

    public void setVectorTime(TimeVector vectorTime) {
        this.vectorTime = new TimeVector(vectorTime);
    }
}
