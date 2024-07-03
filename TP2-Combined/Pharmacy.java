import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;

public class Pharmacy {
    private BST medStock;
    private LocalDate currDate;
    private ArrayList<Commande> orders = new ArrayList<>();

    public Pharmacy() {
        medStock = new BST();
    }

    public ArrayList<Commande> getOrders() {
        return orders;
    }

    public void writeStock(FileWriter fileWriter) throws IOException {
        fileWriter.write("STOCK " + currDate + '\n');

        writeMeds(fileWriter, medStock.getRoot());

        fileWriter.write('\n');
        fileWriter.flush();
    }

    private void writeMeds(FileWriter fileWriter, BST.Node node) throws IOException {
        if (node == null) {
            return;
        }

        writeMeds(fileWriter, node.left);

        writeMedSubtree(fileWriter, node.value, node.subtree.getRoot());

        writeMeds(fileWriter, node.right);
    }

    private void writeMedSubtree(FileWriter fileWriter, int medId, BST.Node node) throws IOException {
        if (node == null) {
            return;
        }

        writeMedSubtree(fileWriter, medId ,node.left);

        fileWriter.write("Médicament" + medId + " " + node.quantity + " " + node.expirationDate + "\n");

        writeMedSubtree(fileWriter, medId ,node.right);
    }

    // Update date (up-Date hehe)
    public void upDate(LocalDate newDate, FileWriter fileWriter) throws IOException {
        currDate = newDate;
        fileWriter.write(newDate.toString());

        if (orders.isEmpty()) {
            fileWriter.write(" OK\n\n");
        } else {
            fileWriter.write( " COMMANDES :\n");

            for (Commande order : orders) {
                fileWriter.write(order.name + " " + order.amount + '\n');
            }

            fileWriter.write('\n');
        }
        removeExpiredMedications();

        orders.clear();
        fileWriter.flush();
    }

    public void handleApprov(FileWriter fileWriter, BufferedReader bufferedReader) throws IOException {
        fileWriter.write("APPROV OK" + "\n");
        String line;

        // Process lines under "APPROV" condition
        while ( !(line = bufferedReader.readLine()).contains(";") ){
            String[] splitLine = line.split("[ \t]+");

            int value = Integer.parseInt((splitLine[0]).substring(10));
            int quantity = Integer.parseInt(splitLine[1]);
            LocalDate expirationDate = LocalDate.parse(splitLine[2]);

            BST.Node mainMedNode = medStock.search(value);

            if (mainMedNode == null) {
                medStock.insert(value, new BST(quantity, expirationDate));
            } else {
                mainMedNode.subtree.insert(quantity, expirationDate);
            }
        }
        fileWriter.flush();
    }

    public void handlePrescription(BufferedReader bufferedReader, FileWriter fileWriter,
                                          int prescriptionNumber) throws IOException {
        String line;
        fileWriter.write("PRESCRIPTION " + prescriptionNumber + '\n');

        // Read each line until ; to parse all medications of prescription
        while ( !( line = bufferedReader.readLine() ).equals(";") ) {
            String[] splitLine = line.split("[ \t]+");

            String medName = splitLine[0];
            int medId = Integer.parseInt(medName.substring(10));
            int doses = Integer.parseInt(splitLine[1]);
            int reps = Integer.parseInt(splitLine[2]);

            int medsNeeded = doses * reps;
            LocalDate treatmentEnd = currDate.plusDays(medsNeeded);

            BST.Node medNode = medStock.search(medId);

            if (medNode != null) {
                BST.Node medSubNode = findPrescriptionMed(medNode.subtree, medsNeeded, treatmentEnd);

                if (medSubNode != null){
                    subtractStock(medStock, medNode.subtree, medNode, medSubNode, medsNeeded);
                    fileWriter.write(medName + " " + doses + " " + reps + " OK\n");
                } else{
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

    private void addToOrder(Commande newOrder, ArrayList<Commande> orders) {
        // Check if medication has already been ordered, increase amount to order
        // if so
        for (Commande order : orders) {
            if (order.name.equals(newOrder.name)) {
                order.amount += newOrder.amount;
                return;
            }
        }

        // Otherwise just add the new order to list of orders
        orders.add(newOrder);
    }

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
            mainTree.remove(mainNode.value);
        }

    }

    public void removeExpiredMedications() {
        removeExpiredMedications(medStock.getRoot());
    }

    private void removeExpiredMedications(BST.Node node) {
        if (node == null) {
            return;
        }

        // In-order traversal to ensure all nodes are checked
        removeExpiredMedications(node.left);

        if (node.subtree != null) {
            removeExpiredMedicationsFromSubtree(node.subtree.getRoot(), node.subtree);
            if (node.subtree.getRoot() == null) {
                medStock.remove(node.value);  // Remove main node if subtree is empty
            }
        }

        removeExpiredMedications(node.right);
    }

    private void removeExpiredMedicationsFromSubtree(BST.Node subNode, BST subtree) {
        if (subNode == null) {
            return;
        }

        // In-order traversal to ensure all nodes are checked
        removeExpiredMedicationsFromSubtree(subNode.left, subtree);

        if (subNode.expirationDate.isBefore(currDate)) {
            subtree.remove(subNode.expirationDate);  // Remove expired subnode
        }

        removeExpiredMedicationsFromSubtree(subNode.right, subtree);
    }

}
