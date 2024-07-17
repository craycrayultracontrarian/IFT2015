import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.HashSet;

public class Planner {
    public static PriorityQueue<Edge> kruskal(PriorityQueue<Edge> edges, ArrayList<String> vertices) {
        DisjointSet disjointSet = new DisjointSet(vertices);
        PriorityQueue<Edge> result = new PriorityQueue<>(new Comparator<Edge>() {
            public int compare(Edge e1, Edge e2) {
                if (!e1.src.equals(e2.src)) {
                    // return Edge.alphanumericCompare(e1.src, e2.src);
                    return e1.src.compareTo(e2.src);
                }

                // return Edge.alphanumericCompare(e1.dst, e2.dst);
                return e1.dst.compareTo(e2.dst);
            }
        });

        int chosenEdgesAmount = 0;
        int verticesInGraphAmount = vertices.size();

        while (chosenEdgesAmount < verticesInGraphAmount - 1) {
            Edge nextEdge = edges.poll();

            String nextEdgeSrcParent = disjointSet.find(nextEdge.src);
            String nextEdgeDstParent = disjointSet.find(nextEdge.dst);

            if (!nextEdgeSrcParent.equals(nextEdgeDstParent)) {
                result.add(nextEdge);
                disjointSet.union(nextEdgeSrcParent, nextEdgeDstParent);
                chosenEdgesAmount++;
            }
        }

        return result;
    }
}
