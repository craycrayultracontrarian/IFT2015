import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("2 arguments required: java Tp3 [Input file] [Output file]");
        }
        String inputFileName = args[0];
        String outputFileName = args[1];

        ArrayList<String> vertices = new ArrayList<>();
        PriorityQueue<Edge> edges = new PriorityQueue<>();

        FileReader fileReader = new FileReader(inputFileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;

        while (!(line = bufferedReader.readLine()).contains("---")) {
            // Add vertex to list of vertices after removing all possible whitespaces
            vertices.add(line.replaceAll("[ \t]+", ""));
        }

        while (!(line = bufferedReader.readLine()).contains("---")) {
            line = line.replaceAll("[:;]", "");
            String[] splitLine = line.split("[ \t]+");

            String edgeName = splitLine[0];
            String edgeSrc = splitLine[1];
            String edgeDst = splitLine[2];
            int edgeWeight = Integer.parseInt(splitLine[3]);

            edges.add(new Edge(edgeName, edgeSrc, edgeDst, edgeWeight));
        }
        fileReader.close();

        PriorityQueue<Edge> result = Planner.kruskal(edges, vertices);

        int totalWeight = 0;
        for (Edge edge : result) {
            totalWeight += edge.weight;
        }

        /*
        while (!result.isEmpty()) {
            Edge e = result.poll();
            System.out.println(e.name + " " + e.src + " " + e.dst + " " + e.weight);
        }
        */

        FileWriter fileWriter = new FileWriter(outputFileName);

        Collections.sort(vertices);
        for (String vertex : vertices) {
            fileWriter.write(vertex + '\n');
        }

        while (!result.isEmpty()) {
            Edge edge = result.poll();
            fileWriter.write(edge.name + '\t' + edge.src + '\t' + edge.dst + '\t' + edge.weight + '\n');
        }

        fileWriter.write("---\n" + totalWeight + '\n');

        fileWriter.close();
    }
}
