import java.io.*;
import java.util.ArrayList;

public class Prescription {
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

