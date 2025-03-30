# Project Tasks

## Task 1: Brainfuck Interpreter

Implement an interpreter for the **Brainfuck** programming language according to the definition described here:  
[Brainfuck on Wikipedia](https://en.wikipedia.org/wiki/Brainfuck).

The factory method **`Brainfuck.createInstance(String code, PrintStream out, InputStream in, int memorySize)`** is used for testing.  
For input/output operations, **do not** use `System.in` or `System.out` directly. Instead, use the provided `in` and `out` parameters.

### Important Notes:
1. **The Polish Wikipedia page contains errors in the command descriptions!**  
   Follow the English Wikipedia version.
2. **Certain behaviors are undefined in the language specification**  
   (e.g., what happens if a value goes below 0).  
   The interpreter has flexibility in such cases, and tests will not cover these undefined behaviors.

---

## Task 2: Random Battleship Board Generator

Implement a generator to randomly create valid **Battleship** game boards.  
The method to implement is:  
**`uj.wmii.pwj.collections.collections.BattleshipGenerator.generateMap()`**.

The `defaultInstance` method in the `BattleshipGenerator` interface acts as a factory  
and should return an instance of the implemented class.

### Board Details:
- The board is a **10x10 square grid**.
- The API should return a **`String` of length 100**.  
  - **Indices 0-9:** First row  
  - **Indices 10-19:** Second row  
  - And so on...
- Each cell contains:
  - **`*`** → Ship part (mast)
  - **`.`** → Water

### Ship Rules:
- Ships can be **1, 2, 3, or 4** masts in size.
- A ship consists of one or more adjacent `*` cells connected **horizontally or vertically**.
- Ships **cannot** be connected diagonally.
- **Valid ship count:**
  - **4** single-mast ships  
  - **3** two-mast ships  
  - **2** three-mast ships  
  - **1** four-mast ship  
- **Ships must not touch each other, even diagonally.**  
  There must be at least one empty cell between them.

### Valid Ships (surrounded by water):
```
...
.#.  -> Single-mast ship
...

......
.##.#. -> Two two-mast ships
....#.

.....
..#..  -> Three-mast ship
.##..

.........
......##.
.####.##. -> Two four-mast ships
.........
```

### Invalid Ships:
```
......
..#...  -> Invalid two-mast ship (diagonal connection)
...#..
......
.......
...#...
..#.#..  -> Invalid four-mast ship (diagonal connection)
...#...
```

### Example of a Valid Board:
```
..#.......#......#..#..#........##............##...##................#..##...#...##....#.#.......#..
```

### For better readability, the board with **line breaks every 10 characters**:
```
..#.......
#......#..
#..#......
..##......
......##..
.##.......
.........#
..##...#..
.##....#.#
.......#..
```
