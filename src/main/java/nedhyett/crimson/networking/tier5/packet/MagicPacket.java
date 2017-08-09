package nedhyett.crimson.networking.tier5.packet;

import nedhyett.crimson.networking.tier4.Side;
import nedhyett.crimson.networking.tier5.Tier5Client;
import nedhyett.crimson.networking.tier5.Tier5ClientPipeline;
import nedhyett.crimson.networking.tier5.Tier5Packet;
import nedhyett.crimson.networking.tier5.Tier5ServerPipeline;

/**
 * Created by ned on 24/02/2017.
 */
public class MagicPacket extends Tier5Packet {

    @Override
    public void processPacketServer(Tier5Client c, Tier5ServerPipeline p) {

    }

    @Override
    public void processPacketClient(Tier5ClientPipeline p) {

    }

    @Override
    public boolean canProcessOnSide(Side side) {
        return true;
    }

}
