package nedhyett.crimson.networking.tier5;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by ned on 27/02/2017.
 */
public class Tier5Channel implements Serializable {

    public String name;

    public void writeChannelData(DataOutputStream dos) throws IOException {
        dos.writeUTF(name);
    }

    public Tier5Channel readChannelData(DataInputStream dis) throws IOException {
        name = dis.readUTF();
        return this;
    }

}
