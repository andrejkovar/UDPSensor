package rassus.lab2;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents UDP client which is sending own measurements to other sensors
 * and resending unconfirmed measurements.
 */
public class SensorUDPClient implements Runnable {

    private UDPSensor sensor;
    private List<MeasurementPacket> unconfirmedPackets;
    private DatagramSocket socket;

    /**
     * Sensor UDP client constructor.
     *
     * @param sensor sensor
     * @param socket socket
     * @throws SocketException
     */
    public SensorUDPClient(UDPSensor sensor, DatagramSocket socket) throws SocketException {
        this.sensor = sensor;
        this.unconfirmedPackets = Collections.synchronizedList(new ArrayList<>());
        this.socket = socket;
    }


    public List<MeasurementPacket> getUnconfirmedPackets(){
        return unconfirmedPackets;
    }

    /**
     * Every 1 second resend unconfirmed measurements, updates sensor time vector,
     * reads new measurement and sends it to other sensors.
     */
    @Override
    public void run() {

        Measurement measurement;
        while(sensor.isSensorOn()){

            resendUnconfirmedPackets();

            sensor.updateTimeVector();
            measurement =  new Measurement(CO2Reader.read(sensor.getSensorClockTime()), sensor.getSensorClockTime(), sensor.getTimeVector());
            sensor.getMeasurements().add(measurement);

            broadcastMeasurement(measurement);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Removes confirmed packet from private List and resend unconfirmed packets.
     */
    private void resendUnconfirmedPackets() {

        unconfirmedPackets = unconfirmedPackets.stream().filter(packet -> !packet.isConfirmed()).collect(Collectors.toList());

        System.out.println("Resending unconfirmed packets..");

        unconfirmedPackets.forEach(packet -> {
            if(!packet.isConfirmed()) {
                for(Measurement tempMeasurement : sensor.getMeasurements()){
                    if(tempMeasurement.getId() == packet.getId()){

                        sensor.updateTimeVector();
                        sendPacket(tempMeasurement, packet.getReceiverAddress());
                        break;
                    }
                }
            }
        });
    }

    /**
     * Sends measurement to other sensors and updates private List of unconfirmed measurements.
     *
     * @param measurement
     */
    private void broadcastMeasurement(Measurement measurement){

        System.out.println("Broadcasting new measurement..");

        MeasurementPacket packet;
        for(Integer tempPort : UDPSensor.SENSOR_PORTS){

            if(tempPort != sensor.getPort()){

                sensor.updateTimeVector();
                packet = new MeasurementPacket(measurement.getId(), new InetSocketAddress("127.0.0.1", tempPort));
                sendPacket(measurement, packet.getReceiverAddress());
                System.out.println(measurement.getVectorTime());

                if(!unconfirmedPackets.contains(packet)){
                    unconfirmedPackets.add(packet);
                }
            }
        }
    }

    /**
     * Sends measurement packet on given address using UDP protocol.
     *
     * @param measurement measurement
     * @param address address
     */
    private void sendPacket(Measurement measurement, InetSocketAddress address){

        byte[] bytes = measurement.toString().getBytes();
        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, address);
        try {
            socket.send(datagramPacket);
            System.out.println("Measurement (ID: " + measurement.getId() + ", CO2: " + measurement.getCO2Value() + ") sent to: " + address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

