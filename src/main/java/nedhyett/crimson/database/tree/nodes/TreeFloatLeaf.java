package nedhyett.crimson.database.tree.nodes;

import nedhyett.crimson.database.tree.ITreeNode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by ned on 22/02/2017.
 */
public class TreeFloatLeaf implements ITreeNode {

    public float value;

    public TreeFloatLeaf() {

    }

    public TreeFloatLeaf(float value) {
        this.value = value;
    }

    @Override
    public void load(DataInputStream dis) throws IOException {
        value = dis.readFloat();
    }

    @Override
    public void save(DataOutputStream dos) throws IOException {
        dos.writeFloat(value);
    }

    @Override
    public String getTagID() {
        return "Float";
    }
}
