package nedhyett.crimson.database.tree;

import nedhyett.crimson.database.tree.nodes.*;
import nedhyett.crimson.logging.CrimsonLog;
import nedhyett.crimson.logging.MiniLogger;

import java.util.HashMap;

/**
 * Created by ned on 20/02/2017.
 */
public class TreeEngine {

    public static final MiniLogger log = CrimsonLog.spawnLogger("TreeStore");
    private static HashMap<String, Class<? extends ITreeNode>> nodes = new HashMap<>();

    public static void registerNode(Class<? extends ITreeNode> node) {
        try {
            nodes.putIfAbsent(node.newInstance().getTagID(), node);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static ITreeNode createNodeFromID(String id) {
        try {
            return nodes.get(id).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.critical("Cannot load tag with ID %s, the handler class cannot be found.", id);
            return null;
        }
    }

    static {
        registerNode(TreeNamedFork.class);
        registerNode(TreeListFork.class);
        registerNode(TreeByteLeaf.class);
        registerNode(TreeDoubleLeaf.class);
        registerNode(TreeFloatLeaf.class);
        registerNode(TreeIntLeaf.class);
        registerNode(TreeLongLeaf.class);
        registerNode(TreeShortLeaf.class);
        registerNode(TreeStringLeaf.class);

    }

}
