## Florida Insurance ##

The file `FL_insurance.csv.zip` contains data on insurance in Florida from 2011-2012. Since there is a large amount of data, the file is compressed.

Using the file reading API, the data should be read from this file (it is recommended to use `ZipFile`) and converted into a list containing these records in memory. The list should be of type `List<InsuranceEntry>`.

Using stream processing, the following operations must be performed:

* Generate a file named `count.txt`, which should contain the number of counties.
* Generate a file named `tiv2012.txt`, which should contain the total insurance value of all properties for the year 2012 (column `"tiv_2012"`).
* Generate a file named `most_valuable.txt`, containing two columns: `"county"` and `"value"`.  
  - The `"county"` column should include the names of the 10 counties where the total insurance value increased the most between 2011 and 2012.  
  - The names should be sorted in descending order, from the largest increase to the smallest.  
  - The `"value"` column should contain the increase in insurance value for each county, rounded to two decimal places (5 rounded up; always showing two decimal places, even if it is 0).  

The generated files should be placed in the project's root directory. All files must include a header row.  
Column separator: comma `,`. Decimal separator: dot `.`.

### IMPORTANT:  
Hardcoding the results, even if the tests pass, does not count as completing the assignment.  
To receive a top grade, each processing step must be handled in a single stream operation.
