# A
### A language compiles to C, which gets compiled to an executable or library. This means that A achieves the same cross-platform performance as C, with a simpler syntax and object-oriented code.

### Give it a spin
Download the [A-Sample](https://github.com/Osiris-Team/A-Sample) repo, open a terminal in that folder and
execute `./a/a` or `.\a\a.exe` if you are on Windows.

### Pros
- Object-oriented code without performance loss.
- Simple and easy to learn syntax.

### Cons
- Misses a lot of important features, since its in early development.
- Compilation takes longer since we have the extra compilation step from A to C.

### Statements
Multiple statements on a single line must be seperated by a semicolon `;`, otherwise
the semicolon is optional.
```A
int a = 10
int b = 20; int c = 30
```

### Comments
`//` marks the start of a comment that goes until the end of the line.
```A
int a = 1; // Single line comment
```

### Project
Each file represents one object (must have no file extensions).
```
project
 README.md
 Main   
-folder
  Math
  Number
--subfolder
   Math
   Person
   Wallet
   ...
```
If we want to use the `Math` object in our `Main` object, add its path to the top in `Main`:
```A
/folder/Math

Math math = Math();
```
If you have `Math` twice enter the path directly when creating the object:
```
/folder/Math math1 = Math();
/folder/subfolder/Math math2 = Math();
```

### Variables
Variables are made of 3 parts: `int a = 3;` They have a type (int), name (a) and value (3). Note that the name cannot contain spaces.

Its also valid to define a variable without value: `int a;`. In that case a would return `null`, since memory only gets allocated when using `=`.

 - `obj` parent type of all the below types. Holds the memory address of its variable name and value.
 - `boolean` has only two possible values: true and false. Represents one bit (0 or 1) of information.
 - `byte` is an 8-bit signed two's complement integer. It has a minimum value of -128 and a maximum value of 127 (inclusive).
 - `short` is a 16-bit signed two's complement integer. It has a minimum value of -32,768 and a maximum value of 32,767 (inclusive).
 - `int` is a 32-bit signed two's complement integer, which has a minimum value of -2^31 and a maximum value of 2^31 -1.
 - `long` is a 64-bit two's complement integer. The signed long has a minimum value of -2^63 and a maximum value of 2^63 -1.
 - `float` is a single-precision 32-bit IEEE 754 floating point.
 - `double` is a double-precision 64-bit IEEE 754 floating point.
 - `char` is a single 16-bit Unicode character. It has a minimum value of '\u0000' (or 0) and a maximum value of '\uffff' (or 65,535 inclusive).
 - `code` is a single code block. 

### Functions and Scopes
Code within brackets `{}` is private, which means that its variables are not accessible from outside the brackets.
```A
int a = 3;
{
  int b = 0;
  // a can be used here
}
// a can be used here
// b cannot be used here
```
Strictly speaking the variable b from above is inside a function that returns null.
To access that function and return something useful however, the returned type and function name must be added.
```A
int b = 0;
code setNumber = {
  b = 9;
}
setNumber; // Does nothing
setNumber(); // Executes the code

code int getNumber = {
  b = 0;
  return b;
}
int result = getNumber(); // Executes the code and returns b

code replaceNumber = (int x) {
  b = x;
}
replaceNumber(10);
```

### Constructors
Each file/object can have a contructor, which must be named the same as the file.
```A
code Main = {
}
```
If not provided the compiler adds it nevertheless. It behaves like a regular function,
which means that it can also return values and have parameters.
The only difference is in the way you call it: `new Main()` instead of `Main()`.
```A
code Main = (int a, int b) {
  return a * 10;
}
```

### Inheritance
With the `extends` keyword you can inherit all the another objects' public methods/variables and you can override them.
With the `overrides` keyword you must inherit all the other objects' public methods/variables and you must override them.
These keywords must be at the top of the file.
```A
extends AnotherObject, AnotherObject2
overrides AnotherObject3, AnotherObject4
```
