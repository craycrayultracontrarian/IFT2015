import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class WriteStock {
    public static void writeStock(BST medStock, String outputFileName, LocalDate currDate) throws IOException {
        FileWriter fw = new FileWriter(outputFileName);

        fw.write("Stock " + currDate + '\n');

	// Create array to store all nodes in the main tree
        ArrayList<BST.Node> mainNodes = getAllNodes(medStock.getRoot());

	// For each node in the main tree, go through it's subtree and write the name
	// of the medication, stock and expiration date
	// Format: [Name] [Amount in stock] [Expiration date]
        for (BST.Node node : mainNodes) {
            writeSubTree(fw, node.value, node.subtree.getRoot());
        }

        fw.close();
    }

    // Return an ArrayList with all nodes in order in tree with given root 
    private static ArrayList<BST.Node> getAllNodes(BST.Node node) throws IOException {
        ArrayList<BST.Node> nodes = new ArrayList<>();
        getMainTreeNodes(nodes, node);
        return nodes;

    }

    // Used by primary method to get all nodes recursively in order
    private static void getMainTreeNodes(ArrayList<BST.Node> mainNodes, BST.Node node) throws IOException {
        if (node == null) {
            return;
        }
        getMainTreeNodes(mainNodes, node.left);

        mainNodes.add(node);

        getMainTreeNodes(mainNodes, node.right);
    }

    // Write each batch of a medication with differents amounts in stock and expiration dates line by line
    // recurseively in order 
    private static void writeSubTree(FileWriter fw, int medNumber, BST.Node node) throws IOException {
        if (node == null) {
            return;
        }

        writeSubTree(fw, medNumber, node.left);

	    // Write information, format: [Name] [Amount in stock] [Expiration date]
        fw.write("Médicament" + medNumber + " " + node.quantity + " " + node.expirationDate + '\n');

        writeSubTree(fw, medNumber, node.right);

    }
}
