import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public void main(String[] args) throws IOException {
        long startTime = System.nanoTime();

        // Check number of arguments to know which file is input/output
        if (args.length != 1 && args.length != 2) {
            throw new IllegalArgumentException("Exactly 1 or 2 arguments required!");
        } else {
            PathFinder pathFinder = new PathFinder();

            pathFinder.run(args);
        }

        // Get max load of truck, number of boxes to collect and store in maxLoad
        // and boxesToCollect respectively
        getTruckInfoFromFile(infoFileName);
        getWarehousesFromFile(infoFileName);

        startCoords = getStartCoordinates(warehouses);

        // Calculate distance between the truck and each warehouse
        for (Warehouse w : warehouses) {
            w.setDistToStart(startCoords);
        }

        // Sort the list of warehouses by ascending distance
        selectionSort(warehouses);

        ArrayList<Warehouse> visited = collectBoxes(warehouses);

        if (args.length == 2) {
            writeResult(visited, resultFileName);
        }

        long endTime = System.nanoTime() - startTime;
        writeTime(warehouses.size(), endTime / 1E6, "time1.txt");

    }

    private static void writeTime(int size, double runTime, String filename) throws IOException {
        FileWriter fileWriter = new FileWriter(filename, true);

        fileWriter.write(size + "," + runTime + "\n");
        fileWriter.close();
    }

    private void getTruckInfoFromFile(String filename) throws IOException{
        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);

        // Get maximum load of truck and boxes to collect from first line of file
        String[] truckInfo = br.readLine().split(" ");
        boxesToCollect = Integer.parseInt(truckInfo[0]);
        maxLoad = Integer.parseInt(truckInfo[1]);
    }

    private void getWarehousesFromFile(String filename) throws IOException {
        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);

        // Skip first line with truck info
        br.readLine();

        // Iterate through all lines in file to get info of each warehouse
        String line = br.readLine();
        while (line != null) {
            String[] splitLine = line.split(" ");

            // Iterate through elements of line 2 by 2 to get boxes and coordinates
            // of each warehouse
            for (int i = 0; i < splitLine.length; i += 2) {
                int boxes = Integer.parseInt(splitLine[i]);

                String coordsString = splitLine[i+1];
                double[] coordinates = getCoordsFromString(coordsString);

                warehouses.add(new Warehouse(boxes, coordinates));
            }
            // Next line
            line = br.readLine();
        }
    }

    private double[] getCoordsFromString(String coordsString) {
        // Clean the string to remove all parentheses { "(", ")" } and spaces { " " }
        String cleanCoordsString = coordsString.replace("(", "").replace(")", "").replace(" ", "");
        String[] splitCoords = cleanCoordsString.split(",");

        return new double[] {Double.parseDouble(splitCoords[0]), Double.parseDouble(splitCoords[1])};
    }

    private double[] getStartCoordinates(ArrayList<Warehouse> warehouses) {
        int max = 0;
        int maxIndex = 0;

        for (int i = 0; i < warehouses.size(); i++) {
            if (warehouses.get(i).getBoxes() > max) {
                maxIndex = i;
                max = warehouses.get(i).getBoxes();
            }
        }

        return warehouses.get(maxIndex).getCoordinates();
    }

    private void selectionSort(ArrayList<Warehouse> warehouses) {
        int startIndex, minIndex;
        double minDist;
        int warehouseSize = warehouses.size();

        for (int i = 0; i < warehouseSize; i++) {
            startIndex = minIndex = i;
            minDist = warehouses.get(i).getDistToStart();

            for (int j = startIndex + 1; j < warehouseSize; j++) {
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

    private ArrayList<Warehouse> collectBoxes(ArrayList<Warehouse> warehouses) {
        int i = 0;
        ArrayList<Warehouse> visited = new ArrayList<>();

        // Iterate through the sorted list of warehouses and storing each visited
        // in an array until the required number of boxes have been collected
        while (boxesToCollect != 0 && i < warehouses.size()) {
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

    private void writeResult(ArrayList<Warehouse> visited, String resultFileName) throws IOException {
        File resultFile = new File(resultFileName);

        if (!resultFile.exists()) {
            resultFile.createNewFile();
        }

        FileWriter fileWriter = new FileWriter(resultFileName);

        fileWriter.write("Truck position: (" + startCoords[0] + ',' + startCoords[1] + ")\n");

        for (int i = 0; i < visited.size(); i++) {
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
}
