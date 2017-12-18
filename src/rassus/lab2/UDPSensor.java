package rassus.lab2;

import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * Represents UDP sensor.
 */
public class UDPSensor {

    private final int port;

    private boolean isSensorOn;
    public static List<Integer> SENSOR_PORTS;
    private List<Measurement> measurements;
    private EmulatedSystemClock clock;
    private TimeVector timeVector;
    private DatagramSocket socket;
    private SensorUDPClient UDPClient;
    private SensorUDPServer UDPServer;

    public final static double LOSS_RATE = 0.25;
    public final static int AVERAGE_DELAY = 1000;

    /**
     * UDP sensor constructor. It runs sensor on given port.
     *
     * @param port port
     * @throws SocketException
     */
    public UDPSensor(int port) throws SocketException {

        this.port = port;
        this.isSensorOn = false;
        this.measurements = Collections.synchronizedList(new ArrayList<>());

        SENSOR_PORTS = new ArrayList<>();
        SENSOR_PORTS.add(10101);
        SENSOR_PORTS.add(20202);
        SENSOR_PORTS.add(30303);

        this.timeVector = new TimeVector();
        this.clock = new EmulatedSystemClock();

        this.socket = new SimpleSimulatedDatagramSocket(port, LOSS_RATE, AVERAGE_DELAY);

        this.UDPClient = new SensorUDPClient(this, socket);
        this.UDPServer = new SensorUDPServer(this, socket);

        System.out.println("Sensor created on port " + port + ".");
    }

    /**
     * Method starts sensor. It starts his UDP server and UDP client for sending and receiving measurements and confirmations.
     * Every 5 seconds it prints sensor status of sorted measurements and average value of CO2.
     */
    public void start(){

        isSensorOn = true;

        Executors.newSingleThreadExecutor().execute(UDPServer);
        Executors.newSingleThreadExecutor().execute(UDPClient);

        while(isSensorOn){
            try {
                Thread.sleep(5000);
                System.out.println("*******************STATUS*******************");
                scalarSort();
                System.out.println("---------------------------------------------");
                vectorSort();
                System.out.println("---------------------------------------------");
                calculateAverageValue();
                System.out.println("---------------------------------------------");
                measurements.clear();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Prints measurements in scalar time order.
     */
    private void scalarSort() {

        System.out.println("Scalar sort results: ");

        if(measurements.isEmpty()) {
            System.out.println("ERROR: no measurements!!");
            return;
        }

        measurements.sort(Measurement.ScalarComparator);
        measurements.forEach(measurement -> {
            System.out.println("Scalar value: " + measurement.getScalarTime() + " CO2 value: " + measurement.getCO2Value() );
        });
    }

    /**
     * Prints measurements in vector time order.
     */
    private void vectorSort() {

        System.out.println("Vector sort results: ");
        if(measurements.isEmpty()){
            System.out.println("ERROR: no measurements!!");
            return;
        }
        measurements.sort(Measurement.VectorComparator);
        measurements.forEach(measurement -> {
            System.out.println("Vector value: " + measurement.getVectorTime() + " CO2 value: " + measurement.getCO2Value());
        });
    }

    /**
     * Prints average value of CO2.
     */
    private void calculateAverageValue() {

        if(measurements.isEmpty()){
            System.out.println("Error: no measurements for calculating average value of CO2");
            return;
        }

        float sumOfValues = 0f;
        for(Measurement tempMeasurement : measurements){
            sumOfValues += tempMeasurement.getCO2Value();
        }

        System.out.println("Average value of CO2 (" + measurements.size() + " values): " + (sumOfValues / measurements.size()));
    }

    /**
     * Method stops sensor.
     */
    public void stop(){

        isSensorOn = false;
    }

    /**
     * Updates sensor time vector.
     */
    public void updateTimeVector(){
        timeVector.update(port);
    }

    /**
     * Updates sensor vector time with given time vector.
     *
     * @param timeVector time vector
     */
    public void updateTimeVector(TimeVector timeVector) {

        updateTimeVector();

        for(Integer tempPort : SENSOR_PORTS){
            if(tempPort != port){
                if(timeVector.getVector().get(tempPort) > this.timeVector.getVector().get(tempPort)){
                    this.timeVector.getVector().put(tempPort, timeVector.getVector().get(tempPort));
                }
            }
        }
    }

    /**
     * Update measurement confirmation.
     *
     * @param confirmation confirmation
     * @param address address
     */
    public void setPacketConfirmation(Confirmation confirmation, SocketAddress address){
        UDPClient.getUnconfirmedPackets().forEach(packet -> {
            if(packet.getId() == confirmation.getMeasurementIdConfirmation() && packet.getReceiverAddress().equals(address)){
                packet.setConfirmed(true);
            }
        });
    }

    public int getPort() {
        return port;
    }

    public boolean isSensorOn() {
        return isSensorOn;
    }

    public List<Measurement> getMeasurements(){
        return measurements;
    }

    public long getSensorClockTime(){
        return clock.currentTimeMillis();
    }

    public TimeVector getTimeVector(){
        return new TimeVector(timeVector);
    }

    public static void main(String[] args){

        Scanner s = new Scanner(System.in);
        try {
            System.out.println("Enter sensor port: ");
            UDPSensor sensor = new UDPSensor(Integer.parseInt(s.nextLine()));

            System.out.println("Sensor ready to start. Press enter.. ");
            s.nextLine();
            sensor.start();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
