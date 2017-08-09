package nedhyett.crimson.database.tree.nodes;

import nedhyett.crimson.database.tree.ITreeNode;
import nedhyett.crimson.database.tree.TreeEngine;
import nedhyett.crimson.registry.NamedRegistryBase;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by ned on 20/02/2017.
 */
public class TreeNamedFork extends NamedRegistryBase<ITreeNode> implements ITreeNode {

    @Override
    public void load(DataInputStream dis) throws IOException {
        int numNodes = dis.readInt();
        for(int i = 0; i < numNodes; i++) {
            String name = dis.readUTF();
            ITreeNode node = TreeEngine.createNodeFromID(dis.readUTF());
            if(node == null) {
                return;
            }
            node.load(dis);
            this.registerEntry(name, node);
        }
    }

    @Override
    public void save(DataOutputStream dos) throws IOException {
        dos.writeInt(this.size());
        for(Map.Entry<String, ITreeNode> e : this.keypairs()) {
            dos.writeUTF(e.getKey());
            dos.writeUTF(e.getValue().getTagID());
            e.getValue().save(dos);
        }
    }

    @Override
    public String getTagID() {
        return "NamedFork";
    }

}
