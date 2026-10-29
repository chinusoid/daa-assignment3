public class Problem05 {
    public int countDependencyGroups(int n, int[][] dependencies){
        int[] offsets = new int[n + 1];
        int[] reverseOffsets = new int[n + 1];
        for (int[] dependency : dependencies) {
            offsets[dependency[0] + 1]++;
            reverseOffsets[dependency[1] + 1]++;
        }
        for (int vertex = 0; vertex < n; vertex++) {
            offsets[vertex + 1] += offsets[vertex];
            reverseOffsets[vertex + 1] += reverseOffsets[vertex];
        }

        int[] neighbors = new int[dependencies.length];
        int[] reverseNeighbors = new int[dependencies.length];
        int[] nextEdge = offsets.clone();
        int[] reverseNextEdge = reverseOffsets.clone();
        for (int[] dependency : dependencies) {
            neighbors[nextEdge[dependency[0]]++] = dependency[1];
            reverseNeighbors[reverseNextEdge[dependency[1]]++] = dependency[0];
        }
        System.arraycopy(offsets, 0, nextEdge, 0, n);

        boolean[] visited = new boolean[n];
        int[] stack = new int[n];
        int[] finishOrder = new int[n];
        int finished = 0;
        for (int start = 0; start < n; start++) {
            if (visited[start]) {
                continue;
            }
            int size = 0;
            stack[size++] = start;
            visited[start] = true;
            while (size > 0) {
                int vertex = stack[size - 1];
                // Keep each frame's edge cursor until all descendants finish.
                if (nextEdge[vertex] < offsets[vertex + 1]) {
                    int neighbor = neighbors[nextEdge[vertex]++];
                    if (!visited[neighbor]) {
                        visited[neighbor] = true;
                        stack[size++] = neighbor;
                    }
                } else {
                    // Postorder is recorded only when the DFS frame returns.
                    finishOrder[finished++] = vertex;
                    size--;
                }
            }
        }

        java.util.Arrays.fill(visited, false);
        int groups = 0;
        for (int index = finished - 1; index >= 0; index--) {
            int start = finishOrder[index];
            if (visited[start]) {
                continue;
            }
            groups++;
            int size = 0;
            stack[size++] = start;
            visited[start] = true;
            while (size > 0) {
                int vertex = stack[--size];
                for (int edge = reverseOffsets[vertex];
                     edge < reverseOffsets[vertex + 1]; edge++) {
                    int neighbor = reverseNeighbors[edge];
                    // Mark on push so every vertex occupies the stack once.
                    if (!visited[neighbor]) {
                        visited[neighbor] = true;
                        stack[size++] = neighbor;
                    }
                }
            }
        }
        return groups;
    }

}
