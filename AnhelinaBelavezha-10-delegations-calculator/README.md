## Delegations-calculator

Implement a calculator for the amount owed for a business trip delegation: class `Calc`, method `calculate`.

Test data can be found in the file `src/test/resources/delegations.csv`.

Parameters:
* `start`: start of the delegation, in the format `yyyy-mm-dd HH:MM timezone`
* `end`: end of the delegation, in the format `yyyy-mm-dd HH:MM timezone`
* `dailyRate`: daily rate for the delegation.

The test data also includes an `expected` column, which contains the expected result of the calculation.

Delegation calculation rules:
* For a full day (24 hours) - the full daily rate is owed.
* For up to 8 hours - 1/3 of the daily rate is owed.
* For more than 8 and up to 12 hours - 1/2 of the daily rate is owed.
* For more than 12 hours - the full daily rate is owed.
* If the start time is later than or equal to the end time, 0 is owed.
