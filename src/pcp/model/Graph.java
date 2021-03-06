package pcp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/*
 * simple datastructure to store graph information
 */
public class Graph {
    private static final Logger logger = Logger.getLogger( Graph.class.getName());
    
    private final Node[] node;
    private final Node[][] nodeInPartition;
    private final int partitionSize[];
    private final int partitions;
    private final ArrayList<Integer[]> edges;

    public Graph(Node[] node, Node[][] nodeInPartition, int partitionSize[], ArrayList<Integer[]> edges) {
        this.node = node;
        this.nodeInPartition = nodeInPartition;
        this.partitionSize = partitionSize;
        this.partitions = partitionSize.length;
        this.edges = edges;
    }

    public String toString() {
        String ret = "size " + node.length + ";\n";
        for (Node n : node) {
            ret += "n" + n.getId() + ": p=" + n.getPartition() + "; ";
            ret += "degree=" + n.getDegree() + "; ";
            int neig = 0;
            for (Node neighbour : n.getNeighbours()) {
                ret += "n" + (neig++) + "=" + neighbour.getId() + ", ";
            }
            ret += "\n";
        }
        return ret;
    }
    
    public int getHighestDegree() {
        int maxdegree = 0;
        for (Node n : node) {
            if (n.getDegree() > maxdegree) {
                maxdegree = n.getDegree();
            }
        }
        return maxdegree;
    }
    
    public Node getNode(int idx) {
        return node[idx];
    }

    public Node[] getNodes() {
        return node;
    }

    public int getPartitionSize( int partition) {
        return partitionSize[partition];
    }

    public Node getNodeOfPartition( int partition, int node) {
        return nodeInPartition[partition][node];
    }

    public Node[] getNodesOfPartition( int partition) {
        return nodeInPartition[partition];
    }

    public int getPartitionAmount() {
        return partitions;
    }

    public ArrayList<Integer[]> getEdges() {
        return edges;
    }
    
}
