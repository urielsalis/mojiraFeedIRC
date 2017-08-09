package nedhyett.crimson.networking.tier5;

import nedhyett.crimson.utility.Semver;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ned on 24/02/2017.
 */
public class ProtocolInfo implements Serializable {

    public final String name;
    public final Semver version;
    public final ArrayList<Tier5Channel> channels = new ArrayList<>();

    public ProtocolInfo(String name, Semver version) {
        this.name = name;
        this.version = version;
    }

    public String compare(ProtocolInfo other) {
        if(!other.name.equalsIgnoreCase(name)) {
            return "Incompatible Protocol Name!";
        }
        if (!other.version.isSafe(version)) {
            return "Incompatible Protocol Version!";
        }
        return "GOOD";
    }

}
