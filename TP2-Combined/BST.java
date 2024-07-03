import java.time.LocalDate;

import static java.lang.Math.max;

public class BST {
    private Node root;

    public BST() {}

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
        String medName;
        BST subtree;

        // For sub-tree
        int quantity;
        LocalDate expirationDate;

        // Constructor for main tree node
        public Node(String medName, BST subtree) {
            this.medName = medName;
            this.subtree = subtree;
        }

        // Constructor for subtree node
        public Node(int quantity, LocalDate expirationDate) {
            this.quantity = quantity;
            this.expirationDate = expirationDate;
        }
    }

    private int height(Node node) {
        return node != null ? node.height : -1;
    }

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
    private Node unbalancedInsert(String medName, BST subtree, Node node) {
        if (node == null) {
            node = new Node(medName, subtree);
        } else if (medName.compareTo(node.medName) < 0) {
            node.left = insert(medName, subtree, node.left);
        } else if (medName.compareTo(node.medName) > 0) {
            node.right = insert(medName, subtree, node.right);
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

    public void insert(String medName, BST subtree) {
        root = insert(medName, subtree, root);
    }

    // Insertion in main tree (!!!make sure the value isn't already in the tree!!!!)
    public Node insert(String medName, BST subtree, Node node) {
        node = unbalancedInsert(medName, subtree, node);

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
    private Node unbalancedDeleteNode(String medName, Node node) {
        // No node at current position --> go up the recursion
        if (node == null) {
            return null;
        }

        // Traverse the tree to the left or right depending on the key
        if (medName.compareTo(node.medName) < 0) {
            node.left = unbalancedDeleteNode(medName, node.left);
        } else if (medName.compareTo(node.medName) > 0) {
            node.right = unbalancedDeleteNode(medName, node.right);
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
    public void remove(String medName) {
        root = remove(medName, root);
    }

    private Node remove(String medName, Node node) {
        node = unbalancedDeleteNode(medName, node);

        if (node == null) {
            return null;
        }

        updateHeight(node);

        return rebalance(node);
    }

    // For subtree
    public void remove(LocalDate expirationDate) {
        root = remove(expirationDate, root);
    }

    private Node remove(LocalDate expirationDate, Node node) {
        node = unbalancedDeleteNode(expirationDate, node);

        if (node == null) {
            return null;
        }

        updateHeight(node);

        return rebalance(node);
    }

    private void deleteNodeWithTwoChildren(Node node) {
        // Find minimum node of right subtree ("inorder successor" of current node)
        Node inOrderSuccessor = findMinimum(node.right);

        // Copy inorder successor's data to current node
        node.medName = inOrderSuccessor.medName;
        node.subtree = inOrderSuccessor.subtree;
        node.quantity = inOrderSuccessor.quantity;
        node.expirationDate = inOrderSuccessor.expirationDate;

        // Delete inorder successor recursively
        if (node.subtree != null) {
            node.right = unbalancedDeleteNode(inOrderSuccessor.medName, node.right);
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

    public Node search(String medName) {
        return search(medName, root);
    }

    public Node search(String medName, Node node) {
        if (node == null) {
            return null;
        }

        if (medName.compareTo(node.medName) < 0) {
            return search(medName, node.left);
        } else if (medName.compareTo(node.medName) > 0) {
            return search(medName, node.right);
        } else {
            return node;
        }
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
            System.out.println(node.medName);
            printTreeInOrder(node.subtree.root);
        } else {
            System.out.println("Stock: " + node.quantity);
            System.out.println("Expiration date: " + node.expirationDate);
        }

        printTreeInOrder(node.right);
    }

    public void clear() {
        clear(root);
        root = null;  // Set root to null after clearing all nodes
    }

    private void clear(BST.Node node) {
        if (node == null) {
            return;
        }

        // Post-order traversal to ensure all nodes are removed
        clear(node.left);
        clear(node.right);

        // Clear the subtree if it exists
        if (node.subtree != null) {
            node.subtree.clear();  // Clear the subtree
        }

        // After clearing children and subtree, remove this node
        node = null;
    }

    /*
    public void clear(BST.Node node) {
        if (node == null) {
            return;
        }

        // In-order traversal to ensure all nodes are checked
        clear(node.left);

        remove(node.medName);  // Remove main node if subtree is empty

        clear(node.right);
    } */
}
