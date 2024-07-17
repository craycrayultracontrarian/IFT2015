import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class DisjointSet {
    private Map<String, String> parent;
    private Map<String, Integer> rank;

    // Constructeur pour initialiser les structures
    public DisjointSet(ArrayList<String> vertices) {
        parent = new HashMap<>();
        rank = new HashMap<>();
        for (String vertex : vertices) {
            parent.put(vertex, vertex);  // Chaque élément est son propre parent initialement
            rank.put(vertex, 0);    // Le rang de chaque élément est initialisé à 0
        }
    }

    // Méthode find avec compression de chemin
    public String find(String x) {
        if (!parent.get(x).equals(x)) {
            parent.put(x, find(parent.get(x)));  // Compression de chemin : mettre à jour le parent de x
        }
        return parent.get(x);
    }

    // Méthode union avec union par rang
    public void union(String x, String y) {
        String rootX = find(x);
        String rootY = find(y);

        if (!rootX.equals(rootY)) {
            if (rank.get(rootX) > rank.get(rootY)) {
                parent.put(rootY, rootX);  // Le rang de rootX est supérieur, donc rootY pointe vers rootX
            } else if (rank.get(rootX) < rank.get(rootY)) {
                parent.put(rootX, rootY);  // Le rang de rootY est supérieur, donc rootX pointe vers rootY
            } else {
                parent.put(rootY, rootX);  // Les rangs sont égaux, donc rootY pointe vers rootX
                rank.put(rootX, rank.get(rootX) + 1);
            }
        }
    }
}
