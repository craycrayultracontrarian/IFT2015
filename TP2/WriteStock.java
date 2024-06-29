import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class WriteStock {
    public static void writeStock(NewBST medStock, String outputFileName, LocalDate currDate) throws IOException {
        FileWriter fw = new FileWriter(outputFileName);

        fw.write("Stock " + currDate + '\n');

        ArrayList<NewBST.Node> mainNodes = getMainTreeNodes(medStock.getRoot());

        for (NewBST.Node node : mainNodes) {
            writeSubTree(fw, node.value, node.subtree.getRoot());
        }

        fw.close();
    }

    private static ArrayList<NewBST.Node> getMainTreeNodes(NewBST.Node node) throws IOException {
        ArrayList<NewBST.Node> nodes = new ArrayList<>();
        getMainTreeNodes(nodes, node);
        return nodes;

    }

    private static void getMainTreeNodes(ArrayList<NewBST.Node> mainNodes, NewBST.Node node) throws IOException {
        if (node == null) {
            return;
        }
        getMainTreeNodes(mainNodes, node.left);

        mainNodes.add(node);

        getMainTreeNodes(mainNodes, node.right);
    }

    private static void writeSubTree(FileWriter fw, int medNumber, NewBST.Node node) throws IOException {
        if (node == null) {
            return;
        }

        writeSubTree(fw, medNumber, node.left);

        fw.write("Médicament" + medNumber + " " + node.quantity + " " + node.expirationDate + '\n');

        writeSubTree(fw, medNumber, node.right);

    }
}
