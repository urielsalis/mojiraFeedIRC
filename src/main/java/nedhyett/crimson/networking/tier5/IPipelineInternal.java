package nedhyett.crimson.networking.tier5;

import nedhyett.crimson.eventreactor.IEvent;
import nedhyett.crimson.networking.tier4.packet.AckPacket;

/**
 * Created by ned on 24/02/2017.
 */
interface IPipelineInternal extends IPipeline {


    /**
     * Internal use only!
     *
     * @param e
     *
     * @return result of publish
     */
    boolean publishEvent(IEvent event);

    void publishAcknowledgeEvent(String id, AckPacket packet);

}
