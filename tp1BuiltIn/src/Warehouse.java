import java.util.Arrays;

public class Warehouse {
    private final double[] coordinates;
    private double distToStart;
    private int  boxes;

    public Warehouse(int boxes, double[] coordinates) {
        this.boxes = boxes;
        this.coordinates = coordinates;
    }

    public void setDistToStart(double[] startCoords) {
        distToStart = getDistance(coordinates, startCoords);
    }

    public double getDistToStart() {
        return distToStart;
    }

    public double getRoundedDist() {
        return (double) Math.round(getDistToStart() * 10) / 10;
    }

    public int getBoxes() {
        return boxes;
    }

    public void setBoxes(int amount) {
        boxes = amount;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    // Haversin function -> sin^2 (theta / 2)
    private static double haversin(double theta) {
        return Math.pow( Math.sin(theta / 2), 2 );
    }

    // Get distance in meters between 2 coordinates
    public static double getDistance(double[] coords1, double[] coords2) {
        int r = 6371000;  // Radius of the earth

        // Convert coordinates to radians
        coords1 = Arrays.stream(coords1)
                .map(x -> x * (Math.PI/180))
                .toArray();

        coords2 = Arrays.stream(coords2)
                .map(x -> x * (Math.PI/180))
                .toArray();

        // Return distance between the points in meters
        return 2 * r * Math.asin( Math.sqrt( haversin(coords2[0] - coords1[0]) + Math.cos(coords1[0]) *
                Math.cos(coords2[0]) * haversin( coords2[1] - coords1[1]) ) );
    }
}
