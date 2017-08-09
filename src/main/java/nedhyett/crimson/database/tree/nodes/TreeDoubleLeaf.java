package nedhyett.crimson.database.tree.nodes;

import nedhyett.crimson.database.tree.ITreeNode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by ned on 22/02/2017.
 */
public class TreeDoubleLeaf implements ITreeNode {

    public double value;

    public TreeDoubleLeaf() {

    }

    public TreeDoubleLeaf(double value) {
        this.value = value;
    }

    @Override
    public void load(DataInputStream dis) throws IOException {
        value = dis.readDouble();
    }

    @Override
    public void save(DataOutputStream dos) throws IOException {
        dos.writeDouble(value);
    }

    @Override
    public String getTagID() {
        return "Double";
    }

}
