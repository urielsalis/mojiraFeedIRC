package nedhyett.crimson.database.tree.nodes;

import nedhyett.crimson.database.tree.ITreeNode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by ned on 20/02/2017.
 */
public class TreeByteLeaf implements ITreeNode {

    public byte value;

    public TreeByteLeaf() {

    }

    public TreeByteLeaf(byte value) {
        this.value = value;
    }

    @Override
    public void load(DataInputStream dis) throws IOException {
        value = dis.readByte();
    }

    @Override
    public void save(DataOutputStream dos) throws IOException {
        dos.writeByte(value);
    }

    @Override
    public String getTagID() {
        return "Byte";
    }

}
