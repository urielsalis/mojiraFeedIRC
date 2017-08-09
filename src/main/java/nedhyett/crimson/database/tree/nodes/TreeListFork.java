package nedhyett.crimson.database.tree.nodes;

import nedhyett.crimson.database.tree.ITreeNode;
import nedhyett.crimson.database.tree.TreeEngine;
import nedhyett.crimson.registry.NumericalRegistry;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by ned on 20/02/2017.
 */
public class TreeListFork extends NumericalRegistry<ITreeNode> implements ITreeNode {

    @Override
    public void load(DataInputStream dis) throws IOException {
        int numNodes = dis.readInt();
        for(int i = 0; i < numNodes; i++) {
            ITreeNode node = TreeEngine.createNodeFromID(dis.readUTF());
            if(node == null) return;
            node.load(dis);
            this.registerEntry(node);
        }
    }

    @Override
    public void save(DataOutputStream dos) throws IOException {
        dos.writeInt(this.size());
        for(ITreeNode node : items) {
            dos.writeUTF(node.getTagID());
            node.save(dos);
        }
    }

    @Override
    public String getTagID() {
        return "ListFork";
    }

}
