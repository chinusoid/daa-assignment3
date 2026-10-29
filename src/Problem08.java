public class Problem08 {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
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

        int[] queue = new int[numCourses];
        int head = 0;
        int tail = 0;
        for (int course = 0; course < numCourses; course++) {
            if (indegree[course] == 0) {
                queue[tail++] = course;
            }
        }

        while (head < tail) {
            int course = queue[head++];
            for (int next : graph[course]) {
                // Remove this prerequisite; enqueue when all are satisfied.
                if (--indegree[next] == 0) {
                    queue[tail++] = next;
                }
            }
        }
        // A cycle leaves at least one course with an unmet prerequisite.
        return head == numCourses;
    }
}
