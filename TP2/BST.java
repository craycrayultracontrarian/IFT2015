import java.time.LocalDate;

import static java.lang.Math.max;

public class BST {
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

    public Node search(int value, Node node) {
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

}