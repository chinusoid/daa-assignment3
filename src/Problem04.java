import java.util.Arrays;

public class Problem04 {
    public int minCostConnectPoints(int[][] points) {
        int n = points.length;
        if (n == 0) {
            return 0;
        }
        boolean[] inTree = new boolean[n];
        int[] bestDistance = new int[n];
        Arrays.fill(bestDistance, Integer.MAX_VALUE);
        bestDistance[0] = 0;
        int cost = 0;

        for (int selected = 0; selected < n; selected++) {
            int next = -1;
            for (int point = 0; point < n; point++) {
                if (!inTree[point]
                        && (next == -1 || bestDistance[point] < bestDistance[next])) {
                    next = point;
                }
            }
            inTree[next] = true;
            cost += bestDistance[next];

            // Compute distances on demand instead of storing the complete graph.
            for (int point = 0; point < n; point++) {
                if (!inTree[point]) {
                    int distance = Math.abs(points[next][0] - points[point][0])
                            + Math.abs(points[next][1] - points[point][1]);
                    bestDistance[point] = Math.min(bestDistance[point], distance);
                }
            }
        }
        return cost;
    }
}
