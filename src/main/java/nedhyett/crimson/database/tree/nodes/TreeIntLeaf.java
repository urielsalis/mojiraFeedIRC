package nedhyett.crimson.database.tree.nodes;

import nedhyett.crimson.database.tree.ITreeNode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by ned on 20/02/2017.
 */
public class TreeIntLeaf implements ITreeNode {

    public int value;

    public TreeIntLeaf() {

    }

    public TreeIntLeaf(int value) {
        this.value = value;
    }

    @Override
    public void load(DataInputStream dis) throws IOException {
        value = dis.readInt();
    }

    @Override
    public void save(DataOutputStream dos) throws IOException {
        dos.writeInt(value);
    }

    @Override
    public String getTagID() {
        return "Integer";
    }
}
