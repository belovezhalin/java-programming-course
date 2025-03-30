# Project Overview

This project consists of four tasks.

## Task 1 - Hello World
The `HelloWorld` class should be implemented as a runnable application.  
It should print all input parameters, each on a new line.  
If no parameters are provided, it should display the message:  
**"No input parameters provided"**.

## Task 2 - Quadratic Equation
In the `QuadraticEquation` class, the `findRoots` method should be implemented  
to solve quadratic equations in the form of `ax² + bx + c = 0`.  

- If two roots are found, return an array of size **2** containing the roots.  
- If there is only one root, return an array of size **1**.  
- If there are no roots, return an empty array.

## Task 3 - Reverser
The `Reverser` class should implement two methods:

- **`reverse`**  
  - Returns the given string with all characters in reverse order.  
  - Leading and trailing whitespace should be removed.

- **`reverseWords`**  
  - Returns the given string with all words in reverse order.  
  - Leading and trailing whitespace should be removed.  
  - Words are separated by spaces.

## Task 4 - Banner
The `Banner` class should implement the `toBanner` method  
to return the input text as ASCII art, using the **"Banner"** font.  
The output should consist of **uppercase letters only**.  

If the input is `null`, the method should return an empty array.  
Each element in the returned array should represent a row of the ASCII-art text.  
The output text should follow these formatting rules:

- **Font:** [Banner](https://patorjk.com/software/taag/#p=display&f=Banner&t=)  
- **Text height:** 7  
- **Character width:** ~7  
- **Space between letters:** 1  
- **Space width:** 4
