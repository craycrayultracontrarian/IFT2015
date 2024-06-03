import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        // Check number of arguments to know whether to output error
        if (args.length != 1 && args.length != 2) {
            throw new IllegalArgumentException("Exactly 1 or 2 arguments required!");
        } else {
            long startTime = System.nanoTime();

            PathFinder pathFinder = new PathFinder();
            pathFinder.run(args);

            System.out.println(pathFinder.getWarehouses().size()+","+(System.nanoTime()-startTime)/1e6);
        }
    }
}