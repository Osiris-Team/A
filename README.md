# A
A language

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
Variables are made of 3 parts: `int a = 3;` They have a type (int), name (a) and value (3).
If we want the type can also be detected automatically like so: `a = 3;`
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
