## Test Engine  

The task is to enhance the Test Engine from the lecture.  

### Requirements:  

* Extend (or completely redesign) the annotation system so that, in addition to runtime parameters, it is possible to pass an expected result correlated with them.  
* Verify whether the test result is correct. Each test can have one of three possible states:  
  * `PASS` – if the test completes successfully and the result is correct.  
  * `FAIL` – if the test completes but the result is incorrect.  
  * `ERROR` – if an unhandled exception (`java.lang.Exception`) is thrown.  
    **IMPORTANT**: The occurrence of this status does not interrupt the entire engine, but only terminates the single test!  
* Display the Test Engine name in ASCII ART along with test information before execution.  
* Provide a clear and readable output for test results. Optionally, display test execution progress.  
* Prepare a set of sample tests with execution scripts to demonstrate the functionality of the Test Engine.
