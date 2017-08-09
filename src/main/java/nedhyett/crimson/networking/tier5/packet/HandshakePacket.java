package nedhyett.crimson.networking.tier5.packet;

import nedhyett.crimson.networking.tier4.Side;
import nedhyett.crimson.networking.tier4.event.ClientConnectedEvent;
import nedhyett.crimson.networking.tier4.packet.ConnectPacket;
import nedhyett.crimson.networking.tier5.*;

/**
 * Created by ned on 27/02/2017.
 */
public class HandshakePacket extends Tier5Packet {

    public String uuid;
    public ProtocolInfo protocolInfo;
    public String encryptionKey;

    public HandshakePacket() {

    }

    public HandshakePacket(String uuid) {
        this(uuid, null);
    }

    public HandshakePacket(String uuid, ProtocolInfo protocolInfo) {
        this(uuid, protocolInfo, null);
    }

    public HandshakePacket(String uuid, ProtocolInfo protocolInfo, String encryptionKey) {
        this.uuid = uuid;
        this.protocolInfo = protocolInfo;
        this.encryptionKey = encryptionKey;
    }

    @Override
    public void processPacketServer(Tier5Client c, Tier5ServerPipeline p) {
        String reason;
        if(!(reason = p.getProtocolInfo().compare(protocolInfo)).equalsIgnoreCase("GOOD")) {
            c.kick(reason);
            return;
        }
        //p.publishEvent(new ClientConnectedEvent(c, p));
        //c.sendPacket(new ConnectPacket());
    }

    @Override
    public void processPacketClient(Tier5ClientPipeline p) {
        //Communicate the UUID sent by the server to somewhere for storage, how?
        //Respond with a new Handshake packet detailing our connection details. At this point we don't know what version the server is. This is intentional to prevent version bias!
        HandshakePacket newPacket = new HandshakePacket(uuid, p.getProtocolInfo(), p.getEncryptionKey());
        p.sendPacket(newPacket);
    }

    @Override
    public boolean canProcessOnSide(Side side) {
        return false;
    }
}
