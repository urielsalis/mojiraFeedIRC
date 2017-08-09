package nedhyett.crimson.networking.tier5;

import nedhyett.crimson.eventreactor.IEvent;
import nedhyett.crimson.networking.tier4.IPacketMorpher;
import nedhyett.crimson.networking.tier4.packet.AckPacket;
import nedhyett.crimson.threading.IProgressReportTask;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;

/**
 * Created by ned on 27/02/2017.
 */
public class Tier5ClientPipeline implements IPipelineInternal, IEndpoint {
    @Override
    public boolean publishEvent(IEvent event) {
        return false;
    }

    @Override
    public void publishAcknowledgeEvent(String id, AckPacket packet) {

    }

    @Override
    public DataInputStream getInputStream() {
        return null;
    }

    @Override
    public DataOutputStream getOutputStream() {
        return null;
    }

    @Override
    public InetAddress getAddress() {
        return null;
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public boolean close() {
        return false;
    }

    @Override
    public boolean close(String reason) {
        return false;
    }

    @Override
    public String getCloseReason() {
        return null;
    }

    @Override
    public void sendPacket(Tier5Packet p) {

    }

    @Override
    public Tier5Packet sendPacketAcknowledge(Tier5Packet p, int timeout, IProgressReportTask... progressListeners) {
        return null;
    }

    @Override
    public boolean connect() {
        return false;
    }

    @Override
    public void disconnect() {

    }

    @Override
    public ProtocolInfo getProtocolInfo() {
        return null;
    }

    @Override
    public void registerConnectionListener(Object o) {

    }

    @Override
    public void unregisterConnectionListener(Object o) {

    }

    @Override
    public void setEncryptionKey(String key) {

    }

    @Override
    public String getEncryptionKey() {
        return null;
    }

    @Override
    public void addPacketMorpher(IPacketMorpher morpher) {

    }

    @Override
    public void removePacketMorpher(IPacketMorpher morpher) {

    }
}
