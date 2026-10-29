### DAA Assignment3 - Report and Defense (50 points)

The ten solutions preserve the supplied public method signatures. Each source is
a standalone Java 8 class and constructs its working state inside the method, so
calling the same instance repeatedly does not retain previous results. Inputs are
read without modification. Throughout this report, `n` denotes the number of
vertices or points and `m` denotes the number of supplied directed edges. Space
bounds include constructed graphs but exclude the caller-owned input.
All DFS implementations use explicit array stacks, which handle deep graphs
without consuming one Java call frame per vertex.

##### Problem 1 and 2 - Number of Provinces (10 points)

**Problem 1: iterative DFS.** The symmetric matrix describes an undirected graph.
The outer loop starts a search at every city that has not been visited and
increments the province count. Each search pops a city, scans its entire matrix
row, and pushes unvisited connected cities. A city is marked when pushed, ensuring
that it enters the stack once even when several neighbors discover it.

The search invariant is that every marked city belongs to a component already
started, while the stack contains the discovered cities whose rows still need
processing. Following all incident edges eventually visits exactly the starting
city's connected component. Therefore each outer-loop search counts one distinct
province, and the completed loop counts every province. For
`[[1,1,0],[1,1,0],[0,0,1]]`, the first search visits cities 0 and 1; the next new
search visits city 2, giving **2**. Scanning one row per city takes **O(n²)** time;
the visited array and stack require **O(n)** additional space. The diagonal ones
do not create extra work items because a city is already marked.

**Problem 2: disjoint set union.** Initially, every city is a singleton and the
component count is `n`. The code checks only entries above the diagonal, because
the matrix is symmetric. For every connection, `union` finds both representatives
and merges distinct sets, decreasing the component count only when a merge
actually happens. It attaches the smaller tree to the larger and compresses paths
by repeatedly linking a node to its grandparent.

The invariant is that DSU sets are precisely the connected components formed by
the edges processed so far. A successful union joins two components; a redundant
edge changes neither the partition nor its count. After all pairs, this partition
matches the graph's components. The same matrix performs one successful merge
and returns **2**. Time is **O(n² α(n))** amortized and additional space is
**O(n)**, where `α` is the inverse Ackermann function. Unlike DFS, DSU does not
explicitly traverse a component. For defense, explain why decrementing for every
matrix one would fail on triangles and duplicate observations of an edge.

##### Problem 3 and 4 - Min Cost Spanning Tree (10 points)

Both methods model the points as a complete undirected graph with Manhattan
weight `|x1-x2| + |y1-y2|`. The goal is a minimum spanning tree (MST), which connects
every point with exactly `n-1` selected edges when `n > 0`.

**Problem 3: Kruskal.** The implementation creates every unordered point pair,
stores its endpoints and weight, and sorts the resulting edges by increasing
weight. A DSU accepts an edge exactly when its endpoints have different roots.
Accepted weights are added until `n-1` edges have been selected.

The accepted edges always form a forest. When the next acceptable edge joins two
components, it is a lightest edge crossing the relevant component cut. The cut
property makes this edge safe: some MST contains the previously accepted forest
plus this edge. Induction preserves that property; a forest with `n-1` edges is
then a spanning tree and an MST. For
`[[0,0],[2,2],[3,10],[5,2],[7,0]]`, one selected tree has weights 3, 4, 4, and 9,
totaling **20**. There are Θ(n²) candidate edges, so sorting costs
**O(n² log n)** time and storage costs **O(n²)**. DSU operations do not dominate.
Tied weights may produce different trees with the same cost.

**Problem 4: dense Prim.** `inTree` records selected vertices, while
`bestDistance[v]` stores the cheapest edge from the selected tree to an unselected
point `v`. Vertex 0 begins with key zero. Each iteration linearly scans all
unselected points, selects the minimum key, adds that key to the cost, and
computes distances from the new vertex to update the remaining keys.

The invariant defining each key is maintained by the minimum update. Selecting
the smallest key chooses a lightest edge across the cut between the current
tree and remaining vertices, so the cut property again proves safety. The first
zero key starts the tree without inventing an edge. On the five-point example,
the added costs are 0, 4, 3, 4, and 9, giving **20**. Each of `n` iterations
performs linear scans, giving **O(n²)** time and **O(n)** additional space.
Distances are computed on demand. Dense Prim saves Kruskal's quadratic edge
storage and sorting; a priority queue is unnecessary for this complete graph.

**Integer safety.** The official constraints are `n <= 1000` and coordinates
between `-10^6` and `10^6`. Each coordinate subtraction has absolute value at most
2,000,000, and an edge weight is at most 4,000,000, so individual computations
fit `int`. To bound the total tightly, consider a spanning path sorted by
x-coordinate. Its horizontal cost telescopes to at most 2,000,000, and its
vertical cost is at most `(n-1) * 2,000,000`. Therefore its cost, and hence the
MST cost, is at most **2,000,000,000**, below `Integer.MAX_VALUE`. Both algorithms
maintain edges extendable to an MST; because weights are nonnegative, every
running sum is bounded by the final MST cost. This justifies the supplied `int`
return type and the implementation's accumulators.

##### Problem 5 and 6 - Software Dependency Cycles (10 points)

A dependency `[a,b]` is stored as `a -> b`, following the assignment's dependency
direction. The required groups are strongly connected components (SCCs): maximal
sets of mutually reachable vertices. A singleton also counts, even without a
self-loop. Both implementations build compressed sparse row (CSR) adjacency:
prefix sums delimit each vertex's contiguous interval in a neighbor array.

**Problem 5: Kosaraju.** The code builds forward and reversed CSR graphs. Its
first DFS keeps an edge cursor for each vertex and records a vertex only after
all outgoing edges and descendants have finished. It then clears the visited
array and searches the reversed graph in decreasing finishing order. Each new
search increments the SCC count.

Contracting SCCs produces a directed acyclic graph. For an edge between different
components, the source component has a larger maximum first-pass finishing time
than the destination. Thus the next unvisited component selected in decreasing
order cannot escape into another unvisited component during reverse traversal.
Reversal preserves mutual reachability, so this search visits exactly one SCC.
For `[[0,1],[1,2],[2,0],[2,3],[3,4]]`, the groups are `{0,1,2}`, `{3}`, and `{4}`:
the result is **3**. Building and traversing both graphs takes **O(n+m)** time
and **O(n+m)** additional space. Recording discovery order instead of finishing
order would invalidate the second-pass argument.

**Problem 6: Tarjan.** A single forward CSR graph is sufficient. Each discovered
vertex receives a discovery number and an initial equal low link. One explicit
stack simulates suspended DFS frames; another holds discovered vertices not yet
assigned to an SCC. The `onStack` flag refers to this second stack. After a child
returns, its completed low link is propagated to its parent. An edge to an
already discovered vertex still on the component stack updates the low link
using that vertex's discovery number. Edges into completed SCCs are ignored.

The low-link invariant identifies the earliest unresolved discovery reachable
through the DFS subtree and an eligible edge into the active component stack.
When `low[v] == discovery[v]`, no vertex in the unresolved block rooted at `v`
can reach an earlier active root; popping through `v` therefore extracts exactly
one maximal SCC. Each vertex is pushed and popped once, and each edge is scanned
once, giving **O(n+m)** time and **O(n+m)** total additional space. The supplied
five-vertex example again returns **3**. Tarjan avoids the reversed graph and
second traversal, but its return-time propagation requires more care. In
particular, the DFS stack and SCC stack cannot be treated as interchangeable:
a finished DFS frame may remain unresolved on the SCC stack.

##### Problem 7 and 8 - Course Schedule (10 points)

Here a pair `[a,b]` means course `b` must precede `a`, so both methods construct
edges **`b -> a`**. This orientation matters for valid scheduling. Each adjacency
list is an exactly sized primitive integer array, allocated after counting
outgoing edges. Disconnected vertices are included.

**Problem 7: DFS cycle detection.** Vertices have three states: unvisited,
active, and finished. The explicit DFS stack represents the current path; a
per-vertex cursor remembers the next outgoing neighbor. Descending marks an
unvisited neighbor active, and exhausting a vertex's list marks it finished. An
edge to an active neighbor immediately returns `false`.

The invariant is that active vertices are exactly the current DFS path. An edge
to one of them closes a directed cycle, so rejection is correct. Conversely, a
directed cycle would prevent all its vertices from finishing without an edge
reaching an active vertex; every component is searched, so no cycle is missed.
Therefore completing all searches returns `true` exactly for an acyclic graph.
For `numCourses=2, prerequisites=[[1,0]]`, the result is **true**; adding `[0,1]`
makes it **false**. Graph construction and DFS take **O(n+m)** time and
**O(n+m)** additional space. A boolean visited flag alone cannot distinguish an
active ancestor from an already completed vertex.

**Problem 8: Kahn.** The implementation counts each course's indegree and queues
every zero-indegree vertex. Processing a queued course removes its outgoing
edges by decrementing the corresponding indegrees. A neighbor is queued when its
indegree becomes zero. The queue uses an array with increasing head and tail
indices. The answer is whether the number processed equals `numCourses`.

At every step, indegree counts prerequisites from unprocessed courses; every
queued course is therefore available. A DAG always has a zero-indegree vertex,
and removal preserves acyclicity, so every vertex is processed. If processing
stops early, the remaining graph has no zero-indegree vertex and contains a
cycle. The same two examples return **true** and **false**, respectively. Each
vertex enters the queue once and every edge is removed once, yielding
**O(n+m)** time and **O(n+m)** additional space. Unlike DFS, Kahn detects a cycle
through an incomplete count. Duplicate edges remain consistent because both
construction and removal count them individually.

##### Problem 9 and 10 - Course Schedule II (10 points)

These methods must return an entire topological order, or an empty array when
no order exists. A valid result contains every course exactly once and places
`b` before `a` for every pair `[a,b]`. Several different arrays can be correct.

**Problem 9: DFS reverse finishing order.** The traversal and cycle detection
match Problem 7. On finishing a vertex, the implementation writes it into the
result from right to left. This directly produces reverse postorder without a
separate reversal. Encountering an active neighbor returns an empty array and
discards any partially constructed result.

For every edge `u -> v` in an acyclic graph, `v` finishes before `u`: either DFS
explores `v` below `u`, or `v` has already finished. The remaining possibility,
an active `v`, would be a cycle. Reverse finishing order consequently places
every prerequisite before its dependent course. The outer loop includes all
components and visits each vertex once, establishing completeness and
uniqueness. For four courses and
`[[1,0],[2,0],[3,1],[3,2]]`, this implementation returns **`[0,1,2,3]`**.
Time is **O(n+m)** and additional space is **O(n+m)**, including the graph,
traversal arrays, and output. Returning ordinary finishing order would reverse
the prerequisite relationship.

**Problem 10: Kahn topological order.** The algorithm maintains the same
indegrees and readiness invariant as Problem 8. Its output array also acts as
the queue: newly ready courses are appended, and courses are processed from
the next head position. Once a course is appended, every prerequisite has
already been processed and therefore appears earlier in this array.

Induction shows that every appended prefix respects all prerequisites of its
members. If the array contains all courses, it is a valid topological order.
Otherwise a cycle blocks the remaining courses, so the method returns an empty
array. The four-course example produces **`[0,2,1,3]`** with the implementation's
adjacency insertion order; both this and the DFS result are valid. The two-course
cycle yields an empty array in both methods. Time and additional space are
**O(n+m)**. Compared with DFS, Kahn emits courses in readiness order instead of
waiting for descendants to finish. Tests should validate the permutation and
edge positions rather than require one arbitrary order.

#### Reproducible validation

Run `sh test.sh` with **JDK 17 or newer**. The script compiles the standalone
solutions and test harness with `--release 8 -Xlint:all,-options -Werror`, then runs with
assertions enabled and a deliberately small stack using `java -ea -Xss256k`.
The newer JDK is a test-tool requirement; the submitted solution classes remain
Java 8 compatible.

The harness contains **1,241 cases**: 308 province, 309 MST, 312 SCC, and 312
course cases. Each family includes 300 fixed-seed random cases. Independent
reachability closure supplies component and feasibility oracles; small MST cases
use exhaustive spanning-tree enumeration. These checks go beyond comparing the
two implementations of the same problem, which could share a mistake. Course
orders are checked as permutations respecting every prerequisite.

Stress coverage includes 100,000-vertex graphs with 200,000 edges, 1,000 points,
and 2,000 courses. Additional cases check isolated vertices, self-loops,
disconnected graphs, cycles, repeated calls on a shared solution instance, and
preservation of the caller's arrays. The test run passed on **OpenJDK 21.0.12.1**,
targeting Java 8 and using the 256 KiB stack. Its execution result was:

```text
Passed 31102 assertions across 1241 cases.
```

#### Defense prompts

1. Why must iterative province DFS mark a vertex when pushing it?
2. Why does DSU decrement the count only after a successful union?
3. Which cut makes a Kruskal edge safe, and why are cycle edges skipped?
4. What does Prim's key represent, and why does its total fit `int`?
5. Why does Kosaraju need postorder and the reversed graph?
6. Why does Tarjan need two stacks and ignore edges into completed SCCs?
7. Why does only an edge to an active DFS vertex prove a directed cycle?
8. Why does an incomplete Kahn traversal imply a remaining cycle?
9. Why does reversing DFS finishing order respect every prerequisite?
10. Why may valid DFS and Kahn course orders differ?

#### Primary task references

- [Number of Provinces](https://leetcode.com/problems/number-of-provinces/).
- [Min Cost to Connect All Points](https://leetcode.com/problems/min-cost-to-connect-all-points/).
- [Software Dependency Cycles: Kosaraju](Problem05.md) and
  [Tarjan](Problem06.md), including the supplied SCC examples and constraints.
- [Course Schedule](https://leetcode.com/problems/course-schedule/).
- [Course Schedule II](https://leetcode.com/problems/course-schedule-ii/).
