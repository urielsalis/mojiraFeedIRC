package nedhyett.crimson.database.tree.nodes;

import nedhyett.crimson.database.tree.ITreeNode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by ned on 20/02/2017.
 */
public class TreeStringLeaf implements ITreeNode {

    public String value;

    public TreeStringLeaf() {

    }

    public TreeStringLeaf(String value) {
        this.value = value;
    }

    @Override
    public void load(DataInputStream dis) throws IOException {
        value = dis.readUTF();
    }

    @Override
    public void save(DataOutputStream dos) throws IOException {
        dos.writeUTF(value);
    }

    @Override
    public String getTagID() {
        return "String";
    }
}
