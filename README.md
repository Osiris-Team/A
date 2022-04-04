# A
**A language compiles to C, which gets compiled to an executable or library. 
This means that A achieves the same cross-platform performance as C, with a 
simpler syntax and object-oriented code.**

**Download the [A-Sample](https://github.com/Osiris-Team/A-Sample) repo,
open a terminal in that folder and
execute `./a/a` or `.\a\a.exe` if you are on Windows.**

### Pros
- Object-oriented code without performance loss.
- Simple and easy to learn syntax.

### Cons
- Misses a lot of important features, since its in early development.
- Compilation takes longer since we have the extra compilation step from A to C.

### Statements
Multiple statements on a single line must be separated by a semicolon `;` otherwise
the semicolon is optional.
```A
int a = 10
int b = 20; int c = 30
```

### Comments
Everything inside a comment is ignored. 
`//` marks the start of a comment and goes until the end of the line.
```A
int a = 1 // Single line comment
```

### Variables
Variables are made of 3 parts: `int a = 3;` They have a type (int), name (a) and value (3). Note that the name cannot contain spaces.

It's also valid to define a variable without value: `int a;`. In that case a would return `null`, since memory only gets allocated when using `=`.

 - `boolean` has only two possible values: true and false. Represents one bit (0 or 1) of information.
 - `byte` is an 8-bit signed two's complement integer. It has a minimum value of -128 and a maximum value of 127 (inclusive).
 - `short` is a 16-bit signed two's complement integer. It has a minimum value of -32,768 and a maximum value of 32,767 (inclusive).
 - `int` is a 32-bit signed two's complement integer, which has a minimum value of -2^31 and a maximum value of 2^31 -1.
 - `long` is a 64-bit two's complement integer. The signed long has a minimum value of -2^63 and a maximum value of 2^63 -1.
 - `float` is a single-precision 32-bit IEEE 754 floating point.
 - `double` is a double-precision 64-bit IEEE 754 floating point.
 - `char` is a single 16-bit Unicode character. It has a minimum value of '\u0000' (or 0) and a maximum value of '\uffff' (or 65,535 inclusive).
 - `string` is a string of characters, or more accurately: an array of char.
 - `code` is a single code block. 
 - `T` is the generic type, which can be any of the types from above or any object.

Variables can have additional/optional attributes which get added 
after the type name example: `int final a = 10`
 - `final` makes the variable unchangeable after first value assignment.
 - `[<size>]` creates an array of the current type, of the specified size (integer type). 

### Scopes
A scope is code within brackets `{}`. 
It makes the variables within it inaccessible from outside.
```A
int a = 3
{
  int b = 0
  // a can be used here
}
// a can be used here
// b cannot be used here
```
Let's say the code above is located in the `Utils` file and we want to access 
it in our `Main` file:
```A
Utils utils = new Utils()
utils.a // Can be accessed
utils.b // Error: Cannot be accessed
```
### Functions
Functions are special code blocks that are held by the `code` variable.
Note that functions are final by default due to the limitations by the underlying C language.
```A
int b = 0;
code setNumber = {
  b = 9
}
setNumber // Does nothing
setNumber() // Executes the code

code getNumber = returns int {
  return b
}
int result = getNumber() // Executes the code and returns b
```
Parameters can be passed over too like so:
```A
code multiply = (int a, int b) returns int {
  return a * b
}
multiply(10, 20)
```
Note that changing a parameter's value in a function,
affects the original variables value:
```A
code setTo10 = (int a) {
  a = 10
}
int myVariable = 27
setTo10(myVariable)
// myVariable is now 10
```

### Files and Objects
Each file represents one object (must have no file extension).
```
project
 - Main
 - Math
 - folder
    - Math
```
`Math` can be used in `Main` like so:
```A
Math math = new Math()
```
`Math` in /folder can be used in `Main` like so:
```
Math math = new Math()
/folder/Math math1 = new Math()
```

### Constructors
Each object can have a contructor, which must be named the same as the file.
```A
code Main = {
}
```
If not provided the compiler adds it nevertheless. It behaves like a regular function,
which means that it can also return values and have parameters.
The only difference is in the way you call it: `new Main()` instead of `Main()`.
```A
code Main = (int a, int b) returns int {
  return a * 10;
}
```

### Inheritance
With the `extends` keyword you **can** inherit all the another objects' 
public methods/variables and you **can** override them.

With the `overrides` keyword you **must** inherit all the other objects'
public methods/variables and you **must** override them.

These keywords must be at the top of the file.
```A
extends AnotherObject, AnotherObject2
overrides AnotherObject3, /path/to/AnotherObject4

code Main = {
}
```

### Object example
The project structure used for this example:
```
project
 - Main
 - Math
```

`Main`
```A
int a = 10
int b = 20
Math math = new Math()
math.multiply(a, b)
math.divide(a, b)
```

`Math`
```A
// Public stuff:
code multiply
code divide

// Private stuff:
{ 
  int anotherValue = 10
  int tempValue = 4
  
  multiply = (int a, int b) returns int{
    return a * b;
  }
  
  divide = (int a, int b) returns int{
    return a / b;
  }
}
```


