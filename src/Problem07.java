public class Problem07 {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        int[][] graph = buildGraph(numCourses, prerequisites);
        // 0 = unvisited, 1 = on the current DFS path, 2 = finished.
        byte[] state = new byte[numCourses];
        int[] stack = new int[numCourses];
        int[] nextNeighbor = new int[numCourses];

        for (int start = 0; start < numCourses; start++) {
            if (state[start] != 0) {
                continue;
            }
            int top = 0;
            stack[top] = start;
            state[start] = 1;

            // The explicit stack avoids recursion depth limits on long chains.
            while (top >= 0) {
                int course = stack[top];
                if (nextNeighbor[course] == graph[course].length) {
                    state[course] = 2;
                    top--;
                    continue;
                }

                int next = graph[course][nextNeighbor[course]++];
                if (state[next] == 1) {
                    return false;
                }
                if (state[next] == 0) {
                    state[next] = 1;
                    stack[++top] = next;
                }
            }
        }
        return true;
    }

    private int[][] buildGraph(int numCourses, int[][] prerequisites) {
        int[] degree = new int[numCourses];
        for (int[] edge : prerequisites) {
            degree[edge[1]]++;
        }
        int[][] graph = new int[numCourses][];
        for (int course = 0; course < numCourses; course++) {
            graph[course] = new int[degree[course]];
        }
        for (int[] edge : prerequisites) {
            // A prerequisite b must precede course a: b -> a.
            graph[edge[1]][--degree[edge[1]]] = edge[0];
        }
        return graph;
    }
}
