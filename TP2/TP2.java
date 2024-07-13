import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

import static java.lang.Math.max;

public class TP2 {
    private final BufferedWriter bw;
    private final BufferedReader br;
    int counter = 1;
    String lineToRead;
    BST bstInstance = new BST();
    LocalDate curDate;
    LocalDate expDateNeeded;

    // Constructor
    public TP2(String inputFile, String outputFile) throws IOException {
        bw = new BufferedWriter(new FileWriter(outputFile));
        br = new BufferedReader(new FileReader(inputFile));
    }

    public void main() throws IOException {
        boolean processingAPPROV = false;
        boolean processingPRESCRIPTION = false;
        // Reading the file
        while((lineToRead = br.readLine()) != null) {
            if (lineToRead.endsWith(";")) {
                processingAPPROV = false; // Reset flag when ';' is encountered
                processingPRESCRIPTION = false;
            }
            // Checking for all the keywords
            if (processingAPPROV) {
                if (lineToRead.contains(";")){
                    bw.write("APPROV OK" + "\n");
                }
                // Process lines under "APPROV" condition
                String[] splitLine = lineToRead.split("[ \t]+");
                int value = Integer.parseInt((splitLine[0]).substring(11)); //get int
                BST subTree = bstInstance;
                BST.Node node = bstInstance.search(value);
                int quantity = Integer.parseInt(splitLine[1]);
                LocalDate expirationDate = LocalDate.parse(splitLine[2]);
                restock (value, subTree, node, quantity, expirationDate);
                continue; // Skip to the next iteration to avoid processing other keywords
            }
    
            if (lineToRead.contains("APPROV")) {
                processingAPPROV = true; // Set flag to start special processing on subsequent lines
                continue; // Skip to the next iteration to immediately start processing under "APPROV"
            }

            if (processingPRESCRIPTION) {
                bw.write("PRESCRIPTION" + " " + counter + "\n");
                String[] splitLine = lineToRead.split("[ \t]+");
                int value = Integer.parseInt((splitLine[0]).substring(11)); //get int
                int quantity = Integer.parseInt(splitLine[1]);
                int cycles = Integer.parseInt(splitLine[2]);
                int total = quantity * cycles;
                expDateNeeded = curDate.plusDays(total);
                //check if the medication exists in the stock
                if (bstInstance.search(value) == null) {
                    bw.write(splitLine + "COMMANDE" + "\n");
                    continue;
                }




                // check if it has any expiration dates that are after the needed date 
                // maybe I should print the validated exp dates and their quantities? And then if there is one with enough quantity take from that one? 
                // the logic of the examples lead us to believe that we should take all the quantity from the same exp date, even though it isn't intuitively the most efficient way.
                
                // while exp dates are after needed exp date, check if the quantity is enough
                while (bstInstance.search(value).expirationDate.isAfter(expDateNeeded) || bstInstance.search(value).expirationDate.isEqual(expDateNeeded)) {
                    // if the subtree node currently being analyzed has enough quantity, take from it and break the loop (it should also be the one expiring the soonest greater than the needed date)
                    // maybe store it as a candidate and then take from the soonest time at the end of the loop? 
                    // if the subtree node currently being analyzed does not have enough quantity, ignore it and continue to the next node
                }



                // else write commande and add to commande array

               
                    // return all exp dates that are after the needed date with their quantities attached
                    for (BST.Node node : bstInstance.something(bstInstance.search(value).subtree.getRoot())) {
                        if (node.expirationDate.isAfter(expDateNeeded) || node.expirationDate.isEqual(expDateNeeded)) {
                            bw.write("Médicament" + value + " " + node.quantity + " " + node.expirationDate + "\n");
                        }
                    }
                    
                    bw.write(splitLine + "COMMANDE" + "\n");
                    continue;
                

                // if the exp dates available, in the quantity available are before the needed date, write commande'


                // temp tree = bstInstance.removeBeforeExp(expDateNeeded);
                // print the temp tree for only the medication we want
                // check quantity and make adjustments



                else {
                    // need a function that will take away the medication from the existing stock
                    bstInstance.remove(value, quantity, expDateNeeded);
                    bw.write(splitLine + "OK" + "\n");
                }
                // check if the exp date is all good
                // if yes, write that its OK and remove from the stock
                // if no, write "commande" and add to commande array
                counter ++;
                continue; // Skip to the next iteration to avoid processing other keywords
            }
            if (lineToRead.contains("PRESCRIPTION")) {
                processingPRESCRIPTION = true;
                continue; 
            }
            if (lineToRead.contains("STOCK")) {
                bw.write("Stock " + curDate + '\n');
                bstInstance.printTreeInOrder();
            }
            if (lineToRead.contains("DATE")) {
                String date = lineToRead.split(" ")[1]; //getting raw date
                vali_date(date); //calling the date function
                String vali_date = vali_date(date); //storing the response
                curDate = LocalDate.parse(date); //parsing the date
                bw.write("DATE" + " " + date + vali_date + "\n"); //writing 
                // write the next day's commande array, if application
                // if the array size is > 0, print "commandes" + curDate.plusDays(1) + array (line break after each)
            }
        }
        bw.close();
    }

    public String vali_date(String date) {
        if (LocalDate.parse(date)==null){
           return "INVALIDE";
        } else {
            up_date(date);
            return "OK";
        }
    }

    public void up_date(String date) {
        vali_date(date);
        LocalDate curDate= LocalDate.parse(date);
        bstInstance.removeBeforeExp(curDate);
        // write the current commandes array (not yet implemented)
    }

    public void restock(int value, BST subTree, BST.Node node, int quantity, LocalDate expirationDate) {
        if (bstInstance.search(value) == null) {
            bstInstance.insert(value, subTree, node);
            bstInstance.insert(quantity, expirationDate, node);
        } else {
            bstInstance.insert(quantity, expirationDate, node);    
        }
    }

    


    //from here on below in the TP2 class, I did not refactor / modify the code

    



    class Commande {
        String name;
        int amount;
    
        // Constructor
        Commande(String name, int amount){
            this.name = name;
            this.amount  = amount;
        }
    
        public String getName(){
            return  name;
        }
        public int getAmount(){
            return amount;
        }
    }

    class Prescription {
    // these may not be necessary anymore since I implemented the 
    // buffer writer in the main class, as well as the BST instance
    private final BufferedWriter bw;
    private final BufferedReader br;
    BST BSTStock =  new BST();
    int counter = 1;
    String lineToRead;

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
    }

    class WriteStock {
        public static void writeStock(BST medStock, String outputFileName, LocalDate currDate) throws IOException {
            FileWriter fw = new FileWriter(outputFileName);
    
            fw.write("Stock " + currDate + '\n');
    
        // Create array to store all nodes in the main tree
            ArrayList<BST.Node> mainNodes = getMainTreeNodes(medStock.getRoot());
    
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
}


class BST {
    private Node root;

    public BST() {

    }

    public BST(int quantity, LocalDate expirationDate) {
        root = new Node(quantity, expirationDate);
    }

    public Node getRoot() {
        return root;
    }

    public class Node {
        Node left, right;
        int height;

        // For main tree
        int value;
        BST subtree;

        // For sub-tree
        int quantity;
        LocalDate expirationDate;

        // Constructor for main tree node
        public Node(int value, BST subtree) {
            this.value = value;
            this.subtree = subtree;
        }

        // Constructor for subtree node
        public Node(int quantity, LocalDate expirationDate) {
            this.quantity = quantity;
            this.expirationDate = expirationDate;
        }
    }

    // Get height of node
    private int height(Node node) {
        return node != null ? node.height : -1;
    }

    // Update the height of 
    private void updateHeight(Node node) {
        int leftChildHeight = height(node.left);
        int rightChildHeight = height(node.right);
        node.height = max(leftChildHeight, rightChildHeight) + 1;
    }

    private int balanceFactor(Node node) {
        return height(node.right) - height(node.left);
    }

    private Node rotateRight(Node node) {
        Node leftChild = node.left;

        node.left = leftChild.right;
        leftChild.right = node;

        updateHeight(node);
        updateHeight(leftChild);

        return leftChild;
    }

    private Node rotateLeft(Node node) {
        Node rightChild = node.right;

        node.right = rightChild.left;
        rightChild.left = node;

        updateHeight(node);
        updateHeight(rightChild);

        return rightChild;
    }

    private Node rebalance(Node node) {
        int balanceFactor = balanceFactor(node);

        // Left-heavy?
        if (balanceFactor < -1) {
            if (balanceFactor(node.left) <= 0) {    // Case 1
                // Rotate right
                node = rotateRight(node);
            } else {                                // Case 2
                // Rotate left-right
                node.left = rotateLeft(node.left);
                node = rotateRight(node);
            }
        }

        // Right-heavy?
        if (balanceFactor > 1) {
            if (balanceFactor(node.right) >= 0) {    // Case 3
                // Rotate left
                node = rotateLeft(node);
            } else {                                 // Case 4
                // Rotate right-left
                node.right = rotateRight(node.right);
                node = rotateLeft(node);
            }
        }

        return node;
    }

    // For main tree
    private Node unbalancedInsert(int value, BST subtree, Node node) {
        if (node == null) {
            node = new Node(value, subtree);
        } else if (value < node.value) {
            node.left = insert(value, subtree, node.left);
        } else if (value > node.value) {
            node.right = insert(value, subtree, node.right);
        }

        return node;
    }

    // For sub tree
    private Node unbalancedInsert(int quantity, LocalDate expirationDate, Node node) {
        if (node == null) {
            node = new Node(quantity, expirationDate);
        } else if (expirationDate.isBefore(node.expirationDate)) {
            node.left = insert(quantity, expirationDate, node.left);
        } else if (expirationDate.isAfter(node.expirationDate)) {
            node.right = insert(quantity, expirationDate, node.right);
        } else {
            node.quantity += quantity;
        }

        return node;
    }

    public void insert(int value, BST subtree) {
        root = insert(value, subtree, root);
    }

    // Insertion in main tree (!!!make sure the value isn't already in the tree!!!!)
    public Node insert(int value, BST subtree, Node node) {
        node = unbalancedInsert(value, subtree, node);

        updateHeight(node);

        return rebalance(node);
    }

    public void insert(int quantity, LocalDate expirationDate){
        root = insert(quantity, expirationDate, root);
    }

    // Insertion in subtree (no need to check if date already in tree)
    public Node insert(int quantity, LocalDate expirationDate, Node node) {
        node = unbalancedInsert(quantity, expirationDate, node);

        updateHeight(node);

        return rebalance(node);
    }

    // For main tree
    private Node unbalancedDeleteNode(int value, Node node) {
        // No node at current position --> go up the recursion
        if (node == null) {
            return null;
        }

        // Traverse the tree to the left or right depending on the key
        if (value < node.value) {
            node.left = unbalancedDeleteNode(value, node.left);
        } else if (value > node.value) {
            node.right = unbalancedDeleteNode(value, node.right);
        }

        // At this point, "node" is the node to be deleted

        // Node has no children --> just delete it
        else if (node.left == null && node.right == null) {
            node = null;
        }

        // Node has only one child --> replace node by its single child
        else if (node.left == null) {
            node = node.right;
        } else if (node.right == null) {
            node = node.left;
        }

        // Node has two children
        else {
            deleteNodeWithTwoChildren(node);
        }

        return node;
    }

    // For subtree
    private Node unbalancedDeleteNode(LocalDate expirationDate, Node node) {
        // No node at current position --> go up the recursion
        if (node == null) {
            return null;
        }

        // Traverse the tree to the left or right depending on the key
        if (expirationDate.isBefore(node.expirationDate)) {
            node.left = unbalancedDeleteNode(expirationDate, node.left);
        } else if (expirationDate.isAfter(node.expirationDate)) {
            node.right = unbalancedDeleteNode(expirationDate, node.right);
        }

        // At this point, "node" is the node to be deleted

        // Node has no children --> just delete it
        else if (node.left == null && node.right == null) {
            node = null;
        }

        // Node has only one child --> replace node by its single child
        else if (node.left == null) {
            node = node.right;
        } else if (node.right == null) {
            node = node.left;
        }

        // Node has two children
        else {
            deleteNodeWithTwoChildren(node);
        }

        return node;
    }

    // For main tree
    public void remove(int value) {
        root = remove(value, root);
    }

    private Node remove(int value, Node node) {
        node = unbalancedDeleteNode(value, node);

        updateHeight(node);

        return rebalance(node);
    }

    // For subtree
    public void remove(LocalDate expirationDate) {
        root = remove(expirationDate, root);
    }

    private Node remove(LocalDate expirationDate, Node node) {
        node = unbalancedDeleteNode(expirationDate, node);

        updateHeight(node);

        return rebalance(node);
    }

    private void deleteNodeWithTwoChildren(Node node) {
        // Find minimum node of right subtree ("inorder successor" of current node)
        Node inOrderSuccessor = findMinimum(node.right);

        // Copy inorder successor's data to current node
        node.value = inOrderSuccessor.value;
        node.subtree = inOrderSuccessor.subtree;
        node.quantity = inOrderSuccessor.quantity;
        node.expirationDate = inOrderSuccessor.expirationDate;

        // Delete inorder successor recursively
        if (node.subtree != null) {
            node.right = unbalancedDeleteNode(inOrderSuccessor.value, node.right);
        } else {
            node.right = unbalancedDeleteNode(inOrderSuccessor.expirationDate, node.right);
        }
    }

    private Node findMinimum(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    public Node search (int value) {
        return search(value, root);
    }

    private Node search(int value, Node node) {
        if (node == null) {
            return null;
        }

        if (value < node.value) {
            search(value, node.left);
        } else if (value > node.value) {
            search(value, node.right);
        } else {
            return node;
        }

        return null;
    }

    public void printTreeInOrder() {
        printTreeInOrder(root);
    }

    private void printTreeInOrder(Node node) {
        if (node == null) {
            return;
        }

        printTreeInOrder(node.left);

        if (node.subtree != null) {
            System.out.println("Medicament" + node.value);
            printTreeInOrder(node.subtree.root);
        } else {
            System.out.println("Stock: " + node.quantity);
            System.out.println("Expiration date: " + node.expirationDate);
        }

        printTreeInOrder(node.right);
    }
    // remove before Exp function to be used during date validation function
    public void removeBeforeExp(LocalDate date) {
        root = removeBeforeExp(root, date);
    }
    
    private Node removeBeforeExp(Node node, LocalDate date) {
        if (node == null) {
            return null;
        }
    
        node.left = removeBeforeExp(node.left, date);
        node.right = removeBeforeExp(node.right, date);
    
        if (node.expirationDate != null && node.expirationDate.isBefore(date)) {
            if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            } else {
                Node temp = findMinimum(node.right);
                node.expirationDate = temp.expirationDate;
                node.quantity = temp.quantity;
                node.right = removeBeforeExp(node.right, node.expirationDate);
            }
        }
        return node;
    }
}
