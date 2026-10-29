import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Problem03 {
    public int minCostConnectPoints(int[][] points) {
        int n = points.length;
        List<Edge> edges = new ArrayList<>();
        for (int first = 0; first < n; first++) {
            for (int second = first + 1; second < n; second++) {
                int distance = Math.abs(points[first][0] - points[second][0])
                        + Math.abs(points[first][1] - points[second][1]);
                edges.add(new Edge(first, second, distance));
            }
        }
        edges.sort(Comparator.comparingInt(edge -> edge.weight));

        DisjointSet sets = new DisjointSet(n);
        int cost = 0;
        int selected = 0;
        for (Edge edge : edges) {
            if (sets.union(edge.first, edge.second)) {
                cost += edge.weight;
                if (++selected == n - 1) {
                    break;
                }
            }
        }
        return cost;
    }

    private static class Edge {
        final int first;
        final int second;
        final int weight;

        Edge(int first, int second, int weight) {
            this.first = first;
            this.second = second;
            this.weight = weight;
        }
    }

    private static class DisjointSet {
        private final int[] parent;
        private final int[] size;

        DisjointSet(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        private int find(int node) {
            while (node != parent[node]) {
                parent[node] = parent[parent[node]];
                node = parent[node];
            }
            return node;
        }

        boolean union(int first, int second) {
            int firstRoot = find(first);
            int secondRoot = find(second);
            if (firstRoot == secondRoot) {
                return false;
            }
            if (size[firstRoot] < size[secondRoot]) {
                int temporary = firstRoot;
                firstRoot = secondRoot;
                secondRoot = temporary;
            }
            parent[secondRoot] = firstRoot;
            size[firstRoot] += size[secondRoot];
            return true;
        }
    }
}
