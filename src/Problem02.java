public class Problem02 {
    public int findCircleNum(int[][] isConnected) {
        int n = isConnected.length;
        DisjointSet sets = new DisjointSet(n);
        int provinces = n;

        for (int city = 0; city < n; city++) {
            // The matrix is symmetric, so inspect each pair only once.
            for (int neighbor = city + 1; neighbor < n; neighbor++) {
                if (isConnected[city][neighbor] == 1 && sets.union(city, neighbor)) {
                    provinces--;
                }
            }
        }
        return provinces;
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
