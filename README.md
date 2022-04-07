# A
**A language compiles to C, which gets compiled to an executable or library. 
This means that A achieves the same cross-platform performance as C, with a
significantly simpler syntax and object-oriented code. Maybe you will even have
fun coding in this language?**

**Download the [A-Sample](https://github.com/Osiris-Team/A-Sample) repo,
open a terminal in that folder and
execute `./a/a` or `.\a\a.exe` if you are on Windows.**

If you are unsure how to pronouce A, there are two good tutorials [here](https://www.youtube.com/watch?v=yBLdQ1a4-JI) and [here](https://www.youtube.com/watch?v=pwTzHbIXSlI).

### Aim
 - Provide both high and low level methods to everything.
 - Don't overcomplicate things and keep it easy to read/write.
 - Stay relevant, meaning new good stuff will replace old stuff. Breaking changes are ok.
 - Allow easy use of GPU and hardware acceleration, instead of only writing CPU code.
 - Performance and readability have the same importance.
 - Provide cross-platform methods for all input and output devices, like (touch-)screens, keyboards, files, etc.
 - Encourage the use of event listeners.

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

#### Numbers
 - `boolean` has only two possible values: true (1) and false (0). Represents one bit of information.
 - `byte` is an 8-bit signed two's complement integer. It has a minimum value of -128 and a maximum value of 127 (inclusive).
 - `short` is a 16-bit signed two's complement integer. It has a minimum value of -32,768 and a maximum value of 32,767 (inclusive).
 - `int` is a 32-bit signed two's complement integer, which has a minimum value of -2^31 and a maximum value of 2^31 -1.
 - `long` is a 64-bit two's complement integer. The signed long has a minimum value of -2^63 and a maximum value of 2^63 -1.
 - `float` is a single-precision 32-bit IEEE 754 floating point.
 - `double` is a double-precision 64-bit IEEE 754 floating point.
#### Text
 - `char` is a single 16-bit Unicode character. It has a minimum value of '\u0000' (or 0) and a maximum value of '\uffff' (or 65,535 inclusive).
 - `string` is a string of characters, or more accurately: an array of char with variable length.
#### Special
 - `code` is a single code block.
 - `<ObjectName>` is a type/object you created and named, that contains variables.
 - `any` is the generic type, which can be any of the types from above or any object.

Variables can have additional/optional attributes which get added 
after the type name example: `int locked a = 10`
 - `locked` makes the variable unchangeable after first value assignment.
 - `[<size>]` creates an array of the current type, of the specified size (integer type).
 - `!null` this variable is not allowed to be null. 

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
Note that functions are locked by default due to the limitations by the underlying C language.
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
multiply(a:10, b:20)
```
Note that changing a parameter's value in a function,
affects the original variables value:
```A
code setTo10 = (int a) {
  a = 10
}
int myVariable = 27
setTo10(a:myVariable)
// myVariable is now 10
```
Function parameters are by default optional, which means that
the compiler won't show errors if the function above was called without
parameters like this: `setTo10()`.
That's why the `!null` (not null) attribute exists, which forces the user
to pass over a parameter.
```A
code setTo10 = (int !null a) {
  a = 10
}
int myVariable = 27
setTo10(a:myVariable) // Valid
setTo10() // Not valid
```
This is done to avoid unnecessary function overloading and
writing cleaner/less code and less duplicate documentation.
```A
code multiply = (int !null a, int !null b, int c) {
  if(c!=null) return a * b * c
  else return a * b
}
multiply(a:10, b:20) // Valid
multiply(a:10, b:20, c:30) // Valid
multiply(a:10) // Not valid
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

### Constructor and the `new` keyword
`new ObjectName()` creates an instance of an object, which means that the code inside the objects'
file gets copied into memory and run. 
The example below should clarify it:

`Person`
```A
code Main = {
}
```
If not provided the compiler adds it nevertheless. It behaves like a regular function,
which means that it can also have parameters.
The difference is in the way you execute it: `new Main()` instead of `Main()` and
that it cannot return stuff since it returns the object.
```A
code Main = (int a, int b) {
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
extends Math

int a = 10
int b = 20

multiply(a, b)
divide(a, b)

// Or instead of extending you can do this:
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


