import java.util.Comparator;

public class Edge implements Comparable<Edge> {
        String name, src, dst;
    int weight;

    public Edge(String name, String src, String dst, int weight) {
        this.name = name;
        this.src = src;
        this.dst = dst;
        this.weight = weight;
    }

    // First compare the edge weights, if those are equal then compare the names
    // of their source nodes, if those are also equal, compare the names of their
    // destination nodes
    public int compareTo(Edge e) {
        if (this.weight != e.weight) {
            return this.weight - e.weight;
        } else {
            if (!this.src.equals(e.src)) {
                // return alphanumericCompare(this.src, e.src);
                return this.src.compareTo(e.src);
            } else {
                // return alphanumericCompare(this.dst, e.dst);
                return this.dst.compareTo(e.dst);
            }
        }
    }

    public static int alphanumericCompare(String s1, String s2) {
        int i = 0, j = 0;
        while (i < s1.length() && j < s2.length()) {
            char c1 = s1.charAt(i);
            char c2 = s2.charAt(j);

            if (Character.isDigit(c1) && Character.isDigit(c2)) {
                // Compare numbers
                int start1 = i, start2 = j;
                while (i < s1.length() && Character.isDigit(s1.charAt(i))) i++;
                while (j < s2.length() && Character.isDigit(s2.charAt(j))) j++;

                String num1 = s1.substring(start1, i);
                String num2 = s2.substring(start2, j);

                int diff = Integer.compare(Integer.parseInt(num1), Integer.parseInt(num2));
                if (diff != 0) return diff;
            } else if (c1 != c2) {
                // Compare characters
                return Character.compare(c1, c2);
            } else {
                // If the characters are the same, move to the next character
                i++;
                j++;
            }
        }

        return s1.length() - s2.length();
    }
}