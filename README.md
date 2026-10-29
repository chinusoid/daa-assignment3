# DAA-Assignment3 - Graph Algorithms

You are to solve the following graph problems. Each problem is 5 points.

1. [Number of Provinces using DFS](Problem01.md) **(5 points)**.
2. [Number of Provinces using DSU](Problem02.md) **(5 points)**.
3. [Min Cost to Connect All Points using Kruskal](Problem03.md) **(5 points)**.
4. [Min Cost to Connect All Points using Prim](Problem04.md) **(5 points)**.
5. [Software Dependency Cycles using Kosaraju](Problem05.md) **(5 points)**.
6. [Software Dependency Cycles using Tarjan](Problem06.md) **(5 points)**
7. [Course Schedule using DFS with cycle detection](Problem07.md) **(5 points)**.
8. [Course Schedule using Kahn](Problem08.md) **(5 points)**.
9. [Course Schedule II using DFS with reverse finishing order](Problem09.md) **(5 points)**.
10. [Course Schedule II using Kahn](Problem10.md) **(5 points)**.

**Write and defend** a report describing your solutions for each problem in [REPORT.md](REPORT.md) **(50 points)**.

## Completed solutions

The ten standalone solution classes are in [src/](src/). They preserve the
assignment's public signatures and are compatible with **Java 8**. The
[report](REPORT.md) explains each implementation, correctness, complexity,
examples, and questions for the defense.

## Run the checks

Install **JDK 17 or newer**, then run from this repository:

```sh
sh test.sh
```

The [test script](test.sh) compiles against Java 8 with source compiler warnings
treated as errors, then runs the [test harness](tests/Assignment3Test.java) with
assertions enabled. The suite includes deterministic examples, fixed-seed random
cases checked against independent oracles, large graphs, repeated calls, and
input-preservation checks. Compilation output stays in a temporary directory.

Assignment clarification: [Problem06.md](Problem06.md) contains a link typo that
points to `Problem05.java`. The Tarjan solution is correctly implemented in
[src/Problem06.java](src/Problem06.java).
