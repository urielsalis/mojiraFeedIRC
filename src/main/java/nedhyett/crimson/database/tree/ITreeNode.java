package nedhyett.crimson.database.tree;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by ned on 20/02/2017.
 */
public interface ITreeNode extends Serializable {

    void load(DataInputStream dis) throws IOException;
    void save(DataOutputStream dos) throws IOException;

    String getTagID();

}
