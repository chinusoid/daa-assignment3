public class Problem01 {
    public int findCircleNum(int[][] isConnected) {
        int n = isConnected.length;
        boolean[] visited = new boolean[n];
        int[] stack = new int[n];
        int provinces = 0;

        for (int start = 0; start < n; start++) {
            if (visited[start]) {
                continue;
            }
            provinces++;
            int size = 0;
            stack[size++] = start;
            visited[start] = true;

            while (size > 0) {
                int city = stack[--size];
                for (int neighbor = 0; neighbor < n; neighbor++) {
                    if (isConnected[city][neighbor] == 1 && !visited[neighbor]) {
                        // Mark on insertion so each city enters the stack once.
                        visited[neighbor] = true;
                        stack[size++] = neighbor;
                    }
                }
            }
        }
        return provinces;
    }
}
