import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class Pharmacy {
    private BST medStock;
    private LocalDate currDate;
    private BST orders = new BST();

    public Pharmacy() {
        medStock = new BST();
    }

    // Write the current stock into the file
    public void writeStock(FileWriter fileWriter) throws IOException {
        fileWriter.write("STOCK " + currDate + '\n');

        writeMeds(fileWriter, medStock.getRoot());

        // Make an empty line after stock
        fileWriter.write('\n');
        fileWriter.flush();
    }

    // Go through stock tree in order to write all medications
    private void writeMeds(FileWriter fileWriter, BST.Node node) throws IOException {
        if (node == null) {
            return;
        }

        writeMeds(fileWriter, node.left);

        // Go into subtree of each medication
        writeMedSubtree(fileWriter, node.medName, node.subtree.getRoot());

        writeMeds(fileWriter, node.right);
    }

    // Used to go through subtree of a medication in order to get every variation
    // Expiration date + quantity
    private void writeMedSubtree(FileWriter fileWriter, String medName, BST.Node node) throws IOException {
        if (node == null) {
            return;
        }

        writeMedSubtree(fileWriter, medName, node.left);

        fileWriter.write(medName + " " + node.quantity + " " + node.expirationDate + "\n");

        writeMedSubtree(fileWriter, medName, node.right);
    }

    // Update date (up-Date hehe)
    public void upDate(LocalDate newDate, FileWriter fileWriter) throws IOException {
        currDate = newDate;
        fileWriter.write(newDate.toString());

        // Check if orders were made
        // If not then just write OK after date
        if (orders.getRoot() == null) {
            fileWriter.write(" OK\n\n");
        } else { // If yes then write COMMANDES and write the orders placed
            fileWriter.write( " COMMANDES :\n");

            writeOrders(orders.getRoot(), fileWriter);

            fileWriter.write('\n');
        }
        // Remove all medications that have expired by the new date
        removeExpiredMedications();

        // Clear placed orders
        orders.clear();
        fileWriter.flush();
    }

    // Go through the orders tree in order to write each type ordered
    private void writeOrders(BST.Node node, FileWriter fileWriter) throws IOException {
        if (node == null) {
            return;
        }

        writeOrders(node.left, fileWriter);

        fileWriter.write(node.medName + " " + node.subtree.getRoot().quantity + '\n');

        writeOrders(node.right, fileWriter);
    }

    // Handle the APPROV transaction
    public void handleApprov(FileWriter fileWriter, BufferedReader bufferedReader) throws IOException {
        fileWriter.write("APPROV OK" + "\n");
        String line;

        // Process lines under "APPROV" condition
        while ( !(line = bufferedReader.readLine()).contains(";") ){
            String[] splitLine = line.split("[ \t]+");

            // Get attributes of medication
            String medName = splitLine[0];
            int quantity = Integer.parseInt(splitLine[1]);
            LocalDate expirationDate = LocalDate.parse(splitLine[2]);

            // Search for the medication
            BST.Node mainMedNode = medStock.search(medName);

            // If the medication is expired, dont put it in stock
            if ((currDate != null) && (expirationDate.isBefore(currDate) || expirationDate.isEqual(currDate))) {
                continue;
            }
            // If that medication isn't already in stock then insert its node
            // into the main tree
            if (mainMedNode == null) {
                medStock.insert(medName, new BST(quantity, expirationDate));
            } else { // Otherwise add that specific expiration date and quantity
                     // into that medications subtree
                mainMedNode.subtree.insert(quantity, expirationDate);
            }
        }

        fileWriter.flush();
    }

    // Handle the PRESCRIPTION transaction
    public void handlePrescription(BufferedReader bufferedReader, FileWriter fileWriter,
                                          int prescriptionNumber) throws IOException {
        String line;
        fileWriter.write("PRESCRIPTION " + prescriptionNumber + '\n');

        // Read each line until ; to parse all medications of prescription
        while ( !( line = bufferedReader.readLine() ).equals(";") ) {
            String[] splitLine = line.split("[ \t]+");

            // Get attributes of medication
            String medName = splitLine[0];
            int doses = Integer.parseInt(splitLine[1]);
            int reps = Integer.parseInt(splitLine[2]);

            // Get amount of the medication needed and the date for end of treatment
            int medsNeeded = doses * reps;
            LocalDate treatmentEnd = currDate.plusDays(medsNeeded);

            // Search for the medication
            BST.Node medNode = medStock.search(medName);

            // If the medication exists in the tree, check if there's enough that
            // won't be expired by the end of the treatment
            if (medNode != null) {
                BST.Node medSubNode = findPrescriptionMed(medNode.subtree, medsNeeded, treatmentEnd);
                
                // If there is enough, subtract the amount from the stock and write 
                // OK to file
                if (medSubNode != null){
                    subtractStock(medStock, medNode.subtree, medNode, medSubNode, medsNeeded);
                    fileWriter.write(medName + " " + doses + " " + reps + " OK\n");
                } else{ // Otherwise add it to the order and write COMMANDE to file
                    addToOrder(new Commande(medName, medsNeeded), orders);
                    fileWriter.write(medName + " " + doses + " " + reps + " COMMANDE\n");
                }
            } else {
                addToOrder(new Commande(medName, medsNeeded), orders);
                fileWriter.write(medName + " " + doses + " " + reps + " COMMANDE\n");
            }
        }

        fileWriter.write('\n');
        fileWriter.flush();
    }

    // Add a given order to the orders list
    private void addToOrder(Commande newOrder, BST orders) {
        BST.Node orderNode = orders.search(newOrder.name);

        // Check if medication has already been ordered, increase amount to order
        // if so
        if (orderNode != null) {
            orderNode.subtree.getRoot().quantity += newOrder.amount;
            return;
        }

        // Otherwise just add the new order to list of orders
        orders.insert(newOrder.name, new BST(newOrder.amount, null));
    }

    // Find the node containing a given medication
    private BST.Node findPrescriptionMed(BST subtree, int medsNeeded, LocalDate treatmentEnd) {
        return findPrescriptionMed(subtree.getRoot(), medsNeeded, treatmentEnd);
    }

    // Return the node containing adequate amount of medication that will not expire
    // before the end of treatment if exists, null otherwise
    private BST.Node findPrescriptionMed(BST.Node node, int medsNeeded, LocalDate treatmentEnd) {
        if (node == null) {
            return null;
        }
        // Traverse subtree in order to find medication with expiration date closest
        // to date of end of treatment with enough pills
        //findPrescriptionMed(node.left, medsNeeded, treatmentEnd);

        BST.Node foundNode = findPrescriptionMed(node.left, medsNeeded, treatmentEnd);
        if (foundNode != null) {
            return foundNode;
        }

        if ( (node.expirationDate.isAfter(treatmentEnd) || node.expirationDate.isEqual(treatmentEnd) ) && node.quantity >= medsNeeded) {
            return node;
        }

        return findPrescriptionMed(node.right, medsNeeded, treatmentEnd);
    }

    // Subtract amount of meds needed from stock, if this reduces stock to 0,
    // remove the subnode, if there is no stock left of the medication, remove
    // main node of medication
    private void subtractStock(BST mainTree, BST subTree, BST.Node mainNode ,BST.Node subNode, int amount) {
        subNode.quantity -= amount;

        if (subNode.quantity <= 0) {
            subTree.remove(subNode.expirationDate);
        }

        if (mainNode.subtree != null && mainNode.subtree.getRoot() == null) {
            mainTree.remove(mainNode.medName);
        }

    }

    // Remove all expired medications from the stock
    public void removeExpiredMedications() {
        while (hasExpiredMedications()) {
            removeExpiredMedications(medStock.getRoot());
        }
    }

    // Go through the stock tree in post order and remove expired medications
    private void removeExpiredMedications(BST.Node node) {
        ArrayList<BST.Node> subNodesToRemove = new ArrayList<>();

        if (node == null) {
            return;
        }

        // Use post-order traversal to avoid modifying the tree during traversal
        removeExpiredMedications(node.left);
        removeExpiredMedications(node.right);

        if (node.subtree != null) {
            getExpiredMedicationsFromSubtree(node.subtree.getRoot(), subNodesToRemove);

            for (BST.Node nodeToRemove : subNodesToRemove) {
                node.subtree.remove(nodeToRemove.expirationDate);
            }

            if (node.subtree.getRoot() == null) {
                medStock.remove(node.medName);
            }
        }
    }

    // Helper method to remove expired medications from the subtrees
    private void getExpiredMedicationsFromSubtree(BST.Node node, ArrayList<BST.Node> nodesToRemove) {
        if (node == null) {
            return;
        }

        getExpiredMedicationsFromSubtree(node.left, nodesToRemove);

        if (node.expirationDate.isBefore(currDate) || node.expirationDate.isEqual(currDate)) {
            nodesToRemove.add(node);
        }

        getExpiredMedicationsFromSubtree(node.right, nodesToRemove);
    }

    // Return true if there are expired medications in stock, false otherwise
    public boolean hasExpiredMedications() {
        return checkExpiredMedications(medStock.getRoot());
    }

    
    private boolean checkExpiredMedications(BST.Node node) {
        if (node == null) {
            return false;
        }

        // Use in-order traversal to check nodes
        if (checkExpiredMedications(node.left)) {
            return true;
        }

        if (node.subtree != null) {
            if (checkExpiredMedicationsInSubtree(node.subtree.getRoot())) {
                return true;
            }
        } else if (node.expirationDate != null && node.expirationDate.isBefore(currDate)) {
            return true;
        }

        return checkExpiredMedications(node.right);
    }

    private boolean checkExpiredMedicationsInSubtree(BST.Node subNode) {
        if (subNode == null) {
            return false;
        }

        if (checkExpiredMedicationsInSubtree(subNode.left)) {
            return true;
        }

        if (subNode.expirationDate.isBefore(currDate)) {
            return true;
        }

        return checkExpiredMedicationsInSubtree(subNode.right);
    }
}
