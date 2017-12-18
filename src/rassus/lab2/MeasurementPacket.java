package rassus.lab2;

import java.net.InetSocketAddress;

/**
 * Represents measurement packet. It contains Measurement id,
 * receiver address and confirmation status.
 */
public class MeasurementPacket {

    private int id;
    private InetSocketAddress receiverAddress;
    private boolean confirmed;

    /**
     * Measurement packet constructor.
     *
     * @param id
     * @param receiverAddress
     */
    public MeasurementPacket(int id, InetSocketAddress receiverAddress){
        this.receiverAddress = new InetSocketAddress(receiverAddress.getAddress(), receiverAddress.getPort());
        this.confirmed = false;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeasurementPacket that = (MeasurementPacket) o;

        if (id != that.id) return false;
        return receiverAddress.equals(that.receiverAddress);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + receiverAddress.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MeasurementPacket{" +
                "id=" + id +
                ", receiverAddress=" + receiverAddress +
                ", confirmed=" + confirmed +
                '}';
    }


    public int getId() {
        return id;
    }

    public InetSocketAddress getReceiverAddress() {
        return receiverAddress;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}
