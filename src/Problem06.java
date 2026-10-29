public class Problem06 {
    public int countDependencyGroups(int n, int[][] dependencies){
        int[] offsets = new int[n + 1];
        for (int[] dependency : dependencies) {
            offsets[dependency[0] + 1]++;
        }
        for (int vertex = 0; vertex < n; vertex++) {
            offsets[vertex + 1] += offsets[vertex];
        }
        int[] neighbors = new int[dependencies.length];
        int[] nextEdge = offsets.clone();
        for (int[] dependency : dependencies) {
            neighbors[nextEdge[dependency[0]]++] = dependency[1];
        }
        System.arraycopy(offsets, 0, nextEdge, 0, n);

        int[] discovery = new int[n];
        java.util.Arrays.fill(discovery, -1);
        int[] low = new int[n];
        boolean[] onStack = new boolean[n];
        int[] componentStack = new int[n];
        int componentSize = 0;
        int[] dfsStack = new int[n];
        int time = 0;
        int groups = 0;

        for (int start = 0; start < n; start++) {
            if (discovery[start] != -1) {
                continue;
            }
            int dfsSize = 0;
            discovery[start] = low[start] = time++;
            componentStack[componentSize++] = start;
            onStack[start] = true;
            dfsStack[dfsSize++] = start;

            while (dfsSize > 0) {
                int vertex = dfsStack[dfsSize - 1];
                // The saved edge cursor resumes this frame after a child returns.
                if (nextEdge[vertex] < offsets[vertex + 1]) {
                    int neighbor = neighbors[nextEdge[vertex]++];
                    if (discovery[neighbor] == -1) {
                        discovery[neighbor] = low[neighbor] = time++;
                        componentStack[componentSize++] = neighbor;
                        onStack[neighbor] = true;
                        dfsStack[dfsSize++] = neighbor;
                    } else if (onStack[neighbor]) {
                        // Edges to completed components must not change low links.
                        low[vertex] = Math.min(low[vertex], discovery[neighbor]);
                    }
                } else {
                    dfsSize--;
                    if (low[vertex] == discovery[vertex]) {
                        groups++;
                        int member;
                        do {
                            member = componentStack[--componentSize];
                            onStack[member] = false;
                        } while (member != vertex);
                    }
                    // Propagate the final low link only when the child returns.
                    if (dfsSize > 0) {
                        int parent = dfsStack[dfsSize - 1];
                        low[parent] = Math.min(low[parent], low[vertex]);
                    }
                }
            }
        }
        return groups;
    }

}
