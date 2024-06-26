import java.io.*;
import java.util.ArrayList;

public class PathFinder {
    String infoFileName, resultFileName;
    int maxLoad, boxesToCollect;
    double[] startCoords;
    ArrayList<Warehouse> warehouses = new ArrayList<>();

    // this function runs in O(n) time because of the for loop
    public void run(String[] fileNames) throws IOException {
        infoFileName = fileNames[0];
        resultFileName = fileNames[1];

        // Get max load of truck, number of boxes to collect and store in maxLoad
        // and boxesToCollect respectively
        getTruckInfoFromFile(infoFileName);
        startCoords = getWarehousesFromFile(infoFileName);

        // Calculate distance between the truck and each warehouse
        for (Warehouse w : warehouses) {   // O(n)
            w.setDistToStart(startCoords);
        }

        // Sort the list of warehouses by ascending distance
        selectionSort(warehouses);

        ArrayList<Warehouse> visited = collectBoxes(warehouses);
        writeResult(visited, resultFileName);
    }

    // This function runs in O(1) time
    private void getTruckInfoFromFile(String filename) throws IOException {
        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);

        // Get maximum load of truck and boxes to collect from first line of file
        String[] truckInfo = br.readLine().split(" ");
        boxesToCollect = Integer.parseInt(truckInfo[0]);
        maxLoad = Integer.parseInt(truckInfo[1]);
    }

    // Parse file for info of warehouses and return coordinates of warehouse
    // with the greatest number of boxes. It runs in O(n) time
    private double[] getWarehousesFromFile(String filename) throws IOException {
        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);

        // Skip first line with truck info
        br.readLine();

        // Counter for greatest max number of boxes in a warehouse
        int maxBoxes = 0;
        double[] startCoords = new double[2];

        // Iterate through all lines in file to get info of each warehouse
        String line = br.readLine();

        // The while and for loops together in O(n) time
        while (line != null) {
            String[] splitLine = line.split(" ");

            // Iterate through elements of line 2 by 2 to get boxes and coordinates
            // of each warehouse
            for (int i = 0; i < splitLine.length; i += 2) {
                int boxes = Integer.parseInt(splitLine[i]);

                String coordsString = splitLine[i+1];
                double[] coordinates = getCoordsFromString(coordsString);

                if (boxes > maxBoxes) {
                    maxBoxes = boxes;
                    startCoords = coordinates;
                }

                warehouses.add(new Warehouse(boxes, coordinates));
            }
            // Next line
            line = br.readLine();
        }

        return startCoords;
    }

    // This function runs in O(1) time
    private double[] getCoordsFromString(String coordsString) {
        // Clean the string to remove all parentheses { "(", ")" } and spaces { " " }
        String cleanCoordsString = coordsString.replace("(", "").replace(")", "").replace(" ", "");
        String[] splitCoords = cleanCoordsString.split(",");

        return new double[] {Double.parseDouble(splitCoords[0]), Double.parseDouble(splitCoords[1])};
    }

    // This function runs in O(n^2) time because of the nested for loop inside the other for loop
    private void selectionSort(ArrayList<Warehouse> warehouses) {
        int startIndex, minIndex;
        double minDist;
        int warehouseSize = warehouses.size();

        //The two for loops run together in a O(n^2) time because of O(n)*O(n)
        for (int i = 0; i < warehouseSize; i++) {    // O(n)
            startIndex = minIndex = i;
            minDist = warehouses.get(i).getDistToStart();

            for (int j = startIndex + 1; j < warehouseSize; j++) {  // O(n)
                double distance = warehouses.get(j).getDistToStart();

                if (distance < minDist) {
                    minDist = distance;
                    minIndex = j;
                }
            }

            Warehouse tmp = warehouses.get(startIndex);
            warehouses.set(startIndex, warehouses.get(minIndex));
            warehouses.set(minIndex, tmp);
        }
    }

    // Runs in O(n) time
    private ArrayList<Warehouse> collectBoxes(ArrayList<Warehouse> warehouses) {
        int i = 0;
        ArrayList<Warehouse> visited = new ArrayList<>();

        // Iterate through the sorted list of warehouses and storing each visited
        // in an array until the required number of boxes have been collected
        while (boxesToCollect != 0 && i < warehouses.size()) {  // O(n)
            Warehouse warehouse = warehouses.get(i);
            int boxesInWarehouse = warehouse.getBoxes();

            if (boxesInWarehouse > boxesToCollect) {
                warehouse.setBoxes(boxesInWarehouse - boxesToCollect);
                boxesToCollect = 0;

            } else {
                boxesToCollect -= boxesInWarehouse;
                warehouse.setBoxes(0);
            }

            visited.add(warehouse);
            i++;
        }

        return visited;
    }

    //Runs in O(n) time because of the for loop
    private void writeResult(ArrayList<Warehouse> visited, String resultFileName) throws IOException {
        File resultFile = new File(resultFileName);

        if (!resultFile.exists()) {
            resultFile.createNewFile();
        }

        FileWriter fileWriter = new FileWriter(resultFileName);

        fileWriter.write("Truck position: (" + startCoords[0] + ',' + startCoords[1] + ")\n");

        for (int i = 0; i < visited.size(); i++) {    // O(n)
            Warehouse w = visited.get(i);

            if (i == 0) {
                fileWriter.write("Distance:" + (int) w.getRoundedDist() + "\t\tNumber of boxes:"
                        + w.getBoxes() + "\t\tPosition:(" + w.getCoordinates()[0] + ","
                        + w.getCoordinates()[1] + ")\n");
            } else {
                fileWriter.write("Distance:" + w.getRoundedDist() + "\t\tNumber of boxes:"
                        + w.getBoxes() + "\t\tPosition:(" + w.getCoordinates()[0] + ","
                        + w.getCoordinates()[1] + ")\n");
            }
        }

        fileWriter.close();
    }

    // For testing only!!
    public ArrayList<Warehouse> getWarehouses() {
        return warehouses;
    }
}