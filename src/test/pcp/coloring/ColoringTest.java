package test.pcp.coloring;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import pcp.PCP;
import pcp.model.Coloring;
import pcp.model.NodeColorInfo;
import pcp.model.Graph;
import pcp.model.Node;

/**
 *
 * @author gilbert
 */
public class ColoringTest {

    private static final Logger logger = Logger.getLogger(PCP.class.getName());

    /*
     * performs all tests
     */
    public static boolean performAll(Coloring c) {
        if (testSolutionValidityNoConflicts(c)
                && testSolutionValiditySelection(c)
                && testCorrectConflictsValues(c)
                && testCorrectCountingColorValues(c)
                && testCorrectSetContents(c)) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * Test if there are no two adjacent nodes colored with the same color
     */
    public static boolean testSolutionValidityNoConflicts(Coloring c) {
        for (NodeColorInfo nci : c.getSelectedColoredNCIs()) {
            Node n = nci.getNode();
            int color = nci.getColor();
            for (Node neigh : n.getNeighbours()) {
                NodeColorInfo neighNci = c.getNciById(neigh.getId());
                int neighColor = neighNci.getColor();
                if (color == neighColor) {
                    logger.severe("TEST FAILED: Solution has unresolved conflicts!");
                    return false;
                }
            }
        }
        logger.severe("TEST SUCCEEDED: Solution is valid due to unresolved conflicts.");
        return true;
    }

    /*
     * Tests if there is exactly one node per partition selected
     */
    public static boolean testSolutionValiditySelection(Coloring c) {
        if (c.getGraph().getPartitionSize().length != c.getSelectedColoredNCIs().size()) {
            logger.severe("TEST FAILED: Solution has an invalid selection or not all nodes are colored.");
            return false;
        }
        boolean checkSelected[] = new boolean[c.getSelectedColoredNCIs().size()];
        for (int i = 0; i < checkSelected.length; i++) {
            checkSelected[i] = false;
        }
        for (NodeColorInfo nci : c.getSelectedColoredNCIs()) {
            Node n = nci.getNode();
            if (checkSelected[n.getPartition()]) {
                logger.severe("TEST FAILED: There are more than one Nodes selected in the same partition.");
                return false;
            }
            checkSelected[n.getPartition()] = true;
        }
        logger.severe("TEST SUCCEEDED: There is exactly one node selected per partition.");
        return true;
    }

    /*
     * Test if all colorvalues are correct
     */
    public static boolean testCorrectConflictsValues(Coloring c) {
        for (Node n : c.getGraph().getNodes()) {
            //init conflictarray and collect
            NodeColorInfo nci = c.getNciById(n.getId());

            int[] conflicts = new int[c.getChromatic()];
            for (int i = 0; i < conflicts.length; i++) {
                conflicts[i] = 0;
            }
            for (Node neigh : n.getNeighbours()) {
                NodeColorInfo neighNci = c.getNciById(neigh.getId());
                if (neighNci.getColor() >= 0) {
                    conflicts[neighNci.getColor()]++;
                }
            }
            //compare to colorarray held by the nci
            for (int i = 0; i < conflicts.length; i++) {
                if (conflicts[i] != nci.getConflicts(i)) {
                    logger.severe("TEST FAILED: Node " + n.getId() + " has wrong conflictinfo for color " + i);
                    return false;
                }
            }
        }
        logger.severe("TEST SUCCEEDED: conflictinfos are correct");
        return true;
    }

    /*
     * Test if all counting colorvalues are correct
     */
    public static boolean testCorrectCountingColorValues(Coloring c) {
//      logger.fine("\nTesting correct counting values");
        for (Node n : c.getGraph().getNodes()) {
//            logger.fine("Node " + n.getId());
            NodeColorInfo nci = c.getNciById(n.getId());
            int colorsAvailable = 0;
            int colorsUnavailable = 0;
            for (int i = 0; i < c.getChromatic(); i++) {
                if (nci.isColorAvailable(i)) {
                    colorsAvailable++;
                } else if (nci.getConflicts(i) > 0) {
                    colorsUnavailable++;
                }
            }
            int uncoloredNeighbours = 0;
            int degreeToSelected = 0;
            for (Node neigh : n.getNeighbours()) {
                NodeColorInfo neighNci = c.getNciById(neigh.getId());
                if (neighNci.getColor() == PCP.NODE_UNCOLORED) {
                    uncoloredNeighbours++;
                }
                if (neighNci.getColor() != PCP.NODE_UNSELECTED) {
                    degreeToSelected++;
                }
            }
            //compare to values holded by nci
//            logger.fine("colorsAvailable:\t" + colorsAvailable + " vs " + nci.getColorsAvailable());
//            logger.fine("diffcolors:\t" + colorsUnavailable + " vs " + nci.getDiffColoredNeighbours());
//            logger.fine("uncoloredNeighs:\t" + uncoloredNeighbours + " vs " + nci.getUncoloredNeighbours());
//            logger.fine("degreeToSelected\t" + degreeToSelected + " vs " + nci.getDegreeToSelected());

            if (colorsAvailable != nci.getColorsAvailable() || colorsUnavailable != nci.getDiffColoredNeighbours()
                    || uncoloredNeighbours != nci.getUncoloredNeighbours() || degreeToSelected != nci.getDegreeToSelected()) {
                logger.severe("TEST FAILED: some colorvalues are not correct!");
                return false;
            }
        }
        logger.severe("TEST SUCCEEDED: all counting color values are correct.");
        return true;
    }

    public static boolean testCorrectSetContents(Coloring c) {
        Set<NodeColorInfo> selectedColoredNCIs = new HashSet<NodeColorInfo>(c.getGraph().getNodes().length);
        Set<NodeColorInfo> selectedUncoloredNCIs = new HashSet<NodeColorInfo>(c.getGraph().getNodes().length);
        Set<NodeColorInfo> unselectedNCIs = new HashSet<NodeColorInfo>(c.getGraph().getNodes().length);
        for (Node n : c.getGraph().getNodes()) {
            NodeColorInfo nci = c.getNciById(n.getId());
            if (nci.getColor() >= 0) {
                selectedColoredNCIs.add(nci);
            } else if (nci.getColor() == PCP.NODE_UNCOLORED) {
                selectedUncoloredNCIs.add(nci);
            } else if (!c.isPartitionSelected(n.getPartition())) {
                unselectedNCIs.add(nci);
            }
        }

        if (selectedColoredNCIs.size() != c.getSelectedColoredNCIs().size()) {
            logger.severe("TEST FAILED: selectedColorNCIs holds more/less nci than necessary!"
                    + " test: " + selectedColoredNCIs.size() + "; orig: " + c.getSelectedColoredNCIs().size());
            return false;
        }
        for (NodeColorInfo selectedColoredNci : selectedColoredNCIs) {
            if (!c.getSelectedColoredNCIs().contains(selectedColoredNci)) {
                logger.severe("TEST FAILED: selectedColoredNcis has insufficient content.");
                return false;
            }
        }

        if (selectedUncoloredNCIs.size() != c.getSelectedUncoloredNCIs().size()) {
            logger.severe("TEST FAILED: selectedUncoloredNCIs holds more/less nci than necessary!"
                    + " test: " + selectedUncoloredNCIs.size() + "; orig: " + c.getSelectedUncoloredNCIs().size());
            return false;
        }
        for (NodeColorInfo selectedUncoloredNci : selectedUncoloredNCIs) {
            if (!c.getSelectedUncoloredNCIs().contains(selectedUncoloredNci)) {
                logger.severe("TEST FAILED: selectedUncoloredNCIs has insufficient content.");
                return false;
            }
        }

        if (unselectedNCIs.size() != c.getUnselectedNCIs().size()) {
            logger.severe("TEST FAILED: unselectedNCIs holds more/less nci than necessary!"
                    + " test: " + unselectedNCIs.size() + "; orig: " + c.getUnselectedNCIs().size());
            return false;
        }
        for (NodeColorInfo unselectedNci : unselectedNCIs) {
            if (!c.getUnselectedNCIs().contains(unselectedNci)) {
                logger.severe("TEST FAILED: selectedColorInfoNcis has insufficient content.");
                return false;
            }
        }

        for (NodeColorInfo conflictingNci : c.getConflictingNCIs()) {
            logger.severe("TEST conflicting: " + conflictingNci.getNode().getId());
            if (!c.getSelectedColoredNCIs().contains(conflictingNci)) {
                logger.severe("TEST FAILED: a conflicting node is not in set of selectedColoredNcis");
                return false;
            }
        }

        logger.severe("TEST SUCCEEDED: all Sets have correct content.");
        return true;
    }
}