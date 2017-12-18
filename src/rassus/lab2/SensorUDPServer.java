package rassus.lab2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Represents sensor UDP server which is waiting on messages and process them.
 */
public class SensorUDPServer implements Runnable {

    private UDPSensor sensor;
    private DatagramSocket socket;
    private DatagramPacket packet;

    public static final int BUFFER_SIZE = 2048;

    /**
     * Sensor UDP server constructor.
     *
     * @param sensor sensor
     * @param socket socket
     * @throws SocketException
     */
    public SensorUDPServer(UDPSensor sensor, DatagramSocket socket) throws SocketException {

        this.sensor = sensor;
        this.socket = socket;
    }

    /**
     * Waiting on packet. When packet is arrived, it process it.
     */
    @Override
    public void run() {

        System.out.println("Waiting on packets..");
        while(sensor.isSensorOn()){
            try {
                byte[] rcvBuf = new byte[BUFFER_SIZE];
                packet = new DatagramPacket(rcvBuf, rcvBuf.length);
                socket.receive(packet);
                processPacket();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processPacket(){

        String rcvString = new String(packet.getData(), packet.getOffset(), packet.getLength());

        if (rcvString.toLowerCase().contains("confirmation")) {

            // Received confirmation for sent measurement
            Confirmation confirmation = Confirmation.createConfirmation(rcvString);
            sensor.setPacketConfirmation(confirmation, packet.getSocketAddress());
            sensor.updateTimeVector();

            System.out.println("Measurement (ID: " + confirmation.getMeasurementIdConfirmation() + ") confirmation received.");

        } else {

            // Received measurement from another sensor
            Measurement measurement = Measurement.createMeasurement(rcvString);
            sensor.updateTimeVector(measurement.getVectorTime());

            if(sensor.getMeasurements().contains(measurement)){
                return;
            }

            measurement.updateScalarTime(sensor.getSensorClockTime());
            measurement.updateVectorTime(sensor.getTimeVector());
            sensor.getMeasurements().add(measurement);

            System.out.println("Received measurement (ID:" + measurement.getId() + " CO2: "+ measurement.getCO2Value() + ") from: " + packet.getSocketAddress());

            // Sending confirmation for received measurement
            Confirmation confirmation = new Confirmation(measurement.getId());
            byte[] bytes = confirmation.toString().getBytes();
            DatagramPacket confirmationPacket = new DatagramPacket(bytes, bytes.length, packet.getSocketAddress());
            try {
                socket.send(confirmationPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
