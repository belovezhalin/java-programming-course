## Task for Lecture 2: Spreadsheet

The task is to implement calculations for a simple spreadsheet.  
The spreadsheet operates only on **integer numbers**, constrained by the `int` type.

In the class **`uj.wmii.pwj.spreadsheet.Spreadsheet`**,  
the `calculate` method should be implemented to process the input spreadsheet  
(provided as a parameter) and return a result spreadsheet with calculated values.  
The input array contains **rows and columns** in sequential order.

### Supported Operations:
- **Value:** If a cell contains a number, it should remain unchanged.
- **Reference:** If a cell starts with the `$` symbol, it refers to another cell.  
  References are structured similarly to Excel:  
  - `$A1` refers to **column A (1st column)** and **row 1**.  
  - `$C7` refers to **column C (3rd column)** and **row 7**.
- **Formula:** If a cell starts with the `=` symbol, it contains a formula.  
  A formula consists of:
  - `=` symbol.
  - Formula name.
  - Two comma-separated parameters enclosed in parentheses.  
  Parameters can be either **values** or **references** (but not another formula).

### Available Formulas:
- **`ADD`** - Adds both parameters.
- **`SUB`** - Subtracts the second parameter from the first.
- **`MUL`** - Multiplies both parameters.
- **`DIV`** - Performs integer division.
- **`MOD`** - Computes the remainder of integer division.

The spreadsheet will **not** contain circular references  
(e.g., cell `A1` referring to `B2`, while `B2` refers back to `A1`).

### Example:

#### Input Spreadsheet:
```xslt
1,2,3
4,5,6
$A1,$C1,$B3
=ADD(10,$A1),=SUB($C3,$A1),0
```

### Expected Output:
```xslt
1,2,3
4,5,6
1,3,3
11,2,0
```
