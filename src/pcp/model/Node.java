package pcp.model;

import java.util.logging.Logger;
import pcp.tools.MergeSort;

/*
 * simple datastructure to store node information
 */
public class Node {

    private static final Logger logger = Logger.getLogger(Node.class.getName());
    private int id;
    private int idxInPartition;
    private Node[] neighbour;
    private int partition;
    private int degree = 0;//counted up in addNeighbour()

    public Node(int id, int idxInPartition, int partition, int neighbourAmount) {
        this.id = id;
        this.idxInPartition = idxInPartition;
        this.partition = partition;
        this.neighbour = new Node[neighbourAmount];
        for (Node n : neighbour) {
            n = null;
        }
    }

    public Node getNeighbour(int idx) {
        return neighbour[idx];
    }

    public void setNeighbour(int idx, Node n) {
        neighbour[idx] = n;
    }

    public void setNeighbours(Node[] neighbour) {
        this.neighbour = neighbour;
    }

    public int getId() {
        return id;
    }

    public Node[] getNeighbours() {
        return neighbour;
    }

    public void addNeighbour(Node neighbour) {
        this.neighbour[degree] = neighbour;
        degree++;
    }

    public int getPartition() {
        return partition;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public void decreaseDegree() {
        degree--;
    }

    public int getIdxInPartition() {
        return idxInPartition;
    }
}