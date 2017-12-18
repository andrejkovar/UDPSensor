package rassus.lab2;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents Time vector.
 */
public class TimeVector {

    private Map<Integer, Integer> vector;

    /**
     * Time vector constructor.
     */
    public TimeVector(){
        this.vector = new HashMap<>();
        for(Integer tempPort : UDPSensor.SENSOR_PORTS){
            vector.put(tempPort, 0);
        }
    }

    public TimeVector(TimeVector timeVector){
        this.vector = new HashMap<>(timeVector.getVector());
    }

    /**
     * Updates value for given port in time vector.
     *
     * @param port port
     */
    public void update(Integer port){

        int oldValue = vector.get(port);
        vector.put(port, oldValue + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeVector that = (TimeVector) o;

        return vector.equals(that.vector);
    }

    @Override
    public int hashCode() {
        return vector.hashCode();
    }

    @Override
    public String toString(){
        return vector.toString();
    }

    public Map<Integer, Integer> getVector(){
        return vector;
    }
}
