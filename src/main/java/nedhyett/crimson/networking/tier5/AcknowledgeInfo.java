package nedhyett.crimson.networking.tier5;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by ned on 24/02/2017.
 */
public class AcknowledgeInfo implements Serializable {

    public final String code;

    public AcknowledgeInfo(String code) {
        this.code = code;
    }



}
