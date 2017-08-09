package nedhyett.crimson.networking.tier5;

import nedhyett.crimson.networking.tier4.Tier4ServerPipeline;
import nedhyett.crimson.networking.tier4.packet.DisconnectPacket;
import nedhyett.crimson.threading.IProgressReportTask;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by ned on 27/02/2017.
 */
public class Tier5Client implements IEndpoint {

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

    /**
     * Disconnect this client.
     */
    public void kick() {
        kick("No reason given!");
    }

    /**
     * Disconnect this client with a reason.
     *
     * @param reason
     */
    public void kick(String reason) {
        kick(reason, true);
    }

    /**
     * Disconnect this client with a reason and specify whether to notify the client that they have been kicked.
     *
     * @param reason
     * @param notify
     */
    public void kick(String reason, boolean notify) {
//        if(notify) sendPacket(new DisconnectPacket("Kicked from server: " + reason));
//        parent.stopTrackingClient(this, reason);
//        try {
//            s.close();
//        } catch(IOException e) {
//            Tier4ServerPipeline.logger.warning("Failed to close socket for client %s!", uuid);
//            Tier4ServerPipeline.logger.warning(e);
//        }
//        if(worker != null) worker.interrupt();
    }

}
