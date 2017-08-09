package nedhyett.crimson.database.tree.nodes;

import nedhyett.crimson.database.tree.ITreeNode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by ned on 22/02/2017.
 */
public class TreeShortLeaf implements ITreeNode {

    public short value;

    public TreeShortLeaf() {

    }

    public TreeShortLeaf(short value) {
        this.value = value;
    }

    @Override
    public void load(DataInputStream dis) throws IOException {
        value = dis.readShort();
    }

    @Override
    public void save(DataOutputStream dos) throws IOException {
        dos.writeShort(value);
    }

    @Override
    public String getTagID() {
        return "Short";
    }
}
