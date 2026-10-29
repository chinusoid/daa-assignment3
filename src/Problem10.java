public class Problem10 {
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        int[] outdegree = new int[numCourses];
        int[] indegree = new int[numCourses];
        for (int[] edge : prerequisites) {
            outdegree[edge[1]]++;
            indegree[edge[0]]++;
        }
        int[][] graph = new int[numCourses][];
        for (int course = 0; course < numCourses; course++) {
            graph[course] = new int[outdegree[course]];
        }
        for (int[] edge : prerequisites) {
            graph[edge[1]][--outdegree[edge[1]]] = edge[0];
        }

        // The queue also stores the final topological order.
        int[] order = new int[numCourses];
        int head = 0;
        int tail = 0;
        for (int course = 0; course < numCourses; course++) {
            if (indegree[course] == 0) {
                order[tail++] = course;
            }
        }

        while (head < tail) {
            int course = order[head++];
            for (int next : graph[course]) {
                if (--indegree[next] == 0) {
                    order[tail++] = next;
                }
            }
        }
        // Cyclic courses never become ready, so a partial order is invalid.
        return tail == numCourses ? order : new int[0];
    }
}
