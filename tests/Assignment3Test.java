import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/** Dependency-free checks using independent small-input oracles. */
public final class Assignment3Test {
    private static final Problem01 DFS_PROVINCES = new Problem01();
    private static final Problem02 DSU_PROVINCES = new Problem02();
    private static final Problem03 KRUSKAL = new Problem03();
    private static final Problem04 PRIM = new Problem04();
    private static final Problem05 KOSARAJU = new Problem05();
    private static final Problem06 TARJAN = new Problem06();
    private static final Problem07 DFS_COURSES = new Problem07();
    private static final Problem08 KAHN_COURSES = new Problem08();
    private static final Problem09 DFS_ORDER = new Problem09();
    private static final Problem10 KAHN_ORDER = new Problem10();
    private static long assertions;
    private static int provinceCases;
    private static int mstCases;
    private static int sccCases;
    private static int courseCases;

    private Assignment3Test() {
    }

    public static void main(String[] args) {
        testProvinces();
        testMst();
        testScc();
        testCourses();
        int cases = provinceCases + mstCases + sccCases + courseCases;
        System.out.println("Passed " + assertions + " assertions across " + cases + " cases.");
        System.out.println("Provinces: " + provinceCases + "; MST: " + mstCases
                + "; SCC: " + sccCases + "; courses: " + courseCases + ".");
    }

    private static void testProvinces() {
        checkProvinces(new int[][] {{1, 1, 0}, {1, 1, 0}, {0, 0, 1}}, 2);
        checkProvinces(new int[][] {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}}, 3);
        checkProvinces(new int[][] {{1}}, 1);
        checkProvinces(new int[0][0], 0);
        int[][] isolated = new int[200][200];
        int[][] complete = new int[200][200];
        int[][] chain = new int[200][200];
        for (int city = 0; city < 200; city++) {
            isolated[city][city] = 1;
            chain[city][city] = 1;
            Arrays.fill(complete[city], 1);
            if (city > 0) {
                chain[city][city - 1] = 1;
                chain[city - 1][city] = 1;
            }
        }
        checkProvinces(isolated, 200);
        checkProvinces(complete, 1);
        checkProvinces(chain, 1);

        Random random = new Random(0xDAA001L);
        for (int trial = 0; trial < 300; trial++) {
            int n = 1 + random.nextInt(20);
            int[][] matrix = new int[n][n];
            int density = new int[] {5, 25, 55, 90}[trial % 4];
            for (int city = 0; city < n; city++) {
                matrix[city][city] = 1;
                for (int other = city + 1; other < n; other++) {
                    if (random.nextInt(100) < density) {
                        matrix[city][other] = matrix[other][city] = 1;
                    }
                }
            }
            checkProvinces(matrix, provinceOracle(matrix));
        }
        checkProvinces(new int[][] {{1}}, 1);
    }

    private static void checkProvinces(int[][] matrix, int expected) {
        String context = "province case " + (++provinceCases);
        int[][] original = copy(matrix);
        equal(expected, DFS_PROVINCES.findCircleNum(matrix), context + " DFS");
        unchanged(original, matrix, context + " DFS input");
        equal(expected, DSU_PROVINCES.findCircleNum(matrix), context + " DSU");
        unchanged(original, matrix, context + " DSU input");
    }

    private static int provinceOracle(int[][] matrix) {
        int n = matrix.length;
        boolean[][] reach = new boolean[n][n];
        for (int first = 0; first < n; first++) {
            for (int second = 0; second < n; second++) {
                reach[first][second] = first == second || matrix[first][second] == 1;
            }
        }
        closeTransitively(reach);
        int groups = 0;
        for (int city = 0; city < n; city++) {
            boolean represented = false;
            for (int earlier = 0; earlier < city; earlier++) {
                represented |= reach[city][earlier];
            }
            if (!represented) {
                groups++;
            }
        }
        return groups;
    }

    private static void testMst() {
        checkMst(new int[][] {{0, 0}, {2, 2}, {3, 10}, {5, 2}, {7, 0}}, 20);
        checkMst(new int[][] {{3, 12}, {-2, 5}, {-4, 1}}, 18);
        checkMst(new int[][] {{0, 0}}, 0);
        checkMst(new int[0][2], 0);
        checkMst(new int[][] {{5, -7}, {5, -7}, {5, -7}}, 0);
        checkMst(new int[][] {{0, 0}, {0, 1}, {1, 0}, {1, 1}}, 3);
        checkMst(new int[][] {{-1000000, -1000000}, {1000000, 1000000}}, 4000000);
        Random random = new Random(0xDAA002L);
        for (int trial = 0; trial < 300; trial++) {
            int n = 1 + random.nextInt(6);
            int[][] points = new int[n][2];
            for (int[] point : points) {
                point[0] = random.nextInt(11) - 5;
                point[1] = random.nextInt(11) - 5;
            }
            checkMst(points, exhaustiveMstOracle(points));
        }
        int[][] large = new int[1000][2];
        for (int point = 0; point < large.length; point++) {
            large[point][0] = point;
            large[point][1] = point;
        }
        // Every connection across consecutive positions costs at least 2.
        checkMst(large, 1998);
        checkMst(new int[][] {{0, 0}}, 0);
    }

    private static void checkMst(int[][] points, int expected) {
        String context = "MST case " + (++mstCases);
        int[][] original = copy(points);
        equal(expected, KRUSKAL.minCostConnectPoints(points), context + " Kruskal");
        unchanged(original, points, context + " Kruskal input");
        equal(expected, PRIM.minCostConnectPoints(points), context + " Prim");
        unchanged(original, points, context + " Prim input");
    }

    private static int exhaustiveMstOracle(int[][] points) {
        int n = points.length;
        if (n < 2) {
            return 0;
        }
        int edges = n * (n - 1) / 2;
        int[] first = new int[edges];
        int[] second = new int[edges];
        int[] weights = new int[edges];
        int edge = 0;
        for (int from = 0; from < n; from++) {
            for (int to = from + 1; to < n; to++) {
                first[edge] = from;
                second[edge] = to;
                weights[edge++] = Math.abs(points[from][0] - points[to][0])
                        + Math.abs(points[from][1] - points[to][1]);
            }
        }
        int best = Integer.MAX_VALUE;
        // Enumerate every n-1-edge subset; connectivity then implies a tree.
        for (int mask = 0; mask < (1 << edges); mask++) {
            if (Integer.bitCount(mask) != n - 1) {
                continue;
            }
            int cost = 0;
            for (edge = 0; edge < edges; edge++) {
                if ((mask & (1 << edge)) != 0) {
                    cost += weights[edge];
                }
            }
            if (cost >= best) {
                continue;
            }
            int reached = 1;
            int previous;
            do {
                previous = reached;
                for (edge = 0; edge < edges; edge++) {
                    int endpoints = (1 << first[edge]) | (1 << second[edge]);
                    if ((mask & (1 << edge)) != 0 && (reached & endpoints) != 0) {
                        reached |= endpoints;
                    }
                }
            } while (reached != previous);
            if (reached == (1 << n) - 1) {
                best = cost;
            }
        }
        return best;
    }

    private static void testScc() {
        checkScc(5, new int[][] {{0, 1}, {1, 2}, {2, 0}, {2, 3}, {3, 4}}, 3);
        checkScc(4, new int[][] {{0, 1}, {1, 0}, {2, 3}, {3, 2}}, 2);
        checkScc(4, new int[][] {{0, 1}, {1, 2}, {2, 3}}, 4);
        checkScc(5, new int[0][2], 5);
        checkScc(1, new int[][] {{0, 0}, {0, 0}}, 1);
        checkScc(6, new int[][] {{0, 1}, {1, 0}, {1, 0}, {2, 3}, {3, 4}, {4, 2}}, 3);
        checkScc(4, new int[][] {{0, 1}, {0, 2}, {2, 1}, {2, 3}, {3, 2}}, 3);
        checkScc(4, new int[][] {{0, 1}, {1, 2}, {2, 0}, {0, 3}, {3, 1}}, 1);
        Random random = new Random(0xDAA003L);
        for (int trial = 0; trial < 300; trial++) {
            int n = 1 + random.nextInt(10);
            int[][] edges = new int[random.nextInt(n * n + 11)][2];
            for (int[] edge : edges) {
                edge[0] = random.nextInt(n);
                edge[1] = random.nextInt(n);
            }
            checkScc(n, edges, sccOracle(n, edges));
        }
        int n = 100000;
        int[][] chain = new int[n - 1][2];
        for (int vertex = 0; vertex < n - 1; vertex++) {
            chain[vertex][0] = vertex;
            chain[vertex][1] = vertex + 1;
        }
        checkScc(n, chain, n);
        int[][] cycle = new int[n][2];
        for (int vertex = 0; vertex < n; vertex++) {
            cycle[vertex][0] = vertex;
            cycle[vertex][1] = (vertex + 1) % n;
        }
        checkScc(n, cycle, 1);
        int[][] maximumEdges = new int[200000][2];
        for (int edge = 0; edge < maximumEdges.length; edge++) {
            maximumEdges[edge][0] = edge % n;
            maximumEdges[edge][1] = (edge + 1) % n;
        }
        checkScc(n, maximumEdges, 1);
        checkScc(2, new int[0][2], 2);
    }

    private static void checkScc(int n, int[][] edges, int expected) {
        String context = "SCC case " + (++sccCases);
        int[][] original = copy(edges);
        equal(expected, KOSARAJU.countDependencyGroups(n, edges), context + " Kosaraju");
        unchanged(original, edges, context + " Kosaraju input");
        equal(expected, TARJAN.countDependencyGroups(n, edges), context + " Tarjan");
        unchanged(original, edges, context + " Tarjan input");
    }

    private static int sccOracle(int n, int[][] edges) {
        boolean[][] reach = new boolean[n][n];
        for (int vertex = 0; vertex < n; vertex++) {
            reach[vertex][vertex] = true;
        }
        for (int[] edge : edges) {
            reach[edge[0]][edge[1]] = true;
        }
        closeTransitively(reach);
        int groups = 0;
        for (int vertex = 0; vertex < n; vertex++) {
            boolean represented = false;
            for (int earlier = 0; earlier < vertex; earlier++) {
                represented |= reach[vertex][earlier] && reach[earlier][vertex];
            }
            if (!represented) {
                groups++;
            }
        }
        return groups;
    }

    private static void testCourses() {
        checkCourses(2, new int[][] {{1, 0}}, true);
        checkCourses(2, new int[][] {{1, 0}, {0, 1}}, false);
        checkCourses(4, new int[][] {{1, 0}, {2, 0}, {3, 1}, {3, 2}}, true);
        checkCourses(5, new int[0][2], true);
        checkCourses(1, new int[][] {{0, 0}}, false);
        checkCourses(1, new int[0][2], true);
        checkCourses(4, new int[][] {{1, 0}, {1, 0}, {2, 1}, {2, 1}}, true);
        checkCourses(6, new int[][] {{1, 0}, {3, 2}, {4, 3}, {2, 4}}, false);
        checkCourses(0, new int[0][2], true);
        Random random = new Random(0xDAA004L);
        for (int trial = 0; trial < 300; trial++) {
            int n = 1 + random.nextInt(10);
            List<int[]> edgeList = new ArrayList<>();
            int[] permutation = new int[n];
            for (int vertex = 0; vertex < n; vertex++) {
                permutation[vertex] = vertex;
            }
            for (int vertex = n - 1; vertex > 0; vertex--) {
                int other = random.nextInt(vertex + 1);
                int value = permutation[vertex];
                permutation[vertex] = permutation[other];
                permutation[other] = value;
            }
            if (trial % 2 == 0) {
                for (int before = 0; before < n; before++) {
                    for (int after = before + 1; after < n; after++) {
                        if (random.nextBoolean()) {
                            int[] edge = {permutation[after], permutation[before]};
                            edgeList.add(edge);
                            if (random.nextInt(5) == 0) {
                                edgeList.add(edge.clone());
                            }
                        }
                    }
                }
            } else {
                int count = random.nextInt(n * n + 1);
                for (int edge = 0; edge < count; edge++) {
                    edgeList.add(new int[] {random.nextInt(n), random.nextInt(n)});
                }
            }
            int[][] edges = edgeList.toArray(new int[edgeList.size()][]);
            checkCourses(n, edges, courseOracle(n, edges));
        }
        int n = 2000;
        int[][] dag = new int[5000][2];
        int edge = 0;
        for (int gap = 1; edge < dag.length; gap++) {
            for (int from = 0; from + gap < n && edge < dag.length; from++) {
                dag[edge][0] = from + gap;
                dag[edge++][1] = from;
            }
        }
        checkCourses(n, dag, true);
        int[][] cyclic = copy(dag);
        cyclic[cyclic.length - 1] = new int[] {0, n - 1};
        checkCourses(n, cyclic, false);
        checkCourses(1, new int[0][2], true);
    }

    private static void checkCourses(int n, int[][] edges, boolean expected) {
        String context = "course case " + (++courseCases);
        int[][] original = copy(edges);
        check(DFS_COURSES.canFinish(n, edges) == expected, context + " DFS feasibility");
        unchanged(original, edges, context + " DFS feasibility input");
        check(KAHN_COURSES.canFinish(n, edges) == expected, context + " Kahn feasibility");
        unchanged(original, edges, context + " Kahn feasibility input");
        verifyOrder(DFS_ORDER.findOrder(n, edges), n, edges, expected, context + " DFS order");
        unchanged(original, edges, context + " DFS order input");
        verifyOrder(KAHN_ORDER.findOrder(n, edges), n, edges, expected, context + " Kahn order");
        unchanged(original, edges, context + " Kahn order input");
    }

    private static void verifyOrder(int[] order, int n, int[][] edges,
                                    boolean possible, String context) {
        check(order != null, context + " is nonnull");
        equal(possible ? n : 0, order.length, context + " length");
        if (!possible) {
            return;
        }
        int[] position = new int[n];
        Arrays.fill(position, -1);
        for (int index = 0; index < order.length; index++) {
            int course = order[index];
            check(course >= 0 && course < n, context + " course range");
            check(position[course] == -1, context + " each course appears once");
            position[course] = index;
        }
        for (int[] edge : edges) {
            check(position[edge[1]] < position[edge[0]], context + " prerequisite precedes course");
        }
    }

    private static boolean courseOracle(int n, int[][] edges) {
        boolean[][] reach = new boolean[n][n];
        for (int[] edge : edges) {
            reach[edge[1]][edge[0]] = true;
        }
        closeTransitively(reach);
        for (int course = 0; course < n; course++) {
            if (reach[course][course]) {
                return false;
            }
        }
        return true;
    }

    private static void closeTransitively(boolean[][] reach) {
        for (int middle = 0; middle < reach.length; middle++) {
            for (int first = 0; first < reach.length; first++) {
                for (int second = 0; second < reach.length; second++) {
                    reach[first][second] |= reach[first][middle] && reach[middle][second];
                }
            }
        }
    }

    private static int[][] copy(int[][] matrix) {
        int[][] result = new int[matrix.length][];
        for (int row = 0; row < matrix.length; row++) {
            result[row] = matrix[row].clone();
        }
        return result;
    }

    private static void unchanged(int[][] expected, int[][] actual, String context) {
        check(Arrays.deepEquals(expected, actual), context + " unchanged");
    }

    private static void equal(int expected, int actual, String context) {
        check(expected == actual, context + ": expected " + expected + ", got " + actual);
    }

    private static void check(boolean condition, String context) {
        assertions++;
        if (!condition) {
            throw new AssertionError(context);
        }
    }
}
