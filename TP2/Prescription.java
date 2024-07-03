import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class Prescription {

    // Should refactor parameters for this in the final file
    public static void handlePrescription(BufferedReader bufferedReader, FileWriter fileWriter,
                                   int prescriptionNumber, LocalDate currDate, BST medStock,
                                   ArrayList<Commande> orders) throws IOException {
        String line;
        fileWriter.write("\nPRESCRIPTION " + prescriptionNumber + '\n');

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
                //System.out.println(medSubNode.expirationDate);

                if (medSubNode != null){
                    subtractStock(medStock, medNode.subtree, medNode, medSubNode, medsNeeded);
                    fileWriter.write(line + " OK\n");
                } else{
                    addToOrder(new Commande(medName, medsNeeded), orders);
                    fileWriter.write(line + " COMMANDE\n");
                }
            } else {
                addToOrder(new Commande(medName, medsNeeded), orders);
                fileWriter.write(line + " COMMANDE\n");
            }
        }
    }

    private static void addToOrder(Commande newOrder, ArrayList<Commande> orders) {
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

    private static BST.Node findPrescriptionMed(BST subtree, int medsNeeded, LocalDate treatmentEnd) {
        return findPrescriptionMed(subtree.getRoot(), medsNeeded, treatmentEnd);
    }

    // Return the node containing adequate amount of medication that will not expire
    // before the end of treatment if exists, null otherwise
    private static BST.Node findPrescriptionMed(BST.Node node, int medsNeeded, LocalDate treatmentEnd) {
        if (node == null) {
            return null;
        }
        // Traverse subtree in order to find medication with expiration date closest
        // to date of end of treatment with enough pills
        findPrescriptionMed(node.left, medsNeeded, treatmentEnd);

        if (node.expirationDate.isAfter(treatmentEnd) && node.quantity >= medsNeeded) {
            return node;
        }

        findPrescriptionMed(node.right, medsNeeded, treatmentEnd);

        return null;
    }

    // Subtract amount of meds needed from stock, if this reduces stock to 0,
    // remove the subnode, if there is no stock left of the medication, remove
    // main node of medication
    private static void subtractStock(BST mainTree, BST subTree, BST.Node mainNode ,BST.Node subNode, int amount) {
        subNode.quantity -= amount;

        if (subNode.quantity == 0) {
            subTree.remove(subNode.expirationDate);
        }

        if (mainNode.subtree.getRoot() == null) {
            System.out.println("a");
            mainTree.remove(mainNode.value);
        }

    }

    /*

    // Constructor
    public Prescription(String inputFile, String outputFile) throws IOException {
        FileWriter fw = new FileWriter(outputFile);
        bw = new BufferedWriter(fw);
        br = new BufferedReader(new FileReader(inputFile));
    }

    public void prescriptionName() throws IOException {
        while((lineToRead = br.readLine()) != null) {
            if (lineToRead.contains("PRESCRIPTION")) {
                bw.write("PRESCRIPTION" + " " + counter + "\n");
                counter ++;
                bw.close();
            }
        }
    }

    public void stockChecker() throws IOException {
            ArrayList <Commande> orders = new ArrayList<>();

            // Split on empty spaces or tabs
            String[] sections = lineToRead.split("[ \t]+");

            String name = sections[0]; // Medicines' names

            int dose = Integer.parseInt(sections[1]);
            int repetition = Integer.parseInt(sections[2]);
            int qtyNeeded = dose * repetition;

            Commande commande = new Commande(name, qtyNeeded);
    }
    */
}

