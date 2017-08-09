package nedhyett.crimson.networking.tier5;

import nedhyett.crimson.eventreactor.EventReactor;
import nedhyett.crimson.eventreactor.IEvent;
import nedhyett.crimson.logging.CrimsonLog;
import nedhyett.crimson.logging.MiniLogger;
import nedhyett.crimson.networking.IConnectionDelegate;
import nedhyett.crimson.networking.ServerSocketListener;
import nedhyett.crimson.networking.tier4.*;
import nedhyett.crimson.networking.tier4.packet.AckPacket;
import nedhyett.crimson.networking.tier5.packet.HandshakePacket;
import nedhyett.crimson.utility.Semver;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * Created by ned on 24/02/2017.
 */
public class Tier5ServerPipeline implements IPipelineInternal, IConnectionDelegate {

    static final MiniLogger logger = CrimsonLog.spawnLogger("Tier5 - Server");

    protected final ArrayList<IPacketMorpher> morphers = new ArrayList<>();

    private final EventReactor reactor = new EventReactor("Tier5 - Server", Tier4Event.class);
    private final ServerSocketListener listener;
    private final ProtocolInfo protocolInfo;
    private final HashMap<String, Tier5Client> clients = new HashMap<>();

    private String encryptionKey = null;

    protected HashMap<String, AcknowledgeLock> locks = new HashMap<>();

    /**
     * Used by the server to create a new pipeline. Provides default version information so clients should do the same. Effectively disables version checking.
     *
     * @param port
     *
     * @throws IOException
     */
    public Tier5ServerPipeline(int port) throws IOException {
        this(port, "BaseProtocolName", new Semver(0, 0, 0));
    }

    /**
     * Used by the server to create a new pipeline. Allows for the version information to be specified to make sure that the client is safe to connect!
     *
     * @param port
     * @param protocolName
     * @param protocolVersion
     *
     * @throws IOException
     */
    public Tier5ServerPipeline(int port, String protocolName, Semver protocolVersion) throws IOException {
        this(port, protocolName, protocolVersion, null);
    }

    public Tier5ServerPipeline(int port, String protocolName, Semver protocolVersion, String encryptionKey) throws IOException {
        this.listener = new ServerSocketListener(port, this);
        this.protocolInfo = new ProtocolInfo(protocolName, protocolVersion);
        this.encryptionKey = encryptionKey;
    }

    @Override
    public boolean publishEvent(IEvent event) {
        return false;
    }

    @Override
    public void publishAcknowledgeEvent(String id, AckPacket packet) {

    }

    @Override
    public boolean connect() {
        listener.start();
        return true;
    }

    @Override
    public void disconnect() {
        for(Tier5Client c : clients.values()) c.kick("Server is disconnecting!");
        listener.interrupt();
    }

    @Override
    public void handleConnection(Socket socket) throws Exception {
        String uuid = UUID.randomUUID().toString();
        System.out.println("Got new connection... " + uuid);
        //Tier5Client c = new Tier5Client(socket, uuid, this);
        //clients.put(uuid, c);
       // Tier5InputWorker worker = new Tier5InputWorker(this, c);
        //worker.start();
        //c.sendPacket(new HandshakePacket(uuid));
    }

    @Override
    public ProtocolInfo getProtocolInfo() {
        return protocolInfo;
    }

    @Override
    public void registerConnectionListener(Object o) {

    }

    @Override
    public void unregisterConnectionListener(Object o) {

    }

    @Override
    public void setEncryptionKey(String key) {
        this.encryptionKey = key;
    }

    @Override
    public String getEncryptionKey() {
        return encryptionKey;
    }

    @Override
    public void addPacketMorpher(IPacketMorpher morpher) {
        morphers.add(morpher);
    }

    @Override
    public void removePacketMorpher(IPacketMorpher morpher) {
        morphers.remove(morpher);
    }


}
