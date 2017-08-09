package nedhyett.crimson.database.tree.nodes;

import nedhyett.crimson.database.tree.ITreeNode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by ned on 22/02/2017.
 */
public class TreeLongLeaf implements ITreeNode {

    public long value;

    public TreeLongLeaf() {

    }

    public TreeLongLeaf(long value) {
        this.value = value;
    }

    @Override
    public void load(DataInputStream dis) throws IOException {
        value = dis.readLong();
    }

    @Override
    public void save(DataOutputStream dos) throws IOException {
        dos.writeLong(value);
    }

    @Override
    public String getTagID() {
        return "Long";
    }
}
