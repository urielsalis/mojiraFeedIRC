package nedhyett.crimson.networking.tier5;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by ned on 24/02/2017.
 */
public class AcknowledgeLock {

    public final Tier5Packet packet;
    private CountDownLatch latch = null;

    public AcknowledgeLock(Tier5Packet packet) {
        this.packet = packet;
    }

    public void awaitLatch() throws InterruptedException {
        latch.await();
    }

    public void awaitLatch(long ms) throws InterruptedException {
        latch.await(ms, TimeUnit.MILLISECONDS);
    }

    public void resetLatch() {
        if(latch != null) while(latch.getCount() > 0) latch.countDown();
        latch = new CountDownLatch(1);
    }

}
