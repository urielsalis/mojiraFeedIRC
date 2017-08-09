package nedhyett.crimson.networking.tier5;

import nedhyett.crimson.networking.tier4.Side;

import java.io.Serializable;

/**
 * Created by ned on 24/02/2017.
 */
public abstract class Tier5Packet implements Serializable {

    public abstract void processPacketServer(Tier5Client c, Tier5ServerPipeline p);

    public abstract void processPacketClient(Tier5ClientPipeline p);

    public abstract boolean canProcessOnSide(Side side);

}
